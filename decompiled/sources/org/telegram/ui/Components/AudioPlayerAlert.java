package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.firebase.messaging.Constants;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.audioinfo.AudioInfo;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.AudioPlayerCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.ChatReactionsEditActivity;
import org.telegram.ui.Components.AudioPlayerAlert;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SeekBarView;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.LaunchActivity;
/* loaded from: classes5.dex */
public class AudioPlayerAlert extends BottomSheet implements NotificationCenter.NotificationCenterDelegate, DownloadController.FileDownloadProgressListener {
    private static final int menu_speed_fast = 3;
    private static final int menu_speed_normal = 2;
    private static final int menu_speed_slow = 1;
    private static final int menu_speed_veryfast = 4;
    private int TAG;
    private ActionBar actionBar;
    private AnimatorSet actionBarAnimation;
    private View actionBarShadow;
    private ClippingTextViewSwitcher authorTextView;
    private BackupImageView bigAlbumConver;
    private boolean blurredAnimationInProgress;
    private FrameLayout blurredView;
    private CoverContainer coverContainer;
    private boolean currentAudioFinishedLoading;
    private String currentFile;
    private boolean draggingSeekBar;
    private TextView durationTextView;
    private ImageView emptyImageView;
    private TextView emptySubtitleTextView;
    private TextView emptyTitleTextView;
    private LinearLayout emptyView;
    private boolean inFullSize;
    private long lastBufferedPositionCheck;
    private int lastDuration;
    private MessageObject lastMessageObject;
    long lastRewindingTime;
    private int lastTime;
    long lastUpdateRewindingPlayerTime;
    private LinearLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private RLottieImageView nextButton;
    private ActionBarMenuItem optionsButton;
    private LaunchActivity parentActivity;
    private ImageView playButton;
    private PlayPauseDrawable playPauseDrawable;
    private ActionBarMenuItem playbackSpeedButton;
    private FrameLayout playerLayout;
    private View playerShadow;
    private ArrayList<MessageObject> playlist;
    private RLottieImageView prevButton;
    private LineProgressView progressView;
    private ActionBarMenuItem repeatButton;
    private ActionBarMenuSubItem repeatListItem;
    private ActionBarMenuSubItem repeatSongItem;
    private ActionBarMenuSubItem reverseOrderItem;
    int rewindingForwardPressedCount;
    int rewindingState;
    private ActionBarMenuItem searchItem;
    private int searchOpenOffset;
    private boolean searchWas;
    private boolean searching;
    private SeekBarView seekBarView;
    private ActionBarMenuSubItem shuffleListItem;
    private SimpleTextView timeTextView;
    private ClippingTextViewSwitcher titleTextView;
    private int topBeforeSwitch;
    private ActionBarMenuSubItem[] speedItems = new ActionBarMenuSubItem[4];
    private View[] buttons = new View[5];
    private boolean scrollToSong = true;
    private int searchOpenPosition = -1;
    private int scrollOffsetY = Integer.MAX_VALUE;
    float rewindingProgress = -1.0f;
    private final Runnable forwardSeek = new Runnable() { // from class: org.telegram.ui.Components.AudioPlayerAlert.1
        @Override // java.lang.Runnable
        public void run() {
            long dt;
            long duration = MediaController.getInstance().getDuration();
            if (duration != 0 && duration != C.TIME_UNSET) {
                float currentProgress = AudioPlayerAlert.this.rewindingProgress;
                long t = System.currentTimeMillis();
                long dt2 = t - AudioPlayerAlert.this.lastRewindingTime;
                AudioPlayerAlert.this.lastRewindingTime = t;
                long updateDt = t - AudioPlayerAlert.this.lastUpdateRewindingPlayerTime;
                if (AudioPlayerAlert.this.rewindingForwardPressedCount == 1) {
                    dt = (3 * dt2) - dt2;
                } else if (AudioPlayerAlert.this.rewindingForwardPressedCount == 2) {
                    dt = (6 * dt2) - dt2;
                } else {
                    dt = (12 * dt2) - dt2;
                }
                long currentTime = (((float) duration) * currentProgress) + ((float) dt);
                float currentProgress2 = ((float) currentTime) / ((float) duration);
                if (currentProgress2 < 0.0f) {
                    currentProgress2 = 0.0f;
                }
                AudioPlayerAlert.this.rewindingProgress = currentProgress2;
                MessageObject messageObject = MediaController.getInstance().getPlayingMessageObject();
                if (messageObject != null && messageObject.isMusic()) {
                    if (!MediaController.getInstance().isMessagePaused()) {
                        MediaController.getInstance().getPlayingMessageObject().audioProgress = AudioPlayerAlert.this.rewindingProgress;
                    }
                    AudioPlayerAlert.this.updateProgress(messageObject);
                }
                if (AudioPlayerAlert.this.rewindingState == 1 && AudioPlayerAlert.this.rewindingForwardPressedCount > 0 && MediaController.getInstance().isMessagePaused()) {
                    if (updateDt > 200 || AudioPlayerAlert.this.rewindingProgress == 0.0f) {
                        AudioPlayerAlert.this.lastUpdateRewindingPlayerTime = t;
                        MediaController.getInstance().seekToProgress(MediaController.getInstance().getPlayingMessageObject(), currentProgress2);
                    }
                    if (AudioPlayerAlert.this.rewindingForwardPressedCount > 0 && AudioPlayerAlert.this.rewindingProgress > 0.0f) {
                        AndroidUtilities.runOnUIThread(AudioPlayerAlert.this.forwardSeek, 16L);
                        return;
                    }
                    return;
                }
                return;
            }
            AudioPlayerAlert.this.lastRewindingTime = System.currentTimeMillis();
        }
    };

