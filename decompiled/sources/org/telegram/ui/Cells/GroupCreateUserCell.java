package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import com.google.android.exoplayer2.C;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
/* loaded from: classes4.dex */
public class GroupCreateUserCell extends FrameLayout {
    private ValueAnimator animator;
    private AvatarDrawable avatarDrawable;
    private BackupImageView avatarImageView;
    private CheckBox2 checkBox;
    private int checkBoxType;
    private float checkProgress;
    private int currentAccount;
    private CharSequence currentName;
    private Object currentObject;
    private CharSequence currentStatus;
    private boolean drawDivider;
    private boolean forceDarkTheme;
    private boolean isChecked;
    private TLRPC.FileLocation lastAvatar;
    private String lastName;
    private int lastStatus;
    private SimpleTextView nameTextView;
    private int padding;
    private Paint paint;
    private boolean showSelfAsSaved;
    private SimpleTextView statusTextView;

    public GroupCreateUserCell(Context context, int checkBoxType, int pad, boolean selfAsSaved) {
        this(context, checkBoxType, pad, selfAsSaved, false);
    }

    public GroupCreateUserCell(Context context, int checkBoxType, int pad, boolean selfAsSaved, boolean forCall) {
        super(context);
        this.currentAccount = UserConfig.selectedAccount;
        this.checkBoxType = checkBoxType;
        this.forceDarkTheme = forCall;
        this.drawDivider = false;
        this.padding = pad;
        this.showSelfAsSaved = selfAsSaved;
        this.avatarDrawable = new AvatarDrawable();
        BackupImageView backupImageView = new BackupImageView(context);
        this.avatarImageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(24.0f));
        addView(this.avatarImageView, LayoutHelper.createFrame(46, 46.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : this.padding + 13, 6.0f, LocaleController.isRTL ? this.padding + 13 : 0.0f, 0.0f));
        SimpleTextView simpleTextView = new SimpleTextView(context);
        this.nameTextView = simpleTextView;
        simpleTextView.setTextColor(Theme.getColor(this.forceDarkTheme ? Theme.key_voipgroup_nameText : Theme.key_windowBackgroundWhiteBlackText));
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.nameTextView.setTextSize(16);
        this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        int i = 28;
        addView(this.nameTextView, LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, (LocaleController.isRTL ? 28 : 72) + this.padding, 10.0f, (LocaleController.isRTL ? 72 : 28) + this.padding, 0.0f));
        SimpleTextView simpleTextView2 = new SimpleTextView(context);
        this.statusTextView = simpleTextView2;
        simpleTextView2.setTextSize(14);
        this.statusTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.statusTextView, LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, (LocaleController.isRTL ? 28 : 72) + this.padding, 32.0f, (LocaleController.isRTL ? 72 : i) + this.padding, 0.0f));
        if (checkBoxType == 1) {
            CheckBox2 checkBox2 = new CheckBox2(context, 21);
            this.checkBox = checkBox2;
            checkBox2.setColor(null, Theme.key_windowBackgroundWhite, Theme.key_checkboxCheck);
            this.checkBox.setDrawUnchecked(false);
            this.checkBox.setDrawBackgroundAsArc(3);
            addView(this.checkBox, LayoutHelper.createFrame(24, 24.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : this.padding + 40, 33.0f, LocaleController.isRTL ? this.padding + 39 : 0.0f, 0.0f));
        } else if (checkBoxType == 2) {
            Paint paint = new Paint(1);
            this.paint = paint;
            paint.setStyle(Paint.Style.STROKE);
            this.paint.setStrokeWidth(AndroidUtilities.dp(2.0f));
        }
        setWillNotDraw(false);
    }

    public void setObject(TLObject object, CharSequence name, CharSequence status, boolean drawDivider) {
        setObject(object, name, status);
        this.drawDivider = drawDivider;
    }

    public void setObject(Object object, CharSequence name, CharSequence status) {
        this.currentObject = object;
        this.currentStatus = status;
        this.currentName = name;
        this.drawDivider = false;
        update(0);
    }

    public void setChecked(boolean checked, boolean animated) {
        CheckBox2 checkBox2 = this.checkBox;
        if (checkBox2 != null) {
            checkBox2.setChecked(checked, animated);
        } else if (this.checkBoxType != 2 || this.isChecked == checked) {
        } else {
            this.isChecked = checked;
            ValueAnimator valueAnimator = this.animator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            if (animated) {
                ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
                this.animator = ofFloat;
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Cells.GroupCreateUserCell$$ExternalSyntheticLambda0
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                        GroupCreateUserCell.this.m1655lambda$setChecked$0$orgtelegramuiCellsGroupCreateUserCell(valueAnimator2);
                    }
                });
                this.animator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Cells.GroupCreateUserCell.1
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        GroupCreateUserCell.this.animator = null;
                    }
                });
                this.animator.setDuration(180L);
                this.animator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                this.animator.start();
            } else {
                float f = 0.82f;
                float f2 = 1.0f;
                this.avatarImageView.setScaleX(this.isChecked ? 0.82f : 1.0f);
                BackupImageView backupImageView = this.avatarImageView;
                if (!this.isChecked) {
                    f = 1.0f;
                }
                backupImageView.setScaleY(f);
                if (!this.isChecked) {
                    f2 = 0.0f;
                }
                this.checkProgress = f2;
            }
            invalidate();
        }
    }

    /* renamed from: lambda$setChecked$0$org-telegram-ui-Cells-GroupCreateUserCell */
    public /* synthetic */ void m1655lambda$setChecked$0$orgtelegramuiCellsGroupCreateUserCell(ValueAnimator animation) {
        float v = ((Float) animation.getAnimatedValue()).floatValue();
        float scale = this.isChecked ? 1.0f - (0.18f * v) : 0.82f + (0.18f * v);
        this.avatarImageView.setScaleX(scale);
        this.avatarImageView.setScaleY(scale);
        this.checkProgress = this.isChecked ? v : 1.0f - v;
        invalidate();
    }

    public void setCheckBoxEnabled(boolean enabled) {
        CheckBox2 checkBox2 = this.checkBox;
        if (checkBox2 != null) {
            checkBox2.setEnabled(enabled);
        }
    }

    public boolean isChecked() {
        CheckBox2 checkBox2 = this.checkBox;
        if (checkBox2 != null) {
            return checkBox2.isChecked();
        }
        return this.isChecked;
    }

    public Object getObject() {
        return this.currentObject;
    }

    public void setDrawDivider(boolean value) {
        this.drawDivider = value;
        invalidate();
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(this.currentObject instanceof String ? 50.0f : 58.0f), C.BUFFER_FLAG_ENCRYPTED));
    }

    public void recycle() {
        this.avatarImageView.getImageReceiver().cancelLoadImage();
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:28:0x00b2, code lost:
        if (r1.equals("contacts") != false) goto L39;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void update(int r17) {
        /*
            Method dump skipped, instructions count: 1198
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.GroupCreateUserCell.update(int):void");
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float f = 0.0f;
        if (this.checkBoxType == 2 && (this.isChecked || this.checkProgress > 0.0f)) {
            this.paint.setColor(Theme.getColor(Theme.key_checkboxSquareBackground));
            float cx = this.avatarImageView.getLeft() + (this.avatarImageView.getMeasuredWidth() / 2);
            float cy = this.avatarImageView.getTop() + (this.avatarImageView.getMeasuredHeight() / 2);
            canvas.drawCircle(cx, cy, AndroidUtilities.dp(18.0f) + (AndroidUtilities.dp(4.0f) * this.checkProgress), this.paint);
        }
        if (this.drawDivider) {
            int start = AndroidUtilities.dp(LocaleController.isRTL ? 0.0f : this.padding + 72);
            int measuredWidth = getMeasuredWidth();
            if (LocaleController.isRTL) {
                f = this.padding + 72;
            }
            int end = measuredWidth - AndroidUtilities.dp(f);
            if (this.forceDarkTheme) {
                Theme.dividerExtraPaint.setColor(Theme.getColor(Theme.key_voipgroup_actionBar));
                canvas.drawRect(start, getMeasuredHeight() - 1, end, getMeasuredHeight(), Theme.dividerExtraPaint);
                return;
            }
            canvas.drawRect(start, getMeasuredHeight() - 1, end, getMeasuredHeight(), Theme.dividerPaint);
        }
    }

    @Override // android.view.View
    public boolean hasOverlappingRendering() {
        return false;
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        if (isChecked()) {
            info.setCheckable(true);
            info.setChecked(true);
        }
    }
}
