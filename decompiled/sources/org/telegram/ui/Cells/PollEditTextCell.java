package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Property;
import android.view.ActionMode;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.google.android.exoplayer2.C;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.EditTextCaption;
import org.telegram.ui.Components.LayoutHelper;
/* loaded from: classes4.dex */
public class PollEditTextCell extends FrameLayout {
    private boolean alwaysShowText2;
    private CheckBox2 checkBox;
    private AnimatorSet checkBoxAnimation;
    private ImageView deleteImageView;
    private ImageView moveImageView;
    private boolean needDivider;
    private boolean showNextButton;
    private EditTextBoldCursor textView;
    private SimpleTextView textView2;

    public PollEditTextCell(Context context, View.OnClickListener onDelete) {
        this(context, false, onDelete);
    }

    public PollEditTextCell(Context context, boolean caption, View.OnClickListener onDelete) {
        super(context);
        if (caption) {
            EditTextCaption editTextCaption = new EditTextCaption(context, null) { // from class: org.telegram.ui.Cells.PollEditTextCell.1
                @Override // android.widget.TextView, android.view.View
                public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
                    InputConnection conn = super.onCreateInputConnection(outAttrs);
                    if (PollEditTextCell.this.showNextButton) {
                        outAttrs.imeOptions &= -1073741825;
                    }
                    return conn;
                }

                @Override // org.telegram.ui.Components.EditTextCaption, org.telegram.ui.Components.EditTextBoldCursor, org.telegram.ui.Components.EditTextEffects, android.widget.TextView, android.view.View
                public void onDraw(Canvas canvas) {
                    super.onDraw(canvas);
                    PollEditTextCell.this.onEditTextDraw(this, canvas);
                }

                @Override // org.telegram.ui.Components.EditTextBoldCursor, android.widget.TextView, android.view.View
                public boolean onTouchEvent(MotionEvent event) {
                    if (!isEnabled()) {
                        return false;
                    }
                    if (event.getAction() == 1) {
                        PollEditTextCell.this.onFieldTouchUp(this);
                    }
                    return super.onTouchEvent(event);
                }

                @Override // org.telegram.ui.Components.EditTextCaption, org.telegram.ui.Components.EditTextBoldCursor, android.view.View
                public ActionMode startActionMode(ActionMode.Callback callback, int type) {
                    ActionMode actionMode = super.startActionMode(callback, type);
                    PollEditTextCell.this.onActionModeStart(this, actionMode);
                    return actionMode;
                }

                @Override // org.telegram.ui.Components.EditTextCaption, org.telegram.ui.Components.EditTextBoldCursor, android.view.View
                public ActionMode startActionMode(ActionMode.Callback callback) {
                    ActionMode actionMode = super.startActionMode(callback);
                    PollEditTextCell.this.onActionModeStart(this, actionMode);
                    return actionMode;
                }
            };
            this.textView = editTextCaption;
            editTextCaption.setAllowTextEntitiesIntersection(true);
        } else {
            this.textView = new EditTextBoldCursor(context) { // from class: org.telegram.ui.Cells.PollEditTextCell.2
                @Override // android.widget.TextView, android.view.View
                public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
                    InputConnection conn = super.onCreateInputConnection(outAttrs);
                    if (PollEditTextCell.this.showNextButton) {
                        outAttrs.imeOptions &= -1073741825;
                    }
                    return conn;
                }

                @Override // org.telegram.ui.Components.EditTextBoldCursor, org.telegram.ui.Components.EditTextEffects, android.widget.TextView, android.view.View
                public void onDraw(Canvas canvas) {
                    super.onDraw(canvas);
                    PollEditTextCell.this.onEditTextDraw(this, canvas);
                }

                @Override // org.telegram.ui.Components.EditTextBoldCursor, android.widget.TextView, android.view.View
                public boolean onTouchEvent(MotionEvent event) {
                    if (!isEnabled()) {
                        return false;
                    }
                    if (event.getAction() == 1) {
                        PollEditTextCell.this.onFieldTouchUp(this);
                    }
                    return super.onTouchEvent(event);
                }
            };
        }
        this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.textView.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        this.textView.setTextSize(1, 16.0f);
        int i = 5;
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        this.textView.setBackgroundDrawable(null);
        EditTextBoldCursor editTextBoldCursor = this.textView;
        editTextBoldCursor.setImeOptions(editTextBoldCursor.getImeOptions() | 268435456);
        EditTextBoldCursor editTextBoldCursor2 = this.textView;
        editTextBoldCursor2.setInputType(editTextBoldCursor2.getInputType() | 16384);
        this.textView.setPadding(AndroidUtilities.dp(4.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(11.0f));
        if (onDelete != null) {
            addView(this.textView, LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 16, LocaleController.isRTL ? 58.0f : 64.0f, 0.0f, !LocaleController.isRTL ? 58.0f : 64.0f, 0.0f));
            ImageView imageView = new ImageView(context);
            this.moveImageView = imageView;
            imageView.setFocusable(false);
            this.moveImageView.setScaleType(ImageView.ScaleType.CENTER);
            this.moveImageView.setImageResource(R.drawable.poll_reorder);
            this.moveImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayIcon), PorterDuff.Mode.MULTIPLY));
            addView(this.moveImageView, LayoutHelper.createFrame(48, 48.0f, (LocaleController.isRTL ? 5 : 3) | 48, 6.0f, 2.0f, 6.0f, 0.0f));
            ImageView imageView2 = new ImageView(context);
            this.deleteImageView = imageView2;
            imageView2.setFocusable(false);
            this.deleteImageView.setScaleType(ImageView.ScaleType.CENTER);
            this.deleteImageView.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_stickers_menuSelector)));
            this.deleteImageView.setImageResource(R.drawable.poll_remove);
            this.deleteImageView.setOnClickListener(onDelete);
            this.deleteImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayIcon), PorterDuff.Mode.MULTIPLY));
            this.deleteImageView.setContentDescription(LocaleController.getString("Delete", R.string.Delete));
            addView(this.deleteImageView, LayoutHelper.createFrame(48, 50.0f, (LocaleController.isRTL ? 3 : 5) | 48, LocaleController.isRTL ? 3.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 3.0f, 0.0f));
            SimpleTextView simpleTextView = new SimpleTextView(context);
            this.textView2 = simpleTextView;
            simpleTextView.setTextSize(13);
            this.textView2.setGravity((LocaleController.isRTL ? 3 : 5) | 48);
            addView(this.textView2, LayoutHelper.createFrame(48, 24.0f, (LocaleController.isRTL ? 3 : 5) | 48, LocaleController.isRTL ? 20.0f : 0.0f, 43.0f, LocaleController.isRTL ? 0.0f : 20.0f, 0.0f));
            CheckBox2 checkBox2 = new CheckBox2(context, 21);
            this.checkBox = checkBox2;
            checkBox2.setColor(null, Theme.key_windowBackgroundWhiteGrayIcon, Theme.key_checkboxCheck);
            this.checkBox.setContentDescription(LocaleController.getString("AccDescrQuizCorrectAnswer", R.string.AccDescrQuizCorrectAnswer));
            this.checkBox.setDrawUnchecked(true);
            this.checkBox.setChecked(true, false);
            this.checkBox.setAlpha(0.0f);
            this.checkBox.setDrawBackgroundAsArc(8);
            addView(this.checkBox, LayoutHelper.createFrame(48, 48.0f, (!LocaleController.isRTL ? 3 : i) | 48, 6.0f, 2.0f, 6.0f, 0.0f));
            this.checkBox.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Cells.PollEditTextCell$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    PollEditTextCell.this.m1664lambda$new$0$orgtelegramuiCellsPollEditTextCell(view);
                }
            });
            return;
        }
        addView(this.textView, LayoutHelper.createFrame(-1, -2.0f, (!LocaleController.isRTL ? 3 : i) | 16, 19.0f, 0.0f, 19.0f, 0.0f));
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Cells-PollEditTextCell */
    public /* synthetic */ void m1664lambda$new$0$orgtelegramuiCellsPollEditTextCell(View v) {
        if (this.checkBox.getTag() == null) {
            return;
        }
        onCheckBoxClick(this, !this.checkBox.isChecked());
    }

    public void createErrorTextView() {
        this.alwaysShowText2 = true;
        SimpleTextView simpleTextView = new SimpleTextView(getContext());
        this.textView2 = simpleTextView;
        simpleTextView.setTextSize(13);
        int i = 3;
        this.textView2.setGravity((LocaleController.isRTL ? 3 : 5) | 48);
        SimpleTextView simpleTextView2 = this.textView2;
        if (!LocaleController.isRTL) {
            i = 5;
        }
        addView(simpleTextView2, LayoutHelper.createFrame(48, 24.0f, i | 48, LocaleController.isRTL ? 20.0f : 0.0f, 17.0f, LocaleController.isRTL ? 0.0f : 20.0f, 0.0f));
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int right;
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        ImageView imageView = this.deleteImageView;
        if (imageView != null) {
            imageView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), C.BUFFER_FLAG_ENCRYPTED));
        }
        ImageView imageView2 = this.moveImageView;
        if (imageView2 != null) {
            imageView2.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), C.BUFFER_FLAG_ENCRYPTED));
        }
        SimpleTextView simpleTextView = this.textView2;
        if (simpleTextView != null) {
            simpleTextView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0f), C.BUFFER_FLAG_ENCRYPTED));
        }
        CheckBox2 checkBox2 = this.checkBox;
        if (checkBox2 != null) {
            checkBox2.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), C.BUFFER_FLAG_ENCRYPTED));
        }
        if (this.textView2 == null) {
            right = 42;
        } else if (this.deleteImageView == null) {
            right = 70;
        } else {
            right = 122;
        }
        this.textView.measure(View.MeasureSpec.makeMeasureSpec(((width - getPaddingLeft()) - getPaddingRight()) - AndroidUtilities.dp(right), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(0, 0));
        int h = this.textView.getMeasuredHeight();
        setMeasuredDimension(width, Math.max(AndroidUtilities.dp(50.0f), this.textView.getMeasuredHeight()) + (this.needDivider ? 1 : 0));
        SimpleTextView simpleTextView2 = this.textView2;
        if (simpleTextView2 != null && !this.alwaysShowText2) {
            simpleTextView2.setAlpha(h >= AndroidUtilities.dp(52.0f) ? 1.0f : 0.0f);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.checkBox != null) {
            setShowCheckBox(shouldShowCheckBox(), false);
            this.checkBox.setChecked(isChecked(this), false);
        }
    }

    public void onCheckBoxClick(PollEditTextCell editText, boolean checked) {
        this.checkBox.setChecked(checked, true);
    }

    protected boolean isChecked(PollEditTextCell editText) {
        return false;
    }

    protected void onActionModeStart(EditTextBoldCursor editText, ActionMode actionMode) {
    }

    public void callOnDelete() {
        ImageView imageView = this.deleteImageView;
        if (imageView == null) {
            return;
        }
        imageView.callOnClick();
    }

    public void setShowNextButton(boolean value) {
        this.showNextButton = value;
    }

    public EditTextBoldCursor getTextView() {
        return this.textView;
    }

    public CheckBox2 getCheckBox() {
        return this.checkBox;
    }

    public void addTextWatcher(TextWatcher watcher) {
        this.textView.addTextChangedListener(watcher);
    }

    protected boolean drawDivider() {
        return true;
    }

    protected void onEditTextDraw(EditTextBoldCursor editText, Canvas canvas) {
    }

    protected boolean shouldShowCheckBox() {
        return false;
    }

    public void setChecked(boolean checked, boolean animated) {
        this.checkBox.setChecked(checked, animated);
    }

    public String getText() {
        return this.textView.getText().toString();
    }

    public int length() {
        return this.textView.length();
    }

    public void setTextColor(int color) {
        this.textView.setTextColor(color);
    }

    public void setShowCheckBox(boolean show, boolean animated) {
        if (show == (this.checkBox.getTag() != null)) {
            return;
        }
        AnimatorSet animatorSet = this.checkBoxAnimation;
        Integer num = null;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.checkBoxAnimation = null;
        }
        CheckBox2 checkBox2 = this.checkBox;
        if (show) {
            num = 1;
        }
        checkBox2.setTag(num);
        float f = 1.0f;
        if (animated) {
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.checkBoxAnimation = animatorSet2;
            Animator[] animatorArr = new Animator[2];
            CheckBox2 checkBox22 = this.checkBox;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = show ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(checkBox22, property, fArr);
            ImageView imageView = this.moveImageView;
            Property property2 = View.ALPHA;
            float[] fArr2 = new float[1];
            if (show) {
                f = 0.0f;
            }
            fArr2[0] = f;
            animatorArr[1] = ObjectAnimator.ofFloat(imageView, property2, fArr2);
            animatorSet2.playTogether(animatorArr);
            this.checkBoxAnimation.setDuration(180L);
            this.checkBoxAnimation.start();
            return;
        }
        this.checkBox.setAlpha(show ? 1.0f : 0.0f);
        ImageView imageView2 = this.moveImageView;
        if (show) {
            f = 0.0f;
        }
        imageView2.setAlpha(f);
    }

    public void setText(CharSequence text, boolean divider) {
        this.textView.setText(text);
        this.needDivider = divider;
        setWillNotDraw(!divider);
    }

    public void setTextAndHint(CharSequence text, String hint, boolean divider) {
        ImageView imageView = this.deleteImageView;
        if (imageView != null) {
            imageView.setTag(null);
        }
        this.textView.setText(text);
        if (!TextUtils.isEmpty(text)) {
            EditTextBoldCursor editTextBoldCursor = this.textView;
            editTextBoldCursor.setSelection(editTextBoldCursor.length());
        }
        this.textView.setHint(hint);
        this.needDivider = divider;
        setWillNotDraw(!divider);
    }

    public void setEnabled(boolean value, ArrayList<Animator> animators) {
        setEnabled(value);
    }

    protected void onFieldTouchUp(EditTextBoldCursor editText) {
    }

    public void setText2(String text) {
        SimpleTextView simpleTextView = this.textView2;
        if (simpleTextView == null) {
            return;
        }
        simpleTextView.setText(text);
    }

    public SimpleTextView getTextView2() {
        return this.textView2;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        float f;
        int i;
        if (this.needDivider && drawDivider()) {
            float f2 = 63.0f;
            if (LocaleController.isRTL) {
                f = 0.0f;
            } else {
                f = AndroidUtilities.dp(this.moveImageView != null ? 63.0f : 20.0f);
            }
            float measuredHeight = getMeasuredHeight() - 1;
            int measuredWidth = getMeasuredWidth();
            if (LocaleController.isRTL) {
                if (this.moveImageView == null) {
                    f2 = 20.0f;
                }
                i = AndroidUtilities.dp(f2);
            } else {
                i = 0;
            }
            canvas.drawLine(f, measuredHeight, measuredWidth - i, getMeasuredHeight() - 1, Theme.dividerPaint);
        }
    }
}
