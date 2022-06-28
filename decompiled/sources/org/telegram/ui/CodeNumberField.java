package org.telegram.ui;

import android.animation.ValueAnimator;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.text.Editable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import androidx.core.content.ContextCompat;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import com.microsoft.appcenter.crashes.ingestion.models.ErrorAttachmentLog;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.SimpleFloatPropertyCompat;
/* loaded from: classes4.dex */
public class CodeNumberField extends EditTextBoldCursor {
    ActionMode actionMode;
    ValueAnimator enterAnimator;
    private float errorProgress;
    ValueAnimator exitAnimator;
    Bitmap exitBitmap;
    Canvas exitCanvas;
    private float focusedProgress;
    boolean replaceAnimation;
    private float successProgress;
    private static final float SPRING_MULTIPLIER = 100.0f;
    private static final FloatPropertyCompat<CodeNumberField> FOCUSED_PROGRESS = new SimpleFloatPropertyCompat("focusedProgress", CodeNumberField$$ExternalSyntheticLambda2.INSTANCE, CodeNumberField$$ExternalSyntheticLambda6.INSTANCE).setMultiplier(SPRING_MULTIPLIER);
    private static final FloatPropertyCompat<CodeNumberField> ERROR_PROGRESS = new SimpleFloatPropertyCompat("errorProgress", CodeNumberField$$ExternalSyntheticLambda3.INSTANCE, CodeNumberField$$ExternalSyntheticLambda7.INSTANCE).setMultiplier(SPRING_MULTIPLIER);
    private static final FloatPropertyCompat<CodeNumberField> SUCCESS_PROGRESS = new SimpleFloatPropertyCompat("successProgress", CodeNumberField$$ExternalSyntheticLambda4.INSTANCE, CodeNumberField$$ExternalSyntheticLambda8.INSTANCE).setMultiplier(SPRING_MULTIPLIER);
    private static final FloatPropertyCompat<CodeNumberField> SUCCESS_SCALE_PROGRESS = new SimpleFloatPropertyCompat("successScaleProgress", CodeNumberField$$ExternalSyntheticLambda5.INSTANCE, CodeNumberField$$ExternalSyntheticLambda9.INSTANCE).setMultiplier(SPRING_MULTIPLIER);
    private float successScaleProgress = 1.0f;
    private SpringAnimation focusedSpringAnimation = new SpringAnimation(this, FOCUSED_PROGRESS);
    private SpringAnimation errorSpringAnimation = new SpringAnimation(this, ERROR_PROGRESS);
    private SpringAnimation successSpringAnimation = new SpringAnimation(this, SUCCESS_PROGRESS);
    private SpringAnimation successScaleSpringAnimation = new SpringAnimation(this, SUCCESS_SCALE_PROGRESS);
    private boolean showSoftInputOnFocusInternal = true;
    float enterAnimation = 1.0f;
    float exitAnimation = 1.0f;
    boolean pressed = false;
    float startX = 0.0f;
    float startY = 0.0f;

    public static /* synthetic */ void lambda$static$1(CodeNumberField obj, float value) {
        obj.focusedProgress = value;
        if (obj.getParent() != null) {
            ((View) obj.getParent()).invalidate();
        }
    }

    public static /* synthetic */ void lambda$static$3(CodeNumberField obj, float value) {
        obj.errorProgress = value;
        if (obj.getParent() != null) {
            ((View) obj.getParent()).invalidate();
        }
    }

    public static /* synthetic */ void lambda$static$5(CodeNumberField obj, float value) {
        obj.successProgress = value;
        if (obj.getParent() != null) {
            ((View) obj.getParent()).invalidate();
        }
    }

    public static /* synthetic */ void lambda$static$7(CodeNumberField obj, float value) {
        obj.successScaleProgress = value;
        if (obj.getParent() != null) {
            ((View) obj.getParent()).invalidate();
        }
    }

