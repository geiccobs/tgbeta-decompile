package org.telegram.ui;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.C;
import com.google.firebase.appindexing.builders.TimerBuilder;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.time.SunDate;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.EmojiThemes;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.AppIconsSelectorCell;
import org.telegram.ui.Cells.BrightnessControlCell;
import org.telegram.ui.Cells.ChatListCell;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.NotificationsCheckCell;
import org.telegram.ui.Cells.RadioButtonCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Cells.ThemePreviewMessagesCell;
import org.telegram.ui.Cells.ThemeTypeCell;
import org.telegram.ui.Cells.ThemesHorizontalListCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SeekBarView;
import org.telegram.ui.Components.ShareAlert;
import org.telegram.ui.Components.SimpleThemeDescription;
import org.telegram.ui.Components.SwipeGestureSettingsView;
import org.telegram.ui.Components.ThemeEditorView;
import org.telegram.ui.ThemeActivity;
/* loaded from: classes4.dex */
public class ThemeActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    public static final int THEME_TYPE_BASIC = 0;
    public static final int THEME_TYPE_NIGHT = 1;
    public static final int THEME_TYPE_OTHER = 2;
    public static final int THEME_TYPE_THEMES_BROWSER = 3;
    private static final int create_theme = 1;
    private static final int day_night_switch = 5;
    private static final int edit_theme = 3;
    private static final int reset_settings = 4;
    private static final int share_theme = 2;
    private int appIconHeaderRow;
    private int appIconSelectorRow;
    private int appIconShadowRow;
    private int automaticBrightnessInfoRow;
    private int automaticBrightnessRow;
    private int automaticHeaderRow;
    private int backgroundRow;
    private int bubbleRadiusHeaderRow;
    private int bubbleRadiusInfoRow;
    private int bubbleRadiusRow;
    private int chatBlurRow;
    private int chatListHeaderRow;
    private int chatListInfoRow;
    private int chatListRow;
    private int contactsReimportRow;
    private int contactsSortRow;
    private int createNewThemeRow;
    private int currentType;
    private int customTabsRow;
    private int directShareRow;
    private int distanceRow;
    private int editThemeRow;
    private int enableAnimationsRow;
    boolean hasThemeAccents;
    boolean lastIsDarkTheme;
    private LinearLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private ActionBarMenuItem menuItem;
    private int newThemeInfoRow;
    private int nightAutomaticRow;
    private int nightDisabledRow;
    private int nightScheduledRow;
    private int nightSystemDefaultRow;
    private int nightThemeRow;
    private int nightTypeInfoRow;
    private int preferedHeaderRow;
    private boolean previousByLocation;
    private int previousUpdatedType;
    private int raiseToSpeakRow;
    private int rowCount;
    private int saveToGalleryOption1Row;
    private int saveToGalleryOption2Row;
    private int saveToGallerySectionRow;
    private int scheduleFromRow;
    private int scheduleFromToInfoRow;
    private int scheduleHeaderRow;
    private int scheduleLocationInfoRow;
    private int scheduleLocationRow;
    private int scheduleToRow;
    private int scheduleUpdateLocationRow;
    private int selectThemeHeaderRow;
    private int sendByEnterRow;
    private int settings2Row;
    private int settingsRow;
    private Theme.ThemeAccent sharingAccent;
    private AlertDialog sharingProgressDialog;
    private Theme.ThemeInfo sharingTheme;
    private RLottieDrawable sunDrawable;
    private int swipeGestureHeaderRow;
    private int swipeGestureInfoRow;
    private int swipeGestureRow;
    private int textSizeHeaderRow;
    private int textSizeRow;
    private int themeAccentListRow;
    private int themeHeaderRow;
    private int themeInfoRow;
    private int themeListRow;
    private int themeListRow2;
    private int themePreviewRow;
    private ThemesHorizontalListCell themesHorizontalListCell;
    private boolean updatingLocation;
    private ArrayList<Theme.ThemeInfo> darkThemes = new ArrayList<>();
    private ArrayList<Theme.ThemeInfo> defaultThemes = new ArrayList<>();
    private GpsLocationListener gpsLocationListener = new GpsLocationListener(this, null);
    private GpsLocationListener networkLocationListener = new GpsLocationListener(this, null);

    /* loaded from: classes4.dex */
    public interface SizeChooseViewDelegate {
        void onSizeChanged();
    }

    /* loaded from: classes4.dex */
    public class GpsLocationListener implements LocationListener {
        private GpsLocationListener() {
            ThemeActivity.this = r1;
        }

        /* synthetic */ GpsLocationListener(ThemeActivity x0, AnonymousClass1 x1) {
            this();
        }

        @Override // android.location.LocationListener
        public void onLocationChanged(Location location) {
            if (location != null) {
                ThemeActivity.this.stopLocationUpdate();
                ThemeActivity.this.updateSunTime(location, false);
            }
        }

        @Override // android.location.LocationListener
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override // android.location.LocationListener
        public void onProviderEnabled(String provider) {
        }

        @Override // android.location.LocationListener
        public void onProviderDisabled(String provider) {
        }
    }

    /* loaded from: classes4.dex */
    public class TextSizeCell extends FrameLayout {
        private int lastWidth;
        private ThemePreviewMessagesCell messagesCell;
        private SeekBarView sizeBar;
        private TextPaint textPaint;
        private int startFontSize = 12;
        private int endFontSize = 30;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public TextSizeCell(Context context) {
            super(context);
            ThemeActivity.this = r10;
            setWillNotDraw(false);
            TextPaint textPaint = new TextPaint(1);
            this.textPaint = textPaint;
            textPaint.setTextSize(AndroidUtilities.dp(16.0f));
            SeekBarView seekBarView = new SeekBarView(context);
            this.sizeBar = seekBarView;
            seekBarView.setReportChanges(true);
            this.sizeBar.setDelegate(new SeekBarView.SeekBarViewDelegate() { // from class: org.telegram.ui.ThemeActivity.TextSizeCell.1
                @Override // org.telegram.ui.Components.SeekBarView.SeekBarViewDelegate
                public void onSeekBarDrag(boolean stop, float progress) {
                    ThemeActivity.this.setFontSize(Math.round(TextSizeCell.this.startFontSize + ((TextSizeCell.this.endFontSize - TextSizeCell.this.startFontSize) * progress)));
                }

                @Override // org.telegram.ui.Components.SeekBarView.SeekBarViewDelegate
                public void onSeekBarPressed(boolean pressed) {
                }

                @Override // org.telegram.ui.Components.SeekBarView.SeekBarViewDelegate
                public CharSequence getContentDescription() {
                    return String.valueOf(Math.round(TextSizeCell.this.startFontSize + ((TextSizeCell.this.endFontSize - TextSizeCell.this.startFontSize) * TextSizeCell.this.sizeBar.getProgress())));
                }

                @Override // org.telegram.ui.Components.SeekBarView.SeekBarViewDelegate
                public int getStepsCount() {
                    return TextSizeCell.this.endFontSize - TextSizeCell.this.startFontSize;
                }
            });
            this.sizeBar.setImportantForAccessibility(2);
            addView(this.sizeBar, LayoutHelper.createFrame(-1, 38.0f, 51, 5.0f, 5.0f, 39.0f, 0.0f));
            this.messagesCell = new ThemePreviewMessagesCell(context, r10.parentLayout, 0);
            if (Build.VERSION.SDK_INT >= 19) {
                this.messagesCell.setImportantForAccessibility(4);
            }
            addView(this.messagesCell, LayoutHelper.createFrame(-1, -2.0f, 51, 0.0f, 53.0f, 0.0f, 0.0f));
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            this.textPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteValueText));
            canvas.drawText("" + SharedConfig.fontSize, getMeasuredWidth() - AndroidUtilities.dp(39.0f), AndroidUtilities.dp(28.0f), this.textPaint);
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            if (this.lastWidth != width) {
                SeekBarView seekBarView = this.sizeBar;
                int i = SharedConfig.fontSize;
                int i2 = this.startFontSize;
                seekBarView.setProgress((i - i2) / (this.endFontSize - i2));
                this.lastWidth = width;
            }
        }

        @Override // android.view.View
        public void invalidate() {
            super.invalidate();
            this.messagesCell.invalidate();
            this.sizeBar.invalidate();
        }

        @Override // android.view.View
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            this.sizeBar.getSeekBarAccessibilityDelegate().onInitializeAccessibilityNodeInfoInternal(this, info);
        }

        @Override // android.view.View
        public boolean performAccessibilityAction(int action, Bundle arguments) {
            return super.performAccessibilityAction(action, arguments) || this.sizeBar.getSeekBarAccessibilityDelegate().performAccessibilityActionInternal(this, action, arguments);
        }
    }

    /* loaded from: classes4.dex */
    public class BubbleRadiusCell extends FrameLayout {
        private SeekBarView sizeBar;
        private TextPaint textPaint;
        private int startRadius = 0;
        private int endRadius = 17;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public BubbleRadiusCell(Context context) {
            super(context);
            ThemeActivity.this = r8;
            setWillNotDraw(false);
            TextPaint textPaint = new TextPaint(1);
            this.textPaint = textPaint;
            textPaint.setTextSize(AndroidUtilities.dp(16.0f));
            SeekBarView seekBarView = new SeekBarView(context);
            this.sizeBar = seekBarView;
            seekBarView.setReportChanges(true);
            this.sizeBar.setDelegate(new SeekBarView.SeekBarViewDelegate() { // from class: org.telegram.ui.ThemeActivity.BubbleRadiusCell.1
                @Override // org.telegram.ui.Components.SeekBarView.SeekBarViewDelegate
                public void onSeekBarDrag(boolean stop, float progress) {
                    ThemeActivity.this.setBubbleRadius(Math.round(BubbleRadiusCell.this.startRadius + ((BubbleRadiusCell.this.endRadius - BubbleRadiusCell.this.startRadius) * progress)), false);
                }

                @Override // org.telegram.ui.Components.SeekBarView.SeekBarViewDelegate
                public void onSeekBarPressed(boolean pressed) {
                }

                @Override // org.telegram.ui.Components.SeekBarView.SeekBarViewDelegate
                public CharSequence getContentDescription() {
                    return String.valueOf(Math.round(BubbleRadiusCell.this.startRadius + ((BubbleRadiusCell.this.endRadius - BubbleRadiusCell.this.startRadius) * BubbleRadiusCell.this.sizeBar.getProgress())));
                }

                @Override // org.telegram.ui.Components.SeekBarView.SeekBarViewDelegate
                public int getStepsCount() {
                    return BubbleRadiusCell.this.endRadius - BubbleRadiusCell.this.startRadius;
                }
            });
            this.sizeBar.setImportantForAccessibility(2);
            addView(this.sizeBar, LayoutHelper.createFrame(-1, 38.0f, 51, 5.0f, 5.0f, 39.0f, 0.0f));
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            this.textPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteValueText));
            canvas.drawText("" + SharedConfig.bubbleRadius, getMeasuredWidth() - AndroidUtilities.dp(39.0f), AndroidUtilities.dp(28.0f), this.textPaint);
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED), heightMeasureSpec);
            SeekBarView seekBarView = this.sizeBar;
            int i = SharedConfig.bubbleRadius;
            int i2 = this.startRadius;
            seekBarView.setProgress((i - i2) / (this.endRadius - i2));
        }

        @Override // android.view.View
        public void invalidate() {
            super.invalidate();
            this.sizeBar.invalidate();
        }

        @Override // android.view.View
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            this.sizeBar.getSeekBarAccessibilityDelegate().onInitializeAccessibilityNodeInfoInternal(this, info);
        }

        @Override // android.view.View
        public boolean performAccessibilityAction(int action, Bundle arguments) {
            return super.performAccessibilityAction(action, arguments) || this.sizeBar.getSeekBarAccessibilityDelegate().performAccessibilityActionInternal(this, action, arguments);
        }
    }

    public ThemeActivity(int type) {
        this.currentType = type;
        updateRows(true);
    }

    public boolean setBubbleRadius(int size, boolean layout) {
        if (size != SharedConfig.bubbleRadius) {
            SharedConfig.bubbleRadius = size;
            SharedPreferences preferences = MessagesController.getGlobalMainSettings();
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("bubbleRadius", SharedConfig.bubbleRadius);
            editor.commit();
            RecyclerView.ViewHolder holder = this.listView.findViewHolderForAdapterPosition(this.textSizeRow);
            if (holder != null && (holder.itemView instanceof TextSizeCell)) {
                TextSizeCell cell = (TextSizeCell) holder.itemView;
                ChatMessageCell[] cells = cell.messagesCell.getCells();
                for (int a = 0; a < cells.length; a++) {
                    cells[a].getMessageObject().resetLayout();
                    cells[a].requestLayout();
                }
                cell.invalidate();
            }
            RecyclerView.ViewHolder holder2 = this.listView.findViewHolderForAdapterPosition(this.bubbleRadiusRow);
            if (holder2 != null && (holder2.itemView instanceof BubbleRadiusCell)) {
                BubbleRadiusCell cell2 = (BubbleRadiusCell) holder2.itemView;
                if (layout) {
                    cell2.requestLayout();
                } else {
                    cell2.invalidate();
                }
            }
            updateMenuItem();
            return true;
        }
        return false;
    }

    public boolean setFontSize(int size) {
        if (size != SharedConfig.fontSize) {
            SharedConfig.fontSize = size;
            SharedPreferences preferences = MessagesController.getGlobalMainSettings();
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("fons_size", SharedConfig.fontSize);
            editor.commit();
            Theme.chat_msgTextPaint.setTextSize(AndroidUtilities.dp(SharedConfig.fontSize));
            RecyclerView.ViewHolder holder = this.listView.findViewHolderForAdapterPosition(this.textSizeRow);
            if (holder != null && (holder.itemView instanceof TextSizeCell)) {
                TextSizeCell cell = (TextSizeCell) holder.itemView;
                ChatMessageCell[] cells = cell.messagesCell.getCells();
                for (int a = 0; a < cells.length; a++) {
                    cells[a].getMessageObject().resetLayout();
                    cells[a].requestLayout();
                }
            }
            updateMenuItem();
            return true;
        }
        return false;
    }

    public void updateRows(boolean notify) {
        int i;
        int i2;
        int i3;
        int i4;
        int i5;
        int oldRowCount = this.rowCount;
        int prevThemeAccentListRow = this.themeAccentListRow;
        int prevEditThemeRow = this.editThemeRow;
        this.rowCount = 0;
        this.contactsReimportRow = -1;
        this.contactsSortRow = -1;
        this.scheduleLocationRow = -1;
        this.scheduleUpdateLocationRow = -1;
        this.scheduleLocationInfoRow = -1;
        this.nightDisabledRow = -1;
        this.nightScheduledRow = -1;
        this.nightAutomaticRow = -1;
        this.nightSystemDefaultRow = -1;
        this.nightTypeInfoRow = -1;
        this.scheduleHeaderRow = -1;
        this.nightThemeRow = -1;
        this.newThemeInfoRow = -1;
        this.scheduleFromRow = -1;
        this.scheduleToRow = -1;
        this.scheduleFromToInfoRow = -1;
        this.themeListRow = -1;
        this.themeListRow2 = -1;
        this.themeAccentListRow = -1;
        this.themeInfoRow = -1;
        this.preferedHeaderRow = -1;
        this.automaticHeaderRow = -1;
        this.automaticBrightnessRow = -1;
        this.automaticBrightnessInfoRow = -1;
        this.textSizeHeaderRow = -1;
        this.themeHeaderRow = -1;
        this.bubbleRadiusHeaderRow = -1;
        this.bubbleRadiusRow = -1;
        this.bubbleRadiusInfoRow = -1;
        this.chatListHeaderRow = -1;
        this.chatListRow = -1;
        this.chatListInfoRow = -1;
        this.chatBlurRow = -1;
        this.textSizeRow = -1;
        this.backgroundRow = -1;
        this.settingsRow = -1;
        this.customTabsRow = -1;
        this.directShareRow = -1;
        this.enableAnimationsRow = -1;
        this.raiseToSpeakRow = -1;
        this.sendByEnterRow = -1;
        this.saveToGalleryOption1Row = -1;
        this.saveToGalleryOption2Row = -1;
        this.saveToGallerySectionRow = -1;
        this.distanceRow = -1;
        this.settings2Row = -1;
        this.swipeGestureHeaderRow = -1;
        this.swipeGestureRow = -1;
        this.swipeGestureInfoRow = -1;
        this.selectThemeHeaderRow = -1;
        this.themePreviewRow = -1;
        this.editThemeRow = -1;
        this.createNewThemeRow = -1;
        this.appIconHeaderRow = -1;
        this.appIconSelectorRow = -1;
        this.appIconShadowRow = -1;
        this.defaultThemes.clear();
        this.darkThemes.clear();
        int a = 0;
        int N = Theme.themes.size();
        while (true) {
            i = 3;
            if (a >= N) {
                break;
            }
            Theme.ThemeInfo themeInfo = Theme.themes.get(a);
            int i6 = this.currentType;
            if (i6 == 0 || i6 == 3 || (!themeInfo.isLight() && (themeInfo.info == null || themeInfo.info.document != null))) {
                if (themeInfo.pathToFile != null) {
                    this.darkThemes.add(themeInfo);
                } else {
                    this.defaultThemes.add(themeInfo);
                }
            }
            a++;
        }
        Collections.sort(this.defaultThemes, ThemeActivity$$ExternalSyntheticLambda10.INSTANCE);
        int i7 = this.currentType;
        if (i7 == 3) {
            int i8 = this.rowCount;
            int i9 = i8 + 1;
            this.rowCount = i9;
            this.selectThemeHeaderRow = i8;
            int i10 = i9 + 1;
            this.rowCount = i10;
            this.themeListRow2 = i9;
            int i11 = i10 + 1;
            this.rowCount = i11;
            this.chatListInfoRow = i10;
            int i12 = i11 + 1;
            this.rowCount = i12;
            this.themePreviewRow = i11;
            int i13 = i12 + 1;
            this.rowCount = i13;
            this.themeHeaderRow = i12;
            this.rowCount = i13 + 1;
            this.themeListRow = i13;
            boolean hasAccentColors = Theme.getCurrentTheme().hasAccentColors();
            this.hasThemeAccents = hasAccentColors;
            ThemesHorizontalListCell themesHorizontalListCell = this.themesHorizontalListCell;
            if (themesHorizontalListCell != null) {
                themesHorizontalListCell.setDrawDivider(hasAccentColors);
            }
            if (this.hasThemeAccents) {
                int i14 = this.rowCount;
                this.rowCount = i14 + 1;
                this.themeAccentListRow = i14;
            }
            int i15 = this.rowCount;
            this.rowCount = i15 + 1;
            this.bubbleRadiusInfoRow = i15;
            Theme.ThemeInfo themeInfo2 = Theme.getCurrentTheme();
            Theme.ThemeAccent accent = themeInfo2.getAccent(false);
            if (themeInfo2.themeAccents != null && !themeInfo2.themeAccents.isEmpty() && accent != null && accent.id >= 100) {
                int i16 = this.rowCount;
                this.rowCount = i16 + 1;
                this.editThemeRow = i16;
            }
            int i17 = this.rowCount;
            int i18 = i17 + 1;
            this.rowCount = i18;
            this.createNewThemeRow = i17;
            this.rowCount = i18 + 1;
            this.swipeGestureInfoRow = i18;
        } else if (i7 == 0) {
            int i19 = this.rowCount;
            int i20 = i19 + 1;
            this.rowCount = i20;
            this.textSizeHeaderRow = i19;
            int i21 = i20 + 1;
            this.rowCount = i21;
            this.textSizeRow = i20;
            int i22 = i21 + 1;
            this.rowCount = i22;
            this.backgroundRow = i21;
            int i23 = i22 + 1;
            this.rowCount = i23;
            this.newThemeInfoRow = i22;
            int i24 = i23 + 1;
            this.rowCount = i24;
            this.themeHeaderRow = i23;
            int i25 = i24 + 1;
            this.rowCount = i25;
            this.themeListRow2 = i24;
            int i26 = i25 + 1;
            this.rowCount = i26;
            this.themeInfoRow = i25;
            int i27 = i26 + 1;
            this.rowCount = i27;
            this.bubbleRadiusHeaderRow = i26;
            int i28 = i27 + 1;
            this.rowCount = i28;
            this.bubbleRadiusRow = i27;
            int i29 = i28 + 1;
            this.rowCount = i29;
            this.bubbleRadiusInfoRow = i28;
            int i30 = i29 + 1;
            this.rowCount = i30;
            this.chatListHeaderRow = i29;
            int i31 = i30 + 1;
            this.rowCount = i31;
            this.chatListRow = i30;
            int i32 = i31 + 1;
            this.rowCount = i32;
            this.chatListInfoRow = i31;
            int i33 = i32 + 1;
            this.rowCount = i33;
            this.swipeGestureHeaderRow = i32;
            int i34 = i33 + 1;
            this.rowCount = i34;
            this.swipeGestureRow = i33;
            int i35 = i34 + 1;
            this.rowCount = i35;
            this.swipeGestureInfoRow = i34;
            int i36 = i35 + 1;
            this.rowCount = i36;
            this.appIconHeaderRow = i35;
            int i37 = i36 + 1;
            this.rowCount = i37;
            this.appIconSelectorRow = i36;
            int i38 = i37 + 1;
            this.rowCount = i38;
            this.appIconShadowRow = i37;
            int i39 = i38 + 1;
            this.rowCount = i39;
            this.settingsRow = i38;
            int i40 = i39 + 1;
            this.rowCount = i40;
            this.nightThemeRow = i39;
            int i41 = i40 + 1;
            this.rowCount = i41;
            this.customTabsRow = i40;
            int i42 = i41 + 1;
            this.rowCount = i42;
            this.directShareRow = i41;
            int i43 = i42 + 1;
            this.rowCount = i43;
            this.enableAnimationsRow = i42;
            int i44 = i43 + 1;
            this.rowCount = i44;
            this.raiseToSpeakRow = i43;
            this.rowCount = i44 + 1;
            this.sendByEnterRow = i44;
            if (SharedConfig.canBlurChat()) {
                int i45 = this.rowCount;
                this.rowCount = i45 + 1;
                this.chatBlurRow = i45;
            }
            int i46 = this.rowCount;
            int i47 = i46 + 1;
            this.rowCount = i47;
            this.distanceRow = i46;
            this.rowCount = i47 + 1;
            this.settings2Row = i47;
        } else {
            int i48 = this.rowCount;
            int i49 = i48 + 1;
            this.rowCount = i49;
            this.nightDisabledRow = i48;
            int i50 = i49 + 1;
            this.rowCount = i50;
            this.nightScheduledRow = i49;
            this.rowCount = i50 + 1;
            this.nightAutomaticRow = i50;
            if (Build.VERSION.SDK_INT >= 29) {
                int i51 = this.rowCount;
                this.rowCount = i51 + 1;
                this.nightSystemDefaultRow = i51;
            }
            int i52 = this.rowCount;
            this.rowCount = i52 + 1;
            this.nightTypeInfoRow = i52;
            if (Theme.selectedAutoNightType == 1) {
                int i53 = this.rowCount;
                int i54 = i53 + 1;
                this.rowCount = i54;
                this.scheduleHeaderRow = i53;
                this.rowCount = i54 + 1;
                this.scheduleLocationRow = i54;
                if (Theme.autoNightScheduleByLocation) {
                    int i55 = this.rowCount;
                    int i56 = i55 + 1;
                    this.rowCount = i56;
                    this.scheduleUpdateLocationRow = i55;
                    this.rowCount = i56 + 1;
                    this.scheduleLocationInfoRow = i56;
                } else {
                    int i57 = this.rowCount;
                    int i58 = i57 + 1;
                    this.rowCount = i58;
                    this.scheduleFromRow = i57;
                    int i59 = i58 + 1;
                    this.rowCount = i59;
                    this.scheduleToRow = i58;
                    this.rowCount = i59 + 1;
                    this.scheduleFromToInfoRow = i59;
                }
            } else if (Theme.selectedAutoNightType == 2) {
                int i60 = this.rowCount;
                int i61 = i60 + 1;
                this.rowCount = i61;
                this.automaticHeaderRow = i60;
                int i62 = i61 + 1;
                this.rowCount = i62;
                this.automaticBrightnessRow = i61;
                this.rowCount = i62 + 1;
                this.automaticBrightnessInfoRow = i62;
            }
            if (Theme.selectedAutoNightType != 0) {
                int i63 = this.rowCount;
                int i64 = i63 + 1;
                this.rowCount = i64;
                this.preferedHeaderRow = i63;
                this.rowCount = i64 + 1;
                this.themeListRow = i64;
                boolean hasAccentColors2 = Theme.getCurrentNightTheme().hasAccentColors();
                this.hasThemeAccents = hasAccentColors2;
                ThemesHorizontalListCell themesHorizontalListCell2 = this.themesHorizontalListCell;
                if (themesHorizontalListCell2 != null) {
                    themesHorizontalListCell2.setDrawDivider(hasAccentColors2);
                }
                if (this.hasThemeAccents) {
                    int i65 = this.rowCount;
                    this.rowCount = i65 + 1;
                    this.themeAccentListRow = i65;
                }
                int i66 = this.rowCount;
                this.rowCount = i66 + 1;
                this.themeInfoRow = i66;
            }
        }
        ThemesHorizontalListCell themesHorizontalListCell3 = this.themesHorizontalListCell;
        if (themesHorizontalListCell3 != null) {
            themesHorizontalListCell3.notifyDataSetChanged(this.listView.getWidth());
        }
        if (this.listAdapter != null) {
            if (this.currentType != 1 || this.previousUpdatedType == Theme.selectedAutoNightType || (i4 = this.previousUpdatedType) == -1) {
                if (notify || this.previousUpdatedType == -1) {
                    this.listAdapter.notifyDataSetChanged();
                } else {
                    if (prevThemeAccentListRow == -1 && (i3 = this.themeAccentListRow) != -1) {
                        this.listAdapter.notifyItemInserted(i3);
                    } else if (prevThemeAccentListRow != -1 && this.themeAccentListRow == -1) {
                        this.listAdapter.notifyItemRemoved(prevThemeAccentListRow);
                        if (prevEditThemeRow != -1) {
                            prevEditThemeRow--;
                        }
                    } else {
                        int i67 = this.themeAccentListRow;
                        if (i67 != -1) {
                            this.listAdapter.notifyItemChanged(i67);
                        }
                    }
                    if (prevEditThemeRow == -1 && (i2 = this.editThemeRow) != -1) {
                        this.listAdapter.notifyItemInserted(i2);
                    } else if (prevEditThemeRow != -1 && this.editThemeRow == -1) {
                        this.listAdapter.notifyItemRemoved(prevEditThemeRow);
                    }
                }
            } else {
                int start = this.nightTypeInfoRow + 1;
                if (i4 != Theme.selectedAutoNightType) {
                    int a2 = 0;
                    while (true) {
                        i5 = 4;
                        if (a2 >= 4) {
                            break;
                        }
                        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findViewHolderForAdapterPosition(a2);
                        if (holder != null && (holder.itemView instanceof ThemeTypeCell)) {
                            ((ThemeTypeCell) holder.itemView).setTypeChecked(a2 == Theme.selectedAutoNightType);
                        }
                        a2++;
                    }
                    if (Theme.selectedAutoNightType == 0) {
                        this.listAdapter.notifyItemRangeRemoved(start, oldRowCount - start);
                    } else if (Theme.selectedAutoNightType == 1) {
                        int i68 = this.previousUpdatedType;
                        if (i68 == 0) {
                            this.listAdapter.notifyItemRangeInserted(start, this.rowCount - start);
                        } else if (i68 == 2) {
                            this.listAdapter.notifyItemRangeRemoved(start, 3);
                            ListAdapter listAdapter = this.listAdapter;
                            if (!Theme.autoNightScheduleByLocation) {
                                i5 = 5;
                            }
                            listAdapter.notifyItemRangeInserted(start, i5);
                        } else if (i68 == 3) {
                            ListAdapter listAdapter2 = this.listAdapter;
                            if (!Theme.autoNightScheduleByLocation) {
                                i5 = 5;
                            }
                            listAdapter2.notifyItemRangeInserted(start, i5);
                        }
                    } else if (Theme.selectedAutoNightType == 2) {
                        int i69 = this.previousUpdatedType;
                        if (i69 == 0) {
                            this.listAdapter.notifyItemRangeInserted(start, this.rowCount - start);
                        } else if (i69 == 1) {
                            ListAdapter listAdapter3 = this.listAdapter;
                            if (!Theme.autoNightScheduleByLocation) {
                                i5 = 5;
                            }
                            listAdapter3.notifyItemRangeRemoved(start, i5);
                            this.listAdapter.notifyItemRangeInserted(start, 3);
                        } else if (i69 == 3) {
                            this.listAdapter.notifyItemRangeInserted(start, 3);
                        }
                    } else if (Theme.selectedAutoNightType == 3) {
                        int i70 = this.previousUpdatedType;
                        if (i70 == 0) {
                            this.listAdapter.notifyItemRangeInserted(start, this.rowCount - start);
                        } else if (i70 == 2) {
                            this.listAdapter.notifyItemRangeRemoved(start, 3);
                        } else if (i70 == 1) {
                            ListAdapter listAdapter4 = this.listAdapter;
                            if (!Theme.autoNightScheduleByLocation) {
                                i5 = 5;
                            }
                            listAdapter4.notifyItemRangeRemoved(start, i5);
                        }
                    }
                } else if (this.previousByLocation != Theme.autoNightScheduleByLocation) {
                    this.listAdapter.notifyItemRangeRemoved(start + 2, Theme.autoNightScheduleByLocation ? 3 : 2);
                    ListAdapter listAdapter5 = this.listAdapter;
                    int i71 = start + 2;
                    if (Theme.autoNightScheduleByLocation) {
                        i = 2;
                    }
                    listAdapter5.notifyItemRangeInserted(i71, i);
                }
            }
        }
        if (this.currentType == 1) {
            this.previousByLocation = Theme.autoNightScheduleByLocation;
            this.previousUpdatedType = Theme.selectedAutoNightType;
        }
        updateMenuItem();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.locationPermissionGranted);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetNewWallpapper);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.themeListUpdated);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.themeAccentListUpdated);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.needShareTheme);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.needSetDayNightTheme);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiPreviewThemesChanged);
        getNotificationCenter().addObserver(this, NotificationCenter.themeUploadedToServer);
        getNotificationCenter().addObserver(this, NotificationCenter.themeUploadError);
        if (this.currentType == 0) {
            Theme.loadRemoteThemes(this.currentAccount, true);
            Theme.checkCurrentRemoteTheme(true);
        }
        return super.onFragmentCreate();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        stopLocationUpdate();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.locationPermissionGranted);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewWallpapper);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.themeListUpdated);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.themeAccentListUpdated);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.needShareTheme);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.needSetDayNightTheme);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiPreviewThemesChanged);
        getNotificationCenter().removeObserver(this, NotificationCenter.themeUploadedToServer);
        getNotificationCenter().removeObserver(this, NotificationCenter.themeUploadError);
        Theme.saveAutoNightThemeConfig();
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        int i;
        AlertDialog alertDialog;
        int i2;
        if (id == NotificationCenter.locationPermissionGranted) {
            updateSunTime(null, true);
        } else if (id == NotificationCenter.didSetNewWallpapper || id == NotificationCenter.emojiLoaded) {
            RecyclerListView recyclerListView = this.listView;
            if (recyclerListView != null) {
                recyclerListView.invalidateViews();
            }
            updateMenuItem();
        } else if (id == NotificationCenter.themeAccentListUpdated) {
            ListAdapter listAdapter = this.listAdapter;
            if (listAdapter != null && (i2 = this.themeAccentListRow) != -1) {
                listAdapter.notifyItemChanged(i2, new Object());
            }
        } else if (id == NotificationCenter.themeListUpdated) {
            updateRows(true);
        } else if (id == NotificationCenter.themeUploadedToServer) {
            Theme.ThemeInfo themeInfo = (Theme.ThemeInfo) args[0];
            Theme.ThemeAccent accent = (Theme.ThemeAccent) args[1];
            if (themeInfo == this.sharingTheme && accent == this.sharingAccent) {
                StringBuilder sb = new StringBuilder();
                sb.append("https://");
                sb.append(getMessagesController().linkPrefix);
                sb.append("/addtheme/");
                sb.append((accent != null ? accent.info : themeInfo.info).slug);
                String link = sb.toString();
                showDialog(new ShareAlert(getParentActivity(), null, link, false, link, false));
                AlertDialog alertDialog2 = this.sharingProgressDialog;
                if (alertDialog2 != null) {
                    alertDialog2.dismiss();
                }
            }
        } else if (id == NotificationCenter.themeUploadError) {
            Theme.ThemeInfo themeInfo2 = (Theme.ThemeInfo) args[0];
            Theme.ThemeAccent accent2 = (Theme.ThemeAccent) args[1];
            if (themeInfo2 == this.sharingTheme && accent2 == this.sharingAccent && (alertDialog = this.sharingProgressDialog) == null) {
                alertDialog.dismiss();
            }
        } else if (id == NotificationCenter.needShareTheme) {
            if (getParentActivity() == null || this.isPaused) {
                return;
            }
            this.sharingTheme = (Theme.ThemeInfo) args[0];
            this.sharingAccent = (Theme.ThemeAccent) args[1];
            AlertDialog alertDialog3 = new AlertDialog(getParentActivity(), 3);
            this.sharingProgressDialog = alertDialog3;
            alertDialog3.setCanCancel(true);
            showDialog(this.sharingProgressDialog, new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.ThemeActivity$$ExternalSyntheticLambda7
                @Override // android.content.DialogInterface.OnDismissListener
                public final void onDismiss(DialogInterface dialogInterface) {
                    ThemeActivity.this.m4620lambda$didReceivedNotification$1$orgtelegramuiThemeActivity(dialogInterface);
                }
            });
        } else if (id == NotificationCenter.needSetDayNightTheme) {
            updateMenuItem();
            checkCurrentDayNight();
        } else if (id == NotificationCenter.emojiPreviewThemesChanged && (i = this.themeListRow2) >= 0) {
            this.listAdapter.notifyItemChanged(i);
        }
    }

    /* renamed from: lambda$didReceivedNotification$1$org-telegram-ui-ThemeActivity */
    public /* synthetic */ void m4620lambda$didReceivedNotification$1$orgtelegramuiThemeActivity(DialogInterface dialog) {
        this.sharingProgressDialog = null;
        this.sharingTheme = null;
        this.sharingAccent = null;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        this.lastIsDarkTheme = !Theme.isCurrentThemeDay();
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(false);
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        int i = this.currentType;
        if (i == 3) {
            this.actionBar.setTitle(LocaleController.getString("BrowseThemes", R.string.BrowseThemes));
            ActionBarMenu menu = this.actionBar.createMenu();
            RLottieDrawable rLottieDrawable = new RLottieDrawable(R.raw.sun, "2131558540", AndroidUtilities.dp(28.0f), AndroidUtilities.dp(28.0f), true, null);
            this.sunDrawable = rLottieDrawable;
            if (this.lastIsDarkTheme) {
                rLottieDrawable.setCurrentFrame(rLottieDrawable.getFramesCount() - 1);
            } else {
                rLottieDrawable.setCurrentFrame(0);
            }
            this.sunDrawable.setPlayInDirectionOfCustomEndFrame(true);
            this.menuItem = menu.addItem(5, this.sunDrawable);
        } else if (i == 0) {
            this.actionBar.setTitle(LocaleController.getString("ChatSettings", R.string.ChatSettings));
            ActionBarMenu menu2 = this.actionBar.createMenu();
            ActionBarMenuItem addItem = menu2.addItem(0, R.drawable.ic_ab_other);
            this.menuItem = addItem;
            addItem.setContentDescription(LocaleController.getString("AccDescrMoreOptions", R.string.AccDescrMoreOptions));
            this.menuItem.addSubItem(2, R.drawable.msg_share, LocaleController.getString("ShareTheme", R.string.ShareTheme));
            this.menuItem.addSubItem(3, R.drawable.msg_edit, LocaleController.getString("EditThemeColors", R.string.EditThemeColors));
            this.menuItem.addSubItem(1, R.drawable.msg_palette, LocaleController.getString("CreateNewThemeMenu", R.string.CreateNewThemeMenu));
            this.menuItem.addSubItem(4, R.drawable.msg_reset, LocaleController.getString("ThemeResetToDefaults", R.string.ThemeResetToDefaults));
        } else {
            this.actionBar.setTitle(LocaleController.getString("AutoNightTheme", R.string.AutoNightTheme));
        }
        this.actionBar.setActionBarMenuOnItemClick(new AnonymousClass1());
        this.listAdapter = new ListAdapter(context);
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.fragmentView = frameLayout;
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView.setLayoutManager(linearLayoutManager);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setAdapter(this.listAdapter);
        ((DefaultItemAnimator) this.listView.getItemAnimator()).setDelayAnimations(false);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListenerExtended() { // from class: org.telegram.ui.ThemeActivity$$ExternalSyntheticLambda1
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended
            public /* synthetic */ boolean hasDoubleTap(View view, int i2) {
                return RecyclerListView.OnItemClickListenerExtended.CC.$default$hasDoubleTap(this, view, i2);
            }

            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended
            public /* synthetic */ void onDoubleTap(View view, int i2, float f, float f2) {
                RecyclerListView.OnItemClickListenerExtended.CC.$default$onDoubleTap(this, view, i2, f, f2);
            }

            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended
            public final void onItemClick(View view, int i2, float f, float f2) {
                ThemeActivity.this.m4619lambda$createView$5$orgtelegramuiThemeActivity(view, i2, f, f2);
            }
        });
        return this.fragmentView;
    }

    /* renamed from: org.telegram.ui.ThemeActivity$1 */
    /* loaded from: classes4.dex */
    public class AnonymousClass1 extends ActionBar.ActionBarMenuOnItemClick {
        AnonymousClass1() {
            ThemeActivity.this = this$0;
        }

        @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
        public void onItemClick(int id) {
            Theme.ThemeInfo themeInfo;
            if (id == -1) {
                ThemeActivity.this.finishFragment();
            } else if (id == 1) {
                ThemeActivity.this.createNewTheme();
            } else if (id == 2) {
                Theme.ThemeInfo currentTheme = Theme.getCurrentTheme();
                Theme.ThemeAccent accent = currentTheme.getAccent(false);
                if (accent.info == null) {
                    ThemeActivity.this.getMessagesController().saveThemeToServer(accent.parentTheme, accent);
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needShareTheme, accent.parentTheme, accent);
                    return;
                }
                String link = "https://" + ThemeActivity.this.getMessagesController().linkPrefix + "/addtheme/" + accent.info.slug;
                ThemeActivity.this.showDialog(new ShareAlert(ThemeActivity.this.getParentActivity(), null, link, false, link, false));
            } else if (id == 3) {
                ThemeActivity.this.editTheme();
            } else if (id == 4) {
                if (ThemeActivity.this.getParentActivity() == null) {
                    return;
                }
                AlertDialog.Builder builder1 = new AlertDialog.Builder(ThemeActivity.this.getParentActivity());
                builder1.setTitle(LocaleController.getString("ThemeResetToDefaultsTitle", R.string.ThemeResetToDefaultsTitle));
                builder1.setMessage(LocaleController.getString("ThemeResetToDefaultsText", R.string.ThemeResetToDefaultsText));
                builder1.setPositiveButton(LocaleController.getString(TimerBuilder.RESET, R.string.Reset), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ThemeActivity$1$$ExternalSyntheticLambda0
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        ThemeActivity.AnonymousClass1.this.m4625lambda$onItemClick$0$orgtelegramuiThemeActivity$1(dialogInterface, i);
                    }
                });
                builder1.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                AlertDialog alertDialog = builder1.create();
                ThemeActivity.this.showDialog(alertDialog);
                TextView button = (TextView) alertDialog.getButton(-1);
                if (button != null) {
                    button.setTextColor(Theme.getColor(Theme.key_dialogTextRed2));
                }
            } else if (id == 5) {
                SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0);
                String dayThemeName = preferences.getString("lastDayTheme", "Blue");
                if (Theme.getTheme(dayThemeName) == null || Theme.getTheme(dayThemeName).isDark()) {
                    dayThemeName = "Blue";
                }
                String nightThemeName = preferences.getString("lastDarkTheme", "Dark Blue");
                if (Theme.getTheme(nightThemeName) == null || !Theme.getTheme(nightThemeName).isDark()) {
                    nightThemeName = "Dark Blue";
                }
                Theme.ThemeInfo themeInfo2 = Theme.getActiveTheme();
                if (dayThemeName.equals(nightThemeName)) {
                    if (themeInfo2.isDark() || dayThemeName.equals("Dark Blue") || dayThemeName.equals("Night")) {
                        dayThemeName = "Blue";
                    } else {
                        nightThemeName = "Dark Blue";
                    }
                }
                boolean toDark = dayThemeName.equals(themeInfo2.getKey());
                if (toDark) {
                    themeInfo = Theme.getTheme(nightThemeName);
                } else {
                    themeInfo = Theme.getTheme(dayThemeName);
                }
                ThemeActivity.this.menuItem.getIconView().getLocationInWindow(pos);
                int[] pos = {pos[0] + (ThemeActivity.this.menuItem.getIconView().getMeasuredWidth() / 2), pos[1] + (ThemeActivity.this.menuItem.getIconView().getMeasuredHeight() / 2)};
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needSetDayNightTheme, themeInfo, false, pos, -1, Boolean.valueOf(toDark), ThemeActivity.this.menuItem.getIconView());
                ThemeActivity.this.updateRows(true);
            }
        }

        /* renamed from: lambda$onItemClick$0$org-telegram-ui-ThemeActivity$1 */
        public /* synthetic */ void m4625lambda$onItemClick$0$orgtelegramuiThemeActivity$1(DialogInterface dialogInterface, int i) {
            boolean changed = false;
            if (ThemeActivity.this.setFontSize(AndroidUtilities.isTablet() ? 18 : 16)) {
                changed = true;
            }
            if (ThemeActivity.this.setBubbleRadius(10, true)) {
                changed = true;
            }
            if (changed) {
                ThemeActivity.this.listAdapter.notifyItemChanged(ThemeActivity.this.textSizeRow, new Object());
                ThemeActivity.this.listAdapter.notifyItemChanged(ThemeActivity.this.bubbleRadiusRow, new Object());
            }
            if (ThemeActivity.this.themesHorizontalListCell != null) {
                Theme.ThemeInfo themeInfo = Theme.getTheme("Blue");
                Theme.ThemeInfo currentTheme = Theme.getCurrentTheme();
                Theme.ThemeAccent accent = themeInfo.themeAccentsMap.get(Theme.DEFALT_THEME_ACCENT_ID);
                if (accent != null) {
                    Theme.OverrideWallpaperInfo info = new Theme.OverrideWallpaperInfo();
                    info.slug = Theme.DEFAULT_BACKGROUND_SLUG;
                    info.fileName = "Blue_99_wp.jpg";
                    info.originalFileName = "Blue_99_wp.jpg";
                    accent.overrideWallpaper = info;
                    themeInfo.setOverrideWallpaper(info);
                }
                boolean z = false;
                if (themeInfo != currentTheme) {
                    themeInfo.setCurrentAccentId(Theme.DEFALT_THEME_ACCENT_ID);
                    Theme.saveThemeAccents(themeInfo, true, false, true, false);
                    ThemeActivity.this.themesHorizontalListCell.selectTheme(themeInfo);
                    ThemeActivity.this.themesHorizontalListCell.smoothScrollToPosition(0);
                } else if (themeInfo.currentAccentId != Theme.DEFALT_THEME_ACCENT_ID) {
                    NotificationCenter globalInstance = NotificationCenter.getGlobalInstance();
                    int i2 = NotificationCenter.needSetDayNightTheme;
                    Object[] objArr = new Object[4];
                    objArr[0] = currentTheme;
                    if (ThemeActivity.this.currentType == 1) {
                        z = true;
                    }
                    objArr[1] = Boolean.valueOf(z);
                    objArr[2] = null;
                    objArr[3] = Integer.valueOf(Theme.DEFALT_THEME_ACCENT_ID);
                    globalInstance.postNotificationName(i2, objArr);
                    ThemeActivity.this.listAdapter.notifyItemChanged(ThemeActivity.this.themeAccentListRow);
                } else {
                    Theme.reloadWallpaper();
                }
            }
        }
    }

    /* renamed from: lambda$createView$5$org-telegram-ui-ThemeActivity */
    public /* synthetic */ void m4619lambda$createView$5$orgtelegramuiThemeActivity(View view, final int position, float x, float y) {
        int currentMinute;
        int currentHour;
        String type;
        if (position == this.enableAnimationsRow) {
            SharedPreferences preferences = MessagesController.getGlobalMainSettings();
            boolean animations = preferences.getBoolean("view_animations", true);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("view_animations", !animations);
            editor.commit();
            if (view instanceof TextCheckCell) {
                ((TextCheckCell) view).setChecked(!animations);
                return;
            }
            return;
        }
        boolean enabled = false;
        if (position == this.backgroundRow) {
            presentFragment(new WallpapersListActivity(0));
        } else if (position == this.sendByEnterRow) {
            SharedPreferences preferences2 = MessagesController.getGlobalMainSettings();
            boolean send = preferences2.getBoolean("send_by_enter", false);
            SharedPreferences.Editor editor2 = preferences2.edit();
            editor2.putBoolean("send_by_enter", !send);
            editor2.commit();
            if (view instanceof TextCheckCell) {
                ((TextCheckCell) view).setChecked(!send);
            }
        } else if (position == this.raiseToSpeakRow) {
            SharedConfig.toogleRaiseToSpeak();
            if (view instanceof TextCheckCell) {
                ((TextCheckCell) view).setChecked(SharedConfig.raiseToSpeak);
            }
        } else if (position == this.distanceRow) {
            if (getParentActivity() == null) {
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("DistanceUnitsTitle", R.string.DistanceUnitsTitle));
            builder.setItems(new CharSequence[]{LocaleController.getString("DistanceUnitsAutomatic", R.string.DistanceUnitsAutomatic), LocaleController.getString("DistanceUnitsKilometers", R.string.DistanceUnitsKilometers), LocaleController.getString("DistanceUnitsMiles", R.string.DistanceUnitsMiles)}, new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ThemeActivity$$ExternalSyntheticLambda4
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    ThemeActivity.this.m4616lambda$createView$2$orgtelegramuiThemeActivity(dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            showDialog(builder.create());
        } else if (position == this.customTabsRow) {
            SharedConfig.toggleCustomTabs();
            if (view instanceof TextCheckCell) {
                ((TextCheckCell) view).setChecked(SharedConfig.customTabs);
            }
        } else if (position == this.directShareRow) {
            SharedConfig.toggleDirectShare();
            if (view instanceof TextCheckCell) {
                ((TextCheckCell) view).setChecked(SharedConfig.directShare);
            }
        } else if (position != this.contactsReimportRow) {
            if (position == this.contactsSortRow) {
                if (getParentActivity() == null) {
                    return;
                }
                AlertDialog.Builder builder2 = new AlertDialog.Builder(getParentActivity());
                builder2.setTitle(LocaleController.getString("SortBy", R.string.SortBy));
                builder2.setItems(new CharSequence[]{LocaleController.getString("Default", R.string.Default), LocaleController.getString("SortFirstName", R.string.SortFirstName), LocaleController.getString("SortLastName", R.string.SortLastName)}, new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ThemeActivity$$ExternalSyntheticLambda6
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        ThemeActivity.this.m4617lambda$createView$3$orgtelegramuiThemeActivity(position, dialogInterface, i);
                    }
                });
                builder2.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                showDialog(builder2.create());
            } else if (position == this.chatBlurRow) {
                SharedConfig.toggleChatBlur();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(SharedConfig.chatBlurEnabled());
                }
            } else if (position == this.nightThemeRow) {
                if ((LocaleController.isRTL && x <= AndroidUtilities.dp(76.0f)) || (!LocaleController.isRTL && x >= view.getMeasuredWidth() - AndroidUtilities.dp(76.0f))) {
                    NotificationsCheckCell checkCell = (NotificationsCheckCell) view;
                    if (Theme.selectedAutoNightType == 0) {
                        Theme.selectedAutoNightType = 2;
                        checkCell.setChecked(true);
                    } else {
                        Theme.selectedAutoNightType = 0;
                        checkCell.setChecked(false);
                    }
                    Theme.saveAutoNightThemeConfig();
                    Theme.checkAutoNightThemeConditions(true);
                    if (Theme.selectedAutoNightType != 0) {
                        enabled = true;
                    }
                    String value = enabled ? Theme.getCurrentNightThemeName() : LocaleController.getString("AutoNightThemeOff", R.string.AutoNightThemeOff);
                    if (enabled) {
                        if (Theme.selectedAutoNightType == 1) {
                            type = LocaleController.getString("AutoNightScheduled", R.string.AutoNightScheduled);
                        } else if (Theme.selectedAutoNightType == 3) {
                            type = LocaleController.getString("AutoNightSystemDefault", R.string.AutoNightSystemDefault);
                        } else {
                            type = LocaleController.getString("AutoNightAdaptive", R.string.AutoNightAdaptive);
                        }
                        value = type + " " + value;
                    }
                    checkCell.setTextAndValueAndCheck(LocaleController.getString("AutoNightTheme", R.string.AutoNightTheme), value, enabled, true);
                    return;
                }
                presentFragment(new ThemeActivity(1));
            } else if (position == this.nightDisabledRow) {
                if (Theme.selectedAutoNightType == 0) {
                    return;
                }
                Theme.selectedAutoNightType = 0;
                updateRows(true);
                Theme.checkAutoNightThemeConditions();
            } else if (position == this.nightScheduledRow) {
                if (Theme.selectedAutoNightType == 1) {
                    return;
                }
                Theme.selectedAutoNightType = 1;
                if (Theme.autoNightScheduleByLocation) {
                    updateSunTime(null, true);
                }
                updateRows(true);
                Theme.checkAutoNightThemeConditions();
            } else if (position == this.nightAutomaticRow) {
                if (Theme.selectedAutoNightType == 2) {
                    return;
                }
                Theme.selectedAutoNightType = 2;
                updateRows(true);
                Theme.checkAutoNightThemeConditions();
            } else if (position == this.nightSystemDefaultRow) {
                if (Theme.selectedAutoNightType == 3) {
                    return;
                }
                Theme.selectedAutoNightType = 3;
                updateRows(true);
                Theme.checkAutoNightThemeConditions();
            } else if (position == this.scheduleLocationRow) {
                Theme.autoNightScheduleByLocation = !Theme.autoNightScheduleByLocation;
                ((TextCheckCell) view).setChecked(Theme.autoNightScheduleByLocation);
                updateRows(true);
                if (Theme.autoNightScheduleByLocation) {
                    updateSunTime(null, true);
                }
                Theme.checkAutoNightThemeConditions();
            } else if (position == this.scheduleFromRow || position == this.scheduleToRow) {
                if (getParentActivity() == null) {
                    return;
                }
                if (position == this.scheduleFromRow) {
                    currentHour = Theme.autoNightDayStartTime / 60;
                    currentMinute = Theme.autoNightDayStartTime - (currentHour * 60);
                } else {
                    int currentHour2 = Theme.autoNightDayEndTime;
                    currentHour = currentHour2 / 60;
                    currentMinute = Theme.autoNightDayEndTime - (currentHour * 60);
                }
                final TextSettingsCell cell = (TextSettingsCell) view;
                TimePickerDialog dialog = new TimePickerDialog(getParentActivity(), new TimePickerDialog.OnTimeSetListener() { // from class: org.telegram.ui.ThemeActivity$$ExternalSyntheticLambda2
                    @Override // android.app.TimePickerDialog.OnTimeSetListener
                    public final void onTimeSet(TimePicker timePicker, int i, int i2) {
                        ThemeActivity.this.m4618lambda$createView$4$orgtelegramuiThemeActivity(position, cell, timePicker, i, i2);
                    }
                }, currentHour, currentMinute, true);
                showDialog(dialog);
            } else if (position == this.scheduleUpdateLocationRow) {
                updateSunTime(null, true);
            } else if (position == this.createNewThemeRow) {
                createNewTheme();
            } else if (position == this.editThemeRow) {
                editTheme();
            }
        }
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-ThemeActivity */
    public /* synthetic */ void m4616lambda$createView$2$orgtelegramuiThemeActivity(DialogInterface dialog, int which) {
        SharedConfig.setDistanceSystemType(which);
        RecyclerView.ViewHolder holder = this.listView.findViewHolderForAdapterPosition(this.distanceRow);
        if (holder != null) {
            this.listAdapter.onBindViewHolder(holder, this.distanceRow);
        }
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-ThemeActivity */
    public /* synthetic */ void m4617lambda$createView$3$orgtelegramuiThemeActivity(int position, DialogInterface dialog, int which) {
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("sortContactsBy", which);
        editor.commit();
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyItemChanged(position);
        }
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-ThemeActivity */
    public /* synthetic */ void m4618lambda$createView$4$orgtelegramuiThemeActivity(int position, TextSettingsCell cell, TimePicker view1, int hourOfDay, int minute) {
        int time = (hourOfDay * 60) + minute;
        if (position == this.scheduleFromRow) {
            Theme.autoNightDayStartTime = time;
            cell.setTextAndValue(LocaleController.getString("AutoNightFrom", R.string.AutoNightFrom), String.format("%02d:%02d", Integer.valueOf(hourOfDay), Integer.valueOf(minute)), true);
            return;
        }
        Theme.autoNightDayEndTime = time;
        cell.setTextAndValue(LocaleController.getString("AutoNightTo", R.string.AutoNightTo), String.format("%02d:%02d", Integer.valueOf(hourOfDay), Integer.valueOf(minute)), true);
    }

    public void editTheme() {
        Theme.ThemeInfo currentTheme = Theme.getCurrentTheme();
        Theme.ThemeAccent accent = currentTheme.getAccent(false);
        presentFragment(new ThemePreviewActivity(currentTheme, false, 1, accent.id >= 100, this.currentType == 1));
    }

    public void createNewTheme() {
        if (getParentActivity() == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("NewTheme", R.string.NewTheme));
        builder.setMessage(LocaleController.getString("CreateNewThemeAlert", R.string.CreateNewThemeAlert));
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        builder.setPositiveButton(LocaleController.getString("CreateTheme", R.string.CreateTheme), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ThemeActivity$$ExternalSyntheticLambda3
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                ThemeActivity.this.m4615lambda$createNewTheme$6$orgtelegramuiThemeActivity(dialogInterface, i);
            }
        });
        showDialog(builder.create());
    }

    /* renamed from: lambda$createNewTheme$6$org-telegram-ui-ThemeActivity */
    public /* synthetic */ void m4615lambda$createNewTheme$6$orgtelegramuiThemeActivity(DialogInterface dialog, int which) {
        AlertsCreator.createThemeCreateDialog(this, 0, null, null);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            updateRows(true);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen) {
            AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
            AndroidUtilities.setAdjustResizeToNothing(getParentActivity(), this.classGuid);
        }
    }

    private void updateMenuItem() {
        if (this.menuItem == null) {
            return;
        }
        Theme.ThemeInfo themeInfo = Theme.getCurrentTheme();
        Theme.ThemeAccent accent = themeInfo.getAccent(false);
        if (themeInfo.themeAccents != null && !themeInfo.themeAccents.isEmpty() && accent != null && accent.id >= 100) {
            this.menuItem.showSubItem(2);
            this.menuItem.showSubItem(3);
        } else {
            this.menuItem.hideSubItem(2);
            this.menuItem.hideSubItem(3);
        }
        int fontSize = AndroidUtilities.isTablet() ? 18 : 16;
        Theme.ThemeInfo currentTheme = Theme.getCurrentTheme();
        if (SharedConfig.fontSize != fontSize || SharedConfig.bubbleRadius != 10 || !currentTheme.firstAccentIsDefault || currentTheme.currentAccentId != Theme.DEFALT_THEME_ACCENT_ID || (accent != null && accent.overrideWallpaper != null && !Theme.DEFAULT_BACKGROUND_SLUG.equals(accent.overrideWallpaper.slug))) {
            this.menuItem.showSubItem(4);
        } else {
            this.menuItem.hideSubItem(4);
        }
    }

    public void updateSunTime(Location lastKnownLocation, boolean forceUpdate) {
        Activity activity;
        LocationManager locationManager = (LocationManager) ApplicationLoader.applicationContext.getSystemService("location");
        if (Build.VERSION.SDK_INT >= 23 && (activity = getParentActivity()) != null && activity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") != 0) {
            activity.requestPermissions(new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, 2);
            return;
        }
        if (getParentActivity() != null) {
            if (!getParentActivity().getPackageManager().hasSystemFeature("android.hardware.location.gps")) {
                return;
            }
            try {
                LocationManager lm = (LocationManager) ApplicationLoader.applicationContext.getSystemService("location");
                if (!lm.isProviderEnabled("gps")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                    builder.setTopAnimation(R.raw.permission_request_location, 72, false, Theme.getColor(Theme.key_dialogTopBackground));
                    builder.setMessage(LocaleController.getString("GpsDisabledAlertText", R.string.GpsDisabledAlertText));
                    builder.setPositiveButton(LocaleController.getString("ConnectingToProxyEnable", R.string.ConnectingToProxyEnable), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ThemeActivity$$ExternalSyntheticLambda5
                        @Override // android.content.DialogInterface.OnClickListener
                        public final void onClick(DialogInterface dialogInterface, int i) {
                            ThemeActivity.this.m4622lambda$updateSunTime$7$orgtelegramuiThemeActivity(dialogInterface, i);
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    showDialog(builder.create());
                    return;
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        try {
            lastKnownLocation = locationManager.getLastKnownLocation("gps");
            if (lastKnownLocation == null) {
                lastKnownLocation = locationManager.getLastKnownLocation("network");
            }
            if (lastKnownLocation == null) {
                lastKnownLocation = locationManager.getLastKnownLocation("passive");
            }
        } catch (Exception e2) {
            FileLog.e(e2);
        }
        if (lastKnownLocation == null || forceUpdate) {
            startLocationUpdate();
            if (lastKnownLocation == null) {
                return;
            }
        }
        Theme.autoNightLocationLatitude = lastKnownLocation.getLatitude();
        Theme.autoNightLocationLongitude = lastKnownLocation.getLongitude();
        int[] time = SunDate.calculateSunriseSunset(Theme.autoNightLocationLatitude, Theme.autoNightLocationLongitude);
        Theme.autoNightSunriseTime = time[0];
        Theme.autoNightSunsetTime = time[1];
        Theme.autoNightCityName = null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        Theme.autoNightLastSunCheckDay = calendar.get(5);
        Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.ThemeActivity$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                ThemeActivity.this.m4624lambda$updateSunTime$9$orgtelegramuiThemeActivity();
            }
        });
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findViewHolderForAdapterPosition(this.scheduleLocationInfoRow);
        if (holder != null && (holder.itemView instanceof TextInfoPrivacyCell)) {
            ((TextInfoPrivacyCell) holder.itemView).setText(getLocationSunString());
        }
        if (Theme.autoNightScheduleByLocation && Theme.selectedAutoNightType == 1) {
            Theme.checkAutoNightThemeConditions();
        }
    }

    /* renamed from: lambda$updateSunTime$7$org-telegram-ui-ThemeActivity */
    public /* synthetic */ void m4622lambda$updateSunTime$7$orgtelegramuiThemeActivity(DialogInterface dialog, int id) {
        if (getParentActivity() == null) {
            return;
        }
        try {
            getParentActivity().startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
        } catch (Exception e) {
        }
    }

    /* renamed from: lambda$updateSunTime$9$org-telegram-ui-ThemeActivity */
    public /* synthetic */ void m4624lambda$updateSunTime$9$orgtelegramuiThemeActivity() {
        String name;
        try {
            Geocoder gcd = new Geocoder(ApplicationLoader.applicationContext, Locale.getDefault());
            List<Address> addresses = gcd.getFromLocation(Theme.autoNightLocationLatitude, Theme.autoNightLocationLongitude, 1);
            if (addresses.size() > 0) {
                name = addresses.get(0).getLocality();
            } else {
                name = null;
            }
        } catch (Exception e) {
            name = null;
        }
        final String nameFinal = name;
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ThemeActivity$$ExternalSyntheticLambda9
            @Override // java.lang.Runnable
            public final void run() {
                ThemeActivity.this.m4623lambda$updateSunTime$8$orgtelegramuiThemeActivity(nameFinal);
            }
        });
    }

    /* renamed from: lambda$updateSunTime$8$org-telegram-ui-ThemeActivity */
    public /* synthetic */ void m4623lambda$updateSunTime$8$orgtelegramuiThemeActivity(String nameFinal) {
        RecyclerListView.Holder holder;
        Theme.autoNightCityName = nameFinal;
        if (Theme.autoNightCityName == null) {
            Theme.autoNightCityName = String.format("(%.06f, %.06f)", Double.valueOf(Theme.autoNightLocationLatitude), Double.valueOf(Theme.autoNightLocationLongitude));
        }
        Theme.saveAutoNightThemeConfig();
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null && (holder = (RecyclerListView.Holder) recyclerListView.findViewHolderForAdapterPosition(this.scheduleUpdateLocationRow)) != null && (holder.itemView instanceof TextSettingsCell)) {
            ((TextSettingsCell) holder.itemView).setTextAndValue(LocaleController.getString("AutoNightUpdateLocation", R.string.AutoNightUpdateLocation), Theme.autoNightCityName, false);
        }
    }

    private void startLocationUpdate() {
        if (this.updatingLocation) {
            return;
        }
        this.updatingLocation = true;
        LocationManager locationManager = (LocationManager) ApplicationLoader.applicationContext.getSystemService("location");
        try {
            locationManager.requestLocationUpdates("gps", 1L, 0.0f, this.gpsLocationListener);
        } catch (Exception e) {
            FileLog.e(e);
        }
        try {
            locationManager.requestLocationUpdates("network", 1L, 0.0f, this.networkLocationListener);
        } catch (Exception e2) {
            FileLog.e(e2);
        }
    }

    public void stopLocationUpdate() {
        this.updatingLocation = false;
        LocationManager locationManager = (LocationManager) ApplicationLoader.applicationContext.getSystemService("location");
        locationManager.removeUpdates(this.gpsLocationListener);
        locationManager.removeUpdates(this.networkLocationListener);
    }

    public String getLocationSunString() {
        int currentHour = Theme.autoNightSunriseTime / 60;
        int currentMinute = Theme.autoNightSunriseTime - (currentHour * 60);
        String sunriseTimeStr = String.format("%02d:%02d", Integer.valueOf(currentHour), Integer.valueOf(currentMinute));
        int currentHour2 = Theme.autoNightSunsetTime / 60;
        int currentMinute2 = Theme.autoNightSunsetTime - (currentHour2 * 60);
        String sunsetTimeStr = String.format("%02d:%02d", Integer.valueOf(currentHour2), Integer.valueOf(currentMinute2));
        return LocaleController.formatString("AutoNightUpdateLocationInfo", R.string.AutoNightUpdateLocationInfo, sunsetTimeStr, sunriseTimeStr);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class InnerAccentView extends View {
        private ObjectAnimator checkAnimator;
        private boolean checked;
        private float checkedState;
        private Theme.ThemeAccent currentAccent;
        private Theme.ThemeInfo currentTheme;
        private final Paint paint = new Paint(1);

        InnerAccentView(Context context) {
            super(context);
        }

        void setThemeAndColor(Theme.ThemeInfo themeInfo, Theme.ThemeAccent accent) {
            this.currentTheme = themeInfo;
            this.currentAccent = accent;
            updateCheckedState(false);
        }

        void updateCheckedState(boolean animate) {
            this.checked = this.currentTheme.currentAccentId == this.currentAccent.id;
            ObjectAnimator objectAnimator = this.checkAnimator;
            if (objectAnimator != null) {
                objectAnimator.cancel();
            }
            float f = 1.0f;
            if (animate) {
                float[] fArr = new float[1];
                if (!this.checked) {
                    f = 0.0f;
                }
                fArr[0] = f;
                ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, "checkedState", fArr);
                this.checkAnimator = ofFloat;
                ofFloat.setDuration(200L);
                this.checkAnimator.start();
                return;
            }
            if (!this.checked) {
                f = 0.0f;
            }
            setCheckedState(f);
        }

        public void setCheckedState(float state) {
            this.checkedState = state;
            invalidate();
        }

        public float getCheckedState() {
            return this.checkedState;
        }

        @Override // android.view.View
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            updateCheckedState(false);
        }

        @Override // android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(62.0f), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(62.0f), C.BUFFER_FLAG_ENCRYPTED));
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            float radius = AndroidUtilities.dp(20.0f);
            float cx = getMeasuredWidth() * 0.5f;
            float cy = getMeasuredHeight() * 0.5f;
            this.paint.setColor(this.currentAccent.accentColor);
            this.paint.setStyle(Paint.Style.STROKE);
            this.paint.setStrokeWidth(AndroidUtilities.dp(3.0f));
            this.paint.setAlpha(Math.round(this.checkedState * 255.0f));
            canvas.drawCircle(cx, cy, radius - (this.paint.getStrokeWidth() * 0.5f), this.paint);
            this.paint.setAlpha(255);
            this.paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(cx, cy, radius - (AndroidUtilities.dp(5.0f) * this.checkedState), this.paint);
            if (this.checkedState != 0.0f) {
                this.paint.setColor(-1);
                this.paint.setAlpha(Math.round(this.checkedState * 255.0f));
                canvas.drawCircle(cx, cy, AndroidUtilities.dp(2.0f), this.paint);
                canvas.drawCircle(cx - (AndroidUtilities.dp(7.0f) * this.checkedState), cy, AndroidUtilities.dp(2.0f), this.paint);
                canvas.drawCircle((AndroidUtilities.dp(7.0f) * this.checkedState) + cx, cy, AndroidUtilities.dp(2.0f), this.paint);
            }
            if (this.currentAccent.myMessagesAccentColor != 0 && this.checkedState != 1.0f) {
                this.paint.setColor(this.currentAccent.myMessagesAccentColor);
                canvas.drawCircle(cx, cy, AndroidUtilities.dp(8.0f) * (1.0f - this.checkedState), this.paint);
            }
        }

        @Override // android.view.View
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setText(LocaleController.getString("ColorPickerMainColor", R.string.ColorPickerMainColor));
            info.setClassName(Button.class.getName());
            info.setChecked(this.checked);
            info.setCheckable(true);
            info.setEnabled(true);
        }
    }

    /* loaded from: classes4.dex */
    private static class InnerCustomAccentView extends View {
        private final Paint paint = new Paint(1);
        private int[] colors = new int[7];

        InnerCustomAccentView(Context context) {
            super(context);
        }

        public void setTheme(Theme.ThemeInfo themeInfo) {
            if (themeInfo.defaultAccentCount < 8) {
                this.colors = new int[7];
            } else {
                this.colors = new int[]{themeInfo.getAccentColor(6), themeInfo.getAccentColor(4), themeInfo.getAccentColor(7), themeInfo.getAccentColor(2), themeInfo.getAccentColor(0), themeInfo.getAccentColor(5), themeInfo.getAccentColor(3)};
            }
        }

        @Override // android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(62.0f), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(62.0f), C.BUFFER_FLAG_ENCRYPTED));
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            float centerX = getMeasuredWidth() * 0.5f;
            float centerY = getMeasuredHeight() * 0.5f;
            float radSmall = AndroidUtilities.dp(5.0f);
            float radRing = AndroidUtilities.dp(20.0f) - radSmall;
            this.paint.setStyle(Paint.Style.FILL);
            this.paint.setColor(this.colors[0]);
            canvas.drawCircle(centerX, centerY, radSmall, this.paint);
            double angle = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
            for (int a = 0; a < 6; a++) {
                float cx = (((float) Math.sin(angle)) * radRing) + centerX;
                float cy = centerY - (((float) Math.cos(angle)) * radRing);
                this.paint.setColor(this.colors[a + 1]);
                canvas.drawCircle(cx, cy, radSmall, this.paint);
                angle += 1.0471975511965976d;
            }
        }

        @Override // android.view.View
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setText(LocaleController.getString("ColorPickerMainColor", R.string.ColorPickerMainColor));
            info.setClassName(Button.class.getName());
            info.setEnabled(true);
        }
    }

    /* loaded from: classes4.dex */
    public class ThemeAccentsListAdapter extends RecyclerListView.SelectionAdapter {
        private Theme.ThemeInfo currentTheme;
        private Context mContext;
        private ArrayList<Theme.ThemeAccent> themeAccents;

        ThemeAccentsListAdapter(Context context) {
            ThemeActivity.this = r1;
            this.mContext = context;
            notifyDataSetChanged();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyDataSetChanged() {
            this.currentTheme = ThemeActivity.this.currentType == 1 ? Theme.getCurrentNightTheme() : Theme.getCurrentTheme();
            this.themeAccents = new ArrayList<>(this.currentTheme.themeAccents);
            super.notifyDataSetChanged();
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return false;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            return position == getItemCount() - 1 ? 1 : 0;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case 0:
                    return new RecyclerListView.Holder(new InnerAccentView(this.mContext));
                default:
                    return new RecyclerListView.Holder(new InnerCustomAccentView(this.mContext));
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (getItemViewType(position)) {
                case 0:
                    InnerAccentView view = (InnerAccentView) holder.itemView;
                    view.setThemeAndColor(this.currentTheme, this.themeAccents.get(position));
                    return;
                case 1:
                    InnerCustomAccentView view2 = (InnerCustomAccentView) holder.itemView;
                    view2.setTheme(this.currentTheme);
                    return;
                default:
                    return;
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            if (this.themeAccents.isEmpty()) {
                return 0;
            }
            return this.themeAccents.size() + 1;
        }

        public int findCurrentAccent() {
            return this.themeAccents.indexOf(this.currentTheme.getAccent(false));
        }
    }

    /* loaded from: classes4.dex */
    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private static final int TYPE_APP_ICON = 20;
        private static final int TYPE_BRIGHTNESS = 6;
        private static final int TYPE_BUBBLE_RADIUS = 13;
        private static final int TYPE_CHAT_LIST = 9;
        private static final int TYPE_DEFAULT_THEMES_PREVIEW = 17;
        private static final int TYPE_HEADER = 5;
        private static final int TYPE_NIGHT_THEME = 10;
        private static final int TYPE_SAVE_TO_GALLERY = 19;
        private static final int TYPE_SHADOW = 3;
        private static final int TYPE_SWIPE_GESTURE = 15;
        private static final int TYPE_TEXT_CHECK = 7;
        private static final int TYPE_TEXT_INFO_PRIVACY = 2;
        private static final int TYPE_TEXT_PREFERENCE = 14;
        private static final int TYPE_TEXT_SETTING = 1;
        private static final int TYPE_TEXT_SIZE = 8;
        private static final int TYPE_THEME_ACCENT_LIST = 12;
        private static final int TYPE_THEME_LIST = 11;
        private static final int TYPE_THEME_PREVIEW = 16;
        private static final int TYPE_THEME_TYPE = 4;
        private boolean first = true;
        private Context mContext;

        public ListAdapter(Context context) {
            ThemeActivity.this = r1;
            this.mContext = context;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return ThemeActivity.this.rowCount;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int type = holder.getItemViewType();
            return type == 0 || type == 1 || type == 4 || type == 7 || type == 10 || type == 11 || type == 12 || type == 14 || type == 18 || type == 20;
        }

        public void showOptionsForTheme(final Theme.ThemeInfo themeInfo) {
            int[] icons;
            CharSequence[] items;
            boolean hasDelete;
            if (ThemeActivity.this.getParentActivity() != null) {
                if ((themeInfo.info != null && !themeInfo.themeLoaded) || ThemeActivity.this.currentType == 1) {
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(ThemeActivity.this.getParentActivity());
                String str = null;
                if (themeInfo.pathToFile == null) {
                    hasDelete = false;
                    items = new CharSequence[]{null, LocaleController.getString("ExportTheme", R.string.ExportTheme)};
                    icons = new int[]{0, R.drawable.msg_shareout};
                } else {
                    hasDelete = themeInfo.info == null || !themeInfo.info.isDefault;
                    CharSequence[] charSequenceArr = new CharSequence[5];
                    charSequenceArr[0] = LocaleController.getString("ShareFile", R.string.ShareFile);
                    charSequenceArr[1] = LocaleController.getString("ExportTheme", R.string.ExportTheme);
                    charSequenceArr[2] = (themeInfo.info == null || (!themeInfo.info.isDefault && themeInfo.info.creator)) ? LocaleController.getString("Edit", R.string.Edit) : null;
                    charSequenceArr[3] = (themeInfo.info == null || !themeInfo.info.creator) ? null : LocaleController.getString("ThemeSetUrl", R.string.ThemeSetUrl);
                    if (hasDelete) {
                        str = LocaleController.getString("Delete", R.string.Delete);
                    }
                    charSequenceArr[4] = str;
                    items = charSequenceArr;
                    icons = new int[]{R.drawable.msg_share, R.drawable.msg_shareout, R.drawable.msg_edit, R.drawable.msg_link, R.drawable.msg_delete};
                }
                builder.setItems(items, icons, new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ThemeActivity$ListAdapter$$ExternalSyntheticLambda2
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        ThemeActivity.ListAdapter.this.m4631x1cb31ddc(themeInfo, dialogInterface, i);
                    }
                });
                AlertDialog alertDialog = builder.create();
                ThemeActivity.this.showDialog(alertDialog);
                if (hasDelete) {
                    alertDialog.setItemColor(alertDialog.getItemsCount() - 1, Theme.getColor(Theme.key_dialogTextRed2), Theme.getColor(Theme.key_dialogRedIcon));
                }
            }
        }

        /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:29:0x00db -> B:74:0x0101). Please submit an issue!!! */
        /* renamed from: lambda$showOptionsForTheme$1$org-telegram-ui-ThemeActivity$ListAdapter */
        public /* synthetic */ void m4631x1cb31ddc(final Theme.ThemeInfo themeInfo, DialogInterface dialog, int which) {
            File currentFile;
            if (ThemeActivity.this.getParentActivity() == null) {
                return;
            }
            if (which == 0) {
                if (themeInfo.info == null) {
                    ThemeActivity.this.getMessagesController().saveThemeToServer(themeInfo, null);
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needShareTheme, themeInfo, null);
                    return;
                }
                String link = "https://" + ThemeActivity.this.getMessagesController().linkPrefix + "/addtheme/" + themeInfo.info.slug;
                ThemeActivity.this.showDialog(new ShareAlert(ThemeActivity.this.getParentActivity(), null, link, false, link, false));
            } else if (which == 1) {
                if (themeInfo.pathToFile == null && themeInfo.assetName == null) {
                    StringBuilder result = new StringBuilder();
                    for (Map.Entry<String, Integer> entry : Theme.getDefaultColors().entrySet()) {
                        result.append(entry.getKey());
                        result.append("=");
                        result.append(entry.getValue());
                        result.append("\n");
                    }
                    currentFile = new File(ApplicationLoader.getFilesDirFixed(), "default_theme.attheme");
                    FileOutputStream stream = null;
                    try {
                        try {
                            try {
                                stream = new FileOutputStream(currentFile);
                                stream.write(AndroidUtilities.getStringBytes(result.toString()));
                                stream.close();
                            } catch (Exception e) {
                                FileLog.e(e);
                                if (stream != null) {
                                    stream.close();
                                }
                            }
                        } catch (Exception e2) {
                            FileLog.e(e2);
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
                } else {
                    currentFile = themeInfo.assetName != null ? Theme.getAssetFile(themeInfo.assetName) : new File(themeInfo.pathToFile);
                }
                String name = themeInfo.name;
                if (!name.endsWith(".attheme")) {
                    name = name + ".attheme";
                }
                File finalFile = new File(FileLoader.getDirectory(4), FileLoader.fixFileName(name));
                try {
                    if (!AndroidUtilities.copyFile(currentFile, finalFile)) {
                        return;
                    }
                    Intent intent = new Intent("android.intent.action.SEND");
                    intent.setType("text/xml");
                    if (Build.VERSION.SDK_INT >= 24) {
                        try {
                            intent.putExtra("android.intent.extra.STREAM", FileProvider.getUriForFile(ThemeActivity.this.getParentActivity(), "org.telegram.messenger.beta.provider", finalFile));
                            intent.setFlags(1);
                        } catch (Exception e4) {
                            intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(finalFile));
                        }
                    } else {
                        intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(finalFile));
                    }
                    ThemeActivity.this.startActivityForResult(Intent.createChooser(intent, LocaleController.getString("ShareFile", R.string.ShareFile)), 500);
                } catch (Exception e5) {
                    FileLog.e(e5);
                }
            } else if (which == 2) {
                if (ThemeActivity.this.parentLayout != null) {
                    Theme.applyTheme(themeInfo);
                    ThemeActivity.this.parentLayout.rebuildAllFragmentViews(true, true);
                    new ThemeEditorView().show(ThemeActivity.this.getParentActivity(), themeInfo);
                }
            } else if (which == 3) {
                ThemeActivity.this.presentFragment(new ThemeSetUrlActivity(themeInfo, null, false));
            } else if (ThemeActivity.this.getParentActivity() != null) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(ThemeActivity.this.getParentActivity());
                builder1.setTitle(LocaleController.getString("DeleteThemeTitle", R.string.DeleteThemeTitle));
                builder1.setMessage(LocaleController.getString("DeleteThemeAlert", R.string.DeleteThemeAlert));
                builder1.setPositiveButton(LocaleController.getString("Delete", R.string.Delete), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ThemeActivity$ListAdapter$$ExternalSyntheticLambda1
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        ThemeActivity.ListAdapter.this.m4630x3771af1b(themeInfo, dialogInterface, i);
                    }
                });
                builder1.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                AlertDialog alertDialog = builder1.create();
                ThemeActivity.this.showDialog(alertDialog);
                TextView button = (TextView) alertDialog.getButton(-1);
                if (button != null) {
                    button.setTextColor(Theme.getColor(Theme.key_dialogTextRed2));
                }
            }
        }

        /* renamed from: lambda$showOptionsForTheme$0$org-telegram-ui-ThemeActivity$ListAdapter */
        public /* synthetic */ void m4630x3771af1b(Theme.ThemeInfo themeInfo, DialogInterface dialogInterface, int i) {
            MessagesController.getInstance(themeInfo.account).saveTheme(themeInfo, null, themeInfo == Theme.getCurrentNightTheme(), true);
            if (Theme.deleteTheme(themeInfo)) {
                ThemeActivity.this.parentLayout.rebuildAllFragmentViews(true, true);
            }
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.themeListUpdated, new Object[0]);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 1:
                    View textSettingsCell = new TextSettingsCell(this.mContext);
                    textSettingsCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view = textSettingsCell;
                    break;
                case 2:
                    View textInfoPrivacyCell = new TextInfoPrivacyCell(this.mContext);
                    textInfoPrivacyCell.setBackground(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    view = textInfoPrivacyCell;
                    break;
                case 3:
                    view = new ShadowSectionCell(this.mContext);
                    break;
                case 4:
                    View themeTypeCell = new ThemeTypeCell(this.mContext);
                    themeTypeCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view = themeTypeCell;
                    break;
                case 5:
                    View headerCell = new HeaderCell(this.mContext);
                    headerCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view = headerCell;
                    break;
                case 6:
                    View view2 = new BrightnessControlCell(this.mContext) { // from class: org.telegram.ui.ThemeActivity.ListAdapter.1
                        @Override // org.telegram.ui.Cells.BrightnessControlCell
                        protected void didChangedValue(float value) {
                            int oldValue = (int) (Theme.autoNightBrighnessThreshold * 100.0f);
                            int newValue = (int) (value * 100.0f);
                            Theme.autoNightBrighnessThreshold = value;
                            if (oldValue != newValue) {
                                RecyclerListView.Holder holder = (RecyclerListView.Holder) ThemeActivity.this.listView.findViewHolderForAdapterPosition(ThemeActivity.this.automaticBrightnessInfoRow);
                                if (holder != null) {
                                    TextInfoPrivacyCell cell = (TextInfoPrivacyCell) holder.itemView;
                                    cell.setText(LocaleController.formatString("AutoNightBrightnessInfo", R.string.AutoNightBrightnessInfo, Integer.valueOf((int) (Theme.autoNightBrighnessThreshold * 100.0f))));
                                }
                                Theme.checkAutoNightThemeConditions(true);
                            }
                        }
                    };
                    view2.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view = view2;
                    break;
                case 7:
                    View textCheckCell = new TextCheckCell(this.mContext);
                    textCheckCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view = textCheckCell;
                    break;
                case 8:
                    View textSizeCell = new TextSizeCell(this.mContext);
                    textSizeCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view = textSizeCell;
                    break;
                case 9:
                    View view3 = new ChatListCell(this.mContext) { // from class: org.telegram.ui.ThemeActivity.ListAdapter.2
                        @Override // org.telegram.ui.Cells.ChatListCell
                        protected void didSelectChatType(boolean threeLines) {
                            SharedConfig.setUseThreeLinesLayout(threeLines);
                        }
                    };
                    view3.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view = view3;
                    break;
                case 10:
                    View notificationsCheckCell = new NotificationsCheckCell(this.mContext, 21, 64, false);
                    notificationsCheckCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view = notificationsCheckCell;
                    break;
                case 11:
                    this.first = true;
                    ThemeActivity.this.themesHorizontalListCell = new ThemesHorizontalListCell(this.mContext, ThemeActivity.this.currentType, ThemeActivity.this.defaultThemes, ThemeActivity.this.darkThemes) { // from class: org.telegram.ui.ThemeActivity.ListAdapter.3
                        @Override // org.telegram.ui.Cells.ThemesHorizontalListCell
                        protected void showOptionsForTheme(Theme.ThemeInfo themeInfo) {
                            ThemeActivity.this.listAdapter.showOptionsForTheme(themeInfo);
                        }

                        @Override // org.telegram.ui.Cells.ThemesHorizontalListCell
                        protected void presentFragment(BaseFragment fragment) {
                            ThemeActivity.this.presentFragment(fragment);
                        }

                        @Override // org.telegram.ui.Cells.ThemesHorizontalListCell
                        protected void updateRows() {
                            ThemeActivity.this.updateRows(false);
                        }
                    };
                    ThemeActivity.this.themesHorizontalListCell.setDrawDivider(ThemeActivity.this.hasThemeAccents);
                    ThemeActivity.this.themesHorizontalListCell.setFocusable(false);
                    View view4 = ThemeActivity.this.themesHorizontalListCell;
                    view4.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(148.0f)));
                    view = view4;
                    break;
                case 12:
                    final TintRecyclerListView tintRecyclerListView = new TintRecyclerListView(this.mContext) { // from class: org.telegram.ui.ThemeActivity.ListAdapter.4
                        @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup
                        public boolean onInterceptTouchEvent(MotionEvent e) {
                            if (getParent() != null && getParent().getParent() != null) {
                                getParent().getParent().requestDisallowInterceptTouchEvent(canScrollHorizontally(-1));
                            }
                            return super.onInterceptTouchEvent(e);
                        }
                    };
                    tintRecyclerListView.setFocusable(false);
                    tintRecyclerListView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    tintRecyclerListView.setItemAnimator(null);
                    tintRecyclerListView.setLayoutAnimation(null);
                    tintRecyclerListView.setPadding(AndroidUtilities.dp(11.0f), 0, AndroidUtilities.dp(11.0f), 0);
                    tintRecyclerListView.setClipToPadding(false);
                    LinearLayoutManager accentsLayoutManager = new LinearLayoutManager(this.mContext);
                    accentsLayoutManager.setOrientation(0);
                    tintRecyclerListView.setLayoutManager(accentsLayoutManager);
                    final ThemeAccentsListAdapter accentsAdapter = new ThemeAccentsListAdapter(this.mContext);
                    tintRecyclerListView.setAdapter(accentsAdapter);
                    tintRecyclerListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.ThemeActivity$ListAdapter$$ExternalSyntheticLambda4
                        @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
                        public final void onItemClick(View view5, int i) {
                            ThemeActivity.ListAdapter.this.m4626x25c35c2(accentsAdapter, tintRecyclerListView, view5, i);
                        }
                    });
                    tintRecyclerListView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() { // from class: org.telegram.ui.ThemeActivity$ListAdapter$$ExternalSyntheticLambda5
                        @Override // org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener
                        public final boolean onItemClick(View view5, int i) {
                            return ThemeActivity.ListAdapter.this.m4629xb2208205(accentsAdapter, view5, i);
                        }
                    });
                    tintRecyclerListView.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(62.0f)));
                    view = tintRecyclerListView;
                    break;
                case 13:
                    View bubbleRadiusCell = new BubbleRadiusCell(this.mContext);
                    bubbleRadiusCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view = bubbleRadiusCell;
                    break;
                case 14:
                case 18:
                default:
                    View textCell = new TextCell(this.mContext);
                    textCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view = textCell;
                    break;
                case 15:
                    view = new SwipeGestureSettingsView(this.mContext, ThemeActivity.this.currentAccount);
                    break;
                case 16:
                    ThemePreviewMessagesCell messagesCell = new ThemePreviewMessagesCell(this.mContext, ThemeActivity.this.parentLayout, 0);
                    if (Build.VERSION.SDK_INT >= 19) {
                        messagesCell.setImportantForAccessibility(4);
                    }
                    view = messagesCell;
                    break;
                case 17:
                    Context context = this.mContext;
                    ThemeActivity themeActivity = ThemeActivity.this;
                    DefaultThemesPreviewCell cell = new DefaultThemesPreviewCell(context, themeActivity, themeActivity.currentType);
                    cell.setFocusable(false);
                    cell.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                    view = cell;
                    break;
                case 19:
                    view = new RadioButtonCell(this.mContext);
                    break;
                case 20:
                    Context context2 = this.mContext;
                    ThemeActivity themeActivity2 = ThemeActivity.this;
                    view = new AppIconsSelectorCell(context2, themeActivity2, themeActivity2.currentAccount);
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        /* renamed from: lambda$onCreateViewHolder$2$org-telegram-ui-ThemeActivity$ListAdapter */
        public /* synthetic */ void m4626x25c35c2(ThemeAccentsListAdapter accentsAdapter, RecyclerListView accentsListView, View view1, int position) {
            Theme.ThemeInfo currentTheme = ThemeActivity.this.currentType == 1 ? Theme.getCurrentNightTheme() : Theme.getCurrentTheme();
            if (position != accentsAdapter.getItemCount() - 1) {
                Theme.ThemeAccent accent = (Theme.ThemeAccent) accentsAdapter.themeAccents.get(position);
                if (!TextUtils.isEmpty(accent.patternSlug) && accent.id != Theme.DEFALT_THEME_ACCENT_ID) {
                    Theme.PatternsLoader.createLoader(false);
                }
                if (currentTheme.currentAccentId != accent.id) {
                    NotificationCenter globalInstance = NotificationCenter.getGlobalInstance();
                    int i = NotificationCenter.needSetDayNightTheme;
                    Object[] objArr = new Object[4];
                    objArr[0] = currentTheme;
                    objArr[1] = Boolean.valueOf(ThemeActivity.this.currentType == 1);
                    objArr[2] = null;
                    objArr[3] = Integer.valueOf(accent.id);
                    globalInstance.postNotificationName(i, objArr);
                    EmojiThemes.saveCustomTheme(currentTheme, accent.id);
                } else {
                    ThemeActivity.this.presentFragment(new ThemePreviewActivity(currentTheme, false, 1, accent.id >= 100, ThemeActivity.this.currentType == 1));
                }
            } else {
                ThemeActivity.this.presentFragment(new ThemePreviewActivity(currentTheme, false, 1, false, ThemeActivity.this.currentType == 1));
            }
            int left = view1.getLeft();
            int right = view1.getRight();
            int extra = AndroidUtilities.dp(52.0f);
            if (left - extra < 0) {
                accentsListView.smoothScrollBy(left - extra, 0);
            } else if (right + extra > accentsListView.getMeasuredWidth()) {
                accentsListView.smoothScrollBy((right + extra) - accentsListView.getMeasuredWidth(), 0);
            }
            int count = accentsListView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = accentsListView.getChildAt(a);
                if (child instanceof InnerAccentView) {
                    ((InnerAccentView) child).updateCheckedState(true);
                }
            }
        }

        /* renamed from: lambda$onCreateViewHolder$5$org-telegram-ui-ThemeActivity$ListAdapter */
        public /* synthetic */ boolean m4629xb2208205(final ThemeAccentsListAdapter accentsAdapter, View view12, int position) {
            if (position >= 0 && position < accentsAdapter.themeAccents.size()) {
                final Theme.ThemeAccent accent = (Theme.ThemeAccent) accentsAdapter.themeAccents.get(position);
                if (accent.id < 100 || accent.isDefault) {
                    return false;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(ThemeActivity.this.getParentActivity());
                CharSequence[] items = new CharSequence[4];
                items[0] = LocaleController.getString("OpenInEditor", R.string.OpenInEditor);
                items[1] = LocaleController.getString("ShareTheme", R.string.ShareTheme);
                items[2] = (accent.info == null || !accent.info.creator) ? null : LocaleController.getString("ThemeSetUrl", R.string.ThemeSetUrl);
                items[3] = LocaleController.getString("DeleteTheme", R.string.DeleteTheme);
                int[] icons = {R.drawable.msg_edit, R.drawable.msg_share, R.drawable.msg_link, R.drawable.msg_delete};
                builder.setItems(items, icons, new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ThemeActivity$ListAdapter$$ExternalSyntheticLambda0
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        ThemeActivity.ListAdapter.this.m4628xccdf1344(accent, accentsAdapter, dialogInterface, i);
                    }
                });
                AlertDialog alertDialog = builder.create();
                ThemeActivity.this.showDialog(alertDialog);
                alertDialog.setItemColor(alertDialog.getItemsCount() - 1, Theme.getColor(Theme.key_dialogTextRed2), Theme.getColor(Theme.key_dialogRedIcon));
                return true;
            }
            return false;
        }

        /* renamed from: lambda$onCreateViewHolder$4$org-telegram-ui-ThemeActivity$ListAdapter */
        public /* synthetic */ void m4628xccdf1344(final Theme.ThemeAccent accent, final ThemeAccentsListAdapter accentsAdapter, DialogInterface dialog, int which) {
            if (ThemeActivity.this.getParentActivity() == null) {
                return;
            }
            int i = 2;
            if (which == 0) {
                ThemeActivity themeActivity = ThemeActivity.this;
                if (which != 1) {
                    i = 1;
                }
                AlertsCreator.createThemeCreateDialog(themeActivity, i, accent.parentTheme, accent);
            } else if (which == 1) {
                if (accent.info == null) {
                    ThemeActivity.this.getMessagesController().saveThemeToServer(accent.parentTheme, accent);
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needShareTheme, accent.parentTheme, accent);
                    return;
                }
                String link = "https://" + ThemeActivity.this.getMessagesController().linkPrefix + "/addtheme/" + accent.info.slug;
                ThemeActivity.this.showDialog(new ShareAlert(ThemeActivity.this.getParentActivity(), null, link, false, link, false));
            } else if (which == 2) {
                ThemeActivity.this.presentFragment(new ThemeSetUrlActivity(accent.parentTheme, accent, false));
            } else if (which != 3 || ThemeActivity.this.getParentActivity() == null) {
            } else {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(ThemeActivity.this.getParentActivity());
                builder1.setTitle(LocaleController.getString("DeleteThemeTitle", R.string.DeleteThemeTitle));
                builder1.setMessage(LocaleController.getString("DeleteThemeAlert", R.string.DeleteThemeAlert));
                builder1.setPositiveButton(LocaleController.getString("Delete", R.string.Delete), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ThemeActivity$ListAdapter$$ExternalSyntheticLambda3
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i2) {
                        ThemeActivity.ListAdapter.this.m4627xe79da483(accentsAdapter, accent, dialogInterface, i2);
                    }
                });
                builder1.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                AlertDialog alertDialog = builder1.create();
                ThemeActivity.this.showDialog(alertDialog);
                TextView button = (TextView) alertDialog.getButton(-1);
                if (button != null) {
                    button.setTextColor(Theme.getColor(Theme.key_dialogTextRed2));
                }
            }
        }

        /* renamed from: lambda$onCreateViewHolder$3$org-telegram-ui-ThemeActivity$ListAdapter */
        public /* synthetic */ void m4627xe79da483(ThemeAccentsListAdapter accentsAdapter, Theme.ThemeAccent accent, DialogInterface dialogInterface, int i) {
            if (Theme.deleteThemeAccent(accentsAdapter.currentTheme, accent, true)) {
                Theme.refreshThemeColors();
                NotificationCenter globalInstance = NotificationCenter.getGlobalInstance();
                int i2 = NotificationCenter.needSetDayNightTheme;
                Object[] objArr = new Object[4];
                boolean z = false;
                objArr[0] = Theme.getActiveTheme();
                if (ThemeActivity.this.currentType == 1) {
                    z = true;
                }
                objArr[1] = Boolean.valueOf(z);
                objArr[2] = null;
                objArr[3] = -1;
                globalInstance.postNotificationName(i2, objArr);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            String value;
            String value2;
            int i;
            String type;
            boolean enabled = false;
            boolean z = true;
            switch (holder.getItemViewType()) {
                case 1:
                    TextSettingsCell cell = (TextSettingsCell) holder.itemView;
                    if (position != ThemeActivity.this.nightThemeRow) {
                        if (position != ThemeActivity.this.scheduleFromRow) {
                            if (position != ThemeActivity.this.scheduleToRow) {
                                if (position != ThemeActivity.this.scheduleUpdateLocationRow) {
                                    if (position != ThemeActivity.this.contactsSortRow) {
                                        if (position != ThemeActivity.this.contactsReimportRow) {
                                            if (position == ThemeActivity.this.distanceRow) {
                                                if (SharedConfig.distanceSystemType == 0) {
                                                    value = LocaleController.getString("DistanceUnitsAutomatic", R.string.DistanceUnitsAutomatic);
                                                } else if (SharedConfig.distanceSystemType == 1) {
                                                    value = LocaleController.getString("DistanceUnitsKilometers", R.string.DistanceUnitsKilometers);
                                                } else {
                                                    value = LocaleController.getString("DistanceUnitsMiles", R.string.DistanceUnitsMiles);
                                                }
                                                cell.setTextAndValue(LocaleController.getString("DistanceUnits", R.string.DistanceUnits), value, false);
                                                return;
                                            }
                                            return;
                                        }
                                        cell.setText(LocaleController.getString("ImportContacts", R.string.ImportContacts), true);
                                        return;
                                    }
                                    SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                                    int sort = preferences.getInt("sortContactsBy", 0);
                                    if (sort == 0) {
                                        value2 = LocaleController.getString("Default", R.string.Default);
                                    } else if (sort == 1) {
                                        value2 = LocaleController.getString("FirstName", R.string.SortFirstName);
                                    } else {
                                        value2 = LocaleController.getString("LastName", R.string.SortLastName);
                                    }
                                    cell.setTextAndValue(LocaleController.getString("SortBy", R.string.SortBy), value2, true);
                                    return;
                                }
                                cell.setTextAndValue(LocaleController.getString("AutoNightUpdateLocation", R.string.AutoNightUpdateLocation), Theme.autoNightCityName, false);
                                return;
                            }
                            int currentHour = Theme.autoNightDayEndTime / 60;
                            int currentMinute = Theme.autoNightDayEndTime - (currentHour * 60);
                            cell.setTextAndValue(LocaleController.getString("AutoNightTo", R.string.AutoNightTo), String.format("%02d:%02d", Integer.valueOf(currentHour), Integer.valueOf(currentMinute)), false);
                            return;
                        }
                        int currentHour2 = Theme.autoNightDayStartTime / 60;
                        int currentMinute2 = Theme.autoNightDayStartTime - (currentHour2 * 60);
                        cell.setTextAndValue(LocaleController.getString("AutoNightFrom", R.string.AutoNightFrom), String.format("%02d:%02d", Integer.valueOf(currentHour2), Integer.valueOf(currentMinute2)), true);
                        return;
                    }
                    if (Theme.selectedAutoNightType == 0) {
                        i = R.string.AutoNightTheme;
                    } else if (Theme.getCurrentNightTheme() != null) {
                        cell.setTextAndValue(LocaleController.getString("AutoNightTheme", R.string.AutoNightTheme), Theme.getCurrentNightThemeName(), false);
                        return;
                    } else {
                        i = R.string.AutoNightTheme;
                    }
                    cell.setTextAndValue(LocaleController.getString("AutoNightTheme", i), LocaleController.getString("AutoNightThemeOff", R.string.AutoNightThemeOff), false);
                    return;
                case 2:
                    TextInfoPrivacyCell cell2 = (TextInfoPrivacyCell) holder.itemView;
                    if (position != ThemeActivity.this.automaticBrightnessInfoRow) {
                        if (position == ThemeActivity.this.scheduleLocationInfoRow) {
                            cell2.setText(ThemeActivity.this.getLocationSunString());
                            return;
                        }
                        return;
                    }
                    cell2.setText(LocaleController.formatString("AutoNightBrightnessInfo", R.string.AutoNightBrightnessInfo, Integer.valueOf((int) (Theme.autoNightBrighnessThreshold * 100.0f))));
                    return;
                case 3:
                    if ((position != ThemeActivity.this.nightTypeInfoRow || ThemeActivity.this.themeInfoRow != -1) && ((position != ThemeActivity.this.themeInfoRow || ThemeActivity.this.nightTypeInfoRow == -1) && position != ThemeActivity.this.saveToGallerySectionRow)) {
                        holder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else {
                        holder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        return;
                    }
                case 4:
                    ThemeTypeCell typeCell = (ThemeTypeCell) holder.itemView;
                    if (position != ThemeActivity.this.nightDisabledRow) {
                        if (position != ThemeActivity.this.nightScheduledRow) {
                            if (position != ThemeActivity.this.nightAutomaticRow) {
                                if (position == ThemeActivity.this.nightSystemDefaultRow) {
                                    String string = LocaleController.getString("AutoNightSystemDefault", R.string.AutoNightSystemDefault);
                                    if (Theme.selectedAutoNightType != 3) {
                                        z = false;
                                    }
                                    typeCell.setValue(string, z, false);
                                    return;
                                }
                                return;
                            }
                            String string2 = LocaleController.getString("AutoNightAdaptive", R.string.AutoNightAdaptive);
                            boolean z2 = Theme.selectedAutoNightType == 2;
                            if (ThemeActivity.this.nightSystemDefaultRow != -1) {
                                enabled = true;
                            }
                            typeCell.setValue(string2, z2, enabled);
                            return;
                        }
                        String string3 = LocaleController.getString("AutoNightScheduled", R.string.AutoNightScheduled);
                        if (Theme.selectedAutoNightType == 1) {
                            enabled = true;
                        }
                        typeCell.setValue(string3, enabled, true);
                        return;
                    }
                    String string4 = LocaleController.getString("AutoNightDisabled", R.string.AutoNightDisabled);
                    if (Theme.selectedAutoNightType == 0) {
                        enabled = true;
                    }
                    typeCell.setValue(string4, enabled, true);
                    return;
                case 5:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position != ThemeActivity.this.scheduleHeaderRow) {
                        if (position != ThemeActivity.this.automaticHeaderRow) {
                            if (position != ThemeActivity.this.preferedHeaderRow) {
                                if (position != ThemeActivity.this.settingsRow) {
                                    if (position == ThemeActivity.this.themeHeaderRow) {
                                        if (ThemeActivity.this.currentType == 3) {
                                            headerCell.setText(LocaleController.getString("BuildMyOwnTheme", R.string.BuildMyOwnTheme));
                                            return;
                                        } else {
                                            headerCell.setText(LocaleController.getString("ColorTheme", R.string.ColorTheme));
                                            return;
                                        }
                                    } else if (position != ThemeActivity.this.textSizeHeaderRow) {
                                        if (position != ThemeActivity.this.chatListHeaderRow) {
                                            if (position != ThemeActivity.this.bubbleRadiusHeaderRow) {
                                                if (position != ThemeActivity.this.swipeGestureHeaderRow) {
                                                    if (position != ThemeActivity.this.selectThemeHeaderRow) {
                                                        if (position == ThemeActivity.this.appIconHeaderRow) {
                                                            headerCell.setText(LocaleController.getString((int) R.string.AppIcon));
                                                            return;
                                                        }
                                                        return;
                                                    }
                                                    headerCell.setText(LocaleController.getString("SelectTheme", R.string.SelectTheme));
                                                    return;
                                                }
                                                headerCell.setText(LocaleController.getString("ChatListSwipeGesture", R.string.ChatListSwipeGesture));
                                                return;
                                            }
                                            headerCell.setText(LocaleController.getString("BubbleRadius", R.string.BubbleRadius));
                                            return;
                                        }
                                        headerCell.setText(LocaleController.getString("ChatList", R.string.ChatList));
                                        return;
                                    } else {
                                        headerCell.setText(LocaleController.getString("TextSizeHeader", R.string.TextSizeHeader));
                                        return;
                                    }
                                }
                                headerCell.setText(LocaleController.getString("SETTINGS", R.string.SETTINGS));
                                return;
                            }
                            headerCell.setText(LocaleController.getString("AutoNightPreferred", R.string.AutoNightPreferred));
                            return;
                        }
                        headerCell.setText(LocaleController.getString("AutoNightBrightness", R.string.AutoNightBrightness));
                        return;
                    }
                    headerCell.setText(LocaleController.getString("AutoNightSchedule", R.string.AutoNightSchedule));
                    return;
                case 6:
                    ((BrightnessControlCell) holder.itemView).setProgress(Theme.autoNightBrighnessThreshold);
                    return;
                case 7:
                    TextCheckCell textCheckCell = (TextCheckCell) holder.itemView;
                    if (position != ThemeActivity.this.scheduleLocationRow) {
                        if (position != ThemeActivity.this.enableAnimationsRow) {
                            if (position != ThemeActivity.this.sendByEnterRow) {
                                if (position != ThemeActivity.this.raiseToSpeakRow) {
                                    if (position != ThemeActivity.this.customTabsRow) {
                                        if (position != ThemeActivity.this.directShareRow) {
                                            if (position == ThemeActivity.this.chatBlurRow) {
                                                textCheckCell.setTextAndCheck(LocaleController.getString("BlurInChat", R.string.BlurInChat), SharedConfig.chatBlurEnabled(), true);
                                                return;
                                            }
                                            return;
                                        }
                                        textCheckCell.setTextAndValueAndCheck(LocaleController.getString("DirectShare", R.string.DirectShare), LocaleController.getString("DirectShareInfo", R.string.DirectShareInfo), SharedConfig.directShare, false, true);
                                        return;
                                    }
                                    textCheckCell.setTextAndValueAndCheck(LocaleController.getString("ChromeCustomTabs", R.string.ChromeCustomTabs), LocaleController.getString("ChromeCustomTabsInfo", R.string.ChromeCustomTabsInfo), SharedConfig.customTabs, false, true);
                                    return;
                                }
                                textCheckCell.setTextAndCheck(LocaleController.getString("RaiseToSpeak", R.string.RaiseToSpeak), SharedConfig.raiseToSpeak, true);
                                return;
                            }
                            SharedPreferences preferences2 = MessagesController.getGlobalMainSettings();
                            textCheckCell.setTextAndCheck(LocaleController.getString("SendByEnter", R.string.SendByEnter), preferences2.getBoolean("send_by_enter", false), true);
                            return;
                        }
                        SharedPreferences preferences3 = MessagesController.getGlobalMainSettings();
                        textCheckCell.setTextAndCheck(LocaleController.getString("EnableAnimations", R.string.EnableAnimations), preferences3.getBoolean("view_animations", true), true);
                        return;
                    }
                    textCheckCell.setTextAndCheck(LocaleController.getString("AutoNightLocation", R.string.AutoNightLocation), Theme.autoNightScheduleByLocation, true);
                    return;
                case 8:
                case 9:
                case 13:
                case 15:
                case 16:
                case 18:
                default:
                    return;
                case 10:
                    NotificationsCheckCell checkCell = (NotificationsCheckCell) holder.itemView;
                    if (position == ThemeActivity.this.nightThemeRow) {
                        if (Theme.selectedAutoNightType != 0) {
                            enabled = true;
                        }
                        String value3 = enabled ? Theme.getCurrentNightThemeName() : LocaleController.getString("AutoNightThemeOff", R.string.AutoNightThemeOff);
                        if (enabled) {
                            if (Theme.selectedAutoNightType == 1) {
                                type = LocaleController.getString("AutoNightScheduled", R.string.AutoNightScheduled);
                            } else if (Theme.selectedAutoNightType == 3) {
                                type = LocaleController.getString("AutoNightSystemDefault", R.string.AutoNightSystemDefault);
                            } else {
                                type = LocaleController.getString("AutoNightAdaptive", R.string.AutoNightAdaptive);
                            }
                            value3 = type + " " + value3;
                        }
                        checkCell.setTextAndValueAndCheck(LocaleController.getString("AutoNightTheme", R.string.AutoNightTheme), value3, enabled, true);
                        return;
                    }
                    return;
                case 11:
                    if (this.first) {
                        ThemeActivity.this.themesHorizontalListCell.scrollToCurrentTheme(ThemeActivity.this.listView.getMeasuredWidth(), false);
                        this.first = false;
                        return;
                    }
                    return;
                case 12:
                    RecyclerListView accentsList = (RecyclerListView) holder.itemView;
                    ThemeAccentsListAdapter adapter = (ThemeAccentsListAdapter) accentsList.getAdapter();
                    adapter.notifyDataSetChanged();
                    int pos = adapter.findCurrentAccent();
                    if (pos == -1) {
                        pos = adapter.getItemCount() - 1;
                    }
                    if (pos != -1) {
                        ((LinearLayoutManager) accentsList.getLayoutManager()).scrollToPositionWithOffset(pos, (ThemeActivity.this.listView.getMeasuredWidth() / 2) - AndroidUtilities.dp(42.0f));
                        return;
                    }
                    return;
                case 14:
                    TextCell cell3 = (TextCell) holder.itemView;
                    cell3.setColors(Theme.key_windowBackgroundWhiteBlueText4, Theme.key_windowBackgroundWhiteBlueText4);
                    if (position != ThemeActivity.this.backgroundRow) {
                        if (position != ThemeActivity.this.editThemeRow) {
                            if (position == ThemeActivity.this.createNewThemeRow) {
                                cell3.setTextAndIcon(LocaleController.getString("CreateNewTheme", R.string.CreateNewTheme), R.drawable.msg_colors, false);
                                return;
                            }
                            return;
                        }
                        cell3.setTextAndIcon(LocaleController.getString("EditCurrentTheme", R.string.EditCurrentTheme), R.drawable.msg_theme, true);
                        return;
                    }
                    cell3.setTextAndIcon(LocaleController.getString("ChangeChatBackground", R.string.ChangeChatBackground), R.drawable.msg_background, false);
                    return;
                case 17:
                    ((DefaultThemesPreviewCell) holder.itemView).updateDayNightMode();
                    return;
                case 19:
                    RadioButtonCell radioCell = (RadioButtonCell) holder.itemView;
                    if (position == ThemeActivity.this.saveToGalleryOption1Row) {
                        radioCell.setTextAndValue("save media only from peer chats", "", true, false);
                        return;
                    } else {
                        radioCell.setTextAndValue("save media from all chats", "", true, false);
                        return;
                    }
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            int type = holder.getItemViewType();
            if (type == 4) {
                ((ThemeTypeCell) holder.itemView).setTypeChecked(holder.getAdapterPosition() == Theme.selectedAutoNightType);
            }
            if (type != 2 && type != 3) {
                holder.itemView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            if (position == ThemeActivity.this.scheduleFromRow || position == ThemeActivity.this.distanceRow || position == ThemeActivity.this.scheduleToRow || position == ThemeActivity.this.scheduleUpdateLocationRow || position == ThemeActivity.this.contactsReimportRow || position == ThemeActivity.this.contactsSortRow) {
                return 1;
            }
            if (position != ThemeActivity.this.automaticBrightnessInfoRow && position != ThemeActivity.this.scheduleLocationInfoRow) {
                if (position != ThemeActivity.this.themeInfoRow && position != ThemeActivity.this.nightTypeInfoRow && position != ThemeActivity.this.scheduleFromToInfoRow && position != ThemeActivity.this.settings2Row && position != ThemeActivity.this.newThemeInfoRow && position != ThemeActivity.this.chatListInfoRow && position != ThemeActivity.this.bubbleRadiusInfoRow && position != ThemeActivity.this.swipeGestureInfoRow && position != ThemeActivity.this.saveToGallerySectionRow && position != ThemeActivity.this.appIconShadowRow) {
                    if (position != ThemeActivity.this.nightDisabledRow && position != ThemeActivity.this.nightScheduledRow && position != ThemeActivity.this.nightAutomaticRow && position != ThemeActivity.this.nightSystemDefaultRow) {
                        if (position != ThemeActivity.this.scheduleHeaderRow && position != ThemeActivity.this.automaticHeaderRow && position != ThemeActivity.this.preferedHeaderRow && position != ThemeActivity.this.settingsRow && position != ThemeActivity.this.themeHeaderRow && position != ThemeActivity.this.textSizeHeaderRow && position != ThemeActivity.this.chatListHeaderRow && position != ThemeActivity.this.bubbleRadiusHeaderRow && position != ThemeActivity.this.swipeGestureHeaderRow && position != ThemeActivity.this.selectThemeHeaderRow && position != ThemeActivity.this.appIconHeaderRow) {
                            if (position != ThemeActivity.this.automaticBrightnessRow) {
                                if (position != ThemeActivity.this.scheduleLocationRow && position != ThemeActivity.this.enableAnimationsRow && position != ThemeActivity.this.sendByEnterRow && position != ThemeActivity.this.raiseToSpeakRow && position != ThemeActivity.this.customTabsRow && position != ThemeActivity.this.directShareRow && position != ThemeActivity.this.chatBlurRow) {
                                    if (position != ThemeActivity.this.textSizeRow) {
                                        if (position != ThemeActivity.this.chatListRow) {
                                            if (position != ThemeActivity.this.nightThemeRow) {
                                                if (position != ThemeActivity.this.themeListRow) {
                                                    if (position != ThemeActivity.this.themeAccentListRow) {
                                                        if (position != ThemeActivity.this.bubbleRadiusRow) {
                                                            if (position != ThemeActivity.this.backgroundRow && position != ThemeActivity.this.editThemeRow && position != ThemeActivity.this.createNewThemeRow) {
                                                                if (position != ThemeActivity.this.swipeGestureRow) {
                                                                    if (position != ThemeActivity.this.themePreviewRow) {
                                                                        if (position != ThemeActivity.this.themeListRow2) {
                                                                            if (position == ThemeActivity.this.saveToGalleryOption1Row || position == ThemeActivity.this.saveToGalleryOption2Row) {
                                                                                return 19;
                                                                            }
                                                                            return position == ThemeActivity.this.appIconSelectorRow ? 20 : 1;
                                                                        }
                                                                        return 17;
                                                                    }
                                                                    return 16;
                                                                }
                                                                return 15;
                                                            }
                                                            return 14;
                                                        }
                                                        return 13;
                                                    }
                                                    return 12;
                                                }
                                                return 11;
                                            }
                                            return 10;
                                        }
                                        return 9;
                                    }
                                    return 8;
                                }
                                return 7;
                            }
                            return 6;
                        }
                        return 5;
                    }
                    return 4;
                }
                return 3;
            }
            return 2;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static abstract class TintRecyclerListView extends RecyclerListView {
        TintRecyclerListView(Context context) {
            super(context);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, TextCheckCell.class, HeaderCell.class, BrightnessControlCell.class, ThemeTypeCell.class, TextSizeCell.class, BubbleRadiusCell.class, ChatListCell.class, NotificationsCheckCell.class, ThemesHorizontalListCell.class, TintRecyclerListView.class, TextCell.class, SwipeGestureSettingsView.class, DefaultThemesPreviewCell.class, AppIconsSelectorCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, null, Theme.key_actionBarDefaultSubmenuBackground));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, null, Theme.key_actionBarDefaultSubmenuItem));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM | ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_actionBarDefaultSubmenuItemIcon));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteValueText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueHeader));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueText4));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueText4));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switchTrack));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switchTrackChecked));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{BrightnessControlCell.class}, new String[]{"leftImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayIcon));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{BrightnessControlCell.class}, new String[]{"rightImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayIcon));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{BrightnessControlCell.class}, new String[]{"seekBarView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_player_progressBackground));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_PROGRESSBAR, new Class[]{BrightnessControlCell.class}, new String[]{"seekBarView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_player_progress));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{ThemeTypeCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{ThemeTypeCell.class}, new String[]{"checkImage"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_featuredStickers_addedIcon));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_PROGRESSBAR, new Class[]{TextSizeCell.class}, new String[]{"sizeBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_player_progress));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, new String[]{"sizeBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_player_progressBackground));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_PROGRESSBAR, new Class[]{BubbleRadiusCell.class}, new String[]{"sizeBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_player_progress));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{BubbleRadiusCell.class}, new String[]{"sizeBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_player_progressBackground));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{ChatListCell.class}, null, null, null, Theme.key_radioBackground));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{ChatListCell.class}, null, null, null, Theme.key_radioBackgroundChecked));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText2));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switchTrack));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switchTrackChecked));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, new Drawable[]{Theme.chat_msgInDrawable, Theme.chat_msgInMediaDrawable}, null, Theme.key_chat_inBubble));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, new Drawable[]{Theme.chat_msgInSelectedDrawable, Theme.chat_msgInMediaSelectedDrawable}, null, Theme.key_chat_inBubbleSelected));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, Theme.chat_msgInDrawable.getShadowDrawables(), null, Theme.key_chat_inBubbleShadow));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, Theme.chat_msgInMediaDrawable.getShadowDrawables(), null, Theme.key_chat_inBubbleShadow));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, Theme.key_chat_outBubble));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, Theme.key_chat_outBubbleGradient1));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, Theme.key_chat_outBubbleGradient2));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, Theme.key_chat_outBubbleGradient3));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, new Drawable[]{Theme.chat_msgOutSelectedDrawable, Theme.chat_msgOutMediaSelectedDrawable}, null, Theme.key_chat_outBubbleSelected));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, Theme.key_chat_outBubbleShadow));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, new Drawable[]{Theme.chat_msgInDrawable, Theme.chat_msgInMediaDrawable}, null, Theme.key_chat_inBubbleShadow));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, null, null, Theme.key_chat_messageTextIn));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, null, null, Theme.key_chat_messageTextOut));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, new Drawable[]{Theme.chat_msgOutCheckDrawable}, null, Theme.key_chat_outSentCheck));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, new Drawable[]{Theme.chat_msgOutCheckSelectedDrawable}, null, Theme.key_chat_outSentCheckSelected));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, new Drawable[]{Theme.chat_msgOutCheckReadDrawable, Theme.chat_msgOutHalfCheckDrawable}, null, Theme.key_chat_outSentCheckRead));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, new Drawable[]{Theme.chat_msgOutCheckReadSelectedDrawable, Theme.chat_msgOutHalfCheckSelectedDrawable}, null, Theme.key_chat_outSentCheckReadSelected));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, new Drawable[]{Theme.chat_msgMediaCheckDrawable, Theme.chat_msgMediaHalfCheckDrawable}, null, Theme.key_chat_mediaSentCheck));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, null, null, Theme.key_chat_inReplyLine));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, null, null, Theme.key_chat_outReplyLine));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, null, null, Theme.key_chat_inReplyNameText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, null, null, Theme.key_chat_outReplyNameText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, null, null, Theme.key_chat_inReplyMessageText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, null, null, Theme.key_chat_outReplyMessageText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, null, null, Theme.key_chat_inReplyMediaMessageSelectedText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, null, null, Theme.key_chat_outReplyMediaMessageSelectedText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, null, null, Theme.key_chat_inTimeText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, null, null, Theme.key_chat_outTimeText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, null, null, Theme.key_chat_inTimeSelectedText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, null, null, Theme.key_chat_outTimeSelectedText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{AppIconsSelectorCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{AppIconsSelectorCell.class}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{AppIconsSelectorCell.class}, null, null, null, Theme.key_windowBackgroundWhiteHintText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{AppIconsSelectorCell.class}, null, null, null, Theme.key_windowBackgroundWhiteValueText));
        themeDescriptions.addAll(SimpleThemeDescription.createThemeDescriptions(new ThemeDescription.ThemeDescriptionDelegate() { // from class: org.telegram.ui.ThemeActivity$$ExternalSyntheticLambda0
            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public final void didSetColor() {
                ThemeActivity.this.m4621lambda$getThemeDescriptions$10$orgtelegramuiThemeActivity();
            }

            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public /* synthetic */ void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        }, Theme.key_windowBackgroundWhiteHintText, Theme.key_windowBackgroundWhiteBlackText, Theme.key_windowBackgroundWhiteValueText));
        return themeDescriptions;
    }

    /* renamed from: lambda$getThemeDescriptions$10$org-telegram-ui-ThemeActivity */
    public /* synthetic */ void m4621lambda$getThemeDescriptions$10$orgtelegramuiThemeActivity() {
        for (int i = 0; i < this.listView.getChildCount(); i++) {
            View ch = this.listView.getChildAt(i);
            if (ch instanceof AppIconsSelectorCell) {
                ((AppIconsSelectorCell) ch).getAdapter().notifyDataSetChanged();
            }
        }
        for (int i2 = 0; i2 < this.listView.getCachedChildCount(); i2++) {
            View ch2 = this.listView.getCachedChildAt(i2);
            if (ch2 instanceof AppIconsSelectorCell) {
                ((AppIconsSelectorCell) ch2).getAdapter().notifyDataSetChanged();
            }
        }
        for (int i3 = 0; i3 < this.listView.getHiddenChildCount(); i3++) {
            View ch3 = this.listView.getHiddenChildAt(i3);
            if (ch3 instanceof AppIconsSelectorCell) {
                ((AppIconsSelectorCell) ch3).getAdapter().notifyDataSetChanged();
            }
        }
        for (int i4 = 0; i4 < this.listView.getAttachedScrapChildCount(); i4++) {
            View ch4 = this.listView.getAttachedScrapChildAt(i4);
            if (ch4 instanceof AppIconsSelectorCell) {
                ((AppIconsSelectorCell) ch4).getAdapter().notifyDataSetChanged();
            }
        }
    }

    public void checkCurrentDayNight() {
        RLottieDrawable rLottieDrawable;
        if (this.currentType != 3) {
            return;
        }
        boolean toDark = !Theme.isCurrentThemeDay();
        if (this.lastIsDarkTheme != toDark) {
            this.lastIsDarkTheme = toDark;
            this.sunDrawable.setCustomEndFrame(toDark ? rLottieDrawable.getFramesCount() - 1 : 0);
            this.menuItem.getIconView().playAnimation();
        }
        if (this.themeListRow2 >= 0) {
            for (int i = 0; i < this.listView.getChildCount(); i++) {
                if (this.listView.getChildAt(i) instanceof DefaultThemesPreviewCell) {
                    DefaultThemesPreviewCell cell = (DefaultThemesPreviewCell) this.listView.getChildAt(i);
                    cell.updateDayNightMode();
                }
            }
        }
    }
}
