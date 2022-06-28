package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.collection.ArrayMap;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.C;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatThemeController;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ResultCallback;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.EmojiThemes;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.ChatThemeBottomSheet;
import org.telegram.ui.Components.Easings;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.HideViewAfterAnimation;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.MotionBackgroundDrawable;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ThemeSmallPreviewView;
import org.telegram.ui.QrActivity;
/* loaded from: classes4.dex */
public class QrActivity extends BaseFragment {
    private static List<EmojiThemes> cachedThemes;
    private static final ArrayMap<String, int[]> qrColorsMap;
    private BackupImageView avatarImageView;
    private View backgroundView;
    private long chatId;
    private ImageView closeImageView;
    private EmojiThemes currentTheme;
    private Bitmap emojiThemeIcon;
    private final EmojiThemes homeTheme;
    private boolean isCurrentThemeDark;
    private RLottieImageView logoImageView;
    private ValueAnimator patternAlphaAnimator;
    private ValueAnimator patternIntensityAnimator;
    private MotionBackgroundDrawable prevMotionDrawable;
    private int prevSystemUiVisibility;
    private QrView qrView;
    private MotionBackgroundDrawable tempMotionDrawable;
    private FrameLayout themeLayout;
    private ThemeListViewController themesViewController;
    private long userId;
    private final ThemeResourcesProvider resourcesProvider = new ThemeResourcesProvider();
    private final Rect logoRect = new Rect();
    private final ArrayMap<String, Bitmap> emojiThemeDarkIcons = new ArrayMap<>();
    private final int[] prevQrColors = new int[4];
    private MotionBackgroundDrawable currMotionDrawable = new MotionBackgroundDrawable();
    private int selectedPosition = -1;

    /* loaded from: classes4.dex */
    public interface OnItemSelectedListener {
        void onItemSelected(EmojiThemes emojiThemes, int i);
    }

    static {
        ArrayMap<String, int[]> arrayMap = new ArrayMap<>();
        qrColorsMap = arrayMap;
        arrayMap.put("🏠d", new int[]{-9324972, -13856649, -6636738, -9915042});
        arrayMap.put("🐥d", new int[]{-12344463, -7684788, -6442695, -8013488});
        arrayMap.put("⛄d", new int[]{-10051073, -10897938, -12469550, -7694337});
        arrayMap.put("💎d", new int[]{-11429643, -11814958, -5408261, -2128185});
        arrayMap.put("👨\u200d🏫d", new int[]{-6637227, -12015466, -13198627, -10631557});
        arrayMap.put("🌷d", new int[]{-1146812, -1991901, -1745517, -3443241});
        arrayMap.put("💜d", new int[]{-1156738, -1876046, -5412366, -28073});
        arrayMap.put("🎄d", new int[]{-1281978, -551386, -1870308, -742870});
        arrayMap.put("🎮d", new int[]{-15092782, -2333964, -1684365, -1269214});
        arrayMap.put("🏠n", new int[]{-15368239, -11899662, -15173939, -13850930});
        arrayMap.put("🐥n", new int[]{-11033320, -14780848, -9594089, -12604587});
        arrayMap.put("⛄n", new int[]{-13930790, -13665098, -14833975, -9732865});
        arrayMap.put("💎n", new int[]{-5089608, -9481473, -14378302, -13337899});
        arrayMap.put("👨\u200d🏫n", new int[]{-14447768, -9199261, -15356801, -15823723});
        arrayMap.put("🌷n", new int[]{-2534316, -2984177, -3258783, -5480504});
        arrayMap.put("💜n", new int[]{-3123030, -2067394, -2599576, -6067757});
        arrayMap.put("🎄n", new int[]{-2725857, -3242459, -3248848, -3569123});
        arrayMap.put("🎮n", new int[]{-3718333, -1278154, -16338695, -6076417});
    }