    public AudioPlayerAlert(final Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context, true, resourcesProvider);
        TLRPC.User user;
        fixNavigationBar();
        MessageObject messageObject = MediaController.getInstance().getPlayingMessageObject();
        if (messageObject != null) {
            this.currentAccount = messageObject.currentAccount;
        } else {
            this.currentAccount = UserConfig.selectedAccount;
        }
        this.parentActivity = (LaunchActivity) context;
        this.TAG = DownloadController.getInstance(this.currentAccount).generateObserverTag();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidReset);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidStart);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileLoaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileLoadProgressChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.musicDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.moreMusicDidLoad);
        this.containerView = new FrameLayout(context) { // from class: org.telegram.ui.Components.AudioPlayerAlert.2
            private int lastMeasturedHeight;
            private int lastMeasturedWidth;
            private RectF rect = new RectF();
            private boolean ignoreLayout = false;

            @Override // android.view.View
            public boolean onTouchEvent(MotionEvent e) {
                return !AudioPlayerAlert.this.isDismissed() && super.onTouchEvent(e);
            }

            @Override // android.widget.FrameLayout, android.view.View
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int padding;
                int totalHeight = View.MeasureSpec.getSize(heightMeasureSpec);
                int w = View.MeasureSpec.getSize(widthMeasureSpec);
                boolean z = false;
                if (totalHeight != this.lastMeasturedHeight || w != this.lastMeasturedWidth) {
                    if (AudioPlayerAlert.this.blurredView.getTag() != null) {
                        AudioPlayerAlert.this.showAlbumCover(false, false);
                    }
                    this.lastMeasturedWidth = w;
                    this.lastMeasturedHeight = totalHeight;
                }
                this.ignoreLayout = true;
                if (Build.VERSION.SDK_INT >= 21 && !AudioPlayerAlert.this.isFullscreen) {
                    setPadding(AudioPlayerAlert.this.backgroundPaddingLeft, AndroidUtilities.statusBarHeight, AudioPlayerAlert.this.backgroundPaddingLeft, 0);
                }
                AudioPlayerAlert.this.playerLayout.setVisibility((AudioPlayerAlert.this.searchWas || AudioPlayerAlert.this.keyboardVisible) ? 4 : 0);
                AudioPlayerAlert.this.playerShadow.setVisibility(AudioPlayerAlert.this.playerLayout.getVisibility());
                int availableHeight = totalHeight - getPaddingTop();
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) AudioPlayerAlert.this.listView.getLayoutParams();
                layoutParams.topMargin = ActionBar.getCurrentActionBarHeight();
                FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) AudioPlayerAlert.this.actionBarShadow.getLayoutParams();
                layoutParams2.topMargin = ActionBar.getCurrentActionBarHeight();
                FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) AudioPlayerAlert.this.blurredView.getLayoutParams();
                layoutParams3.topMargin = -getPaddingTop();
                int contentSize = AndroidUtilities.dp(179.0f);
                if (AudioPlayerAlert.this.playlist.size() > 1) {
                    contentSize += AudioPlayerAlert.this.backgroundPaddingTop + (AudioPlayerAlert.this.playlist.size() * AndroidUtilities.dp(56.0f));
                }
                if (AudioPlayerAlert.this.searching || AudioPlayerAlert.this.keyboardVisible) {
                    padding = AndroidUtilities.dp(8.0f);
                } else {
                    padding = (contentSize < availableHeight ? availableHeight - contentSize : availableHeight - ((int) ((availableHeight / 5) * 3.5f))) + AndroidUtilities.dp(8.0f);
                    if (padding > availableHeight - AndroidUtilities.dp(329.0f)) {
                        padding = availableHeight - AndroidUtilities.dp(329.0f);
                    }
                    if (padding < 0) {
                        padding = 0;
                    }
                }
                if (AudioPlayerAlert.this.listView.getPaddingTop() != padding) {
                    AudioPlayerAlert.this.listView.setPadding(0, padding, 0, (!AudioPlayerAlert.this.searching || !AudioPlayerAlert.this.keyboardVisible) ? AudioPlayerAlert.this.listView.getPaddingBottom() : 0);
                }
                this.ignoreLayout = false;
                super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(totalHeight, C.BUFFER_FLAG_ENCRYPTED));
                AudioPlayerAlert audioPlayerAlert = AudioPlayerAlert.this;
                if (getMeasuredHeight() >= totalHeight) {
                    z = true;
                }
                audioPlayerAlert.inFullSize = z;
            }

            @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                AudioPlayerAlert.this.updateLayout();
                AudioPlayerAlert.this.updateEmptyViewPosition();
            }

            @Override // android.view.ViewGroup
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                boolean dismiss;
                if (ev.getAction() == 0 && AudioPlayerAlert.this.scrollOffsetY != 0 && AudioPlayerAlert.this.actionBar.getAlpha() == 0.0f) {
                    boolean z = false;
                    if (AudioPlayerAlert.this.listAdapter.getItemCount() > 0) {
                        if (ev.getY() < AudioPlayerAlert.this.scrollOffsetY + AndroidUtilities.dp(12.0f)) {
                            z = true;
                        }
                        dismiss = z;
                    } else {
                        if (ev.getY() < getMeasuredHeight() - AndroidUtilities.dp(191.0f)) {
                            z = true;
                        }
                        dismiss = z;
                    }
                    if (dismiss) {
                        AudioPlayerAlert.this.dismiss();
                        return true;
                    }
                }
                boolean dismiss2 = super.onInterceptTouchEvent(ev);
                return dismiss2;
            }

            @Override // android.view.View, android.view.ViewParent
            public void requestLayout() {
                if (this.ignoreLayout) {
                    return;
                }
                super.requestLayout();
            }

            @Override // android.view.View
            protected void onDraw(Canvas canvas) {
                if (AudioPlayerAlert.this.playlist.size() <= 1) {
                    AudioPlayerAlert.this.shadowDrawable.setBounds(0, (getMeasuredHeight() - AudioPlayerAlert.this.playerLayout.getMeasuredHeight()) - AudioPlayerAlert.this.backgroundPaddingTop, getMeasuredWidth(), getMeasuredHeight());
                    AudioPlayerAlert.this.shadowDrawable.draw(canvas);
                    return;
                }
                int offset = AndroidUtilities.dp(13.0f);
                int top = (AudioPlayerAlert.this.scrollOffsetY - AudioPlayerAlert.this.backgroundPaddingTop) - offset;
                if (AudioPlayerAlert.this.currentSheetAnimationType == 1) {
                    top = (int) (top + AudioPlayerAlert.this.listView.getTranslationY());
                }
                int y = AndroidUtilities.dp(20.0f) + top;
                int height = getMeasuredHeight() + AndroidUtilities.dp(15.0f) + AudioPlayerAlert.this.backgroundPaddingTop;
                float rad = 1.0f;
                if (AudioPlayerAlert.this.backgroundPaddingTop + top < ActionBar.getCurrentActionBarHeight()) {
                    float toMove = AndroidUtilities.dp(4.0f) + offset;
                    float moveProgress = Math.min(1.0f, ((ActionBar.getCurrentActionBarHeight() - top) - AudioPlayerAlert.this.backgroundPaddingTop) / toMove);
                    float availableToMove = ActionBar.getCurrentActionBarHeight() - toMove;
                    int diff = (int) (availableToMove * moveProgress);
                    top -= diff;
                    y -= diff;
                    height += diff;
                    rad = 1.0f - moveProgress;
                }
                if (Build.VERSION.SDK_INT >= 21) {
                    top += AndroidUtilities.statusBarHeight;
                    y += AndroidUtilities.statusBarHeight;
                }
                AudioPlayerAlert.this.shadowDrawable.setBounds(0, top, getMeasuredWidth(), height);
                AudioPlayerAlert.this.shadowDrawable.draw(canvas);
                if (rad != 1.0f) {
                    Theme.dialogs_onlineCirclePaint.setColor(AudioPlayerAlert.this.getThemedColor(Theme.key_dialogBackground));
                    this.rect.set(AudioPlayerAlert.this.backgroundPaddingLeft, AudioPlayerAlert.this.backgroundPaddingTop + top, getMeasuredWidth() - AudioPlayerAlert.this.backgroundPaddingLeft, AudioPlayerAlert.this.backgroundPaddingTop + top + AndroidUtilities.dp(24.0f));
                    canvas.drawRoundRect(this.rect, AndroidUtilities.dp(12.0f) * rad, AndroidUtilities.dp(12.0f) * rad, Theme.dialogs_onlineCirclePaint);
                }
                if (rad != 0.0f) {
                    int w = AndroidUtilities.dp(36.0f);
                    this.rect.set((getMeasuredWidth() - w) / 2, y, (getMeasuredWidth() + w) / 2, AndroidUtilities.dp(4.0f) + y);
                    int color = AudioPlayerAlert.this.getThemedColor(Theme.key_sheet_scrollUp);
                    int alpha = Color.alpha(color);
                    Theme.dialogs_onlineCirclePaint.setColor(color);
                    Theme.dialogs_onlineCirclePaint.setAlpha((int) (alpha * 1.0f * rad));
                    canvas.drawRoundRect(this.rect, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), Theme.dialogs_onlineCirclePaint);
                }
                int color1 = AudioPlayerAlert.this.getThemedColor(Theme.key_dialogBackground);
                int finalColor = Color.argb((int) (AudioPlayerAlert.this.actionBar.getAlpha() * 255.0f), (int) (Color.red(color1) * 0.8f), (int) (Color.green(color1) * 0.8f), (int) (Color.blue(color1) * 0.8f));
                Theme.dialogs_onlineCirclePaint.setColor(finalColor);
                canvas.drawRect(AudioPlayerAlert.this.backgroundPaddingLeft, 0.0f, getMeasuredWidth() - AudioPlayerAlert.this.backgroundPaddingLeft, AndroidUtilities.statusBarHeight, Theme.dialogs_onlineCirclePaint);
            }

            @Override // android.view.ViewGroup, android.view.View
            protected void onAttachedToWindow() {
                super.onAttachedToWindow();
                Bulletin.addDelegate(this, new Bulletin.Delegate() { // from class: org.telegram.ui.Components.AudioPlayerAlert.2.1
                    @Override // org.telegram.ui.Components.Bulletin.Delegate
                    public /* synthetic */ void onHide(Bulletin bulletin) {
                        Bulletin.Delegate.CC.$default$onHide(this, bulletin);
                    }

                    @Override // org.telegram.ui.Components.Bulletin.Delegate
                    public /* synthetic */ void onOffsetChange(float f) {
                        Bulletin.Delegate.CC.$default$onOffsetChange(this, f);
                    }

                    @Override // org.telegram.ui.Components.Bulletin.Delegate
                    public /* synthetic */ void onShow(Bulletin bulletin) {
                        Bulletin.Delegate.CC.$default$onShow(this, bulletin);
                    }

                    @Override // org.telegram.ui.Components.Bulletin.Delegate
                    public int getBottomOffset(int tag) {
                        return AudioPlayerAlert.this.playerLayout.getHeight();
                    }
                });
            }

            @Override // android.view.ViewGroup, android.view.View
            protected void onDetachedFromWindow() {
                super.onDetachedFromWindow();
                Bulletin.removeDelegate(this);
            }
        };
        this.containerView.setWillNotDraw(false);
        this.containerView.setPadding(this.backgroundPaddingLeft, 0, this.backgroundPaddingLeft, 0);
        ActionBar actionBar = new ActionBar(context, resourcesProvider) { // from class: org.telegram.ui.Components.AudioPlayerAlert.3
            @Override // android.view.View
            public void setAlpha(float alpha) {
                super.setAlpha(alpha);
                AudioPlayerAlert.this.containerView.invalidate();
            }
        };
        this.actionBar = actionBar;
        actionBar.setBackgroundColor(getThemedColor(Theme.key_player_actionBar));
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setItemsColor(getThemedColor(Theme.key_player_actionBarTitle), false);
        this.actionBar.setItemsBackgroundColor(getThemedColor(Theme.key_player_actionBarSelector), false);
        this.actionBar.setTitleColor(getThemedColor(Theme.key_player_actionBarTitle));
        this.actionBar.setTitle(LocaleController.getString("AttachMusic", R.string.AttachMusic));
        this.actionBar.setSubtitleColor(getThemedColor(Theme.key_player_actionBarSubtitle));
        this.actionBar.setOccupyStatusBar(false);
        this.actionBar.setAlpha(0.0f);
        if (messageObject != null && !MediaController.getInstance().currentPlaylistIsGlobalSearch()) {
            long did = messageObject.getDialogId();
            if (DialogObject.isEncryptedDialog(did)) {
                TLRPC.EncryptedChat encryptedChat = MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf(DialogObject.getEncryptedChatId(did)));
                if (encryptedChat != null && (user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(encryptedChat.user_id))) != null) {
                    this.actionBar.setTitle(ContactsController.formatName(user.first_name, user.last_name));
                }
            } else if (DialogObject.isUserDialog(did)) {
                TLRPC.User user2 = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(did));
                if (user2 != null) {
                    this.actionBar.setTitle(ContactsController.formatName(user2.first_name, user2.last_name));
                }
            } else {
                TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-did));
                if (chat != null) {
                    this.actionBar.setTitle(chat.title);
                }
            }
        }
        ActionBarMenu menu = this.actionBar.createMenu();
        ActionBarMenuItem actionBarMenuItemSearchListener = menu.addItem(0, R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() { // from class: org.telegram.ui.Components.AudioPlayerAlert.4
            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public void onSearchCollapse() {
                if (AudioPlayerAlert.this.searching) {
                    AudioPlayerAlert.this.searchWas = false;
                    AudioPlayerAlert.this.searching = false;
                    AudioPlayerAlert.this.setAllowNestedScroll(true);
                    AudioPlayerAlert.this.listAdapter.search(null);
                }
            }

            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public void onSearchExpand() {
                AudioPlayerAlert audioPlayerAlert = AudioPlayerAlert.this;
                audioPlayerAlert.searchOpenPosition = audioPlayerAlert.layoutManager.findLastVisibleItemPosition();
                View firstVisView = AudioPlayerAlert.this.layoutManager.findViewByPosition(AudioPlayerAlert.this.searchOpenPosition);
                AudioPlayerAlert.this.searchOpenOffset = firstVisView == null ? 0 : firstVisView.getTop();
                AudioPlayerAlert.this.searching = true;
                AudioPlayerAlert.this.setAllowNestedScroll(false);
                AudioPlayerAlert.this.listAdapter.notifyDataSetChanged();
            }

            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public void onTextChanged(EditText editText) {
                if (editText.length() > 0) {
                    AudioPlayerAlert.this.listAdapter.search(editText.getText().toString());
                    return;
                }
                AudioPlayerAlert.this.searchWas = false;
                AudioPlayerAlert.this.listAdapter.search(null);
            }
        });
        this.searchItem = actionBarMenuItemSearchListener;
        actionBarMenuItemSearchListener.setContentDescription(LocaleController.getString("Search", R.string.Search));
        EditTextBoldCursor editText = this.searchItem.getSearchField();
        editText.setHint(LocaleController.getString("Search", R.string.Search));
        editText.setTextColor(getThemedColor(Theme.key_player_actionBarTitle));
        editText.setHintTextColor(getThemedColor(Theme.key_player_time));
        editText.setCursorColor(getThemedColor(Theme.key_player_actionBarTitle));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.Components.AudioPlayerAlert.5
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int id) {
                if (id != -1) {
                    AudioPlayerAlert.this.onSubItemClick(id);
                } else {
                    AudioPlayerAlert.this.dismiss();
                }
            }
        });
        View view = new View(context);
        this.actionBarShadow = view;
        view.setAlpha(0.0f);
        this.actionBarShadow.setBackgroundResource(R.drawable.header_shadow);
        View view2 = new View(context);
        this.playerShadow = view2;
        view2.setBackgroundColor(getThemedColor(Theme.key_dialogShadowLine));
        this.playerLayout = new FrameLayout(context) { // from class: org.telegram.ui.Components.AudioPlayerAlert.6
            @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                if (AudioPlayerAlert.this.playbackSpeedButton != null && AudioPlayerAlert.this.durationTextView != null) {
                    int x = (AudioPlayerAlert.this.durationTextView.getLeft() - AndroidUtilities.dp(4.0f)) - AudioPlayerAlert.this.playbackSpeedButton.getMeasuredWidth();
                    AudioPlayerAlert.this.playbackSpeedButton.layout(x, AudioPlayerAlert.this.playbackSpeedButton.getTop(), AudioPlayerAlert.this.playbackSpeedButton.getMeasuredWidth() + x, AudioPlayerAlert.this.playbackSpeedButton.getBottom());
                }
            }
        };
        CoverContainer coverContainer = new CoverContainer(context) { // from class: org.telegram.ui.Components.AudioPlayerAlert.7
            private long pressTime;

            @Override // android.view.View
            public boolean onTouchEvent(MotionEvent event) {
                int action = event.getAction();
                if (action == 0) {
                    if (getImageReceiver().hasBitmapImage()) {
                        AudioPlayerAlert.this.showAlbumCover(true, true);
                        this.pressTime = SystemClock.elapsedRealtime();
                    }
                } else if (action != 2 && SystemClock.elapsedRealtime() - this.pressTime >= 400) {
                    AudioPlayerAlert.this.showAlbumCover(false, true);
                }
                return true;
            }

            @Override // org.telegram.ui.Components.AudioPlayerAlert.CoverContainer
            protected void onImageUpdated(ImageReceiver imageReceiver) {
                if (AudioPlayerAlert.this.blurredView.getTag() != null) {
                    AudioPlayerAlert.this.bigAlbumConver.setImageBitmap(imageReceiver.getBitmap());
                }
            }
        };
        this.coverContainer = coverContainer;
        this.playerLayout.addView(coverContainer, LayoutHelper.createFrame(44, 44.0f, 53, 0.0f, 20.0f, 20.0f, 0.0f));
        ClippingTextViewSwitcher clippingTextViewSwitcher = new ClippingTextViewSwitcher(context) { // from class: org.telegram.ui.Components.AudioPlayerAlert.8
            @Override // org.telegram.ui.Components.AudioPlayerAlert.ClippingTextViewSwitcher
            protected TextView createTextView() {
                TextView textView = new TextView(context);
                textView.setTextColor(AudioPlayerAlert.this.getThemedColor(Theme.key_player_actionBarTitle));
                textView.setTextSize(1, 17.0f);
                textView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
                textView.setEllipsize(TextUtils.TruncateAt.END);
                textView.setSingleLine(true);
                return textView;
            }
        };
        this.titleTextView = clippingTextViewSwitcher;
        this.playerLayout.addView(clippingTextViewSwitcher, LayoutHelper.createFrame(-1, -2.0f, 51, 20.0f, 20.0f, 72.0f, 0.0f));
        AnonymousClass9 anonymousClass9 = new AnonymousClass9(context, context);
        this.authorTextView = anonymousClass9;
        this.playerLayout.addView(anonymousClass9, LayoutHelper.createFrame(-1, -2.0f, 51, 14.0f, 47.0f, 72.0f, 0.0f));
        SeekBarView seekBarView = new SeekBarView(context, resourcesProvider) { // from class: org.telegram.ui.Components.AudioPlayerAlert.10
            /* JADX INFO: Access modifiers changed from: package-private */
            @Override // org.telegram.ui.Components.SeekBarView
            public boolean onTouch(MotionEvent ev) {
                if (AudioPlayerAlert.this.rewindingState != 0) {
                    return false;
                }
                return super.onTouch(ev);
            }
        };
        this.seekBarView = seekBarView;
        seekBarView.setDelegate(new SeekBarView.SeekBarViewDelegate() { // from class: org.telegram.ui.Components.AudioPlayerAlert.11
            @Override // org.telegram.ui.Components.SeekBarView.SeekBarViewDelegate
            public /* synthetic */ int getStepsCount() {
                return SeekBarView.SeekBarViewDelegate.CC.$default$getStepsCount(this);
            }

            @Override // org.telegram.ui.Components.SeekBarView.SeekBarViewDelegate
            public void onSeekBarDrag(boolean stop, float progress) {
                if (stop) {
                    MediaController.getInstance().seekToProgress(MediaController.getInstance().getPlayingMessageObject(), progress);
                }
                MessageObject messageObject2 = MediaController.getInstance().getPlayingMessageObject();
                if (messageObject2 != null && messageObject2.isMusic()) {
                    AudioPlayerAlert.this.updateProgress(messageObject2);
                }
            }

            @Override // org.telegram.ui.Components.SeekBarView.SeekBarViewDelegate
            public void onSeekBarPressed(boolean pressed) {
                AudioPlayerAlert.this.draggingSeekBar = pressed;
            }

            @Override // org.telegram.ui.Components.SeekBarView.SeekBarViewDelegate
            public CharSequence getContentDescription() {
                String time = LocaleController.formatPluralString("Minutes", AudioPlayerAlert.this.lastTime / 60, new Object[0]) + ' ' + LocaleController.formatPluralString("Seconds", AudioPlayerAlert.this.lastTime % 60, new Object[0]);
                String totalTime = LocaleController.formatPluralString("Minutes", AudioPlayerAlert.this.lastDuration / 60, new Object[0]) + ' ' + LocaleController.formatPluralString("Seconds", AudioPlayerAlert.this.lastDuration % 60, new Object[0]);
                return LocaleController.formatString("AccDescrPlayerDuration", R.string.AccDescrPlayerDuration, time, totalTime);
            }
        });
        this.seekBarView.setReportChanges(true);
        this.playerLayout.addView(this.seekBarView, LayoutHelper.createFrame(-1, 38.0f, 51, 5.0f, 70.0f, 5.0f, 0.0f));
        LineProgressView lineProgressView = new LineProgressView(context);
        this.progressView = lineProgressView;
        lineProgressView.setVisibility(4);
        this.progressView.setBackgroundColor(getThemedColor(Theme.key_player_progressBackground));
        this.progressView.setProgressColor(getThemedColor(Theme.key_player_progress));
        this.playerLayout.addView(this.progressView, LayoutHelper.createFrame(-1, 2.0f, 51, 21.0f, 90.0f, 21.0f, 0.0f));
        SimpleTextView simpleTextView = new SimpleTextView(context);
        this.timeTextView = simpleTextView;
        simpleTextView.setTextSize(12);
        this.timeTextView.setText("0:00");
        this.timeTextView.setTextColor(getThemedColor(Theme.key_player_time));
        this.timeTextView.setImportantForAccessibility(2);
        this.playerLayout.addView(this.timeTextView, LayoutHelper.createFrame(100, -2.0f, 51, 20.0f, 98.0f, 0.0f, 0.0f));
        TextView textView = new TextView(context);
        this.durationTextView = textView;
        textView.setTextSize(1, 12.0f);
        this.durationTextView.setTextColor(getThemedColor(Theme.key_player_time));
        this.durationTextView.setGravity(17);
        this.durationTextView.setImportantForAccessibility(2);
        this.playerLayout.addView(this.durationTextView, LayoutHelper.createFrame(-2, -2.0f, 53, 0.0f, 96.0f, 20.0f, 0.0f));
        ActionBarMenuItem actionBarMenuItem = new ActionBarMenuItem(context, null, 0, getThemedColor(Theme.key_dialogTextBlack), false, resourcesProvider);
        this.playbackSpeedButton = actionBarMenuItem;
        actionBarMenuItem.setLongClickEnabled(false);
        this.playbackSpeedButton.setShowSubmenuByMove(false);
        this.playbackSpeedButton.setAdditionalYOffset(-AndroidUtilities.dp(224.0f));
        this.playbackSpeedButton.setContentDescription(LocaleController.getString("AccDescrPlayerSpeed", R.string.AccDescrPlayerSpeed));
        this.playbackSpeedButton.setDelegate(new ActionBarMenuItem.ActionBarMenuItemDelegate() { // from class: org.telegram.ui.Components.AudioPlayerAlert$$ExternalSyntheticLambda10
            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemDelegate
            public final void onItemClick(int i) {
                AudioPlayerAlert.this.m2191lambda$new$0$orgtelegramuiComponentsAudioPlayerAlert(i);
            }
        });
        this.speedItems[0] = this.playbackSpeedButton.addSubItem(1, R.drawable.msg_speed_0_5, LocaleController.getString("SpeedSlow", R.string.SpeedSlow));
        this.speedItems[1] = this.playbackSpeedButton.addSubItem(2, R.drawable.msg_speed_1, LocaleController.getString("SpeedNormal", R.string.SpeedNormal));
        this.speedItems[2] = this.playbackSpeedButton.addSubItem(3, R.drawable.msg_speed_1_5, LocaleController.getString("SpeedFast", R.string.SpeedFast));
        this.speedItems[3] = this.playbackSpeedButton.addSubItem(4, R.drawable.msg_speed_2, LocaleController.getString("SpeedVeryFast", R.string.SpeedVeryFast));
        if (AndroidUtilities.density >= 3.0f) {
            this.playbackSpeedButton.setPadding(0, 1, 0, 0);
        }
        this.playbackSpeedButton.setAdditionalXOffset(AndroidUtilities.dp(8.0f));
        this.playbackSpeedButton.setShowedFromBottom(true);
        this.playerLayout.addView(this.playbackSpeedButton, LayoutHelper.createFrame(36, 36.0f, 53, 0.0f, 86.0f, 20.0f, 0.0f));
        this.playbackSpeedButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.AudioPlayerAlert$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view3) {
                AudioPlayerAlert.this.m2192lambda$new$1$orgtelegramuiComponentsAudioPlayerAlert(view3);
            }
        });
        this.playbackSpeedButton.setOnLongClickListener(new View.OnLongClickListener() { // from class: org.telegram.ui.Components.AudioPlayerAlert$$ExternalSyntheticLambda7
            @Override // android.view.View.OnLongClickListener
            public final boolean onLongClick(View view3) {
                return AudioPlayerAlert.this.m2193lambda$new$2$orgtelegramuiComponentsAudioPlayerAlert(view3);
            }
        });
        updatePlaybackButton();
        FrameLayout bottomView = new FrameLayout(context) { // from class: org.telegram.ui.Components.AudioPlayerAlert.12
            @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                int dist = ((right - left) - AndroidUtilities.dp(248.0f)) / 4;
                for (int a = 0; a < 5; a++) {
                    int l = AndroidUtilities.dp((a * 48) + 4) + (dist * a);
                    int t = AndroidUtilities.dp(9.0f);
                    AudioPlayerAlert.this.buttons[a].layout(l, t, AudioPlayerAlert.this.buttons[a].getMeasuredWidth() + l, AudioPlayerAlert.this.buttons[a].getMeasuredHeight() + t);
                }
            }
        };
        this.playerLayout.addView(bottomView, LayoutHelper.createFrame(-1, 66.0f, 51, 0.0f, 111.0f, 0.0f, 0.0f));
        View[] viewArr = this.buttons;
        ActionBarMenuItem actionBarMenuItem2 = new ActionBarMenuItem(context, null, 0, 0, false, resourcesProvider);
        this.repeatButton = actionBarMenuItem2;
        viewArr[0] = actionBarMenuItem2;
        actionBarMenuItem2.setLongClickEnabled(false);
        this.repeatButton.setShowSubmenuByMove(false);
        this.repeatButton.setAdditionalYOffset(-AndroidUtilities.dp(166.0f));
        if (Build.VERSION.SDK_INT >= 21) {
            this.repeatButton.setBackgroundDrawable(Theme.createSelectorDrawable(getThemedColor(Theme.key_listSelector), 1, AndroidUtilities.dp(18.0f)));
        }
        bottomView.addView(this.repeatButton, LayoutHelper.createFrame(48, 48, 51));
        this.repeatButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.AudioPlayerAlert$$ExternalSyntheticLambda4
            @Override // android.view.View.OnClickListener
            public final void onClick(View view3) {
                AudioPlayerAlert.this.m2194lambda$new$3$orgtelegramuiComponentsAudioPlayerAlert(view3);
            }
        });
        this.repeatSongItem = this.repeatButton.addSubItem(3, R.drawable.player_new_repeatone, LocaleController.getString("RepeatSong", R.string.RepeatSong));
        this.repeatListItem = this.repeatButton.addSubItem(4, R.drawable.player_new_repeatall, LocaleController.getString("RepeatList", R.string.RepeatList));
        this.shuffleListItem = this.repeatButton.addSubItem(2, R.drawable.player_new_shuffle, LocaleController.getString("ShuffleList", R.string.ShuffleList));
        this.reverseOrderItem = this.repeatButton.addSubItem(1, R.drawable.player_new_order, LocaleController.getString("ReverseOrder", R.string.ReverseOrder));
        this.repeatButton.setShowedFromBottom(true);
        this.repeatButton.setDelegate(new ActionBarMenuItem.ActionBarMenuItemDelegate() { // from class: org.telegram.ui.Components.AudioPlayerAlert$$ExternalSyntheticLambda11
            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemDelegate
            public final void onItemClick(int i) {
                AudioPlayerAlert.this.m2195lambda$new$4$orgtelegramuiComponentsAudioPlayerAlert(i);
            }
        });
        int iconColor = getThemedColor(Theme.key_player_button);
        float touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        View[] viewArr2 = this.buttons;
        AnonymousClass13 anonymousClass13 = new AnonymousClass13(context, touchSlop);
        this.prevButton = anonymousClass13;
        viewArr2[1] = anonymousClass13;
        anonymousClass13.setScaleType(ImageView.ScaleType.CENTER);
        this.prevButton.setAnimation(R.raw.player_prev, 20, 20);
        this.prevButton.setLayerColor("Triangle 3.**", iconColor);
        this.prevButton.setLayerColor("Triangle 4.**", iconColor);
        this.prevButton.setLayerColor("Rectangle 4.**", iconColor);
        if (Build.VERSION.SDK_INT >= 21) {
            this.prevButton.setBackgroundDrawable(Theme.createSelectorDrawable(getThemedColor(Theme.key_listSelector), 1, AndroidUtilities.dp(22.0f)));
        }
        bottomView.addView(this.prevButton, LayoutHelper.createFrame(48, 48, 51));
        this.prevButton.setContentDescription(LocaleController.getString("AccDescrPrevious", R.string.AccDescrPrevious));
        View[] viewArr3 = this.buttons;
        ImageView imageView = new ImageView(context);
        this.playButton = imageView;
        viewArr3[2] = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        ImageView imageView2 = this.playButton;
        PlayPauseDrawable playPauseDrawable = new PlayPauseDrawable(28);
        this.playPauseDrawable = playPauseDrawable;
        imageView2.setImageDrawable(playPauseDrawable);
        this.playPauseDrawable.setPause(!MediaController.getInstance().isMessagePaused(), false);
        this.playButton.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_player_button), PorterDuff.Mode.MULTIPLY));
        if (Build.VERSION.SDK_INT >= 21) {
            this.playButton.setBackgroundDrawable(Theme.createSelectorDrawable(getThemedColor(Theme.key_listSelector), 1, AndroidUtilities.dp(24.0f)));
        }
        bottomView.addView(this.playButton, LayoutHelper.createFrame(48, 48, 51));
        this.playButton.setOnClickListener(AudioPlayerAlert$$ExternalSyntheticLambda6.INSTANCE);
        View[] viewArr4 = this.buttons;
        AnonymousClass14 anonymousClass14 = new AnonymousClass14(context, touchSlop);
        this.nextButton = anonymousClass14;
        viewArr4[3] = anonymousClass14;
        anonymousClass14.setScaleType(ImageView.ScaleType.CENTER);
        this.nextButton.setAnimation(R.raw.player_prev, 20, 20);
        this.nextButton.setLayerColor("Triangle 3.**", iconColor);
        this.nextButton.setLayerColor("Triangle 4.**", iconColor);
        this.nextButton.setLayerColor("Rectangle 4.**", iconColor);
        this.nextButton.setRotation(180.0f);
        if (Build.VERSION.SDK_INT >= 21) {
            this.nextButton.setBackgroundDrawable(Theme.createSelectorDrawable(getThemedColor(Theme.key_listSelector), 1, AndroidUtilities.dp(22.0f)));
        }
        bottomView.addView(this.nextButton, LayoutHelper.createFrame(48, 48, 51));
        this.nextButton.setContentDescription(LocaleController.getString("Next", R.string.Next));
        View[] viewArr5 = this.buttons;
        ActionBarMenuItem actionBarMenuItem3 = new ActionBarMenuItem(context, null, 0, iconColor, false, resourcesProvider);
        this.optionsButton = actionBarMenuItem3;
        viewArr5[4] = actionBarMenuItem3;
        actionBarMenuItem3.setLongClickEnabled(false);
        this.optionsButton.setShowSubmenuByMove(false);
        this.optionsButton.setIcon(R.drawable.ic_ab_other);
        this.optionsButton.setSubMenuOpenSide(2);
        this.optionsButton.setAdditionalYOffset(-AndroidUtilities.dp(157.0f));
        if (Build.VERSION.SDK_INT >= 21) {
            this.optionsButton.setBackgroundDrawable(Theme.createSelectorDrawable(getThemedColor(Theme.key_listSelector), 1, AndroidUtilities.dp(18.0f)));
        }
        bottomView.addView(this.optionsButton, LayoutHelper.createFrame(48, 48, 51));
        this.optionsButton.addSubItem(1, R.drawable.msg_forward, LocaleController.getString("Forward", R.string.Forward));
        this.optionsButton.addSubItem(2, R.drawable.msg_shareout, LocaleController.getString("ShareFile", R.string.ShareFile));
        this.optionsButton.addSubItem(5, R.drawable.msg_download, LocaleController.getString("SaveToMusic", R.string.SaveToMusic));
        this.optionsButton.addSubItem(4, R.drawable.msg_message, LocaleController.getString("ShowInChat", R.string.ShowInChat));
        this.optionsButton.setShowedFromBottom(true);
        this.optionsButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.AudioPlayerAlert$$ExternalSyntheticLambda5
            @Override // android.view.View.OnClickListener
            public final void onClick(View view3) {
                AudioPlayerAlert.this.m2196lambda$new$6$orgtelegramuiComponentsAudioPlayerAlert(view3);
            }
        });
        this.optionsButton.setDelegate(new ActionBarMenuItem.ActionBarMenuItemDelegate() { // from class: org.telegram.ui.Components.AudioPlayerAlert$$ExternalSyntheticLambda12
            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemDelegate
            public final void onItemClick(int i) {
                AudioPlayerAlert.this.onSubItemClick(i);
            }
        });
        this.optionsButton.setContentDescription(LocaleController.getString("AccDescrMoreOptions", R.string.AccDescrMoreOptions));
        LinearLayout linearLayout = new LinearLayout(context);
        this.emptyView = linearLayout;
        linearLayout.setOrientation(1);
        this.emptyView.setGravity(17);
        this.emptyView.setVisibility(8);
        this.containerView.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        this.emptyView.setOnTouchListener(AudioPlayerAlert$$ExternalSyntheticLambda8.INSTANCE);
        ImageView imageView3 = new ImageView(context);
        this.emptyImageView = imageView3;
        imageView3.setImageResource(R.drawable.music_empty);
        this.emptyImageView.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_dialogEmptyImage), PorterDuff.Mode.MULTIPLY));
        this.emptyView.addView(this.emptyImageView, LayoutHelper.createLinear(-2, -2));
        TextView textView2 = new TextView(context);
        this.emptyTitleTextView = textView2;
        textView2.setTextColor(getThemedColor(Theme.key_dialogEmptyText));
        this.emptyTitleTextView.setGravity(17);
        this.emptyTitleTextView.setText(LocaleController.getString("NoAudioFound", R.string.NoAudioFound));
        this.emptyTitleTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.emptyTitleTextView.setTextSize(1, 17.0f);
        this.emptyTitleTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), 0);
        this.emptyView.addView(this.emptyTitleTextView, LayoutHelper.createLinear(-2, -2, 17, 0, 11, 0, 0));
        TextView textView3 = new TextView(context);
        this.emptySubtitleTextView = textView3;
        textView3.setTextColor(getThemedColor(Theme.key_dialogEmptyText));
        this.emptySubtitleTextView.setGravity(17);
        this.emptySubtitleTextView.setTextSize(1, 15.0f);
        this.emptySubtitleTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), 0);
        this.emptyView.addView(this.emptySubtitleTextView, LayoutHelper.createLinear(-2, -2, 17, 0, 6, 0, 0));
        RecyclerListView recyclerListView = new RecyclerListView(context) { // from class: org.telegram.ui.Components.AudioPlayerAlert.15
            boolean ignoreLayout;

            @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup, android.view.View
            public void onLayout(boolean changed, int l, int t, int r, int b) {
                super.onLayout(changed, l, t, r, b);
                if (AudioPlayerAlert.this.searchOpenPosition == -1 || AudioPlayerAlert.this.actionBar.isSearchFieldVisible()) {
                    if (AudioPlayerAlert.this.scrollToSong) {
                        AudioPlayerAlert.this.scrollToSong = false;
                        this.ignoreLayout = true;
                        if (AudioPlayerAlert.this.scrollToCurrentSong(true)) {
                            super.onLayout(false, l, t, r, b);
                        }
                        this.ignoreLayout = false;
                        return;
                    }
                    return;
                }
                this.ignoreLayout = true;
                AudioPlayerAlert.this.layoutManager.scrollToPositionWithOffset(AudioPlayerAlert.this.searchOpenPosition, AudioPlayerAlert.this.searchOpenOffset - AudioPlayerAlert.this.listView.getPaddingTop());
                super.onLayout(false, l, t, r, b);
                this.ignoreLayout = false;
                AudioPlayerAlert.this.searchOpenPosition = -1;
            }

            @Override // org.telegram.ui.Components.RecyclerListView
            protected boolean allowSelectChildAtPosition(float x, float y) {
                return y < AudioPlayerAlert.this.playerLayout.getY() - ((float) AudioPlayerAlert.this.listView.getTop());
            }

            @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View, android.view.ViewParent
            public void requestLayout() {
                if (this.ignoreLayout) {
                    return;
                }
                super.requestLayout();
            }
        };
        this.listView = recyclerListView;
        recyclerListView.setClipToPadding(false);
        RecyclerListView recyclerListView2 = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView2.setLayoutManager(linearLayoutManager);
        this.listView.setHorizontalScrollBarEnabled(false);
        this.listView.setVerticalScrollBarEnabled(false);
        this.containerView.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        RecyclerListView recyclerListView3 = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.listAdapter = listAdapter;
        recyclerListView3.setAdapter(listAdapter);
        this.listView.setGlowColor(getThemedColor(Theme.key_dialogScrollGlow));
        this.listView.setOnItemClickListener(AudioPlayerAlert$$ExternalSyntheticLambda2.INSTANCE);
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.Components.AudioPlayerAlert.16
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 0) {
                    int offset = AndroidUtilities.dp(13.0f);
                    int top = (AudioPlayerAlert.this.scrollOffsetY - AudioPlayerAlert.this.backgroundPaddingTop) - offset;
                    if (AudioPlayerAlert.this.backgroundPaddingTop + top < ActionBar.getCurrentActionBarHeight() && AudioPlayerAlert.this.listView.canScrollVertically(1)) {
                        AudioPlayerAlert.this.listView.getChildAt(0);
                        RecyclerListView.Holder holder = (RecyclerListView.Holder) AudioPlayerAlert.this.listView.findViewHolderForAdapterPosition(0);
                        if (holder != null && holder.itemView.getTop() > AndroidUtilities.dp(7.0f)) {
                            AudioPlayerAlert.this.listView.smoothScrollBy(0, holder.itemView.getTop() - AndroidUtilities.dp(7.0f));
                        }
                    }
                } else if (newState == 1) {
                    AndroidUtilities.hideKeyboard(AudioPlayerAlert.this.getCurrentFocus());
                }
            }

            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                AudioPlayerAlert.this.updateLayout();
                AudioPlayerAlert.this.updateEmptyViewPosition();
                if (!AudioPlayerAlert.this.searchWas) {
                    int firstVisibleItem = AudioPlayerAlert.this.layoutManager.findFirstVisibleItemPosition();
                    int visibleItemCount = firstVisibleItem == -1 ? 0 : Math.abs(AudioPlayerAlert.this.layoutManager.findLastVisibleItemPosition() - firstVisibleItem) + 1;
                    int totalItemCount = recyclerView.getAdapter().getItemCount();
                    MediaController.getInstance().getPlayingMessageObject();
                    if (SharedConfig.playOrderReversed) {
                        if (firstVisibleItem < 10) {
                            MediaController.getInstance().loadMoreMusic();
                        }
                    } else if (firstVisibleItem + visibleItemCount > totalItemCount - 10) {
                        MediaController.getInstance().loadMoreMusic();
                    }
                }
            }
        });
        this.playlist = MediaController.getInstance().getPlaylist();
        this.listAdapter.notifyDataSetChanged();
        this.containerView.addView(this.playerLayout, LayoutHelper.createFrame(-1, 179, 83));
        this.containerView.addView(this.playerShadow, new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight(), 83));
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.playerShadow.getLayoutParams();
        layoutParams.bottomMargin = AndroidUtilities.dp(179.0f);
        this.containerView.addView(this.actionBarShadow, LayoutHelper.createFrame(-1, 3.0f));
        this.containerView.addView(this.actionBar);
        FrameLayout frameLayout = new FrameLayout(context) { // from class: org.telegram.ui.Components.AudioPlayerAlert.17
            @Override // android.view.View
            public boolean onTouchEvent(MotionEvent event) {
                if (AudioPlayerAlert.this.blurredView.getTag() != null) {
                    AudioPlayerAlert.this.showAlbumCover(false, true);
                }
                return true;
            }
        };
        this.blurredView = frameLayout;
        frameLayout.setAlpha(0.0f);
        this.blurredView.setVisibility(4);
        getContainer().addView(this.blurredView);
        BackupImageView backupImageView = new BackupImageView(context);
        this.bigAlbumConver = backupImageView;
        backupImageView.setAspectFit(true);
        this.bigAlbumConver.setRoundRadius(AndroidUtilities.dp(8.0f));
        this.bigAlbumConver.setScaleX(0.9f);
        this.bigAlbumConver.setScaleY(0.9f);
        this.blurredView.addView(this.bigAlbumConver, LayoutHelper.createFrame(-1, -1.0f, 51, 30.0f, 30.0f, 30.0f, 30.0f));
        updateTitle(false);
        updateRepeatButton();
        updateEmptyView();
    }

    /* renamed from: org.telegram.ui.Components.AudioPlayerAlert$9 */
    /* loaded from: classes5.dex */
    public class AnonymousClass9 extends ClippingTextViewSwitcher {
        final /* synthetic */ Context val$context;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass9(Context context, Context context2) {
            super(context);
            AudioPlayerAlert.this = this$0;
            this.val$context = context2;
        }

        @Override // org.telegram.ui.Components.AudioPlayerAlert.ClippingTextViewSwitcher
        protected TextView createTextView() {
            final TextView textView = new TextView(this.val$context);
            textView.setTextColor(AudioPlayerAlert.this.getThemedColor(Theme.key_player_time));
            textView.setTextSize(1, 13.0f);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setSingleLine(true);
            textView.setPadding(AndroidUtilities.dp(6.0f), 0, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(1.0f));
            textView.setBackground(Theme.createRadSelectorDrawable(AudioPlayerAlert.this.getThemedColor(Theme.key_listSelector), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f)));
            textView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.AudioPlayerAlert$9$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    AudioPlayerAlert.AnonymousClass9.this.m2199xa7dff3ce(textView, view);
                }
            });
            return textView;
        }

        /* renamed from: lambda$createTextView$0$org-telegram-ui-Components-AudioPlayerAlert$9 */
        public /* synthetic */ void m2199xa7dff3ce(TextView textView, View view) {
            int dialogsCount = MessagesController.getInstance(AudioPlayerAlert.this.currentAccount).getTotalDialogsCount();
            if (dialogsCount <= 10 || TextUtils.isEmpty(textView.getText().toString())) {
                return;
            }
            String query = textView.getText().toString();
            if (AudioPlayerAlert.this.parentActivity.getActionBarLayout().getLastFragment() instanceof DialogsActivity) {
                DialogsActivity dialogsActivity = (DialogsActivity) AudioPlayerAlert.this.parentActivity.getActionBarLayout().getLastFragment();
                if (!dialogsActivity.onlyDialogsAdapter()) {
                    dialogsActivity.setShowSearch(query, 3);
                    AudioPlayerAlert.this.dismiss();
                    return;
                }
            }
            DialogsActivity fragment = new DialogsActivity(null);
            fragment.setSearchString(query);
            fragment.setInitialSearchType(3);
            AudioPlayerAlert.this.parentActivity.presentFragment(fragment, false, false);
            AudioPlayerAlert.this.dismiss();
        }
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-AudioPlayerAlert */
    public /* synthetic */ void m2191lambda$new$0$orgtelegramuiComponentsAudioPlayerAlert(int id) {
        MediaController.getInstance().getPlaybackSpeed(true);
        if (id == 1) {
            MediaController.getInstance().setPlaybackSpeed(true, 0.5f);
        } else if (id == 2) {
            MediaController.getInstance().setPlaybackSpeed(true, 1.0f);
        } else if (id == 3) {
            MediaController.getInstance().setPlaybackSpeed(true, 1.5f);
        } else {
            MediaController.getInstance().setPlaybackSpeed(true, 1.8f);
        }
        updatePlaybackButton();
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-AudioPlayerAlert */
    public /* synthetic */ void m2192lambda$new$1$orgtelegramuiComponentsAudioPlayerAlert(View v) {
        float currentPlaybackSpeed = MediaController.getInstance().getPlaybackSpeed(true);
        if (Math.abs(currentPlaybackSpeed - 1.0f) > 0.001f) {
            MediaController.getInstance().setPlaybackSpeed(true, 1.0f);
        } else {
            MediaController.getInstance().setPlaybackSpeed(true, MediaController.getInstance().getFastPlaybackSpeed(true));
        }
        updatePlaybackButton();
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-AudioPlayerAlert */
    public /* synthetic */ boolean m2193lambda$new$2$orgtelegramuiComponentsAudioPlayerAlert(View view) {
        this.playbackSpeedButton.toggleSubMenu();
        return true;
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-AudioPlayerAlert */
    public /* synthetic */ void m2194lambda$new$3$orgtelegramuiComponentsAudioPlayerAlert(View v) {
        updateSubMenu();
        this.repeatButton.toggleSubMenu();
    }

    /* renamed from: lambda$new$4$org-telegram-ui-Components-AudioPlayerAlert */
    public /* synthetic */ void m2195lambda$new$4$orgtelegramuiComponentsAudioPlayerAlert(int id) {
        if (id == 1 || id == 2) {
            boolean oldReversed = SharedConfig.playOrderReversed;
            if ((SharedConfig.playOrderReversed && id == 1) || (SharedConfig.shuffleMusic && id == 2)) {
                MediaController.getInstance().setPlaybackOrderType(0);
            } else {
                MediaController.getInstance().setPlaybackOrderType(id);
            }
            this.listAdapter.notifyDataSetChanged();
            if (oldReversed != SharedConfig.playOrderReversed) {
                this.listView.stopScroll();
                scrollToCurrentSong(false);
            }
        } else if (id == 4) {
            if (SharedConfig.repeatMode == 1) {
                SharedConfig.setRepeatMode(0);
            } else {
                SharedConfig.setRepeatMode(1);
            }
        } else if (SharedConfig.repeatMode == 2) {
            SharedConfig.setRepeatMode(0);
        } else {
            SharedConfig.setRepeatMode(2);
        }
        updateRepeatButton();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.telegram.ui.Components.AudioPlayerAlert$13 */
    /* loaded from: classes5.dex */
    public class AnonymousClass13 extends RLottieImageView {
        long lastTime;
        long lastUpdateTime;
        long startTime;
        float startX;
        float startY;
        final /* synthetic */ float val$touchSlop;
        int pressedCount = 0;
        private final Runnable pressedRunnable = new Runnable() { // from class: org.telegram.ui.Components.AudioPlayerAlert.13.1
            @Override // java.lang.Runnable
            public void run() {
                AnonymousClass13.this.pressedCount++;
                if (AnonymousClass13.this.pressedCount == 1) {
                    AudioPlayerAlert.this.rewindingState = -1;
                    AudioPlayerAlert.this.rewindingProgress = MediaController.getInstance().getPlayingMessageObject().audioProgress;
                    AnonymousClass13.this.lastTime = System.currentTimeMillis();
                    AndroidUtilities.runOnUIThread(this, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
                    AndroidUtilities.runOnUIThread(AnonymousClass13.this.backSeek);
                } else if (AnonymousClass13.this.pressedCount == 2) {
                    AndroidUtilities.runOnUIThread(this, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
                }
            }
        };
        private final Runnable backSeek = new Runnable() { // from class: org.telegram.ui.Components.AudioPlayerAlert.13.2
            @Override // java.lang.Runnable
            public void run() {
                long dt;
                long duration = MediaController.getInstance().getDuration();
                if (duration == 0 || duration == C.TIME_UNSET) {
                    AnonymousClass13.this.lastTime = System.currentTimeMillis();
                    return;
                }
                float currentProgress = AudioPlayerAlert.this.rewindingProgress;
                long t = System.currentTimeMillis();
                long dt2 = t - AnonymousClass13.this.lastTime;
                AnonymousClass13.this.lastTime = t;
                long updateDt = t - AnonymousClass13.this.lastUpdateTime;
                if (AnonymousClass13.this.pressedCount == 1) {
                    dt = dt2 * 3;
                } else if (AnonymousClass13.this.pressedCount == 2) {
                    dt = dt2 * 6;
                } else {
                    dt = dt2 * 12;
                }
                long currentTime = (((float) duration) * currentProgress) - ((float) dt);
                float currentProgress2 = ((float) currentTime) / ((float) duration);
                if (currentProgress2 < 0.0f) {
                    currentProgress2 = 0.0f;
                }
                AudioPlayerAlert.this.rewindingProgress = currentProgress2;
                MessageObject messageObject = MediaController.getInstance().getPlayingMessageObject();
                if (messageObject != null && messageObject.isMusic()) {
                    AudioPlayerAlert.this.updateProgress(messageObject);
                }
                if (AudioPlayerAlert.this.rewindingState == -1 && AnonymousClass13.this.pressedCount > 0) {
                    if (updateDt > 200 || AudioPlayerAlert.this.rewindingProgress == 0.0f) {
                        AnonymousClass13.this.lastUpdateTime = t;
                        if (AudioPlayerAlert.this.rewindingProgress == 0.0f) {
                            MediaController.getInstance().seekToProgress(MediaController.getInstance().getPlayingMessageObject(), 0.0f);
                            MediaController.getInstance().pauseByRewind();
                        } else {
                            MediaController.getInstance().seekToProgress(MediaController.getInstance().getPlayingMessageObject(), currentProgress2);
                        }
                    }
                    if (AnonymousClass13.this.pressedCount > 0 && AudioPlayerAlert.this.rewindingProgress > 0.0f) {
                        AndroidUtilities.runOnUIThread(AnonymousClass13.this.backSeek, 16L);
                    }
                }
            }
        };

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass13(Context context, float f) {
            super(context);
            AudioPlayerAlert.this = this$0;
            this.val$touchSlop = f;
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent event) {
            if (AudioPlayerAlert.this.seekBarView.isDragging() || AudioPlayerAlert.this.rewindingState == 1) {
                return false;
            }
            float x = event.getRawX();
            float y = event.getRawY();
            switch (event.getAction()) {
                case 0:
                    this.startX = x;
                    this.startY = y;
                    this.startTime = System.currentTimeMillis();
                    AudioPlayerAlert.this.rewindingState = 0;
                    AndroidUtilities.runOnUIThread(this.pressedRunnable, 300L);
                    if (Build.VERSION.SDK_INT >= 21 && getBackground() != null) {
                        getBackground().setHotspot(this.startX, this.startY);
                    }
                    setPressed(true);
                    break;
                case 1:
                case 3:
                    AndroidUtilities.cancelRunOnUIThread(this.pressedRunnable);
                    AndroidUtilities.cancelRunOnUIThread(this.backSeek);
                    if (AudioPlayerAlert.this.rewindingState == 0 && event.getAction() == 1 && System.currentTimeMillis() - this.startTime < 300) {
                        MediaController.getInstance().playPreviousMessage();
                        AudioPlayerAlert.this.prevButton.setProgress(0.0f);
                        AudioPlayerAlert.this.prevButton.playAnimation();
                    }
                    if (this.pressedCount > 0) {
                        this.lastUpdateTime = 0L;
                        this.backSeek.run();
                        MediaController.getInstance().resumeByRewind();
                    }
                    AudioPlayerAlert.this.rewindingProgress = -1.0f;
                    setPressed(false);
                    AudioPlayerAlert.this.rewindingState = 0;
                    this.pressedCount = 0;
                    break;
                case 2:
                    float dx = x - this.startX;
                    float dy = y - this.startY;
                    float f = (dx * dx) + (dy * dy);
                    float f2 = this.val$touchSlop;
                    if (f > f2 * f2 && AudioPlayerAlert.this.rewindingState == 0) {
                        AndroidUtilities.cancelRunOnUIThread(this.pressedRunnable);
                        setPressed(false);
                        break;
                    }
                    break;
            }
            return true;
        }

        @Override // android.view.View
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.addAction(16);
        }
    }

    public static /* synthetic */ void lambda$new$5(View v) {
        if (MediaController.getInstance().isDownloadingCurrentMessage()) {
            return;
        }
        if (MediaController.getInstance().isMessagePaused()) {
            MediaController.getInstance().playMessage(MediaController.getInstance().getPlayingMessageObject());
        } else {
            MediaController.getInstance().m383lambda$startAudioAgain$7$orgtelegrammessengerMediaController(MediaController.getInstance().getPlayingMessageObject());
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.telegram.ui.Components.AudioPlayerAlert$14 */
    /* loaded from: classes5.dex */
    public class AnonymousClass14 extends RLottieImageView {
        boolean pressed;
        private final Runnable pressedRunnable = new Runnable() { // from class: org.telegram.ui.Components.AudioPlayerAlert.14.1
            @Override // java.lang.Runnable
            public void run() {
                if (MediaController.getInstance().getPlayingMessageObject() == null) {
                    return;
                }
                AudioPlayerAlert.this.rewindingForwardPressedCount++;
                if (AudioPlayerAlert.this.rewindingForwardPressedCount == 1) {
                    AnonymousClass14.this.pressed = true;
                    AudioPlayerAlert.this.rewindingState = 1;
                    if (MediaController.getInstance().isMessagePaused()) {
                        AudioPlayerAlert.this.startForwardRewindingSeek();
                    } else if (AudioPlayerAlert.this.rewindingState == 1) {
                        AndroidUtilities.cancelRunOnUIThread(AudioPlayerAlert.this.forwardSeek);
                        AudioPlayerAlert.this.lastUpdateRewindingPlayerTime = 0L;
                    }
                    MediaController.getInstance().setPlaybackSpeed(true, 4.0f);
                    AndroidUtilities.runOnUIThread(this, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
                } else if (AudioPlayerAlert.this.rewindingForwardPressedCount == 2) {
                    MediaController.getInstance().setPlaybackSpeed(true, 7.0f);
                    AndroidUtilities.runOnUIThread(this, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
                } else {
                    MediaController.getInstance().setPlaybackSpeed(true, 13.0f);
                }
            }
        };
        float startX;
        float startY;
        final /* synthetic */ float val$touchSlop;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass14(Context context, float f) {
            super(context);
            AudioPlayerAlert.this = this$0;
            this.val$touchSlop = f;
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent event) {
            if (AudioPlayerAlert.this.seekBarView.isDragging() || AudioPlayerAlert.this.rewindingState == -1) {
                return false;
            }
            float x = event.getRawX();
            float y = event.getRawY();
            switch (event.getAction()) {
                case 0:
                    this.pressed = false;
                    this.startX = x;
                    this.startY = y;
                    AndroidUtilities.runOnUIThread(this.pressedRunnable, 300L);
                    if (Build.VERSION.SDK_INT >= 21 && getBackground() != null) {
                        getBackground().setHotspot(this.startX, this.startY);
                    }
                    setPressed(true);
                    break;
                case 1:
                case 3:
                    if (!this.pressed && event.getAction() == 1 && isPressed()) {
                        MediaController.getInstance().playNextMessage();
                        AudioPlayerAlert.this.nextButton.setProgress(0.0f);
                        AudioPlayerAlert.this.nextButton.playAnimation();
                    }
                    AndroidUtilities.cancelRunOnUIThread(this.pressedRunnable);
                    if (AudioPlayerAlert.this.rewindingForwardPressedCount > 0) {
                        MediaController.getInstance().setPlaybackSpeed(true, 1.0f);
                        if (MediaController.getInstance().isMessagePaused()) {
                            AudioPlayerAlert.this.lastUpdateRewindingPlayerTime = 0L;
                            AudioPlayerAlert.this.forwardSeek.run();
                        }
                    }
                    AudioPlayerAlert.this.rewindingState = 0;
                    setPressed(false);
                    AudioPlayerAlert.this.rewindingForwardPressedCount = 0;
                    AudioPlayerAlert.this.rewindingProgress = -1.0f;
                    break;
                case 2:
                    float dx = x - this.startX;
                    float dy = y - this.startY;
                    float f = (dx * dx) + (dy * dy);
                    float f2 = this.val$touchSlop;
                    if (f > f2 * f2 && !this.pressed) {
                        AndroidUtilities.cancelRunOnUIThread(this.pressedRunnable);
                        setPressed(false);
                        break;
                    }
                    break;
            }
            return true;
        }

        @Override // android.view.View
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.addAction(16);
        }
    }

    /* renamed from: lambda$new$6$org-telegram-ui-Components-AudioPlayerAlert */
    public /* synthetic */ void m2196lambda$new$6$orgtelegramuiComponentsAudioPlayerAlert(View v) {
        this.optionsButton.toggleSubMenu();
    }

    public static /* synthetic */ boolean lambda$new$7(View v, MotionEvent event) {
        return true;
    }

    public static /* synthetic */ void lambda$new$8(View view, int position) {
        if (view instanceof AudioPlayerCell) {
            ((AudioPlayerCell) view).didPressedButton();
        }
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public int getContainerViewHeight() {
        if (this.playerLayout == null) {
            return 0;
        }
        if (this.playlist.size() <= 1) {
            return this.playerLayout.getMeasuredHeight() + this.backgroundPaddingTop;
        }
        int offset = AndroidUtilities.dp(13.0f);
        int top = (this.scrollOffsetY - this.backgroundPaddingTop) - offset;
        if (this.currentSheetAnimationType == 1) {
            top = (int) (top + this.listView.getTranslationY());
        }
        if (this.backgroundPaddingTop + top < ActionBar.getCurrentActionBarHeight()) {
            float toMove = AndroidUtilities.dp(4.0f) + offset;
            float moveProgress = Math.min(1.0f, ((ActionBar.getCurrentActionBarHeight() - top) - this.backgroundPaddingTop) / toMove);
            float availableToMove = ActionBar.getCurrentActionBarHeight() - toMove;
            int diff = (int) (availableToMove * moveProgress);
            top -= diff;
        }
        if (Build.VERSION.SDK_INT >= 21) {
            top += AndroidUtilities.statusBarHeight;
        }
        return this.container.getMeasuredHeight() - top;
    }

    public void startForwardRewindingSeek() {
        if (this.rewindingState == 1) {
            this.lastRewindingTime = System.currentTimeMillis();
            this.rewindingProgress = MediaController.getInstance().getPlayingMessageObject().audioProgress;
            AndroidUtilities.cancelRunOnUIThread(this.forwardSeek);
            AndroidUtilities.runOnUIThread(this.forwardSeek);
        }
    }

    public void updateEmptyViewPosition() {
        if (this.emptyView.getVisibility() != 0) {
            return;
        }
        int h = this.playerLayout.getVisibility() == 0 ? AndroidUtilities.dp(150.0f) : -AndroidUtilities.dp(30.0f);
        LinearLayout linearLayout = this.emptyView;
        linearLayout.setTranslationY(((linearLayout.getMeasuredHeight() - this.containerView.getMeasuredHeight()) - h) / 2);
    }

    public void updateEmptyView() {
        this.emptyView.setVisibility((!this.searching || this.listAdapter.getItemCount() != 0) ? 8 : 0);
        updateEmptyViewPosition();
    }

    public boolean scrollToCurrentSong(boolean search) {
        int idx;
        MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
        if (playingMessageObject != null) {
            boolean found = false;
            if (search) {
                int count = this.listView.getChildCount();
                int a = 0;
                while (true) {
                    if (a >= count) {
                        break;
                    }
                    View child = this.listView.getChildAt(a);
                    if (!(child instanceof AudioPlayerCell) || ((AudioPlayerCell) child).getMessageObject() != playingMessageObject) {
                        a++;
                    } else if (child.getBottom() <= this.listView.getMeasuredHeight()) {
                        found = true;
                    }
                }
            }
            if (!found && (idx = this.playlist.indexOf(playingMessageObject)) >= 0) {
                if (SharedConfig.playOrderReversed) {
                    this.layoutManager.scrollToPosition(idx);
                    return true;
                }
                this.layoutManager.scrollToPosition(this.playlist.size() - idx);
                return true;
            }
            return false;
        }
        return false;
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public boolean onCustomMeasure(View view, int width, int height) {
        if (width < height) {
        }
        FrameLayout frameLayout = this.blurredView;
        if (view == frameLayout) {
            frameLayout.measure(View.MeasureSpec.makeMeasureSpec(width, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(height, C.BUFFER_FLAG_ENCRYPTED));
            return true;
        }
        return false;
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    protected boolean onCustomLayout(View view, int left, int top, int right, int bottom) {
        int width = right - left;
        int height = bottom - top;
        if (width < height) {
        }
        FrameLayout frameLayout = this.blurredView;
        if (view == frameLayout) {
            frameLayout.layout(left, 0, left + width, height);
            return true;
        }
        return false;
    }

    private void setMenuItemChecked(ActionBarMenuSubItem item, boolean checked) {
        if (checked) {
            item.setTextColor(getThemedColor(Theme.key_player_buttonActive));
            item.setIconColor(getThemedColor(Theme.key_player_buttonActive));
            return;
        }
        item.setTextColor(getThemedColor(Theme.key_actionBarDefaultSubmenuItem));
        item.setIconColor(getThemedColor(Theme.key_actionBarDefaultSubmenuItem));
    }

    private void updateSubMenu() {
        setMenuItemChecked(this.shuffleListItem, SharedConfig.shuffleMusic);
        setMenuItemChecked(this.reverseOrderItem, SharedConfig.playOrderReversed);
        boolean z = false;
        setMenuItemChecked(this.repeatListItem, SharedConfig.repeatMode == 1);
        ActionBarMenuSubItem actionBarMenuSubItem = this.repeatSongItem;
        if (SharedConfig.repeatMode == 2) {
            z = true;
        }
        setMenuItemChecked(actionBarMenuSubItem, z);
    }

    private void updatePlaybackButton() {
        String key;
        float currentPlaybackSpeed = MediaController.getInstance().getPlaybackSpeed(true);
        if (Math.abs(currentPlaybackSpeed - 1.0f) > 0.001f) {
            key = Theme.key_inappPlayerPlayPause;
        } else {
            key = Theme.key_inappPlayerClose;
        }
        this.playbackSpeedButton.setTag(key);
        float speed = MediaController.getInstance().getFastPlaybackSpeed(true);
        if (Math.abs(speed - 1.8f) < 0.001f) {
            this.playbackSpeedButton.setIcon(R.drawable.voice_mini_2_0);
        } else if (Math.abs(speed - 1.5f) < 0.001f) {
            this.playbackSpeedButton.setIcon(R.drawable.voice_mini_1_5);
        } else {
            this.playbackSpeedButton.setIcon(R.drawable.voice_mini_0_5);
        }
        this.playbackSpeedButton.setIconColor(getThemedColor(key));
        if (Build.VERSION.SDK_INT >= 21) {
            this.playbackSpeedButton.setBackgroundDrawable(Theme.createSelectorDrawable(getThemedColor(key) & 436207615, 1, AndroidUtilities.dp(14.0f)));
        }
        for (int a = 0; a < this.speedItems.length; a++) {
            if ((a == 0 && Math.abs(currentPlaybackSpeed - 0.5f) < 0.001f) || ((a == 1 && Math.abs(currentPlaybackSpeed - 1.0f) < 0.001f) || ((a == 2 && Math.abs(currentPlaybackSpeed - 1.5f) < 0.001f) || (a == 3 && Math.abs(currentPlaybackSpeed - 1.8f) < 0.001f)))) {
                this.speedItems[a].setColors(getThemedColor(Theme.key_inappPlayerPlayPause), getThemedColor(Theme.key_inappPlayerPlayPause));
            } else {
                this.speedItems[a].setColors(getThemedColor(Theme.key_actionBarDefaultSubmenuItem), getThemedColor(Theme.key_actionBarDefaultSubmenuItemIcon));
            }
        }
    }

    public void onSubItemClick(int id) {
        MessageObject messageObject = MediaController.getInstance().getPlayingMessageObject();
        if (messageObject == null || this.parentActivity == null) {
            return;
        }
        if (id == 1) {
            if (UserConfig.selectedAccount != this.currentAccount) {
                this.parentActivity.switchToAccount(this.currentAccount, true);
            }
            Bundle args = new Bundle();
            args.putBoolean("onlySelect", true);
            args.putInt("dialogsType", 3);
            DialogsActivity fragment = new DialogsActivity(args);
            final ArrayList<MessageObject> fmessages = new ArrayList<>();
            fmessages.add(messageObject);
            fragment.setDelegate(new DialogsActivity.DialogsActivityDelegate() { // from class: org.telegram.ui.Components.AudioPlayerAlert$$ExternalSyntheticLambda3
                @Override // org.telegram.ui.DialogsActivity.DialogsActivityDelegate
                public final void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
                    AudioPlayerAlert.this.m2198x38f13a48(fmessages, dialogsActivity, arrayList, charSequence, z);
                }
            });
            this.parentActivity.m3653lambda$runLinkRequest$59$orgtelegramuiLaunchActivity(fragment);
            dismiss();
        } else if (id == 2) {
            File f = null;
            try {
                if (!TextUtils.isEmpty(messageObject.messageOwner.attachPath)) {
                    f = new File(messageObject.messageOwner.attachPath);
                    if (!f.exists()) {
                        f = null;
                    }
                }
                if (f == null) {
                    f = FileLoader.getInstance(this.currentAccount).getPathToMessage(messageObject.messageOwner);
                }
                if (f.exists()) {
                    Intent intent = new Intent("android.intent.action.SEND");
                    intent.setType(messageObject.getMimeType());
                    if (Build.VERSION.SDK_INT >= 24) {
                        try {
                            intent.putExtra("android.intent.extra.STREAM", FileProvider.getUriForFile(ApplicationLoader.applicationContext, "org.telegram.messenger.beta.provider", f));
                            intent.setFlags(1);
                        } catch (Exception e) {
                            intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(f));
                        }
                    } else {
                        intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(f));
                    }
                    this.parentActivity.startActivityForResult(Intent.createChooser(intent, LocaleController.getString("ShareFile", R.string.ShareFile)), 500);
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(this.parentActivity);
                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                builder.setMessage(LocaleController.getString("PleaseDownload", R.string.PleaseDownload));
                builder.show();
            } catch (Exception e2) {
                FileLog.e(e2);
            }
        } else if (id == 4) {
            if (UserConfig.selectedAccount != this.currentAccount) {
                this.parentActivity.switchToAccount(this.currentAccount, true);
            }
            Bundle args2 = new Bundle();
            long did = messageObject.getDialogId();
            if (DialogObject.isEncryptedDialog(did)) {
                args2.putInt("enc_id", DialogObject.getEncryptedChatId(did));
            } else if (DialogObject.isUserDialog(did)) {
                args2.putLong("user_id", did);
            } else {
                TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-did));
                if (chat != null && chat.migrated_to != null) {
                    args2.putLong("migrated_to", did);
                    did = -chat.migrated_to.channel_id;
                }
                args2.putLong(ChatReactionsEditActivity.KEY_CHAT_ID, -did);
            }
            args2.putInt(Constants.MessagePayloadKeys.MSGID_SERVER, messageObject.getId());
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
            this.parentActivity.presentFragment(new ChatActivity(args2), false, false);
            dismiss();
        } else if (id == 5) {
            if (Build.VERSION.SDK_INT >= 23 && ((Build.VERSION.SDK_INT <= 28 || BuildVars.NO_SCOPED_STORAGE) && this.parentActivity.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != 0)) {
                this.parentActivity.requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 4);
                return;
            }
            String fileName = FileLoader.getDocumentFileName(messageObject.getDocument());
            if (TextUtils.isEmpty(fileName)) {
                fileName = messageObject.getFileName();
            }
            String path = messageObject.messageOwner.attachPath;
            if (path != null && path.length() > 0) {
                File temp = new File(path);
                if (!temp.exists()) {
                    path = null;
                }
            }
            MediaController.saveFile((path == null || path.length() == 0) ? FileLoader.getInstance(this.currentAccount).getPathToMessage(messageObject.messageOwner).toString() : path, this.parentActivity, 3, fileName, messageObject.getDocument() != null ? messageObject.getDocument().mime_type : "", new Runnable() { // from class: org.telegram.ui.Components.AudioPlayerAlert$$ExternalSyntheticLambda9
                @Override // java.lang.Runnable
                public final void run() {
                    AudioPlayerAlert.this.m2197x607d8ff4();
                }
            });
        }
    }

    /* renamed from: lambda$onSubItemClick$9$org-telegram-ui-Components-AudioPlayerAlert */
    public /* synthetic */ void m2198x38f13a48(ArrayList fmessages, DialogsActivity fragment1, ArrayList dids, CharSequence message, boolean param) {
        if (dids.size() <= 1 && ((Long) dids.get(0)).longValue() != UserConfig.getInstance(this.currentAccount).getClientUserId()) {
            if (message == null) {
                long did = ((Long) dids.get(0)).longValue();
                Bundle args1 = new Bundle();
                args1.putBoolean("scrollToTopOnResume", true);
                if (DialogObject.isEncryptedDialog(did)) {
                    args1.putInt("enc_id", DialogObject.getEncryptedChatId(did));
                } else if (DialogObject.isUserDialog(did)) {
                    args1.putLong("user_id", did);
                } else {
                    args1.putLong(ChatReactionsEditActivity.KEY_CHAT_ID, -did);
                }
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                ChatActivity chatActivity = new ChatActivity(args1);
                if (this.parentActivity.presentFragment(chatActivity, true, false)) {
                    chatActivity.showFieldPanelForForward(true, fmessages);
                    return;
                } else {
                    fragment1.finishFragment();
                    return;
                }
            }
        }
        for (int a = 0; a < dids.size(); a++) {
            long did2 = ((Long) dids.get(a)).longValue();
            if (message != null) {
                SendMessagesHelper.getInstance(this.currentAccount).sendMessage(message.toString(), did2, null, null, null, true, null, null, null, true, 0, null);
            }
            SendMessagesHelper.getInstance(this.currentAccount).sendMessage((ArrayList<MessageObject>) fmessages, did2, false, false, true, 0);
        }
        fragment1.finishFragment();
    }

    /* renamed from: lambda$onSubItemClick$10$org-telegram-ui-Components-AudioPlayerAlert */
    public /* synthetic */ void m2197x607d8ff4() {
        BulletinFactory.of((FrameLayout) this.containerView, this.resourcesProvider).createDownloadBulletin(BulletinFactory.FileType.AUDIO).show();
    }

    public void showAlbumCover(boolean show, boolean animated) {
        if (show) {
            if (this.blurredView.getVisibility() == 0 || this.blurredAnimationInProgress) {
                return;
            }
            this.blurredView.setTag(1);
            this.bigAlbumConver.setImageBitmap(this.coverContainer.getImageReceiver().getBitmap());
            this.blurredAnimationInProgress = true;
            BaseFragment fragment = this.parentActivity.getActionBarLayout().fragmentsStack.get(this.parentActivity.getActionBarLayout().fragmentsStack.size() - 1);
            View fragmentView = fragment.getFragmentView();
            int w = (int) (fragmentView.getMeasuredWidth() / 6.0f);
            int h = (int) (fragmentView.getMeasuredHeight() / 6.0f);
            Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.scale(0.16666667f, 0.16666667f);
            fragmentView.draw(canvas);
            canvas.translate(this.containerView.getLeft() - getLeftInset(), 0.0f);
            this.containerView.draw(canvas);
            Utilities.stackBlurBitmap(bitmap, Math.max(7, Math.max(w, h) / 180));
            this.blurredView.setBackground(new BitmapDrawable(bitmap));
            this.blurredView.setVisibility(0);
            this.blurredView.animate().alpha(1.0f).setDuration(180L).setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.AudioPlayerAlert.18
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    AudioPlayerAlert.this.blurredAnimationInProgress = false;
                }
            }).start();
            this.bigAlbumConver.animate().scaleX(1.0f).scaleY(1.0f).setDuration(180L).start();
        } else if (this.blurredView.getVisibility() != 0) {
        } else {
            this.blurredView.setTag(null);
            if (!animated) {
                this.blurredView.setAlpha(0.0f);
                this.blurredView.setVisibility(4);
                this.bigAlbumConver.setImageBitmap(null);
                this.bigAlbumConver.setScaleX(0.9f);
                this.bigAlbumConver.setScaleY(0.9f);
                return;
            }
            this.blurredAnimationInProgress = true;
            this.blurredView.animate().alpha(0.0f).setDuration(180L).setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.AudioPlayerAlert.19
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    AudioPlayerAlert.this.blurredView.setVisibility(4);
                    AudioPlayerAlert.this.bigAlbumConver.setImageBitmap(null);
                    AudioPlayerAlert.this.blurredAnimationInProgress = false;
                }
            }).start();
            this.bigAlbumConver.animate().scaleX(0.9f).scaleY(0.9f).setDuration(180L).start();
        }
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        AudioPlayerCell cell;
        MessageObject messageObject;
        AudioPlayerCell cell2;
        MessageObject messageObject1;
        MessageObject messageObject2;
        float bufferedProgress;
        int offset = 0;
        if (id == NotificationCenter.messagePlayingDidStart || id == NotificationCenter.messagePlayingPlayStateChanged || id == NotificationCenter.messagePlayingDidReset) {
            updateTitle(id == NotificationCenter.messagePlayingDidReset && ((Boolean) args[1]).booleanValue());
            if (id == NotificationCenter.messagePlayingDidReset || id == NotificationCenter.messagePlayingPlayStateChanged) {
                int count = this.listView.getChildCount();
                for (int a = 0; a < count; a++) {
                    View view = this.listView.getChildAt(a);
                    if ((view instanceof AudioPlayerCell) && (messageObject = (cell = (AudioPlayerCell) view).getMessageObject()) != null && (messageObject.isVoice() || messageObject.isMusic())) {
                        cell.updateButtonState(false, true);
                    }
                }
                if (id == NotificationCenter.messagePlayingPlayStateChanged && MediaController.getInstance().getPlayingMessageObject() != null) {
                    if (MediaController.getInstance().isMessagePaused()) {
                        startForwardRewindingSeek();
                    } else if (this.rewindingState == 1 && this.rewindingProgress != -1.0f) {
                        AndroidUtilities.cancelRunOnUIThread(this.forwardSeek);
                        this.lastUpdateRewindingPlayerTime = 0L;
                        this.forwardSeek.run();
                        this.rewindingProgress = -1.0f;
                    }
                }
            } else if (((MessageObject) args[0]).eventId == 0) {
                int count2 = this.listView.getChildCount();
                for (int a2 = 0; a2 < count2; a2++) {
                    View view2 = this.listView.getChildAt(a2);
                    if ((view2 instanceof AudioPlayerCell) && (messageObject1 = (cell2 = (AudioPlayerCell) view2).getMessageObject()) != null && (messageObject1.isVoice() || messageObject1.isMusic())) {
                        cell2.updateButtonState(false, true);
                    }
                }
            }
        } else if (id == NotificationCenter.messagePlayingProgressDidChanged) {
            MessageObject messageObject3 = MediaController.getInstance().getPlayingMessageObject();
            if (messageObject3 != null && messageObject3.isMusic()) {
                updateProgress(messageObject3);
            }
        } else if (id == NotificationCenter.musicDidLoad) {
            this.playlist = MediaController.getInstance().getPlaylist();
            this.listAdapter.notifyDataSetChanged();
        } else if (id == NotificationCenter.moreMusicDidLoad) {
            this.playlist = MediaController.getInstance().getPlaylist();
            this.listAdapter.notifyDataSetChanged();
            if (SharedConfig.playOrderReversed) {
                this.listView.stopScroll();
                int addedCount = ((Integer) args[0]).intValue();
                this.layoutManager.findFirstVisibleItemPosition();
                int position = this.layoutManager.findLastVisibleItemPosition();
                if (position != -1) {
                    View firstVisView = this.layoutManager.findViewByPosition(position);
                    if (firstVisView != null) {
                        offset = firstVisView.getTop();
                    }
                    this.layoutManager.scrollToPositionWithOffset(position + addedCount, offset);
                }
            }
        } else if (id == NotificationCenter.fileLoaded) {
            String name = (String) args[0];
            if (name.equals(this.currentFile)) {
                updateTitle(false);
                this.currentAudioFinishedLoading = true;
            }
        } else if (id == NotificationCenter.fileLoadProgressChanged) {
            String name2 = (String) args[0];
            if (!name2.equals(this.currentFile) || (messageObject2 = MediaController.getInstance().getPlayingMessageObject()) == null) {
                return;
            }
            Long l = (Long) args[1];
            Long l2 = (Long) args[2];
            if (this.currentAudioFinishedLoading) {
                bufferedProgress = 1.0f;
            } else {
                long newTime = SystemClock.elapsedRealtime();
                if (Math.abs(newTime - this.lastBufferedPositionCheck) >= 500) {
                    float bufferedProgress2 = MediaController.getInstance().isStreamingCurrentAudio() ? FileLoader.getInstance(this.currentAccount).getBufferedProgressFromPosition(messageObject2.audioProgress, this.currentFile) : 1.0f;
                    this.lastBufferedPositionCheck = newTime;
                    bufferedProgress = bufferedProgress2;
                } else {
                    bufferedProgress = -1.0f;
                }
            }
            if (bufferedProgress != -1.0f) {
                this.seekBarView.setBufferedProgress(bufferedProgress);
            }
        }
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    protected boolean canDismissWithSwipe() {
        return false;
    }

    public void updateLayout() {
        if (this.listView.getChildCount() <= 0) {
            RecyclerListView recyclerListView = this.listView;
            int paddingTop = recyclerListView.getPaddingTop();
            this.scrollOffsetY = paddingTop;
            recyclerListView.setTopGlowOffset(paddingTop);
            this.containerView.invalidate();
            return;
        }
        View child = this.listView.getChildAt(0);
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(child);
        int top = child.getTop();
        int newOffset = AndroidUtilities.dp(7.0f);
        if (top >= AndroidUtilities.dp(7.0f) && holder != null && holder.getAdapterPosition() == 0) {
            newOffset = top;
        }
        boolean show = newOffset <= AndroidUtilities.dp(12.0f);
        if ((show && this.actionBar.getTag() == null) || (!show && this.actionBar.getTag() != null)) {
            this.actionBar.setTag(show ? 1 : null);
            AnimatorSet animatorSet = this.actionBarAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.actionBarAnimation = null;
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.actionBarAnimation = animatorSet2;
            animatorSet2.setDuration(180L);
            AnimatorSet animatorSet3 = this.actionBarAnimation;
            Animator[] animatorArr = new Animator[2];
            ActionBar actionBar = this.actionBar;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            float f = 1.0f;
            fArr[0] = show ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(actionBar, property, fArr);
            View view = this.actionBarShadow;
            Property property2 = View.ALPHA;
            float[] fArr2 = new float[1];
            if (!show) {
                f = 0.0f;
            }
            fArr2[0] = f;
            animatorArr[1] = ObjectAnimator.ofFloat(view, property2, fArr2);
            animatorSet3.playTogether(animatorArr);
            this.actionBarAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.AudioPlayerAlert.20
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animation) {
                    AudioPlayerAlert.this.actionBarAnimation = null;
                }
            });
            this.actionBarAnimation.start();
        }
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.listView.getLayoutParams();
        int newOffset2 = newOffset + (layoutParams.topMargin - AndroidUtilities.dp(11.0f));
        if (this.scrollOffsetY != newOffset2) {
            RecyclerListView recyclerListView2 = this.listView;
            this.scrollOffsetY = newOffset2;
            recyclerListView2.setTopGlowOffset(newOffset2 - layoutParams.topMargin);
            this.containerView.invalidate();
        }
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet, android.app.Dialog, android.content.DialogInterface
    public void dismiss() {
        super.dismiss();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidReset);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidStart);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileLoadProgressChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.musicDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.moreMusicDidLoad);
        DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
    }

    @Override // android.app.Dialog
    public void onBackPressed() {
        ActionBar actionBar = this.actionBar;
        if (actionBar != null && actionBar.isSearchFieldVisible()) {
            this.actionBar.closeSearchField();
        } else if (this.blurredView.getTag() != null) {
            showAlbumCover(false, true);
        } else {
            super.onBackPressed();
        }
    }

    @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
    public void onFailedDownload(String fileName, boolean canceled) {
    }

    @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
    public void onSuccessDownload(String fileName) {
    }

    @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
    public void onProgressDownload(String fileName, long downloadedSize, long totalSize) {
        this.progressView.setProgress(Math.min(1.0f, ((float) downloadedSize) / ((float) totalSize)), true);
    }

    @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
    public void onProgressUpload(String fileName, long uploadedSize, long totalSize, boolean isEncrypted) {
    }

    @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
    public int getObserverTag() {
        return this.TAG;
    }

    private void updateRepeatButton() {
        int mode = SharedConfig.repeatMode;
        if (mode == 0 || mode == 1) {
            if (SharedConfig.shuffleMusic) {
                if (mode == 0) {
                    this.repeatButton.setIcon(R.drawable.player_new_shuffle);
                } else {
                    this.repeatButton.setIcon(R.drawable.player_new_repeat_shuffle);
                }
            } else if (SharedConfig.playOrderReversed) {
                if (mode == 0) {
                    this.repeatButton.setIcon(R.drawable.player_new_order);
                } else {
                    this.repeatButton.setIcon(R.drawable.player_new_repeat_reverse);
                }
            } else {
                this.repeatButton.setIcon(R.drawable.player_new_repeatall);
            }
            if (mode == 0 && !SharedConfig.shuffleMusic && !SharedConfig.playOrderReversed) {
                this.repeatButton.setTag(Theme.key_player_button);
                this.repeatButton.setIconColor(getThemedColor(Theme.key_player_button));
                Theme.setSelectorDrawableColor(this.repeatButton.getBackground(), getThemedColor(Theme.key_listSelector), true);
                this.repeatButton.setContentDescription(LocaleController.getString("AccDescrRepeatOff", R.string.AccDescrRepeatOff));
                return;
            }
            this.repeatButton.setTag(Theme.key_player_buttonActive);
            this.repeatButton.setIconColor(getThemedColor(Theme.key_player_buttonActive));
            Theme.setSelectorDrawableColor(this.repeatButton.getBackground(), 436207615 & getThemedColor(Theme.key_player_buttonActive), true);
            if (mode == 0) {
                if (SharedConfig.shuffleMusic) {
                    this.repeatButton.setContentDescription(LocaleController.getString("ShuffleList", R.string.ShuffleList));
                    return;
                } else {
                    this.repeatButton.setContentDescription(LocaleController.getString("ReverseOrder", R.string.ReverseOrder));
                    return;
                }
            }
            this.repeatButton.setContentDescription(LocaleController.getString("AccDescrRepeatList", R.string.AccDescrRepeatList));
        } else if (mode == 2) {
            this.repeatButton.setIcon(R.drawable.player_new_repeatone);
            this.repeatButton.setTag(Theme.key_player_buttonActive);
            this.repeatButton.setIconColor(getThemedColor(Theme.key_player_buttonActive));
            Theme.setSelectorDrawableColor(this.repeatButton.getBackground(), 436207615 & getThemedColor(Theme.key_player_buttonActive), true);
            this.repeatButton.setContentDescription(LocaleController.getString("AccDescrRepeatOne", R.string.AccDescrRepeatOne));
        }
    }

    public void updateProgress(MessageObject messageObject) {
        updateProgress(messageObject, false);
    }

    private void updateProgress(MessageObject messageObject, boolean animated) {
        int newTime;
        float bufferedProgress;
        int i;
        SeekBarView seekBarView = this.seekBarView;
        if (seekBarView != null) {
            if (seekBarView.isDragging()) {
                newTime = (int) (messageObject.getDuration() * this.seekBarView.getProgress());
            } else {
                boolean z = true;
                if (this.rewindingProgress < 0.0f || ((i = this.rewindingState) != -1 && (i != 1 || !MediaController.getInstance().isMessagePaused()))) {
                    z = false;
                }
                boolean updateRewinding = z;
                if (updateRewinding) {
                    this.seekBarView.setProgress(this.rewindingProgress, animated);
                } else {
                    this.seekBarView.setProgress(messageObject.audioProgress, animated);
                }
                if (this.currentAudioFinishedLoading) {
                    bufferedProgress = 1.0f;
                } else {
                    long time = SystemClock.elapsedRealtime();
                    if (Math.abs(time - this.lastBufferedPositionCheck) >= 500) {
                        float bufferedProgress2 = MediaController.getInstance().isStreamingCurrentAudio() ? FileLoader.getInstance(this.currentAccount).getBufferedProgressFromPosition(messageObject.audioProgress, this.currentFile) : 1.0f;
                        this.lastBufferedPositionCheck = time;
                        bufferedProgress = bufferedProgress2;
                    } else {
                        bufferedProgress = -1.0f;
                    }
                }
                if (bufferedProgress != -1.0f) {
                    this.seekBarView.setBufferedProgress(bufferedProgress);
                }
                if (updateRewinding) {
                    int newTime2 = (int) (messageObject.getDuration() * this.seekBarView.getProgress());
                    messageObject.audioProgressSec = newTime2;
                    newTime = newTime2;
                } else {
                    newTime = messageObject.audioProgressSec;
                }
            }
            if (this.lastTime != newTime) {
                this.lastTime = newTime;
                this.timeTextView.setText(AndroidUtilities.formatShortDuration(newTime));
            }
        }
    }

    private void checkIfMusicDownloaded(MessageObject messageObject) {
        File cacheFile = null;
        if (messageObject.messageOwner.attachPath != null && messageObject.messageOwner.attachPath.length() > 0) {
            cacheFile = new File(messageObject.messageOwner.attachPath);
            if (!cacheFile.exists()) {
                cacheFile = null;
            }
        }
        if (cacheFile == null) {
            cacheFile = FileLoader.getInstance(this.currentAccount).getPathToMessage(messageObject.messageOwner);
        }
        boolean canStream = SharedConfig.streamMedia && ((int) messageObject.getDialogId()) != 0 && messageObject.isMusic();
        if (!cacheFile.exists() && !canStream) {
            String fileName = messageObject.getFileName();
            DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(fileName, this);
            Float progress = ImageLoader.getInstance().getFileProgress(fileName);
            this.progressView.setProgress(progress != null ? progress.floatValue() : 0.0f, false);
            this.progressView.setVisibility(0);
            this.seekBarView.setVisibility(4);
            this.playButton.setEnabled(false);
            return;
        }
        DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
        this.progressView.setVisibility(4);
        this.seekBarView.setVisibility(0);
        this.playButton.setEnabled(true);
    }

    private void updateTitle(boolean shutdown) {
        MessageObject messageObject = MediaController.getInstance().getPlayingMessageObject();
        if ((messageObject == null && shutdown) || (messageObject != null && !messageObject.isMusic())) {
            dismiss();
        } else if (messageObject == null) {
            this.lastMessageObject = null;
        } else {
            boolean sameMessageObject = messageObject == this.lastMessageObject;
            this.lastMessageObject = messageObject;
            if (messageObject.eventId != 0 || messageObject.getId() <= -2000000000) {
                this.optionsButton.setVisibility(4);
            } else {
                this.optionsButton.setVisibility(0);
            }
            if (MessagesController.getInstance(this.currentAccount).isChatNoForwards(messageObject.getChatId())) {
                this.optionsButton.hideSubItem(1);
                this.optionsButton.hideSubItem(2);
                this.optionsButton.hideSubItem(5);
                this.optionsButton.setAdditionalYOffset(-AndroidUtilities.dp(16.0f));
            } else {
                this.optionsButton.showSubItem(1);
                this.optionsButton.showSubItem(2);
                this.optionsButton.showSubItem(5);
                this.optionsButton.setAdditionalYOffset(-AndroidUtilities.dp(157.0f));
            }
            checkIfMusicDownloaded(messageObject);
            updateProgress(messageObject, !sameMessageObject);
            updateCover(messageObject, !sameMessageObject);
            if (MediaController.getInstance().isMessagePaused()) {
                this.playPauseDrawable.setPause(false);
                this.playButton.setContentDescription(LocaleController.getString("AccActionPlay", R.string.AccActionPlay));
            } else {
                this.playPauseDrawable.setPause(true);
                this.playButton.setContentDescription(LocaleController.getString("AccActionPause", R.string.AccActionPause));
            }
            String title = messageObject.getMusicTitle();
            String author = messageObject.getMusicAuthor();
            this.titleTextView.setText(title);
            this.authorTextView.setText(author);
            int duration = messageObject.getDuration();
            this.lastDuration = duration;
            TextView textView = this.durationTextView;
            if (textView != null) {
                textView.setText(duration != 0 ? AndroidUtilities.formatShortDuration(duration) : "-:--");
            }
            if (duration > 600) {
                this.playbackSpeedButton.setVisibility(0);
            } else {
                this.playbackSpeedButton.setVisibility(8);
            }
            if (!sameMessageObject) {
                preloadNeighboringThumbs();
            }
        }
    }

    private void updateCover(MessageObject messageObject, boolean animated) {
        CoverContainer coverContainer = this.coverContainer;
        BackupImageView imageView = animated ? coverContainer.getNextImageView() : coverContainer.getImageView();
        AudioInfo audioInfo = MediaController.getInstance().getAudioInfo();
        if (audioInfo != null && audioInfo.getCover() != null) {
            imageView.setImageBitmap(audioInfo.getCover());
            this.currentFile = null;
            this.currentAudioFinishedLoading = true;
        } else {
            TLRPC.Document document = messageObject.getDocument();
            this.currentFile = FileLoader.getAttachFileName(document);
            this.currentAudioFinishedLoading = false;
            String artworkUrl = messageObject.getArtworkUrl(false);
            ImageLocation thumbImageLocation = getArtworkThumbImageLocation(messageObject);
            if (!TextUtils.isEmpty(artworkUrl)) {
                imageView.setImage(ImageLocation.getForPath(artworkUrl), null, thumbImageLocation, null, null, 0L, 1, messageObject);
            } else if (thumbImageLocation != null) {
                imageView.setImage(null, null, thumbImageLocation, null, null, 0L, 1, messageObject);
            } else {
                imageView.setImageDrawable(null);
            }
            imageView.invalidate();
        }
        if (animated) {
            this.coverContainer.switchImageViews();
        }
    }

    private ImageLocation getArtworkThumbImageLocation(MessageObject messageObject) {
        TLRPC.Document document = messageObject.getDocument();
        TLRPC.PhotoSize thumb = document != null ? FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 360) : null;
        if (!(thumb instanceof TLRPC.TL_photoSize) && !(thumb instanceof TLRPC.TL_photoSizeProgressive)) {
            thumb = null;
        }
        if (thumb != null) {
            return ImageLocation.getForDocument(thumb, document);
        }
        String smallArtworkUrl = messageObject.getArtworkUrl(true);
        if (smallArtworkUrl == null) {
            return null;
        }
        return ImageLocation.getForPath(smallArtworkUrl);
    }

    private void preloadNeighboringThumbs() {
        MediaController mediaController = MediaController.getInstance();
        List<MessageObject> playlist = mediaController.getPlaylist();
        if (playlist.size() <= 1) {
            return;
        }
        List<MessageObject> neighboringItems = new ArrayList<>();
        int playingIndex = mediaController.getPlayingMessageObjectNum();
        int nextIndex = playingIndex + 1;
        int prevIndex = playingIndex - 1;
        if (nextIndex >= playlist.size()) {
            nextIndex = 0;
        }
        if (prevIndex <= -1) {
            prevIndex = playlist.size() - 1;
        }
        neighboringItems.add(playlist.get(nextIndex));
        if (nextIndex != prevIndex) {
            neighboringItems.add(playlist.get(prevIndex));
        }
        int N = neighboringItems.size();
        for (int i = 0; i < N; i++) {
            MessageObject messageObject = neighboringItems.get(i);
            ImageLocation thumbImageLocation = getArtworkThumbImageLocation(messageObject);
            if (thumbImageLocation != null) {
                if (thumbImageLocation.path != null) {
                    ImageLoader.getInstance().preloadArtwork(thumbImageLocation.path);
                } else {
                    FileLoader.getInstance(this.currentAccount).loadFile(thumbImageLocation, messageObject, null, 0, 1);
                }
            }
        }
    }

    /* loaded from: classes5.dex */
    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context context;
        private ArrayList<MessageObject> searchResult = new ArrayList<>();
        private Runnable searchRunnable;

        public ListAdapter(Context context) {
            AudioPlayerAlert.this = r1;
            this.context = context;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            if (AudioPlayerAlert.this.playlist.size() > 1) {
                AudioPlayerAlert.this.playerLayout.setBackgroundColor(AudioPlayerAlert.this.getThemedColor(Theme.key_player_background));
                AudioPlayerAlert.this.playerShadow.setVisibility(0);
                AudioPlayerAlert.this.listView.setPadding(0, AudioPlayerAlert.this.listView.getPaddingTop(), 0, AndroidUtilities.dp(179.0f));
            } else {
                AudioPlayerAlert.this.playerLayout.setBackground(null);
                AudioPlayerAlert.this.playerShadow.setVisibility(4);
                AudioPlayerAlert.this.listView.setPadding(0, AudioPlayerAlert.this.listView.getPaddingTop(), 0, 0);
            }
            AudioPlayerAlert.this.updateEmptyView();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            if (!AudioPlayerAlert.this.searchWas) {
                if (AudioPlayerAlert.this.playlist.size() <= 1) {
                    return 0;
                }
                return AudioPlayerAlert.this.playlist.size();
            }
            return this.searchResult.size();
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return true;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = this.context;
            boolean currentPlaylistIsGlobalSearch = MediaController.getInstance().currentPlaylistIsGlobalSearch();
            View view = new AudioPlayerCell(context, currentPlaylistIsGlobalSearch ? 1 : 0, AudioPlayerAlert.this.resourcesProvider);
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            AudioPlayerCell cell = (AudioPlayerCell) holder.itemView;
            if (AudioPlayerAlert.this.searchWas) {
                cell.setMessageObject(this.searchResult.get(position));
            } else if (SharedConfig.playOrderReversed) {
                cell.setMessageObject((MessageObject) AudioPlayerAlert.this.playlist.get(position));
            } else {
                cell.setMessageObject((MessageObject) AudioPlayerAlert.this.playlist.get((AudioPlayerAlert.this.playlist.size() - position) - 1));
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            return 0;
        }

        public void search(final String query) {
            if (this.searchRunnable != null) {
                Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                this.searchRunnable = null;
            }
            if (query == null) {
                this.searchResult.clear();
                notifyDataSetChanged();
                return;
            }
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            Runnable runnable = new Runnable() { // from class: org.telegram.ui.Components.AudioPlayerAlert$ListAdapter$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    AudioPlayerAlert.ListAdapter.this.m2205xe1d541a0(query);
                }
            };
            this.searchRunnable = runnable;
            dispatchQueue.postRunnable(runnable, 300L);
        }

        /* renamed from: lambda$search$0$org-telegram-ui-Components-AudioPlayerAlert$ListAdapter */
        public /* synthetic */ void m2205xe1d541a0(String query) {
            this.searchRunnable = null;
            processSearch(query);
        }

        private void processSearch(final String query) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.AudioPlayerAlert$ListAdapter$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    AudioPlayerAlert.ListAdapter.this.m2204x5e2e9517(query);
                }
            });
        }

        /* renamed from: lambda$processSearch$2$org-telegram-ui-Components-AudioPlayerAlert$ListAdapter */
        public /* synthetic */ void m2204x5e2e9517(final String query) {
            final ArrayList<MessageObject> copy = new ArrayList<>(AudioPlayerAlert.this.playlist);
            Utilities.searchQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.Components.AudioPlayerAlert$ListAdapter$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    AudioPlayerAlert.ListAdapter.this.m2203x389a8c16(query, copy);
                }
            });
        }

        /* renamed from: lambda$processSearch$1$org-telegram-ui-Components-AudioPlayerAlert$ListAdapter */
        public /* synthetic */ void m2203x389a8c16(String query, ArrayList copy) {
            String search1;
            TLRPC.Document document;
            String search12 = query.trim().toLowerCase();
            if (search12.length() == 0) {
                updateSearchResults(new ArrayList<>(), query);
                return;
            }
            String search2 = LocaleController.getInstance().getTranslitString(search12);
            if (search12.equals(search2) || search2.length() == 0) {
                search2 = null;
            }
            String[] search = new String[(search2 != null ? 1 : 0) + 1];
            search[0] = search12;
            if (search2 != null) {
                search[1] = search2;
            }
            ArrayList<MessageObject> resultArray = new ArrayList<>();
            int a = 0;
            while (a < copy.size()) {
                MessageObject messageObject = (MessageObject) copy.get(a);
                int b = 0;
                while (true) {
                    if (b >= search.length) {
                        search1 = search12;
                        break;
                    }
                    String q = search[b];
                    String name = messageObject.getDocumentName();
                    if (name == null) {
                        search1 = search12;
                    } else if (name.length() == 0) {
                        search1 = search12;
                    } else if (name.toLowerCase().contains(q)) {
                        resultArray.add(messageObject);
                        search1 = search12;
                        break;
                    } else {
                        if (messageObject.type == 0) {
                            document = messageObject.messageOwner.media.webpage.document;
                        } else {
                            document = messageObject.messageOwner.media.document;
                        }
                        boolean ok = false;
                        int c = 0;
                        while (true) {
                            if (c >= document.attributes.size()) {
                                search1 = search12;
                                break;
                            }
                            TLRPC.DocumentAttribute attribute = document.attributes.get(c);
                            search1 = search12;
                            if (!(attribute instanceof TLRPC.TL_documentAttributeAudio)) {
                                c++;
                                search12 = search1;
                            } else {
                                if (attribute.performer != null) {
                                    ok = attribute.performer.toLowerCase().contains(q);
                                }
                                if (!ok && attribute.title != null) {
                                    ok = attribute.title.toLowerCase().contains(q);
                                }
                            }
                        }
                        if (ok) {
                            resultArray.add(messageObject);
                            break;
                        }
                    }
                    b++;
                    search12 = search1;
                }
                a++;
                search12 = search1;
            }
            updateSearchResults(resultArray, query);
        }

        private void updateSearchResults(final ArrayList<MessageObject> documents, final String query) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.AudioPlayerAlert$ListAdapter$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    AudioPlayerAlert.ListAdapter.this.m2206xe828b846(documents, query);
                }
            });
        }

        /* renamed from: lambda$updateSearchResults$3$org-telegram-ui-Components-AudioPlayerAlert$ListAdapter */
        public /* synthetic */ void m2206xe828b846(ArrayList documents, String query) {
            if (AudioPlayerAlert.this.searching) {
                AudioPlayerAlert.this.searchWas = true;
                this.searchResult = documents;
                notifyDataSetChanged();
                AudioPlayerAlert.this.layoutManager.scrollToPosition(0);
                AudioPlayerAlert.this.emptySubtitleTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("NoAudioFoundPlayerInfo", R.string.NoAudioFoundPlayerInfo, query)));
            }
        }
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        ThemeDescription.ThemeDescriptionDelegate delegate = new ThemeDescription.ThemeDescriptionDelegate() { // from class: org.telegram.ui.Components.AudioPlayerAlert$$ExternalSyntheticLambda1
            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public final void didSetColor() {
                AudioPlayerAlert.this.m2190xb100d91f();
            }

            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public /* synthetic */ void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        };
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_player_actionBar));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, delegate, Theme.key_player_actionBarTitle));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_player_actionBarTitle));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBTITLECOLOR, null, null, null, null, Theme.key_player_actionBarTitle));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_player_actionBarSelector));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, Theme.key_player_actionBarTitle));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, Theme.key_player_time));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{AudioPlayerCell.class}, null, null, null, Theme.key_chat_inLoader));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{AudioPlayerCell.class}, null, null, null, Theme.key_chat_outLoader));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{AudioPlayerCell.class}, null, null, null, Theme.key_chat_inLoaderSelected));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{AudioPlayerCell.class}, null, null, null, Theme.key_chat_inMediaIcon));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{AudioPlayerCell.class}, null, null, null, Theme.key_chat_inMediaIconSelected));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{AudioPlayerCell.class}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{AudioPlayerCell.class}, null, null, null, Theme.key_chat_inAudioSelectedProgress));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{AudioPlayerCell.class}, null, null, null, Theme.key_chat_inAudioProgress));
        themeDescriptions.add(new ThemeDescription(this.containerView, 0, null, null, new Drawable[]{this.shadowDrawable}, null, Theme.key_dialogBackground));
        themeDescriptions.add(new ThemeDescription(this.progressView, 0, null, null, null, null, Theme.key_player_progressBackground));
        themeDescriptions.add(new ThemeDescription(this.progressView, 0, null, null, null, null, Theme.key_player_progress));
        themeDescriptions.add(new ThemeDescription(this.seekBarView, 0, null, null, null, null, Theme.key_player_progressBackground));
        themeDescriptions.add(new ThemeDescription(this.seekBarView, 0, null, null, null, null, Theme.key_player_progressCachedBackground));
        themeDescriptions.add(new ThemeDescription(this.seekBarView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_player_progress));
        themeDescriptions.add(new ThemeDescription(this.playbackSpeedButton, ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_inappPlayerPlayPause));
        themeDescriptions.add(new ThemeDescription(this.playbackSpeedButton, ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_inappPlayerClose));
        themeDescriptions.add(new ThemeDescription(this.repeatButton, 0, null, null, null, delegate, Theme.key_player_button));
        themeDescriptions.add(new ThemeDescription(this.repeatButton, 0, null, null, null, delegate, Theme.key_player_buttonActive));
        themeDescriptions.add(new ThemeDescription(this.repeatButton, 0, null, null, null, delegate, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(this.repeatButton, 0, null, null, null, delegate, Theme.key_actionBarDefaultSubmenuItem));
        themeDescriptions.add(new ThemeDescription(this.repeatButton, 0, null, null, null, delegate, Theme.key_actionBarDefaultSubmenuBackground));
        themeDescriptions.add(new ThemeDescription(this.optionsButton, 0, null, null, null, delegate, Theme.key_player_button));
        themeDescriptions.add(new ThemeDescription(this.optionsButton, 0, null, null, null, delegate, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(this.optionsButton, 0, null, null, null, delegate, Theme.key_actionBarDefaultSubmenuItem));
        themeDescriptions.add(new ThemeDescription(this.optionsButton, 0, null, null, null, delegate, Theme.key_actionBarDefaultSubmenuBackground));
        RLottieImageView rLottieImageView = this.prevButton;
        themeDescriptions.add(new ThemeDescription(rLottieImageView, 0, (Class[]) null, new RLottieDrawable[]{rLottieImageView.getAnimatedDrawable()}, "Triangle 3", Theme.key_player_button));
        RLottieImageView rLottieImageView2 = this.prevButton;
        themeDescriptions.add(new ThemeDescription(rLottieImageView2, 0, (Class[]) null, new RLottieDrawable[]{rLottieImageView2.getAnimatedDrawable()}, "Triangle 4", Theme.key_player_button));
        RLottieImageView rLottieImageView3 = this.prevButton;
        themeDescriptions.add(new ThemeDescription(rLottieImageView3, 0, (Class[]) null, new RLottieDrawable[]{rLottieImageView3.getAnimatedDrawable()}, "Rectangle 4", Theme.key_player_button));
        themeDescriptions.add(new ThemeDescription(this.prevButton, ThemeDescription.FLAG_IMAGECOLOR | ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, null, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(this.playButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_player_button));
        themeDescriptions.add(new ThemeDescription(this.playButton, ThemeDescription.FLAG_IMAGECOLOR | ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, null, null, null, null, Theme.key_listSelector));
        RLottieImageView rLottieImageView4 = this.nextButton;
        themeDescriptions.add(new ThemeDescription(rLottieImageView4, 0, (Class[]) null, new RLottieDrawable[]{rLottieImageView4.getAnimatedDrawable()}, "Triangle 3", Theme.key_player_button));
        RLottieImageView rLottieImageView5 = this.nextButton;
        themeDescriptions.add(new ThemeDescription(rLottieImageView5, 0, (Class[]) null, new RLottieDrawable[]{rLottieImageView5.getAnimatedDrawable()}, "Triangle 4", Theme.key_player_button));
        RLottieImageView rLottieImageView6 = this.nextButton;
        themeDescriptions.add(new ThemeDescription(rLottieImageView6, 0, (Class[]) null, new RLottieDrawable[]{rLottieImageView6.getAnimatedDrawable()}, "Rectangle 4", Theme.key_player_button));
        themeDescriptions.add(new ThemeDescription(this.nextButton, ThemeDescription.FLAG_IMAGECOLOR | ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, null, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(this.playerLayout, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_player_background));
        themeDescriptions.add(new ThemeDescription(this.playerShadow, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_dialogShadowLine));
        themeDescriptions.add(new ThemeDescription(this.emptyImageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_dialogEmptyImage));
        themeDescriptions.add(new ThemeDescription(this.emptyTitleTextView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_dialogEmptyText));
        themeDescriptions.add(new ThemeDescription(this.emptySubtitleTextView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_dialogEmptyText));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_dialogScrollGlow));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        themeDescriptions.add(new ThemeDescription(this.progressView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_emptyListPlaceholder));
        themeDescriptions.add(new ThemeDescription(this.progressView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_progressCircle));
        themeDescriptions.add(new ThemeDescription(this.durationTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_player_time));
        themeDescriptions.add(new ThemeDescription(this.timeTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_player_time));
        themeDescriptions.add(new ThemeDescription(this.titleTextView.getTextView(), ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_player_actionBarTitle));
        themeDescriptions.add(new ThemeDescription(this.titleTextView.getNextTextView(), ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_player_actionBarTitle));
        themeDescriptions.add(new ThemeDescription(this.authorTextView.getTextView(), ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_player_time));
        themeDescriptions.add(new ThemeDescription(this.authorTextView.getNextTextView(), ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_player_time));
        themeDescriptions.add(new ThemeDescription(this.containerView, 0, null, null, null, null, Theme.key_sheet_scrollUp));
        return themeDescriptions;
    }

    /* renamed from: lambda$getThemeDescriptions$11$org-telegram-ui-Components-AudioPlayerAlert */
    public /* synthetic */ void m2190xb100d91f() {
        EditTextBoldCursor editText = this.searchItem.getSearchField();
        editText.setCursorColor(getThemedColor(Theme.key_player_actionBarTitle));
        ActionBarMenuItem actionBarMenuItem = this.repeatButton;
        actionBarMenuItem.setIconColor(getThemedColor((String) actionBarMenuItem.getTag()));
        Theme.setSelectorDrawableColor(this.repeatButton.getBackground(), getThemedColor(Theme.key_listSelector), true);
        this.optionsButton.setIconColor(getThemedColor(Theme.key_player_button));
        Theme.setSelectorDrawableColor(this.optionsButton.getBackground(), getThemedColor(Theme.key_listSelector), true);
        this.progressView.setBackgroundColor(getThemedColor(Theme.key_player_progressBackground));
        this.progressView.setProgressColor(getThemedColor(Theme.key_player_progress));
        updateSubMenu();
        this.repeatButton.redrawPopup(getThemedColor(Theme.key_actionBarDefaultSubmenuBackground));
        this.optionsButton.setPopupItemsColor(getThemedColor(Theme.key_actionBarDefaultSubmenuItem), false);
        this.optionsButton.setPopupItemsColor(getThemedColor(Theme.key_actionBarDefaultSubmenuItem), true);
        this.optionsButton.redrawPopup(getThemedColor(Theme.key_actionBarDefaultSubmenuBackground));
    }

    /* loaded from: classes5.dex */
    public static abstract class CoverContainer extends FrameLayout {
        private int activeIndex;
        private AnimatorSet animatorSet;
        private final BackupImageView[] imageViews = new BackupImageView[2];

        protected abstract void onImageUpdated(ImageReceiver imageReceiver);

        public CoverContainer(Context context) {
            super(context);
            for (int i = 0; i < 2; i++) {
                this.imageViews[i] = new BackupImageView(context);
                final int index = i;
                this.imageViews[i].getImageReceiver().setDelegate(new ImageReceiver.ImageReceiverDelegate() { // from class: org.telegram.ui.Components.AudioPlayerAlert$CoverContainer$$ExternalSyntheticLambda2
                    @Override // org.telegram.messenger.ImageReceiver.ImageReceiverDelegate
                    public final void didSetImage(ImageReceiver imageReceiver, boolean z, boolean z2, boolean z3) {
                        AudioPlayerAlert.CoverContainer.this.m2202x457889fd(index, imageReceiver, z, z2, z3);
                    }

                    @Override // org.telegram.messenger.ImageReceiver.ImageReceiverDelegate
                    public /* synthetic */ void onAnimationReady(ImageReceiver imageReceiver) {
                        ImageReceiver.ImageReceiverDelegate.CC.$default$onAnimationReady(this, imageReceiver);
                    }
                });
                this.imageViews[i].setRoundRadius(AndroidUtilities.dp(4.0f));
                if (i == 1) {
                    this.imageViews[i].setVisibility(8);
                }
                addView(this.imageViews[i], LayoutHelper.createFrame(-1, -1.0f));
            }
        }

        /* renamed from: lambda$new$0$org-telegram-ui-Components-AudioPlayerAlert$CoverContainer */
        public /* synthetic */ void m2202x457889fd(int index, ImageReceiver imageReceiver, boolean set, boolean thumb, boolean memCache) {
            if (index == this.activeIndex) {
                onImageUpdated(imageReceiver);
            }
        }

        public final void switchImageViews() {
            AnimatorSet animatorSet = this.animatorSet;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            this.animatorSet = new AnimatorSet();
            int i = this.activeIndex == 0 ? 1 : 0;
            this.activeIndex = i;
            BackupImageView[] backupImageViewArr = this.imageViews;
            final BackupImageView prevImageView = backupImageViewArr[i ^ 1];
            final BackupImageView currImageView = backupImageViewArr[i];
            final boolean hasBitmapImage = prevImageView.getImageReceiver().hasBitmapImage();
            currImageView.setAlpha(hasBitmapImage ? 1.0f : 0.0f);
            currImageView.setScaleX(0.8f);
            currImageView.setScaleY(0.8f);
            currImageView.setVisibility(0);
            if (hasBitmapImage) {
                prevImageView.bringToFront();
            } else {
                prevImageView.setVisibility(8);
                prevImageView.setImageDrawable(null);
            }
            ValueAnimator expandAnimator = ValueAnimator.ofFloat(0.8f, 1.0f);
            expandAnimator.setDuration(125L);
            expandAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            expandAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.AudioPlayerAlert$CoverContainer$$ExternalSyntheticLambda1
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    AudioPlayerAlert.CoverContainer.lambda$switchImageViews$1(BackupImageView.this, hasBitmapImage, valueAnimator);
                }
            });
            if (hasBitmapImage) {
                ValueAnimator collapseAnimator = ValueAnimator.ofFloat(prevImageView.getScaleX(), 0.8f);
                collapseAnimator.setDuration(125L);
                collapseAnimator.setInterpolator(CubicBezierInterpolator.EASE_IN);
                collapseAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.AudioPlayerAlert$CoverContainer$$ExternalSyntheticLambda0
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        AudioPlayerAlert.CoverContainer.lambda$switchImageViews$2(BackupImageView.this, currImageView, valueAnimator);
                    }
                });
                collapseAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.AudioPlayerAlert.CoverContainer.1
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        prevImageView.setVisibility(8);
                        prevImageView.setImageDrawable(null);
                        prevImageView.setAlpha(1.0f);
                    }
                });
                this.animatorSet.playSequentially(collapseAnimator, expandAnimator);
            } else {
                this.animatorSet.play(expandAnimator);
            }
            this.animatorSet.start();
        }

        public static /* synthetic */ void lambda$switchImageViews$1(BackupImageView currImageView, boolean hasBitmapImage, ValueAnimator a) {
            float animatedValue = ((Float) a.getAnimatedValue()).floatValue();
            currImageView.setScaleX(animatedValue);
            currImageView.setScaleY(animatedValue);
            if (!hasBitmapImage) {
                currImageView.setAlpha(a.getAnimatedFraction());
            }
        }

        public static /* synthetic */ void lambda$switchImageViews$2(BackupImageView prevImageView, BackupImageView currImageView, ValueAnimator a) {
            float animatedValue = ((Float) a.getAnimatedValue()).floatValue();
            prevImageView.setScaleX(animatedValue);
            prevImageView.setScaleY(animatedValue);
            float fraction = a.getAnimatedFraction();
            if (fraction > 0.25f && !currImageView.getImageReceiver().hasBitmapImage()) {
                prevImageView.setAlpha(1.0f - ((fraction - 0.25f) * 1.3333334f));
            }
        }

        public final BackupImageView getImageView() {
            return this.imageViews[this.activeIndex];
        }

        public final BackupImageView getNextImageView() {
            return this.imageViews[this.activeIndex == 0 ? (char) 1 : (char) 0];
        }

        public final ImageReceiver getImageReceiver() {
            return getImageView().getImageReceiver();
        }
    }

    /* loaded from: classes5.dex */
    public static abstract class ClippingTextViewSwitcher extends FrameLayout {
        private int activeIndex;
        private AnimatorSet animatorSet;
        private final Paint erasePaint;
        private final Matrix gradientMatrix;
        private final Paint gradientPaint;
        private LinearGradient gradientShader;
        private final TextView[] textViews = new TextView[2];
        private final float[] clipProgress = {0.0f, 0.75f};
        private final int gradientSize = AndroidUtilities.dp(24.0f);
        private int stableOffest = -1;
        private final RectF rectF = new RectF();

        protected abstract TextView createTextView();

        public ClippingTextViewSwitcher(Context context) {
            super(context);
            for (int i = 0; i < 2; i++) {
                this.textViews[i] = createTextView();
                if (i == 1) {
                    this.textViews[i].setAlpha(0.0f);
                    this.textViews[i].setVisibility(8);
                }
                addView(this.textViews[i], LayoutHelper.createFrame(-2, -1.0f));
            }
            this.gradientMatrix = new Matrix();
            Paint paint = new Paint(1);
            this.gradientPaint = paint;
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            Paint paint2 = new Paint(1);
            this.erasePaint = paint2;
            paint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        }

        @Override // android.view.View
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            LinearGradient linearGradient = new LinearGradient(this.gradientSize, 0.0f, 0.0f, 0.0f, 0, -16777216, Shader.TileMode.CLAMP);
            this.gradientShader = linearGradient;
            this.gradientPaint.setShader(linearGradient);
        }

        @Override // android.view.ViewGroup
        protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
            TextView[] textViewArr = this.textViews;
            int index = child == textViewArr[0] ? 0 : 1;
            boolean hasStableRect = false;
            if (this.stableOffest > 0 && textViewArr[this.activeIndex].getAlpha() != 1.0f && this.textViews[this.activeIndex].getLayout() != null) {
                float x1 = this.textViews[this.activeIndex].getLayout().getPrimaryHorizontal(0);
                float x2 = this.textViews[this.activeIndex].getLayout().getPrimaryHorizontal(this.stableOffest);
                hasStableRect = true;
                if (x1 == x2) {
                    hasStableRect = false;
                } else if (x2 > x1) {
                    this.rectF.set(x1, 0.0f, x2, getMeasuredHeight());
                } else {
                    this.rectF.set(x2, 0.0f, x1, getMeasuredHeight());
                }
                if (hasStableRect && index == this.activeIndex) {
                    canvas.save();
                    canvas.clipRect(this.rectF);
                    this.textViews[0].draw(canvas);
                    canvas.restore();
                }
            }
            boolean hasStableRect2 = hasStableRect;
            if (this.clipProgress[index] > 0.0f || hasStableRect2) {
                int width = child.getWidth();
                int height = child.getHeight();
                int saveCount = canvas.saveLayer(0.0f, 0.0f, width, height, null, 31);
                boolean result = super.drawChild(canvas, child, drawingTime);
                float gradientStart = (1.0f - this.clipProgress[index]) * width;
                float gradientEnd = gradientStart + this.gradientSize;
                this.gradientMatrix.setTranslate(gradientStart, 0.0f);
                this.gradientShader.setLocalMatrix(this.gradientMatrix);
                canvas.drawRect(gradientStart, 0.0f, gradientEnd, height, this.gradientPaint);
                if (width > gradientEnd) {
                    canvas.drawRect(gradientEnd, 0.0f, width, height, this.erasePaint);
                }
                if (hasStableRect2) {
                    canvas.drawRect(this.rectF, this.erasePaint);
                }
                canvas.restoreToCount(saveCount);
                return result;
            }
            boolean result2 = super.drawChild(canvas, child, drawingTime);
            return result2;
        }

        public void setText(CharSequence text) {
            setText(text, true);
        }

        public void setText(CharSequence text, boolean animated) {
            CharSequence currentText = this.textViews[this.activeIndex].getText();
            if (TextUtils.isEmpty(currentText) || !animated) {
                this.textViews[this.activeIndex].setText(text);
            } else if (!TextUtils.equals(text, currentText)) {
                this.stableOffest = 0;
                int n = Math.min(text.length(), currentText.length());
                for (int i = 0; i < n && text.charAt(i) == currentText.charAt(i); i++) {
                    this.stableOffest++;
                }
                int i2 = this.stableOffest;
                if (i2 <= 3) {
                    this.stableOffest = -1;
                }
                final int index = this.activeIndex == 0 ? 1 : 0;
                final int prevIndex = this.activeIndex;
                this.activeIndex = index;
                AnimatorSet animatorSet = this.animatorSet;
                if (animatorSet != null) {
                    animatorSet.cancel();
                }
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.animatorSet = animatorSet2;
                animatorSet2.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.AudioPlayerAlert.ClippingTextViewSwitcher.1
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        ClippingTextViewSwitcher.this.textViews[prevIndex].setVisibility(8);
                    }
                });
                this.textViews[index].setText(text);
                this.textViews[index].bringToFront();
                this.textViews[index].setVisibility(0);
                ValueAnimator collapseAnimator = ValueAnimator.ofFloat(this.clipProgress[prevIndex], 0.75f);
                collapseAnimator.setDuration(200L);
                collapseAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.AudioPlayerAlert$ClippingTextViewSwitcher$$ExternalSyntheticLambda0
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        AudioPlayerAlert.ClippingTextViewSwitcher.this.m2200x278a1299(prevIndex, valueAnimator);
                    }
                });
                ValueAnimator expandAnimator = ValueAnimator.ofFloat(this.clipProgress[index], 0.0f);
                expandAnimator.setStartDelay(100L);
                expandAnimator.setDuration(200L);
                expandAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.AudioPlayerAlert$ClippingTextViewSwitcher$$ExternalSyntheticLambda1
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        AudioPlayerAlert.ClippingTextViewSwitcher.this.m2201x606a7338(index, valueAnimator);
                    }
                });
                ObjectAnimator fadeOutAnimator = ObjectAnimator.ofFloat(this.textViews[prevIndex], View.ALPHA, 0.0f);
                fadeOutAnimator.setStartDelay(75L);
                fadeOutAnimator.setDuration(150L);
                ObjectAnimator fadeInAnimator = ObjectAnimator.ofFloat(this.textViews[index], View.ALPHA, 1.0f);
                fadeInAnimator.setStartDelay(75L);
                fadeInAnimator.setDuration(150L);
                this.animatorSet.playTogether(collapseAnimator, expandAnimator, fadeOutAnimator, fadeInAnimator);
                this.animatorSet.start();
            }
        }

        /* renamed from: lambda$setText$0$org-telegram-ui-Components-AudioPlayerAlert$ClippingTextViewSwitcher */
        public /* synthetic */ void m2200x278a1299(int prevIndex, ValueAnimator a) {
            this.clipProgress[prevIndex] = ((Float) a.getAnimatedValue()).floatValue();
            invalidate();
        }

        /* renamed from: lambda$setText$1$org-telegram-ui-Components-AudioPlayerAlert$ClippingTextViewSwitcher */
        public /* synthetic */ void m2201x606a7338(int index, ValueAnimator a) {
            this.clipProgress[index] = ((Float) a.getAnimatedValue()).floatValue();
            invalidate();
        }

        public TextView getTextView() {
            return this.textViews[this.activeIndex];
        }

        public TextView getNextTextView() {
            return this.textViews[this.activeIndex == 0 ? (char) 1 : (char) 0];
        }
    }
}