    public CodeNumberField(Context context) {
        super(context);
        setBackground(null);
        setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        setMovementMethod(null);
        addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.CodeNumberField.1
            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                CodeNumberField.this.startEnterAnimation(charSequence.length() != 0);
                CodeNumberField.this.hideActionMode();
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    public void setShowSoftInputOnFocusCompat(boolean showSoftInputOnFocus) {
        this.showSoftInputOnFocusInternal = showSoftInputOnFocus;
        if (Build.VERSION.SDK_INT >= 21) {
            setShowSoftInputOnFocus(showSoftInputOnFocus);
        }
    }

    public float getFocusedProgress() {
        return this.focusedProgress;
    }

    public void animateFocusedProgress(float newProgress) {
        animateSpring(this.focusedSpringAnimation, SPRING_MULTIPLIER * newProgress);
    }

    public float getErrorProgress() {
        return this.errorProgress;
    }

    public void animateErrorProgress(float newProgress) {
        animateSpring(this.errorSpringAnimation, SPRING_MULTIPLIER * newProgress);
    }

    public float getSuccessProgress() {
        return this.successProgress;
    }

    public float getSuccessScaleProgress() {
        return this.successScaleProgress;
    }

    public void animateSuccessProgress(float newProgress) {
        animateSpring(this.successSpringAnimation, newProgress * SPRING_MULTIPLIER);
        this.successScaleSpringAnimation.cancel();
        if (newProgress != 0.0f) {
            this.successScaleSpringAnimation.setSpring(new SpringForce(1.0f).setStiffness(500.0f).setDampingRatio(0.75f).setFinalPosition(SPRING_MULTIPLIER)).setStartValue(SPRING_MULTIPLIER).setStartVelocity(4000.0f).start();
        } else {
            this.successScaleProgress = 1.0f;
        }
    }

    private void animateSpring(SpringAnimation anim, float progress) {
        if (anim.getSpring() == null || progress != anim.getSpring().getFinalPosition()) {
            anim.cancel();
            anim.setSpring(new SpringForce(progress).setStiffness(400.0f).setDampingRatio(1.0f).setFinalPosition(progress)).start();
        }
    }

    @Override // org.telegram.ui.Components.EditTextBoldCursor, org.telegram.ui.Components.EditTextEffects, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.focusedSpringAnimation.cancel();
        this.errorSpringAnimation.cancel();
    }

