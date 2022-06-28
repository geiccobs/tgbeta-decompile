package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import androidx.core.content.ContextCompat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes5.dex */
public class TimerDrawable extends Drawable {
    Context context;
    ColorFilter currentColorFilter;
    private Drawable currentTtlIcon;
    private int currentTtlIconId;
    private int iconColor;
    private boolean isStaticIcon;
    private boolean overrideColor;
    Theme.ResourcesProvider resourcesProvider;
    private StaticLayout timeLayout;
    private TextPaint timePaint = new TextPaint(1);
    private Paint paint = new Paint(1);
    private Paint linePaint = new Paint(1);
    private float timeWidth = 0.0f;
    private int timeHeight = 0;
    private int time = -1;

    public TimerDrawable(Context context, Theme.ResourcesProvider resourcesProvider) {
        this.context = context;
        this.resourcesProvider = resourcesProvider;
        this.timePaint.setTypeface(AndroidUtilities.getTypeface("fonts/rcondensedbold.ttf"));
        this.linePaint.setStrokeWidth(AndroidUtilities.dp(1.0f));
        this.linePaint.setStyle(Paint.Style.STROKE);
    }

    public void setTime(int value) {
        String timeString;
        if (this.time != value) {
            this.time = value;
            Drawable mutate = ContextCompat.getDrawable(this.context, value == 0 ? R.drawable.msg_mini_autodelete : R.drawable.msg_mini_autodelete_empty).mutate();
            this.currentTtlIcon = mutate;
            mutate.setColorFilter(this.currentColorFilter);
            invalidateSelf();
            int i = this.time;
            if (i >= 1 && i < 60) {
                timeString = "" + value;
                if (timeString.length() < 2) {
                    timeString = timeString + LocaleController.getString("SecretChatTimerSeconds", R.string.SecretChatTimerSeconds);
                }
            } else if (i < 60 || i >= 3600) {
                if (i >= 3600 && i < 86400) {
                    timeString = "" + ((value / 60) / 60);
                    if (timeString.length() < 2) {
                        timeString = timeString + LocaleController.getString("SecretChatTimerHours", R.string.SecretChatTimerHours);
                    }
                } else if (i >= 86400 && i < 604800) {
                    timeString = "" + (((value / 60) / 60) / 24);
                    if (timeString.length() < 2) {
                        timeString = timeString + LocaleController.getString("SecretChatTimerDays", R.string.SecretChatTimerDays);
                    }
                } else if (i < 2678400) {
                    timeString = "" + ((((value / 60) / 60) / 24) / 7);
                    if (timeString.length() < 2) {
                        timeString = timeString + LocaleController.getString("SecretChatTimerWeeks", R.string.SecretChatTimerWeeks);
                    } else if (timeString.length() > 2) {
                        timeString = Theme.COLOR_BACKGROUND_SLUG;
                    }
                } else if (i < 31449600) {
                    timeString = "" + ((((value / 60) / 60) / 24) / 30);
                    if (timeString.length() < 2) {
                        timeString = timeString + LocaleController.getString("SecretChatTimerMonths", R.string.SecretChatTimerMonths);
                    }
                } else {
                    timeString = "" + ((((value / 60) / 60) / 24) / 364);
                    if (timeString.length() < 2) {
                        timeString = timeString + LocaleController.getString("SecretChatTimerYears", R.string.SecretChatTimerYears);
                    }
                }
            } else {
                timeString = "" + (value / 60);
                if (timeString.length() < 2) {
                    timeString = timeString + LocaleController.getString("SecretChatTimerMinutes", R.string.SecretChatTimerMinutes);
                }
            }
            this.timePaint.setTextSize(AndroidUtilities.dp(11.0f));
            float measureText = this.timePaint.measureText(timeString);
            this.timeWidth = measureText;
            if (measureText > AndroidUtilities.dp(13.0f)) {
                this.timePaint.setTextSize(AndroidUtilities.dp(9.0f));
                this.timeWidth = this.timePaint.measureText(timeString);
            }
            if (this.timeWidth > AndroidUtilities.dp(13.0f)) {
                this.timePaint.setTextSize(AndroidUtilities.dp(6.0f));
                this.timeWidth = this.timePaint.measureText(timeString);
            }
            try {
                StaticLayout staticLayout = new StaticLayout(timeString, this.timePaint, (int) Math.ceil(this.timeWidth), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                this.timeLayout = staticLayout;
                this.timeHeight = staticLayout.getHeight();
            } catch (Exception e) {
                this.timeLayout = null;
                FileLog.e(e);
            }
            invalidateSelf();
        }
    }

    public static TimerDrawable getTtlIcon(int ttl) {
        TimerDrawable timerDrawable = new TimerDrawable(ApplicationLoader.applicationContext, null);
        timerDrawable.setTime(ttl);
        timerDrawable.isStaticIcon = true;
        return timerDrawable;
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        double d;
        int width = getIntrinsicWidth();
        int height = getIntrinsicHeight();
        if (!this.isStaticIcon) {
            if (!this.overrideColor) {
                this.paint.setColor(Theme.getColor(Theme.key_actionBarDefault, this.resourcesProvider));
            }
            this.timePaint.setColor(Theme.getColor(Theme.key_actionBarDefaultTitle, this.resourcesProvider));
        } else {
            this.timePaint.setColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuItemIcon, this.resourcesProvider));
        }
        if (this.currentTtlIcon != null) {
            if (!this.isStaticIcon) {
                canvas.drawCircle(getBounds().centerX(), getBounds().centerY(), getBounds().width() / 2.0f, this.paint);
                int iconColor = Theme.getColor(Theme.key_actionBarDefaultTitle, this.resourcesProvider);
                if (this.iconColor != iconColor) {
                    this.iconColor = iconColor;
                    this.currentTtlIcon.setColorFilter(new PorterDuffColorFilter(iconColor, PorterDuff.Mode.MULTIPLY));
                }
            }
            AndroidUtilities.rectTmp2.set(getBounds().centerX() - AndroidUtilities.dp(10.5f), getBounds().centerY() - AndroidUtilities.dp(10.5f), (getBounds().centerX() - AndroidUtilities.dp(10.5f)) + this.currentTtlIcon.getIntrinsicWidth(), (getBounds().centerY() - AndroidUtilities.dp(10.5f)) + this.currentTtlIcon.getIntrinsicHeight());
            this.currentTtlIcon.setBounds(AndroidUtilities.rectTmp2);
            this.currentTtlIcon.draw(canvas);
        }
        if (this.time != 0 && this.timeLayout != null) {
            int xOffxet = 0;
            if (AndroidUtilities.density == 3.0f) {
                xOffxet = -1;
            }
            double ceil = Math.ceil(this.timeWidth / 2.0f);
            Double.isNaN(width / 2);
            canvas.translate(((int) (d - ceil)) + xOffxet, (height - this.timeHeight) / 2);
            this.timeLayout.draw(canvas);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter cf) {
        this.currentColorFilter = cf;
        if (this.isStaticIcon) {
            this.currentTtlIcon.setColorFilter(cf);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -2;
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(23.0f);
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(23.0f);
    }

    public void setBackgroundColor(int currentActionBarColor) {
        this.overrideColor = true;
        this.paint.setColor(currentActionBarColor);
    }
}