    public QrActivity(Bundle args) {
        super(args);
        EmojiThemes createHomeQrTheme = EmojiThemes.createHomeQrTheme();
        this.homeTheme = createHomeQrTheme;
        this.currentTheme = createHomeQrTheme;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        this.userId = this.arguments.getLong("user_id");
        this.chatId = this.arguments.getLong(ChatReactionsEditActivity.KEY_CHAT_ID);
        return super.onFragmentCreate();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        TLRPC.Chat chat;
        this.homeTheme.loadPreviewColors(this.currentAccount);
        this.isCurrentThemeDark = Theme.getActiveTheme().isDark();
        this.actionBar.setAddToContainer(false);
        this.actionBar.setBackground(null);
        this.actionBar.setItemsColor(-1, false);
        FrameLayout rootLayout = new FrameLayout(context) { // from class: org.telegram.ui.QrActivity.1
            private boolean prevIsPortrait;

            @Override // android.view.ViewGroup, android.view.View
            public boolean dispatchTouchEvent(MotionEvent ev) {
                super.dispatchTouchEvent(ev);
                return true;
            }

            @Override // android.widget.FrameLayout, android.view.View
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int width = View.MeasureSpec.getSize(widthMeasureSpec);
                int height = View.MeasureSpec.getSize(heightMeasureSpec);
                boolean isPortrait = width < height;
                QrActivity.this.avatarImageView.setVisibility(isPortrait ? 0 : 8);
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                if (isPortrait) {
                    QrActivity.this.themeLayout.measure(View.MeasureSpec.makeMeasureSpec(width, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(height, Integer.MIN_VALUE));
                    QrActivity.this.qrView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(260.0f), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(330.0f), C.BUFFER_FLAG_ENCRYPTED));
                } else {
                    QrActivity.this.themeLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(256.0f), C.BUFFER_FLAG_ENCRYPTED), heightMeasureSpec);
                    QrActivity.this.qrView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(260.0f), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(310.0f), C.BUFFER_FLAG_ENCRYPTED));
                }
                if (this.prevIsPortrait != isPortrait) {
                    QrActivity.this.qrView.onSizeChanged(QrActivity.this.qrView.getMeasuredWidth(), QrActivity.this.qrView.getMeasuredHeight(), 0, 0);
                }
                this.prevIsPortrait = isPortrait;
            }

            @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                int qrTop;
                boolean isPortrait = getWidth() < getHeight();
                QrActivity.this.backgroundView.layout(0, 0, getWidth(), getHeight());
                int themeLayoutHeight = 0;
                if (QrActivity.this.themeLayout.getVisibility() == 0) {
                    themeLayoutHeight = QrActivity.this.themeLayout.getMeasuredHeight();
                }
                int qrLeft = isPortrait ? (getWidth() - QrActivity.this.qrView.getMeasuredWidth()) / 2 : ((getWidth() - QrActivity.this.themeLayout.getMeasuredWidth()) - QrActivity.this.qrView.getMeasuredWidth()) / 2;
                if (!isPortrait) {
                    qrTop = (getHeight() - QrActivity.this.qrView.getMeasuredHeight()) / 2;
                } else {
                    qrTop = ((((getHeight() - themeLayoutHeight) - QrActivity.this.qrView.getMeasuredHeight()) - AndroidUtilities.dp(48.0f)) / 2) + AndroidUtilities.dp(52.0f);
                }
                QrActivity.this.qrView.layout(qrLeft, qrTop, QrActivity.this.qrView.getMeasuredWidth() + qrLeft, QrActivity.this.qrView.getMeasuredHeight() + qrTop);
                if (isPortrait) {
                    int avatarLeft = (getWidth() - QrActivity.this.avatarImageView.getMeasuredWidth()) / 2;
                    int avatarTop = qrTop - AndroidUtilities.dp(48.0f);
                    QrActivity.this.avatarImageView.layout(avatarLeft, avatarTop, QrActivity.this.avatarImageView.getMeasuredWidth() + avatarLeft, QrActivity.this.avatarImageView.getMeasuredHeight() + avatarTop);
                }
                if (QrActivity.this.themeLayout.getVisibility() == 0) {
                    if (isPortrait) {
                        int themeLayoutLeft = (getWidth() - QrActivity.this.themeLayout.getMeasuredWidth()) / 2;
                        QrActivity.this.themeLayout.layout(themeLayoutLeft, bottom - themeLayoutHeight, QrActivity.this.themeLayout.getMeasuredWidth() + themeLayoutLeft, bottom);
                    } else {
                        int themeLayoutTop = (getHeight() - QrActivity.this.themeLayout.getMeasuredHeight()) / 2;
                        QrActivity.this.themeLayout.layout(right - QrActivity.this.themeLayout.getMeasuredWidth(), themeLayoutTop, right, QrActivity.this.themeLayout.getMeasuredHeight() + themeLayoutTop);
                    }
                }
                QrActivity.this.logoImageView.layout(QrActivity.this.logoRect.left + qrLeft, QrActivity.this.logoRect.top + qrTop, QrActivity.this.logoRect.right + qrLeft, QrActivity.this.logoRect.bottom + qrTop);
                int closeLeft = AndroidUtilities.dp(isPortrait ? 14.0f : 17.0f);
                int closeTop = AndroidUtilities.statusBarHeight + AndroidUtilities.dp(isPortrait ? 10.0f : 5.0f);
                QrActivity.this.closeImageView.layout(closeLeft, closeTop, QrActivity.this.closeImageView.getMeasuredWidth() + closeLeft, QrActivity.this.closeImageView.getMeasuredHeight() + closeTop);
            }
        };
        View view = new View(context) { // from class: org.telegram.ui.QrActivity.2
            @Override // android.view.View
            protected void onDraw(Canvas canvas) {
                if (QrActivity.this.prevMotionDrawable != null) {
                    QrActivity.this.prevMotionDrawable.setBounds(0, 0, getWidth(), getHeight());
                }
                QrActivity.this.currMotionDrawable.setBounds(0, 0, getWidth(), getHeight());
                if (QrActivity.this.prevMotionDrawable != null) {
                    QrActivity.this.prevMotionDrawable.drawBackground(canvas);
                }
                QrActivity.this.currMotionDrawable.drawBackground(canvas);
                if (QrActivity.this.prevMotionDrawable != null) {
                    QrActivity.this.prevMotionDrawable.drawPattern(canvas);
                }
                QrActivity.this.currMotionDrawable.drawPattern(canvas);
                super.onDraw(canvas);
            }
        };
        this.backgroundView = view;
        rootLayout.addView(view);
        AvatarDrawable avatarDrawable = null;
        String username = null;
        boolean isPhone = false;
        String userfullname = null;
        ImageLocation imageLocationSmall = null;
        ImageLocation imageLocation = null;
        if (this.userId != 0) {
            TLRPC.User user = getMessagesController().getUser(Long.valueOf(this.userId));
            if (user != null) {
                username = user.username;
                if (username == null) {
                    userfullname = UserObject.getUserName(user);
                    username = user.phone;
                    isPhone = true;
                }
                avatarDrawable = new AvatarDrawable(user);
                imageLocationSmall = ImageLocation.getForUser(user, 1);
                imageLocation = ImageLocation.getForUser(user, 0);
            }
        } else if (this.chatId != 0 && (chat = getMessagesController().getChat(Long.valueOf(this.chatId))) != null) {
            username = chat.username;
            avatarDrawable = new AvatarDrawable(chat);
            imageLocationSmall = ImageLocation.getForChat(chat, 1);
            imageLocation = ImageLocation.getForChat(chat, 0);
        }
        String link = "https://" + MessagesController.getInstance(this.currentAccount).linkPrefix + "/" + username;
        QrView qrView = new QrView(context);
        this.qrView = qrView;
        qrView.setColors(-9324972, -13856649, -6636738, -9915042);
        this.qrView.setData(link, userfullname != null ? userfullname : username, isPhone);
        this.qrView.setCenterChangedListener(new QrView.QrCenterChangedListener() { // from class: org.telegram.ui.QrActivity$$ExternalSyntheticLambda2
            @Override // org.telegram.ui.QrActivity.QrView.QrCenterChangedListener
            public final void onCenterChanged(int i, int i2, int i3, int i4) {
                QrActivity.this.m4517lambda$createView$0$orgtelegramuiQrActivity(i, i2, i3, i4);
            }
        });
        rootLayout.addView(this.qrView);
        RLottieImageView rLottieImageView = new RLottieImageView(context);
        this.logoImageView = rLottieImageView;
        rLottieImageView.setAutoRepeat(true);
        this.logoImageView.setAnimation(R.raw.qr_code_logo_2, 60, 60);
        this.logoImageView.playAnimation();
        rootLayout.addView(this.logoImageView);
        BackupImageView backupImageView = new BackupImageView(context);
        this.avatarImageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(42.0f));
        this.avatarImageView.setSize(AndroidUtilities.dp(84.0f), AndroidUtilities.dp(84.0f));
        rootLayout.addView(this.avatarImageView, LayoutHelper.createFrame(84, 84, 51));
        this.avatarImageView.setImage(imageLocation, "84_84", imageLocationSmall, "50_50", avatarDrawable, null, null, 0, null);
        ImageView imageView = new ImageView(context);
        this.closeImageView = imageView;
        imageView.setBackground(Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(34.0f), 671088640, 687865855));
        this.closeImageView.setImageResource(R.drawable.ic_ab_back);
        this.closeImageView.setScaleType(ImageView.ScaleType.CENTER);
        this.closeImageView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.QrActivity$$ExternalSyntheticLambda4
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                QrActivity.this.m4518lambda$createView$1$orgtelegramuiQrActivity(view2);
            }
        });
        rootLayout.addView(this.closeImageView, LayoutHelper.createFrame(34, 34.0f));
        this.emojiThemeIcon = Bitmap.createBitmap(AndroidUtilities.dp(32.0f), AndroidUtilities.dp(32.0f), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(this.emojiThemeIcon);
        AndroidUtilities.rectTmp.set(0.0f, 0.0f, this.emojiThemeIcon.getWidth(), this.emojiThemeIcon.getHeight());
        Paint paint = new Paint(1);
        paint.setColor(-1);
        canvas.drawRoundRect(AndroidUtilities.rectTmp, AndroidUtilities.dp(5.0f), AndroidUtilities.dp(5.0f), paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        Bitmap bitmap = BitmapFactory.decodeResource(ApplicationLoader.applicationContext.getResources(), R.drawable.msg_qr_mini);
        canvas.drawBitmap(bitmap, (this.emojiThemeIcon.getWidth() - bitmap.getWidth()) * 0.5f, (this.emojiThemeIcon.getHeight() - bitmap.getHeight()) * 0.5f, paint);
        canvas.setBitmap(null);
        ThemeListViewController themeListViewController = new ThemeListViewController(this, getParentActivity().getWindow()) { // from class: org.telegram.ui.QrActivity.3
            @Override // org.telegram.ui.QrActivity.ThemeListViewController
            protected void setDarkTheme(boolean isDark) {
                super.setDarkTheme(isDark);
                QrActivity.this.isCurrentThemeDark = isDark;
                QrActivity qrActivity = QrActivity.this;
                qrActivity.onItemSelected(qrActivity.currentTheme, QrActivity.this.selectedPosition, false);
            }
        };
        this.themesViewController = themeListViewController;
        this.themeLayout = themeListViewController.rootLayout;
        this.themesViewController.onCreate();
        this.themesViewController.setItemSelectedListener(new OnItemSelectedListener() { // from class: org.telegram.ui.QrActivity$$ExternalSyntheticLambda1
            @Override // org.telegram.ui.QrActivity.OnItemSelectedListener
            public final void onItemSelected(EmojiThemes emojiThemes, int i) {
                QrActivity.this.m4519lambda$createView$2$orgtelegramuiQrActivity(emojiThemes, i);
            }
        });
        this.themesViewController.titleView.setText(LocaleController.getString("QrCode", R.string.QrCode));
        this.themesViewController.progressView.setViewType(17);
        this.themesViewController.shareButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.QrActivity$$ExternalSyntheticLambda5
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                QrActivity.this.m4520lambda$createView$3$orgtelegramuiQrActivity(view2);
            }
        });
        rootLayout.addView(this.themeLayout, LayoutHelper.createFrame(-1, -2, 80));
        this.currMotionDrawable.setIndeterminateAnimation(true);
        this.fragmentView = rootLayout;
        onItemSelected(this.currentTheme, 0, false);
        List<EmojiThemes> list = cachedThemes;
        if (list == null || list.isEmpty()) {
            ChatThemeController.requestAllChatThemes(new ResultCallback<List<EmojiThemes>>() { // from class: org.telegram.ui.QrActivity.4
                @Override // org.telegram.tgnet.ResultCallback
                public /* synthetic */ void onError(Throwable th) {
                    ResultCallback.CC.$default$onError(this, th);
                }

                public void onComplete(List<EmojiThemes> result) {
                    QrActivity.this.onDataLoaded(result);
                    List unused = QrActivity.cachedThemes = result;
                }

                @Override // org.telegram.tgnet.ResultCallback
                public void onError(TLRPC.TL_error error) {
                    Toast.makeText(QrActivity.this.getParentActivity(), error.text, 0).show();
                }
            }, true);
        } else {
            onDataLoaded(cachedThemes);
        }
        this.prevSystemUiVisibility = getParentActivity().getWindow().getDecorView().getSystemUiVisibility();
        applyScreenSettings();
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-QrActivity */
    public /* synthetic */ void m4517lambda$createView$0$orgtelegramuiQrActivity(int left, int top, int right, int bottom) {
        this.logoRect.set(left, top, right, bottom);
        this.qrView.requestLayout();
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-QrActivity */
    public /* synthetic */ void m4518lambda$createView$1$orgtelegramuiQrActivity(View v) {
        finishFragment();
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-QrActivity */
    public /* synthetic */ void m4519lambda$createView$2$orgtelegramuiQrActivity(EmojiThemes theme, int position) {
        onItemSelected(theme, position, true);
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-QrActivity */
    public /* synthetic */ void m4520lambda$createView$3$orgtelegramuiQrActivity(View v) {
        this.themesViewController.shareButton.setClickable(false);
        performShare();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        super.onResume();
        applyScreenSettings();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onPause() {
        restoreScreenSettings();
        super.onPause();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        this.themesViewController.onDestroy();
        this.themesViewController = null;
        this.emojiThemeIcon.recycle();
        this.emojiThemeIcon = null;
        for (int i = 0; i < this.emojiThemeDarkIcons.size(); i++) {
            Bitmap bitmap = this.emojiThemeDarkIcons.valueAt(i);
            if (bitmap != null) {
                bitmap.recycle();
            }
        }
        this.emojiThemeDarkIcons.clear();
        restoreScreenSettings();
        super.onFragmentDestroy();
    }

    private void applyScreenSettings() {
        if (getParentActivity() != null) {
            getParentActivity().getWindow().getDecorView().setSystemUiVisibility(this.prevSystemUiVisibility | 1024 | 4);
        }
    }

    private void restoreScreenSettings() {
        if (getParentActivity() != null) {
            getParentActivity().getWindow().getDecorView().setSystemUiVisibility(this.prevSystemUiVisibility);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public int getNavigationBarColor() {
        return getThemedColor(Theme.key_windowBackgroundGray);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public Theme.ResourcesProvider getResourceProvider() {
        return this.resourcesProvider;
    }

    public void onDataLoaded(List<EmojiThemes> result) {
        if (result == null || result.isEmpty() || this.themesViewController == null) {
            return;
        }
        result.set(0, this.homeTheme);
        List<ChatThemeBottomSheet.ChatThemeItem> items = new ArrayList<>(result.size());
        for (int i = 0; i < result.size(); i++) {
            EmojiThemes chatTheme = result.get(i);
            chatTheme.loadPreviewColors(this.currentAccount);
            ChatThemeBottomSheet.ChatThemeItem item = new ChatThemeBottomSheet.ChatThemeItem(chatTheme);
            item.themeIndex = this.isCurrentThemeDark ? 1 : 0;
            item.icon = getEmojiThemeIcon(chatTheme, this.isCurrentThemeDark);
            items.add(item);
        }
        this.themesViewController.adapter.setItems(items);
        int selectedPosition = -1;
        int i2 = 0;
        while (true) {
            if (i2 != items.size()) {
                if (!items.get(i2).chatTheme.getEmoticon().equals(this.currentTheme.getEmoticon())) {
                    i2++;
                } else {
                    this.themesViewController.selectedItem = items.get(i2);
                    selectedPosition = i2;
                    break;
                }
            } else {
                break;
            }
        }
        if (selectedPosition != -1) {
            this.themesViewController.setSelectedPosition(selectedPosition);
        }
        this.themesViewController.onDataLoaded();
    }

    public Bitmap getEmojiThemeIcon(EmojiThemes theme, boolean isDark) {
        if (isDark) {
            Bitmap bitmap = this.emojiThemeDarkIcons.get(theme.emoji);
            if (bitmap == null) {
                bitmap = Bitmap.createBitmap(this.emojiThemeIcon.getWidth(), this.emojiThemeIcon.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                ArrayMap<String, int[]> arrayMap = qrColorsMap;
                int[] colors = arrayMap.get(theme.emoji + "n");
                if (colors != null) {
                    if (this.tempMotionDrawable == null) {
                        this.tempMotionDrawable = new MotionBackgroundDrawable(0, 0, 0, 0, true);
                    }
                    this.tempMotionDrawable.setColors(colors[0], colors[1], colors[2], colors[3]);
                    this.tempMotionDrawable.setBounds(AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), canvas.getWidth() - AndroidUtilities.dp(6.0f), canvas.getHeight() - AndroidUtilities.dp(6.0f));
                    this.tempMotionDrawable.draw(canvas);
                }
                canvas.drawBitmap(this.emojiThemeIcon, 0.0f, 0.0f, (Paint) null);
                canvas.setBitmap(null);
                this.emojiThemeDarkIcons.put(theme.emoji, bitmap);
            }
            return bitmap;
        }
        return this.emojiThemeIcon;
    }

    private void onPatternLoaded(Bitmap bitmap, int intensity, boolean withAnimation) {
        if (bitmap != null) {
            this.currMotionDrawable.setPatternBitmap(intensity, bitmap, true);
            ValueAnimator valueAnimator = this.patternIntensityAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            if (withAnimation) {
                ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
                this.patternIntensityAnimator = ofFloat;
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.QrActivity$$ExternalSyntheticLambda0
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                        QrActivity.this.m4527lambda$onPatternLoaded$4$orgtelegramuiQrActivity(valueAnimator2);
                    }
                });
                this.patternIntensityAnimator.setDuration(250L);
                this.patternIntensityAnimator.start();
                return;
            }
            this.currMotionDrawable.setPatternAlpha(1.0f);
        }
    }

    /* renamed from: lambda$onPatternLoaded$4$org-telegram-ui-QrActivity */
    public /* synthetic */ void m4527lambda$onPatternLoaded$4$orgtelegramuiQrActivity(ValueAnimator animator) {
        this.currMotionDrawable.setPatternAlpha(((Float) animator.getAnimatedValue()).floatValue());
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r4v0, types: [int, boolean] */
    public void onItemSelected(EmojiThemes newTheme, int position, boolean withAnimation) {
        this.selectedPosition = position;
        EmojiThemes prevTheme = this.currentTheme;
        final ?? r4 = this.isCurrentThemeDark;
        this.currentTheme = newTheme;
        EmojiThemes.ThemeItem themeItem = newTheme.getThemeItem(r4 == true ? 1 : 0);
        float duration = 1.0f;
        ValueAnimator valueAnimator = this.patternAlphaAnimator;
        if (valueAnimator != null) {
            duration = 1.0f * Math.max(0.5f, 1.0f - ((Float) valueAnimator.getAnimatedValue()).floatValue());
            this.patternAlphaAnimator.cancel();
        }
        MotionBackgroundDrawable motionBackgroundDrawable = this.currMotionDrawable;
        this.prevMotionDrawable = motionBackgroundDrawable;
        motionBackgroundDrawable.setIndeterminateAnimation(false);
        this.prevMotionDrawable.setAlpha(255);
        MotionBackgroundDrawable motionBackgroundDrawable2 = new MotionBackgroundDrawable();
        this.currMotionDrawable = motionBackgroundDrawable2;
        motionBackgroundDrawable2.setCallback(this.backgroundView);
        this.currMotionDrawable.setColors(themeItem.patternBgColor, themeItem.patternBgGradientColor1, themeItem.patternBgGradientColor2, themeItem.patternBgGradientColor3);
        this.currMotionDrawable.setParentView(this.backgroundView);
        this.currMotionDrawable.setPatternAlpha(1.0f);
        this.currMotionDrawable.setIndeterminateAnimation(true);
        MotionBackgroundDrawable motionBackgroundDrawable3 = this.prevMotionDrawable;
        if (motionBackgroundDrawable3 != null) {
            this.currMotionDrawable.posAnimationProgress = motionBackgroundDrawable3.posAnimationProgress;
        }
        this.qrView.setPosAnimationProgress(this.currMotionDrawable.posAnimationProgress);
        TLRPC.WallPaper wallPaper = this.currentTheme.getWallpaper(r4);
        if (wallPaper == null) {
            ChatThemeController.chatThemeQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.QrActivity$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    QrActivity.this.m4524lambda$onItemSelected$7$orgtelegramuiQrActivity();
                }
            });
        } else {
            this.currMotionDrawable.setPatternBitmap(wallPaper.settings.intensity);
            final long startedLoading = SystemClock.elapsedRealtime();
            this.currentTheme.loadWallpaper(r4, new ResultCallback() { // from class: org.telegram.ui.QrActivity$$ExternalSyntheticLambda10
                @Override // org.telegram.tgnet.ResultCallback
                public final void onComplete(Object obj) {
                    QrActivity.this.m4522lambda$onItemSelected$5$orgtelegramuiQrActivity(r4, startedLoading, (Pair) obj);
                }

                @Override // org.telegram.tgnet.ResultCallback
                public /* synthetic */ void onError(Throwable th) {
                    ResultCallback.CC.$default$onError(this, th);
                }

                @Override // org.telegram.tgnet.ResultCallback
                public /* synthetic */ void onError(TLRPC.TL_error tL_error) {
                    ResultCallback.CC.$default$onError(this, tL_error);
                }
            });
        }
        MotionBackgroundDrawable motionBackgroundDrawable4 = this.currMotionDrawable;
        motionBackgroundDrawable4.setPatternColorFilter(motionBackgroundDrawable4.getPatternColor());
        ArrayMap<String, int[]> arrayMap = qrColorsMap;
        StringBuilder sb = new StringBuilder();
        sb.append(newTheme.emoji);
        sb.append(r4 != 0 ? "n" : Theme.DEFAULT_BACKGROUND_SLUG);
        final int[] newQrColors = arrayMap.get(sb.toString());
        if (withAnimation) {
            this.currMotionDrawable.setAlpha(255);
            this.currMotionDrawable.setBackgroundAlpha(0.0f);
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.patternAlphaAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.QrActivity$$ExternalSyntheticLambda3
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    QrActivity.this.m4525lambda$onItemSelected$8$orgtelegramuiQrActivity(newQrColors, valueAnimator2);
                }
            });
            this.patternAlphaAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.QrActivity.5
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    int[] iArr = newQrColors;
                    if (iArr != null) {
                        System.arraycopy(iArr, 0, QrActivity.this.prevQrColors, 0, 4);
                    }
                    QrActivity.this.prevMotionDrawable = null;
                    QrActivity.this.patternAlphaAnimator = null;
                    QrActivity.this.currMotionDrawable.setBackgroundAlpha(1.0f);
                    QrActivity.this.currMotionDrawable.setPatternAlpha(1.0f);
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animation) {
                    super.onAnimationCancel(animation);
                    float progress = ((Float) ((ValueAnimator) animation).getAnimatedValue()).floatValue();
                    if (newQrColors != null) {
                        int color1 = ColorUtils.blendARGB(QrActivity.this.prevQrColors[0], newQrColors[0], progress);
                        int color2 = ColorUtils.blendARGB(QrActivity.this.prevQrColors[1], newQrColors[1], progress);
                        int color3 = ColorUtils.blendARGB(QrActivity.this.prevQrColors[2], newQrColors[2], progress);
                        int color4 = ColorUtils.blendARGB(QrActivity.this.prevQrColors[3], newQrColors[3], progress);
                        int[] colors = {color1, color2, color3, color4};
                        System.arraycopy(colors, 0, QrActivity.this.prevQrColors, 0, 4);
                    }
                }
            });
            this.patternAlphaAnimator.setDuration((int) (duration * 250.0f));
            this.patternAlphaAnimator.start();
        } else {
            if (newQrColors != null) {
                this.qrView.setColors(newQrColors[0], newQrColors[1], newQrColors[2], newQrColors[3]);
                System.arraycopy(newQrColors, 0, this.prevQrColors, 0, 4);
            }
            this.prevMotionDrawable = null;
            this.backgroundView.invalidate();
        }
        Theme.ThemeInfo currentThemeInfo = this.isCurrentThemeDark ? Theme.getCurrentNightTheme() : Theme.getCurrentTheme();
        ActionBarLayout.ThemeAnimationSettings animationSettings = new ActionBarLayout.ThemeAnimationSettings(null, currentThemeInfo.currentAccentId, this.isCurrentThemeDark, !withAnimation);
        animationSettings.applyTheme = false;
        animationSettings.onlyTopFragment = true;
        animationSettings.resourcesProvider = getResourceProvider();
        animationSettings.duration = (int) (duration * 250.0f);
        if (withAnimation) {
            this.resourcesProvider.initColors(prevTheme, this.isCurrentThemeDark);
        } else {
            this.resourcesProvider.initColors(this.currentTheme, this.isCurrentThemeDark);
        }
        animationSettings.afterStartDescriptionsAddedRunnable = new Runnable() { // from class: org.telegram.ui.QrActivity$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                QrActivity.this.m4526lambda$onItemSelected$9$orgtelegramuiQrActivity();
            }
        };
        this.parentLayout.animateThemedValues(animationSettings);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* renamed from: lambda$onItemSelected$5$org-telegram-ui-QrActivity */
    public /* synthetic */ void m4522lambda$onItemSelected$5$orgtelegramuiQrActivity(boolean isDarkTheme, long startedLoading, Pair pair) {
        if (pair != null && this.currentTheme.getTlTheme(isDarkTheme ? 1 : 0) != null) {
            long themeId = ((Long) pair.first).longValue();
            Bitmap bitmap = (Bitmap) pair.second;
            if (themeId == this.currentTheme.getTlTheme(isDarkTheme).id && bitmap != null) {
                long elapsed = SystemClock.elapsedRealtime() - startedLoading;
                onPatternLoaded(bitmap, this.currMotionDrawable.getIntensity(), elapsed > 150);
            }
        }
    }

    /* renamed from: lambda$onItemSelected$7$org-telegram-ui-QrActivity */
    public /* synthetic */ void m4524lambda$onItemSelected$7$orgtelegramuiQrActivity() {
        final Bitmap bitmap = SvgHelper.getBitmap((int) R.raw.default_pattern, this.backgroundView.getWidth(), this.backgroundView.getHeight(), -16777216);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.QrActivity$$ExternalSyntheticLambda9
            @Override // java.lang.Runnable
            public final void run() {
                QrActivity.this.m4523lambda$onItemSelected$6$orgtelegramuiQrActivity(bitmap);
            }
        });
    }

    /* renamed from: lambda$onItemSelected$6$org-telegram-ui-QrActivity */
    public /* synthetic */ void m4523lambda$onItemSelected$6$orgtelegramuiQrActivity(Bitmap bitmap) {
        onPatternLoaded(bitmap, 34, true);
    }

    /* renamed from: lambda$onItemSelected$8$org-telegram-ui-QrActivity */
    public /* synthetic */ void m4525lambda$onItemSelected$8$orgtelegramuiQrActivity(int[] newQrColors, ValueAnimator animation) {
        float progress = ((Float) animation.getAnimatedValue()).floatValue();
        MotionBackgroundDrawable motionBackgroundDrawable = this.prevMotionDrawable;
        if (motionBackgroundDrawable != null) {
            motionBackgroundDrawable.setBackgroundAlpha(1.0f);
            this.prevMotionDrawable.setPatternAlpha(1.0f - progress);
        }
        this.currMotionDrawable.setBackgroundAlpha(progress);
        this.currMotionDrawable.setPatternAlpha(progress);
        if (newQrColors != null) {
            int color1 = ColorUtils.blendARGB(this.prevQrColors[0], newQrColors[0], progress);
            int color2 = ColorUtils.blendARGB(this.prevQrColors[1], newQrColors[1], progress);
            int color3 = ColorUtils.blendARGB(this.prevQrColors[2], newQrColors[2], progress);
            int color4 = ColorUtils.blendARGB(this.prevQrColors[3], newQrColors[3], progress);
            this.qrView.setColors(color1, color2, color3, color4);
        }
        this.backgroundView.invalidate();
    }

    /* renamed from: lambda$onItemSelected$9$org-telegram-ui-QrActivity */
    public /* synthetic */ void m4526lambda$onItemSelected$9$orgtelegramuiQrActivity() {
        this.resourcesProvider.initColors(this.currentTheme, this.isCurrentThemeDark);
    }

    private void performShare() {
        int width = Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y);
        int height = Math.max(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y);
        if ((height * 1.0f) / width > 1.92f) {
            height = (int) (width * 1.92f);
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        this.themeLayout.setVisibility(8);
        this.closeImageView.setVisibility(8);
        this.logoImageView.stopAnimation();
        RLottieDrawable drawable = this.logoImageView.getAnimatedDrawable();
        int currentFrame = drawable.getCurrentFrame();
        drawable.setCurrentFrame(33, false);
        this.fragmentView.measure(View.MeasureSpec.makeMeasureSpec(width, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(height, C.BUFFER_FLAG_ENCRYPTED));
        this.fragmentView.layout(0, 0, width, height);
        this.fragmentView.draw(canvas);
        canvas.setBitmap(null);
        this.themeLayout.setVisibility(0);
        this.closeImageView.setVisibility(0);
        drawable.setCurrentFrame(currentFrame, false);
        this.logoImageView.playAnimation();
        ViewGroup parent = (ViewGroup) this.fragmentView.getParent();
        this.fragmentView.layout(0, 0, parent.getWidth(), parent.getHeight());
        Uri uri = AndroidUtilities.getBitmapShareUri(bitmap, "qr_tmp.jpg", Bitmap.CompressFormat.JPEG);
        if (uri != null) {
            Intent intent = new Intent("android.intent.action.SEND").setType("image/*").putExtra("android.intent.extra.STREAM", uri);
            try {
                Intent chooserIntent = Intent.createChooser(intent, LocaleController.getString("InviteByQRCode", R.string.InviteByQRCode));
                getParentActivity().startActivityForResult(chooserIntent, 500);
            } catch (ActivityNotFoundException ex) {
                ex.printStackTrace();
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.QrActivity$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                QrActivity.this.m4528lambda$performShare$10$orgtelegramuiQrActivity();
            }
        }, 500L);
    }

    /* renamed from: lambda$performShare$10$org-telegram-ui-QrActivity */
    public /* synthetic */ void m4528lambda$performShare$10$orgtelegramuiQrActivity() {
        this.themesViewController.shareButton.setClickable(true);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = super.getThemeDescriptions();
        themeDescriptions.addAll(this.themesViewController.getThemeDescriptions());
        ThemeDescription.ThemeDescriptionDelegate delegate = new ThemeDescription.ThemeDescriptionDelegate() { // from class: org.telegram.ui.QrActivity$$ExternalSyntheticLambda11
            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public final void didSetColor() {
                QrActivity.this.m4521lambda$getThemeDescriptions$11$orgtelegramuiQrActivity();
            }

            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public /* synthetic */ void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        };
        themeDescriptions.add(new ThemeDescription(this.themesViewController.shareButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, delegate, Theme.key_featuredStickers_addButton));
        themeDescriptions.add(new ThemeDescription(this.themesViewController.shareButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_featuredStickers_addButtonPressed));
        Iterator<ThemeDescription> it = themeDescriptions.iterator();
        while (it.hasNext()) {
            ThemeDescription description = it.next();
            description.resourcesProvider = getResourceProvider();
        }
        return themeDescriptions;
    }

    /* renamed from: lambda$getThemeDescriptions$11$org-telegram-ui-QrActivity */
    public /* synthetic */ void m4521lambda$getThemeDescriptions$11$orgtelegramuiQrActivity() {
        setNavigationBarColor(getThemedColor(Theme.key_windowBackgroundGray));
    }

    /* loaded from: classes4.dex */
    public class ThemeResourcesProvider implements Theme.ResourcesProvider {
        private HashMap<String, Integer> colors;

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
        public /* synthetic */ Drawable getDrawable(String str) {
            return Theme.ResourcesProvider.CC.$default$getDrawable(this, str);
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

        private ThemeResourcesProvider() {
            QrActivity.this = r1;
        }

        void initColors(EmojiThemes theme, boolean isDark) {
            this.colors = theme.createColors(QrActivity.this.currentAccount, isDark ? 1 : 0);
        }

        @Override // org.telegram.ui.ActionBar.Theme.ResourcesProvider
        public Integer getColor(String key) {
            HashMap<String, Integer> hashMap = this.colors;
            if (hashMap != null) {
                return hashMap.get(key);
            }
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static class QrView extends View {
        private Bitmap backgroundBitmap;
        private final Paint bitmapGradientPaint;
        private QrCenterChangedListener centerChangedListener;
        private Bitmap contentBitmap;
        private final MotionBackgroundDrawable gradientDrawable;
        private final BitmapShader gradientShader;
        private boolean isPhone;
        private String link;
        private String username;
        private static final float SHADOW_SIZE = AndroidUtilities.dp(2.0f);
        private static final float RADIUS = AndroidUtilities.dp(20.0f);

        /* loaded from: classes4.dex */
        public interface QrCenterChangedListener {
            void onCenterChanged(int i, int i2, int i3, int i4);
        }

        QrView(Context context) {
            super(context);
            MotionBackgroundDrawable motionBackgroundDrawable = new MotionBackgroundDrawable();
            this.gradientDrawable = motionBackgroundDrawable;
            Paint paint = new Paint(1);
            this.bitmapGradientPaint = paint;
            motionBackgroundDrawable.setIndeterminateAnimation(true);
            motionBackgroundDrawable.setParentView(this);
            BitmapShader bitmapShader = new BitmapShader(motionBackgroundDrawable.getBitmap(), Shader.TileMode.MIRROR, Shader.TileMode.MIRROR);
            this.gradientShader = bitmapShader;
            paint.setShader(bitmapShader);
        }

        @Override // android.view.View
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            if (w != oldw || h != oldh) {
                Bitmap bitmap = this.backgroundBitmap;
                if (bitmap != null) {
                    bitmap.recycle();
                    this.backgroundBitmap = null;
                }
                Paint backgroundPaint = new Paint(1);
                backgroundPaint.setColor(-1);
                float f = SHADOW_SIZE;
                backgroundPaint.setShadowLayer(AndroidUtilities.dp(4.0f), 0.0f, f, AndroidUtilities.LIGHT_STATUS_BAR_OVERLAY);
                this.backgroundBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(this.backgroundBitmap);
                RectF rect = new RectF(f, f, w - f, getHeight() - f);
                float f2 = RADIUS;
                canvas.drawRoundRect(rect, f2, f2, backgroundPaint);
                prepareContent(w, h);
                float xScale = (getWidth() * 1.0f) / this.gradientDrawable.getBitmap().getWidth();
                float yScale = (getHeight() * 1.0f) / this.gradientDrawable.getBitmap().getHeight();
                float maxScale = Math.max(xScale, yScale);
                Matrix matrix = new Matrix();
                matrix.setScale(maxScale, maxScale);
                this.gradientShader.setLocalMatrix(matrix);
            }
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            Bitmap bitmap = this.backgroundBitmap;
            if (bitmap != null) {
                canvas.drawBitmap(bitmap, 0.0f, 0.0f, (Paint) null);
            }
            Bitmap bitmap2 = this.contentBitmap;
            if (bitmap2 != null) {
                canvas.drawBitmap(bitmap2, 0.0f, 0.0f, this.bitmapGradientPaint);
                this.gradientDrawable.updateAnimation(true);
            }
        }

        void setCenterChangedListener(QrCenterChangedListener centerChangedListener) {
            this.centerChangedListener = centerChangedListener;
        }

        void setData(String link, String username, boolean isPhone) {
            this.username = username;
            this.isPhone = isPhone;
            this.link = link;
            prepareContent(getWidth(), getHeight());
            invalidate();
        }

        void setColors(int c1, int c2, int c3, int c4) {
            this.gradientDrawable.setColors(c1, c2, c3, c4);
            invalidate();
        }

        void setPosAnimationProgress(float progress) {
            this.gradientDrawable.posAnimationProgress = progress;
        }

        /* JADX WARN: Removed duplicated region for block: B:61:0x0229 A[RETURN] */
        /* JADX WARN: Removed duplicated region for block: B:62:0x022a  */
        /* JADX WARN: Removed duplicated region for block: B:78:0x01c2 A[EXC_TOP_SPLITTER, SYNTHETIC] */
        /* JADX WARN: Removed duplicated region for block: B:85:0x0217 A[SYNTHETIC] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        private void prepareContent(int r31, int r32) {
            /*
                Method dump skipped, instructions count: 784
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.QrActivity.QrView.prepareContent(int, int):void");
        }
    }

    /* loaded from: classes4.dex */
    public class ThemeListViewController implements NotificationCenter.NotificationCenterDelegate {
        public final ChatThemeBottomSheet.Adapter adapter;
        private final Drawable backgroundDrawable;
        private final View bottomShadow;
        private View changeDayNightView;
        private ValueAnimator changeDayNightViewAnimator;
        private float changeDayNightViewProgress;
        private final RLottieDrawable darkThemeDrawable;
        private final RLottieImageView darkThemeView;
        private final BaseFragment fragment;
        protected boolean isLightDarkChangeAnimation;
        private OnItemSelectedListener itemSelectedListener;
        private LinearLayoutManager layoutManager;
        private boolean prevIsPortrait;
        public final FlickerLoadingView progressView;
        private final RecyclerListView recyclerView;
        public final FrameLayout rootLayout;
        private final LinearSmoothScroller scroller;
        public ChatThemeBottomSheet.ChatThemeItem selectedItem;
        public final TextView shareButton;
        public final TextView titleView;
        private final View topShadow;
        private final Window window;
        private final Paint backgroundPaint = new Paint(1);
        public int prevSelectedPosition = -1;
        private boolean forceDark = !Theme.getActiveTheme().isDark();

        public ThemeListViewController(BaseFragment fragment, Window window) {
            QrActivity.this = r28;
            this.fragment = fragment;
            this.window = window;
            Context context = fragment.getParentActivity();
            this.scroller = new LinearSmoothScroller(context) { // from class: org.telegram.ui.QrActivity.ThemeListViewController.1
                @Override // androidx.recyclerview.widget.LinearSmoothScroller
                public int calculateTimeForScrolling(int dx) {
                    return super.calculateTimeForScrolling(dx) * 6;
                }
            };
            Drawable mutate = context.getResources().getDrawable(R.drawable.sheet_shadow_round).mutate();
            this.backgroundDrawable = mutate;
            mutate.setColorFilter(new PorterDuffColorFilter(fragment.getThemedColor(Theme.key_dialogBackground), PorterDuff.Mode.MULTIPLY));
            FrameLayout frameLayout = new FrameLayout(context, r28, fragment) { // from class: org.telegram.ui.QrActivity.ThemeListViewController.2
                private final Rect backgroundPadding;
                final /* synthetic */ BaseFragment val$fragment;
                final /* synthetic */ QrActivity val$this$0;

                {
                    ThemeListViewController.this = this;
                    this.val$fragment = fragment;
                    Rect rect = new Rect();
                    this.backgroundPadding = rect;
                    this.backgroundPaint.setColor(fragment.getThemedColor(Theme.key_windowBackgroundWhite));
                    this.backgroundDrawable.setCallback(this);
                    this.backgroundDrawable.getPadding(rect);
                    setPadding(0, rect.top + AndroidUtilities.dp(8.0f), 0, rect.bottom);
                }

                @Override // android.widget.FrameLayout, android.view.View
                protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    boolean isPortrait = AndroidUtilities.displaySize.x < AndroidUtilities.displaySize.y;
                    int recyclerPadding = AndroidUtilities.dp(12.0f);
                    if (isPortrait) {
                        ThemeListViewController.this.recyclerView.setLayoutParams(LayoutHelper.createFrame(-1, 104.0f, GravityCompat.START, 0.0f, 44.0f, 0.0f, 0.0f));
                        ThemeListViewController.this.recyclerView.setPadding(recyclerPadding, 0, recyclerPadding, 0);
                        ThemeListViewController.this.shareButton.setLayoutParams(LayoutHelper.createFrame(-1, 48.0f, GravityCompat.START, 16.0f, 162.0f, 16.0f, 16.0f));
                    } else {
                        ThemeListViewController.this.recyclerView.setLayoutParams(LayoutHelper.createFrame(-1, -1.0f, GravityCompat.START, 0.0f, 44.0f, 0.0f, 80.0f));
                        ThemeListViewController.this.recyclerView.setPadding(recyclerPadding, recyclerPadding / 2, recyclerPadding, recyclerPadding);
                        ThemeListViewController.this.shareButton.setLayoutParams(LayoutHelper.createFrame(-1, 48.0f, 80, 16.0f, 0.0f, 16.0f, 16.0f));
                    }
                    if (isPortrait) {
                        ThemeListViewController.this.bottomShadow.setVisibility(8);
                        ThemeListViewController.this.topShadow.setVisibility(8);
                    } else {
                        ThemeListViewController.this.bottomShadow.setVisibility(0);
                        ThemeListViewController.this.bottomShadow.setLayoutParams(LayoutHelper.createFrame(-1, AndroidUtilities.dp(2.0f), 80, 0.0f, 0.0f, 0.0f, 80.0f));
                        ThemeListViewController.this.topShadow.setVisibility(0);
                        ThemeListViewController.this.topShadow.setLayoutParams(LayoutHelper.createFrame(-1, AndroidUtilities.dp(2.0f), 48, 0.0f, 44.0f, 0.0f, 0.0f));
                    }
                    if (ThemeListViewController.this.prevIsPortrait != isPortrait) {
                        RecyclerListView recyclerListView = ThemeListViewController.this.recyclerView;
                        ThemeListViewController themeListViewController = ThemeListViewController.this;
                        recyclerListView.setLayoutManager(themeListViewController.layoutManager = themeListViewController.getLayoutManager(isPortrait));
                        ThemeListViewController.this.recyclerView.requestLayout();
                        if (ThemeListViewController.this.prevSelectedPosition != -1) {
                            ThemeListViewController themeListViewController2 = ThemeListViewController.this;
                            themeListViewController2.setSelectedPosition(themeListViewController2.prevSelectedPosition);
                        }
                        ThemeListViewController.this.prevIsPortrait = isPortrait;
                    }
                    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                }

                @Override // android.view.ViewGroup, android.view.View
                protected void dispatchDraw(Canvas canvas) {
                    if (ThemeListViewController.this.prevIsPortrait) {
                        ThemeListViewController.this.backgroundDrawable.setBounds(-this.backgroundPadding.left, 0, getWidth() + this.backgroundPadding.right, getHeight());
                        ThemeListViewController.this.backgroundDrawable.draw(canvas);
                    } else {
                        AndroidUtilities.rectTmp.set(0.0f, 0.0f, getWidth() + AndroidUtilities.dp(14.0f), getHeight());
                        canvas.drawRoundRect(AndroidUtilities.rectTmp, AndroidUtilities.dp(14.0f), AndroidUtilities.dp(14.0f), ThemeListViewController.this.backgroundPaint);
                    }
                    super.dispatchDraw(canvas);
                }

                @Override // android.view.View
                protected boolean verifyDrawable(Drawable who) {
                    return who == ThemeListViewController.this.backgroundDrawable || super.verifyDrawable(who);
                }
            };
            this.rootLayout = frameLayout;
            TextView textView = new TextView(context);
            this.titleView = textView;
            textView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
            textView.setLines(1);
            textView.setSingleLine(true);
            textView.setTextColor(fragment.getThemedColor(Theme.key_dialogTextBlack));
            textView.setTextSize(1, 20.0f);
            textView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            textView.setPadding(AndroidUtilities.dp(21.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(21.0f), AndroidUtilities.dp(8.0f));
            frameLayout.addView(textView, LayoutHelper.createFrame(-1, -2.0f, 8388659, 0.0f, 0.0f, 62.0f, 0.0f));
            int drawableColor = fragment.getThemedColor(Theme.key_featuredStickers_addButton);
            int drawableSize = AndroidUtilities.dp(28.0f);
            RLottieDrawable rLottieDrawable = new RLottieDrawable(R.raw.sun_outline, "2131558541", drawableSize, drawableSize, false, null);
            this.darkThemeDrawable = rLottieDrawable;
            setForceDark(Theme.getActiveTheme().isDark(), false);
            rLottieDrawable.setPlayInDirectionOfCustomEndFrame(true);
            rLottieDrawable.setColorFilter(new PorterDuffColorFilter(drawableColor, PorterDuff.Mode.MULTIPLY));
            RLottieImageView rLottieImageView = new RLottieImageView(context) { // from class: org.telegram.ui.QrActivity.ThemeListViewController.3
                @Override // android.view.View
                public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
                    super.onInitializeAccessibilityNodeInfo(info);
                    if (QrActivity.this.isCurrentThemeDark) {
                        info.setText(LocaleController.getString("AccDescrSwitchToDayTheme", R.string.AccDescrSwitchToDayTheme));
                    } else {
                        info.setText(LocaleController.getString("AccDescrSwitchToNightTheme", R.string.AccDescrSwitchToNightTheme));
                    }
                }
            };
            this.darkThemeView = rLottieImageView;
            rLottieImageView.setAnimation(rLottieDrawable);
            rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
            rLottieImageView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.QrActivity$ThemeListViewController$$ExternalSyntheticLambda1
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    QrActivity.ThemeListViewController.this.m4529lambda$new$0$orgtelegramuiQrActivity$ThemeListViewController(view);
                }
            });
            rLottieImageView.setAlpha(0.0f);
            rLottieImageView.setVisibility(4);
            frameLayout.addView(rLottieImageView, LayoutHelper.createFrame(44, 44.0f, 8388661, 0.0f, -2.0f, 7.0f, 0.0f));
            FlickerLoadingView flickerLoadingView = new FlickerLoadingView(context, fragment.getResourceProvider());
            this.progressView = flickerLoadingView;
            flickerLoadingView.setVisibility(0);
            frameLayout.addView(flickerLoadingView, LayoutHelper.createFrame(-1, 104.0f, GravityCompat.START, 0.0f, 44.0f, 0.0f, 0.0f));
            this.prevIsPortrait = AndroidUtilities.displaySize.x < AndroidUtilities.displaySize.y;
            RecyclerListView recyclerListView = new RecyclerListView(context);
            this.recyclerView = recyclerListView;
            ChatThemeBottomSheet.Adapter adapter = new ChatThemeBottomSheet.Adapter(r28.currentAccount, r28.resourcesProvider, 2);
            this.adapter = adapter;
            recyclerListView.setAdapter(adapter);
            recyclerListView.setClipChildren(false);
            recyclerListView.setClipToPadding(false);
            recyclerListView.setItemAnimator(null);
            recyclerListView.setNestedScrollingEnabled(false);
            LinearLayoutManager layoutManager = getLayoutManager(this.prevIsPortrait);
            this.layoutManager = layoutManager;
            recyclerListView.setLayoutManager(layoutManager);
            recyclerListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.QrActivity$ThemeListViewController$$ExternalSyntheticLambda4
                @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
                public final void onItemClick(View view, int i) {
                    QrActivity.ThemeListViewController.this.onItemClicked(view, i);
                }
            });
            recyclerListView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.QrActivity.ThemeListViewController.4
                private int yScroll = 0;

                @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    this.yScroll += dy;
                    ThemeListViewController.this.topShadow.setAlpha((this.yScroll * 1.0f) / AndroidUtilities.dp(6.0f));
                }
            });
            frameLayout.addView(recyclerListView);
            View view = new View(context);
            this.topShadow = view;
            view.setAlpha(0.0f);
            view.setBackground(ContextCompat.getDrawable(context, R.drawable.shadowdown));
            view.setRotation(180.0f);
            frameLayout.addView(view);
            View view2 = new View(context);
            this.bottomShadow = view2;
            view2.setBackground(ContextCompat.getDrawable(context, R.drawable.shadowdown));
            frameLayout.addView(view2);
            TextView textView2 = new TextView(context);
            this.shareButton = textView2;
            textView2.setBackground(Theme.AdaptiveRipple.filledRect(fragment.getThemedColor(Theme.key_featuredStickers_addButton), 6.0f));
            textView2.setEllipsize(TextUtils.TruncateAt.END);
            textView2.setGravity(17);
            textView2.setLines(1);
            textView2.setSingleLine(true);
            textView2.setText(LocaleController.getString("ShareQrCode", R.string.ShareQrCode));
            textView2.setTextColor(fragment.getThemedColor(Theme.key_featuredStickers_buttonText));
            textView2.setTextSize(1, 15.0f);
            textView2.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            frameLayout.addView(textView2);
        }

        /* renamed from: lambda$new$0$org-telegram-ui-QrActivity$ThemeListViewController */
        public /* synthetic */ void m4529lambda$new$0$orgtelegramuiQrActivity$ThemeListViewController(View view) {
            if (this.changeDayNightViewAnimator != null) {
                return;
            }
            setupLightDarkTheme(!this.forceDark);
        }

        public void onCreate() {
            ChatThemeController.preloadAllWallpaperThumbs(true);
            ChatThemeController.preloadAllWallpaperThumbs(false);
            ChatThemeController.preloadAllWallpaperImages(true);
            ChatThemeController.preloadAllWallpaperImages(false);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        }

        @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
        public void didReceivedNotification(int id, int account, Object... args) {
            if (id == NotificationCenter.emojiLoaded) {
                this.adapter.notifyDataSetChanged();
            }
        }

        public void onDestroy() {
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
        }

        public void setItemSelectedListener(OnItemSelectedListener itemSelectedListener) {
            this.itemSelectedListener = itemSelectedListener;
        }

        public void onDataLoaded() {
            this.darkThemeView.setAlpha(0.0f);
            this.darkThemeView.animate().alpha(1.0f).setDuration(150L).start();
            this.darkThemeView.setVisibility(0);
            this.progressView.animate().alpha(0.0f).setListener(new HideViewAfterAnimation(this.progressView)).setDuration(150L).start();
            this.recyclerView.setAlpha(0.0f);
            this.recyclerView.animate().alpha(1.0f).setDuration(150L).start();
        }

        public void setSelectedPosition(int selectedPosition) {
            this.prevSelectedPosition = selectedPosition;
            this.adapter.setSelectedItem(selectedPosition);
            if (selectedPosition > 0 && selectedPosition < this.adapter.items.size() / 2) {
                selectedPosition--;
            }
            int finalSelectedPosition = Math.min(selectedPosition, this.adapter.items.size() - 1);
            this.layoutManager.scrollToPositionWithOffset(finalSelectedPosition, 0);
        }

        public void onItemClicked(View view, final int position) {
            if (this.adapter.items.get(position) == this.selectedItem || this.changeDayNightView != null) {
                return;
            }
            this.isLightDarkChangeAnimation = false;
            this.selectedItem = this.adapter.items.get(position);
            this.adapter.setSelectedItem(position);
            this.rootLayout.postDelayed(new Runnable() { // from class: org.telegram.ui.QrActivity$ThemeListViewController$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    QrActivity.ThemeListViewController.this.m4530xb3111e29(position);
                }
            }, 100L);
            for (int i = 0; i < this.recyclerView.getChildCount(); i++) {
                ThemeSmallPreviewView child = (ThemeSmallPreviewView) this.recyclerView.getChildAt(i);
                if (child != view) {
                    child.cancelAnimation();
                }
            }
            if (!this.adapter.items.get(position).chatTheme.showAsDefaultStub) {
                ((ThemeSmallPreviewView) view).playEmojiAnimation();
            }
            OnItemSelectedListener onItemSelectedListener = this.itemSelectedListener;
            if (onItemSelectedListener != null) {
                onItemSelectedListener.onItemSelected(this.selectedItem.chatTheme, position);
            }
        }

        /* renamed from: lambda$onItemClicked$1$org-telegram-ui-QrActivity$ThemeListViewController */
        public /* synthetic */ void m4530xb3111e29(int position) {
            int targetPosition;
            RecyclerView.LayoutManager layoutManager = this.recyclerView.getLayoutManager();
            if (layoutManager != null) {
                if (position > this.prevSelectedPosition) {
                    targetPosition = Math.min(position + 1, this.adapter.items.size() - 1);
                } else {
                    targetPosition = Math.max(position - 1, 0);
                }
                this.scroller.setTargetPosition(targetPosition);
                layoutManager.startSmoothScroll(this.scroller);
            }
            this.prevSelectedPosition = position;
        }

        private void setupLightDarkTheme(final boolean isDark) {
            ValueAnimator valueAnimator = this.changeDayNightViewAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            FrameLayout decorView1 = (FrameLayout) this.fragment.getParentActivity().getWindow().getDecorView();
            FrameLayout decorView2 = (FrameLayout) this.window.getDecorView();
            final Bitmap bitmap = Bitmap.createBitmap(decorView2.getWidth(), decorView2.getHeight(), Bitmap.Config.ARGB_8888);
            final Canvas bitmapCanvas = new Canvas(bitmap);
            this.darkThemeView.setAlpha(0.0f);
            decorView1.draw(bitmapCanvas);
            decorView2.draw(bitmapCanvas);
            this.darkThemeView.setAlpha(1.0f);
            final Paint xRefPaint = new Paint(1);
            xRefPaint.setColor(-16777216);
            xRefPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            final Paint bitmapPaint = new Paint(1);
            bitmapPaint.setFilterBitmap(true);
            int[] position = new int[2];
            this.darkThemeView.getLocationInWindow(position);
            final float x = position[0];
            final float y = position[1];
            final float cx = x + (this.darkThemeView.getMeasuredWidth() / 2.0f);
            final float cy = y + (this.darkThemeView.getMeasuredHeight() / 2.0f);
            final float r = Math.max(bitmap.getHeight(), bitmap.getWidth()) * 0.9f;
            Shader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            bitmapPaint.setShader(bitmapShader);
            this.changeDayNightView = new View(this.fragment.getParentActivity()) { // from class: org.telegram.ui.QrActivity.ThemeListViewController.5
                @Override // android.view.View
                protected void onDraw(Canvas canvas) {
                    super.onDraw(canvas);
                    if (isDark) {
                        if (ThemeListViewController.this.changeDayNightViewProgress > 0.0f) {
                            bitmapCanvas.drawCircle(cx, cy, r * ThemeListViewController.this.changeDayNightViewProgress, xRefPaint);
                        }
                        canvas.drawBitmap(bitmap, 0.0f, 0.0f, bitmapPaint);
                    } else {
                        canvas.drawCircle(cx, cy, r * (1.0f - ThemeListViewController.this.changeDayNightViewProgress), bitmapPaint);
                    }
                    canvas.save();
                    canvas.translate(x, y);
                    ThemeListViewController.this.darkThemeView.draw(canvas);
                    canvas.restore();
                }
            };
            this.changeDayNightViewProgress = 0.0f;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.changeDayNightViewAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.QrActivity$ThemeListViewController$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    QrActivity.ThemeListViewController.this.m4531x3f7022c3(valueAnimator2);
                }
            });
            this.changeDayNightViewAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.QrActivity.ThemeListViewController.6
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    if (ThemeListViewController.this.changeDayNightView != null) {
                        if (ThemeListViewController.this.changeDayNightView.getParent() != null) {
                            ((ViewGroup) ThemeListViewController.this.changeDayNightView.getParent()).removeView(ThemeListViewController.this.changeDayNightView);
                        }
                        ThemeListViewController.this.changeDayNightView = null;
                    }
                    ThemeListViewController.this.changeDayNightViewAnimator = null;
                    super.onAnimationEnd(animation);
                }
            });
            this.changeDayNightViewAnimator.setDuration(400L);
            this.changeDayNightViewAnimator.setInterpolator(Easings.easeInOutQuad);
            this.changeDayNightViewAnimator.start();
            decorView2.addView(this.changeDayNightView, new ViewGroup.LayoutParams(-1, -1));
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.QrActivity$ThemeListViewController$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    QrActivity.ThemeListViewController.this.m4532x6d48bd22(isDark);
                }
            });
        }

        /* renamed from: lambda$setupLightDarkTheme$2$org-telegram-ui-QrActivity$ThemeListViewController */
        public /* synthetic */ void m4531x3f7022c3(ValueAnimator valueAnimator) {
            this.changeDayNightViewProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            this.changeDayNightView.invalidate();
        }

        /* renamed from: lambda$setupLightDarkTheme$3$org-telegram-ui-QrActivity$ThemeListViewController */
        public /* synthetic */ void m4532x6d48bd22(boolean isDark) {
            ChatThemeBottomSheet.Adapter adapter = this.adapter;
            if (adapter == null || adapter.items == null) {
                return;
            }
            setForceDark(isDark, true);
            if (this.selectedItem != null) {
                this.isLightDarkChangeAnimation = true;
                setDarkTheme(isDark);
            }
            if (this.adapter.items != null) {
                for (int i = 0; i < this.adapter.items.size(); i++) {
                    this.adapter.items.get(i).themeIndex = isDark ? 1 : 0;
                    this.adapter.items.get(i).icon = QrActivity.this.getEmojiThemeIcon(this.adapter.items.get(i).chatTheme, isDark);
                }
                QrActivity.this.tempMotionDrawable = null;
                this.adapter.notifyDataSetChanged();
            }
        }

        protected void setDarkTheme(boolean isDark) {
        }

        public void setForceDark(boolean isDark, boolean playAnimation) {
            if (this.forceDark == isDark) {
                return;
            }
            this.forceDark = isDark;
            int frame = isDark ? this.darkThemeDrawable.getFramesCount() - 1 : 0;
            if (playAnimation) {
                this.darkThemeDrawable.setCustomEndFrame(frame);
                RLottieImageView rLottieImageView = this.darkThemeView;
                if (rLottieImageView != null) {
                    rLottieImageView.playAnimation();
                    return;
                }
                return;
            }
            this.darkThemeDrawable.setCustomEndFrame(frame);
            this.darkThemeDrawable.setCurrentFrame(frame, false, true);
            RLottieImageView rLottieImageView2 = this.darkThemeView;
            if (rLottieImageView2 != null) {
                rLottieImageView2.invalidate();
            }
        }

        public LinearLayoutManager getLayoutManager(boolean isPortrait) {
            if (isPortrait) {
                return new LinearLayoutManager(this.fragment.getParentActivity(), 0, false);
            }
            return new GridLayoutManager(this.fragment.getParentActivity(), 3, 1, false);
        }

        public void onAnimationStart() {
            ChatThemeBottomSheet.Adapter adapter = this.adapter;
            if (adapter != null && adapter.items != null) {
                for (ChatThemeBottomSheet.ChatThemeItem item : this.adapter.items) {
                    item.themeIndex = this.forceDark ? 1 : 0;
                }
            }
            if (!this.isLightDarkChangeAnimation) {
                setItemsAnimationProgress(1.0f);
            }
        }

        public void setItemsAnimationProgress(float progress) {
            for (int i = 0; i < this.adapter.getItemCount(); i++) {
                this.adapter.items.get(i).animationProgress = progress;
            }
        }

        public void onAnimationEnd() {
            this.isLightDarkChangeAnimation = false;
        }

        public ArrayList<ThemeDescription> getThemeDescriptions() {
            ThemeDescription.ThemeDescriptionDelegate descriptionDelegate = new ThemeDescription.ThemeDescriptionDelegate() { // from class: org.telegram.ui.QrActivity.ThemeListViewController.7
                private boolean isAnimationStarted = false;

                @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
                public void onAnimationProgress(float progress) {
                    if (progress == 0.0f && !this.isAnimationStarted) {
                        ThemeListViewController.this.onAnimationStart();
                        this.isAnimationStarted = true;
                    }
                    ThemeListViewController.this.darkThemeDrawable.setColorFilter(new PorterDuffColorFilter(ThemeListViewController.this.fragment.getThemedColor(Theme.key_featuredStickers_addButton), PorterDuff.Mode.MULTIPLY));
                    if (ThemeListViewController.this.isLightDarkChangeAnimation) {
                        ThemeListViewController.this.setItemsAnimationProgress(progress);
                    }
                    if (progress == 1.0f && this.isAnimationStarted) {
                        ThemeListViewController.this.isLightDarkChangeAnimation = false;
                        ThemeListViewController.this.onAnimationEnd();
                        this.isAnimationStarted = false;
                    }
                }

                @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
                public void didSetColor() {
                }
            };
            ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
            themeDescriptions.add(new ThemeDescription(null, ThemeDescription.FLAG_BACKGROUND, null, this.backgroundPaint, null, null, Theme.key_dialogBackground));
            themeDescriptions.add(new ThemeDescription(null, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, new Drawable[]{this.backgroundDrawable}, descriptionDelegate, Theme.key_dialogBackground));
            themeDescriptions.add(new ThemeDescription(this.titleView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_dialogTextBlack));
            themeDescriptions.add(new ThemeDescription(this.recyclerView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ThemeSmallPreviewView.class}, null, null, null, Theme.key_dialogBackgroundGray));
            Iterator<ThemeDescription> it = themeDescriptions.iterator();
            while (it.hasNext()) {
                ThemeDescription description = it.next();
                description.resourcesProvider = this.fragment.getResourceProvider();
            }
            return themeDescriptions;
        }
    }
}
