package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.google.android.exoplayer2.C;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocationController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.ShareLocationDrawable;
/* loaded from: classes4.dex */
public class SendLocationCell extends FrameLayout {
    private SimpleTextView accurateTextView;
    private long dialogId;
    private ImageView imageView;
    private boolean live;
    private RectF rect;
    private final Theme.ResourcesProvider resourcesProvider;
    private SimpleTextView titleTextView;
    private int currentAccount = UserConfig.selectedAccount;
    private Runnable invalidateRunnable = new Runnable() { // from class: org.telegram.ui.Cells.SendLocationCell.1
        @Override // java.lang.Runnable
        public void run() {
            SendLocationCell.this.checkText();
            SendLocationCell sendLocationCell = SendLocationCell.this;
            sendLocationCell.invalidate(((int) sendLocationCell.rect.left) - 5, ((int) SendLocationCell.this.rect.top) - 5, ((int) SendLocationCell.this.rect.right) + 5, ((int) SendLocationCell.this.rect.bottom) + 5);
            AndroidUtilities.runOnUIThread(SendLocationCell.this.invalidateRunnable, 1000L);
        }
    };

    public SendLocationCell(Context context, boolean live, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.resourcesProvider = resourcesProvider;
        this.live = live;
        this.imageView = new ImageView(context);
        setBackground(Theme.AdaptiveRipple.rect());
        this.imageView.setTag(live ? "location_sendLiveLocationBackgroundlocation_sendLiveLocationIcon" : "location_sendLocationBackgroundlocation_sendLocationIcon");
        int dp = AndroidUtilities.dp(42.0f);
        String str = Theme.key_location_sendLiveLocationBackground;
        Drawable circle = Theme.createSimpleSelectorCircleDrawable(dp, getThemedColor(live ? str : Theme.key_location_sendLocationBackground), getThemedColor(!live ? Theme.key_location_sendLocationBackground : str));
        if (live) {
            this.rect = new RectF();
            Drawable drawable = new ShareLocationDrawable(context, 4);
            drawable.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_location_sendLiveLocationIcon), PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable = new CombinedDrawable(circle, drawable);
            combinedDrawable.setCustomSize(AndroidUtilities.dp(42.0f), AndroidUtilities.dp(42.0f));
            this.imageView.setBackgroundDrawable(combinedDrawable);
            AndroidUtilities.runOnUIThread(this.invalidateRunnable, 1000L);
            setWillNotDraw(false);
        } else {
            Drawable drawable2 = getResources().getDrawable(R.drawable.pin);
            drawable2.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_location_sendLocationIcon), PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable2 = new CombinedDrawable(circle, drawable2);
            combinedDrawable2.setCustomSize(AndroidUtilities.dp(42.0f), AndroidUtilities.dp(42.0f));
            combinedDrawable2.setIconSize(AndroidUtilities.dp(24.0f), AndroidUtilities.dp(24.0f));
            this.imageView.setBackgroundDrawable(combinedDrawable2);
        }
        int i = 5;
        addView(this.imageView, LayoutHelper.createFrame(42, 42.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 15.0f, 12.0f, !LocaleController.isRTL ? 0.0f : 15.0f, 0.0f));
        SimpleTextView simpleTextView = new SimpleTextView(context);
        this.titleTextView = simpleTextView;
        simpleTextView.setTextSize(16);
        SimpleTextView simpleTextView2 = this.titleTextView;
        String str2 = Theme.key_location_sendLiveLocationText;
        simpleTextView2.setTag(live ? str2 : Theme.key_location_sendLocationText);
        this.titleTextView.setTextColor(getThemedColor(!live ? Theme.key_location_sendLocationText : str2));
        this.titleTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.titleTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        addView(this.titleTextView, LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 16.0f : 73.0f, 12.0f, LocaleController.isRTL ? 73.0f : 16.0f, 0.0f));
        SimpleTextView simpleTextView3 = new SimpleTextView(context);
        this.accurateTextView = simpleTextView3;
        simpleTextView3.setTextSize(14);
        this.accurateTextView.setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteGrayText3));
        this.accurateTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        addView(this.accurateTextView, LayoutHelper.createFrame(-1, 20.0f, (!LocaleController.isRTL ? 3 : i) | 48, LocaleController.isRTL ? 16.0f : 73.0f, 37.0f, LocaleController.isRTL ? 73.0f : 16.0f, 0.0f));
    }

    private ImageView getImageView() {
        return this.imageView;
    }

    public void setHasLocation(boolean value) {
        LocationController.SharingLocationInfo info = LocationController.getInstance(this.currentAccount).getSharingLocationInfo(this.dialogId);
        if (info == null) {
            float f = 1.0f;
            this.titleTextView.setAlpha(value ? 1.0f : 0.5f);
            this.accurateTextView.setAlpha(value ? 1.0f : 0.5f);
            ImageView imageView = this.imageView;
            if (!value) {
                f = 0.5f;
            }
            imageView.setAlpha(f);
        }
        if (this.live) {
            checkText();
        }
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(66.0f), C.BUFFER_FLAG_ENCRYPTED));
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        AndroidUtilities.cancelRunOnUIThread(this.invalidateRunnable);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.rect != null) {
            AndroidUtilities.runOnUIThread(this.invalidateRunnable, 1000L);
        }
    }

    public void setText(String title, String text) {
        this.titleTextView.setText(title);
        this.accurateTextView.setText(text);
    }

    public void setDialogId(long did) {
        this.dialogId = did;
        if (this.live) {
            checkText();
        }
    }

    public void checkText() {
        LocationController.SharingLocationInfo info = LocationController.getInstance(this.currentAccount).getSharingLocationInfo(this.dialogId);
        if (info != null) {
            setText(LocaleController.getString("StopLiveLocation", R.string.StopLiveLocation), LocaleController.formatLocationUpdateDate(info.messageObject.messageOwner.edit_date != 0 ? info.messageObject.messageOwner.edit_date : info.messageObject.messageOwner.date));
        } else {
            setText(LocaleController.getString("SendLiveLocation", R.string.SendLiveLocation), LocaleController.getString("SendLiveLocationInfo", R.string.SendLiveLocationInfo));
        }
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        int currentTime;
        LocationController.SharingLocationInfo currentInfo = LocationController.getInstance(this.currentAccount).getSharingLocationInfo(this.dialogId);
        if (currentInfo == null || currentInfo.stopTime < (currentTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime())) {
            return;
        }
        float progress = Math.abs(currentInfo.stopTime - currentTime) / currentInfo.period;
        if (LocaleController.isRTL) {
            this.rect.set(AndroidUtilities.dp(13.0f), AndroidUtilities.dp(18.0f), AndroidUtilities.dp(43.0f), AndroidUtilities.dp(48.0f));
        } else {
            this.rect.set(getMeasuredWidth() - AndroidUtilities.dp(43.0f), AndroidUtilities.dp(18.0f), getMeasuredWidth() - AndroidUtilities.dp(13.0f), AndroidUtilities.dp(48.0f));
        }
        int color = getThemedColor(Theme.key_location_liveLocationProgress);
        Theme.chat_radialProgress2Paint.setColor(color);
        Theme.chat_livePaint.setColor(color);
        canvas.drawArc(this.rect, -90.0f, progress * (-360.0f), false, Theme.chat_radialProgress2Paint);
        String text = LocaleController.formatLocationLeftTime(Math.abs(currentInfo.stopTime - currentTime));
        float size = Theme.chat_livePaint.measureText(text);
        canvas.drawText(text, this.rect.centerX() - (size / 2.0f), AndroidUtilities.dp(37.0f), Theme.chat_livePaint);
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