    public void startExitAnimation() {
        if (getMeasuredHeight() == 0 || getMeasuredWidth() == 0 || getLayout() == null) {
            return;
        }
        Bitmap bitmap = this.exitBitmap;
        if (bitmap == null || bitmap.getHeight() != getMeasuredHeight() || this.exitBitmap.getWidth() != getMeasuredWidth()) {
            Bitmap bitmap2 = this.exitBitmap;
            if (bitmap2 != null) {
                bitmap2.recycle();
            }
            this.exitBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            this.exitCanvas = new Canvas(this.exitBitmap);
        }
        this.exitBitmap.eraseColor(0);
        CharSequence transformed = getTransformationMethod().getTransformation(getText(), this);
        StaticLayout staticLayout = new StaticLayout(transformed, getLayout().getPaint(), (int) Math.ceil(getLayout().getPaint().measureText(transformed, 0, transformed.length())), Layout.Alignment.ALIGN_NORMAL, getLineSpacingMultiplier(), getLineSpacingExtra(), getIncludeFontPadding());
        this.exitCanvas.save();
        this.exitCanvas.translate((getMeasuredWidth() - staticLayout.getWidth()) / 2.0f, (getMeasuredHeight() - staticLayout.getHeight()) / 2.0f);
        staticLayout.draw(this.exitCanvas);
        this.exitCanvas.restore();
        this.exitAnimation = 0.0f;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.exitAnimator = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.CodeNumberField$$ExternalSyntheticLambda1
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                CodeNumberField.this.m2178lambda$startExitAnimation$8$orgtelegramuiCodeNumberField(valueAnimator);
            }
        });
        this.exitAnimator.setDuration(220L);
        this.exitAnimator.start();
    }

    /* renamed from: lambda$startExitAnimation$8$org-telegram-ui-CodeNumberField */
    public /* synthetic */ void m2178lambda$startExitAnimation$8$orgtelegramuiCodeNumberField(ValueAnimator valueAnimator1) {
        this.exitAnimation = ((Float) valueAnimator1.getAnimatedValue()).floatValue();
        invalidate();
        if (getParent() != null) {
            ((ViewGroup) getParent()).invalidate();
        }
    }

    public void startEnterAnimation(boolean replace) {
        this.replaceAnimation = replace;
        this.enterAnimation = 0.0f;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.enterAnimator = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.CodeNumberField$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                CodeNumberField.this.m2177lambda$startEnterAnimation$9$orgtelegramuiCodeNumberField(valueAnimator);
            }
        });
        if (!this.replaceAnimation) {
            this.enterAnimator.setInterpolator(new OvershootInterpolator(1.5f));
            this.enterAnimator.setDuration(350L);
        } else {
            this.enterAnimator.setDuration(220L);
        }
        this.enterAnimator.start();
    }

    /* renamed from: lambda$startEnterAnimation$9$org-telegram-ui-CodeNumberField */
    public /* synthetic */ void m2177lambda$startEnterAnimation$9$orgtelegramuiCodeNumberField(ValueAnimator valueAnimator1) {
        this.enterAnimation = ((Float) valueAnimator1.getAnimatedValue()).floatValue();
        invalidate();
        if (getParent() != null) {
            ((ViewGroup) getParent()).invalidate();
        }
    }

    @Override // android.view.View
    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
        ((ViewGroup) getParent()).invalidate();
        return super.requestFocus(direction, previouslyFocusedRect);
    }

    @Override // org.telegram.ui.Components.EditTextBoldCursor, android.widget.TextView, android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == 0) {
            this.pressed = true;
            this.startX = event.getX();
            this.startY = event.getY();
        }
        if (event.getAction() == 1 || event.getAction() == 3) {
            CodeFieldContainer codeFieldContainer = null;
            if (getParent() instanceof CodeFieldContainer) {
                codeFieldContainer = (CodeFieldContainer) getParent();
            }
            if (event.getAction() == 1 && this.pressed) {
                if (isFocused() && codeFieldContainer != null) {
                    ClipboardManager clipboard = (ClipboardManager) ContextCompat.getSystemService(getContext(), ClipboardManager.class);
                    if (clipboard == null || clipboard.getPrimaryClipDescription() == null) {
                        return false;
                    }
                    clipboard.getPrimaryClipDescription().hasMimeType(ErrorAttachmentLog.CONTENT_TYPE_TEXT_PLAIN);
                    ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
                    int i = -1;
                    String text = item.getText().toString();
                    try {
                        i = Integer.parseInt(text);
                    } catch (Exception e) {
                    }
                    if (i > 0) {
                        startActionMode(new ActionMode.Callback() { // from class: org.telegram.ui.CodeNumberField.2
                            @Override // android.view.ActionMode.Callback
                            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                                menu.add(0, 16908322, 0, 17039371);
                                return true;
                            }

                            @Override // android.view.ActionMode.Callback
                            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                                return true;
                            }

                            @Override // android.view.ActionMode.Callback
                            public boolean onActionItemClicked(ActionMode mode, MenuItem item2) {
                                switch (item2.getItemId()) {
                                    case 16908322:
                                        CodeNumberField.this.pasteFromClipboard();
                                        CodeNumberField.this.hideActionMode();
                                        return true;
                                    default:
                                        return true;
                                }
                            }

                            @Override // android.view.ActionMode.Callback
                            public void onDestroyActionMode(ActionMode mode) {
                            }
                        });
                    }
                } else {
                    requestFocus();
                }
                setSelection(0);
                if (this.showSoftInputOnFocusInternal) {
                    AndroidUtilities.showKeyboard(this);
                }
            }
            this.pressed = false;
        }
        return this.pressed;
    }

    public void pasteFromClipboard() {
        ClipboardManager clipboard;
        CodeFieldContainer codeFieldContainer = null;
        if (getParent() instanceof CodeFieldContainer) {
            codeFieldContainer = (CodeFieldContainer) getParent();
        }
        if (codeFieldContainer == null || (clipboard = (ClipboardManager) ContextCompat.getSystemService(getContext(), ClipboardManager.class)) == null) {
            return;
        }
        clipboard.getPrimaryClipDescription().hasMimeType(ErrorAttachmentLog.CONTENT_TYPE_TEXT_PLAIN);
        ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
        int i = -1;
        String text = item.getText().toString();
        try {
            i = Integer.parseInt(text);
        } catch (Exception e) {
        }
        if (i > 0) {
            codeFieldContainer.setText(text, true);
        }
    }

    @Override // org.telegram.ui.Components.EditTextBoldCursor, android.widget.TextView, android.view.View
    public void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (!isFocused()) {
            hideActionMode();
        }
    }
}
