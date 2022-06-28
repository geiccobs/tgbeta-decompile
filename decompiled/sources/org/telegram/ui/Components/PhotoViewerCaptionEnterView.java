package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Vibrator;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AdjustPanLayoutHelper;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.EditTextCaption;
import org.telegram.ui.Components.EmojiView;
import org.telegram.ui.Components.SizeNotifierFrameLayoutPhoto;
/* loaded from: classes5.dex */
public class PhotoViewerCaptionEnterView extends FrameLayout implements NotificationCenter.NotificationCenterDelegate, SizeNotifierFrameLayoutPhoto.SizeNotifierFrameLayoutPhotoDelegate {
    private NumberTextView captionLimitView;
    private float chatActivityEnterViewAnimateFromTop;
    private Drawable checkDrawable;
    private int codePointCount;
    public int currentAccount;
    private PhotoViewerCaptionEnterViewDelegate delegate;
    private final ImageView doneButton;
    private ImageView emojiButton;
    private ReplaceableIconDrawable emojiIconDrawable;
    private int emojiPadding;
    private EmojiView emojiView;
    private boolean forceFloatingEmoji;
    private boolean innerTextChange;
    private boolean isInitLineCount;
    private int keyboardHeight;
    private int keyboardHeightLand;
    private boolean keyboardVisible;
    int lastShow;
    private int lastSizeChangeValue1;
    private boolean lastSizeChangeValue2;
    private String lengthText;
    private TextPaint lengthTextPaint;
    private int lineCount;
    private EditTextCaption messageEditText;
    ValueAnimator messageEditTextAnimator;
    private int messageEditTextPredrawHeigth;
    private int messageEditTextPredrawScrollY;
    Paint paint;
    private boolean popupAnimating;
    private final Theme.ResourcesProvider resourcesProvider;
    private ValueAnimator sendButtonColorAnimator;
    private boolean shouldAnimateEditTextWithBounds;
    private SizeNotifierFrameLayoutPhoto sizeNotifierLayout;
    ValueAnimator topBackgroundAnimator;
    private View windowView;
    boolean sendButtonEnabled = true;
    private float sendButtonEnabledProgress = 1.0f;
    float animationProgress = 0.0f;
    float offset = 0.0f;
    private Drawable doneDrawable = Theme.createCircleDrawable(AndroidUtilities.dp(16.0f), -10043398);

    /* loaded from: classes5.dex */
    public interface PhotoViewerCaptionEnterViewDelegate {
        void onCaptionEnter();

        void onEmojiViewCloseEnd();

        void onEmojiViewCloseStart();

        void onEmojiViewOpen();

        void onTextChanged(CharSequence charSequence);

        void onWindowSizeChanged(int i);
    }

    public int getCaptionLimitOffset() {
        return MessagesController.getInstance(this.currentAccount).getCaptionMaxLengthLimit() - this.codePointCount;
    }

    public PhotoViewerCaptionEnterView(Context context, SizeNotifierFrameLayoutPhoto parent, View window, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.currentAccount = UserConfig.selectedAccount;
        Paint paint = new Paint();
        this.paint = paint;
        this.resourcesProvider = resourcesProvider;
        paint.setColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
        setWillNotDraw(false);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setClipChildren(false);
        this.windowView = window;
        this.sizeNotifierLayout = parent;
        LinearLayout textFieldContainer = new LinearLayout(context);
        textFieldContainer.setClipChildren(false);
        textFieldContainer.setOrientation(0);
        addView(textFieldContainer, LayoutHelper.createFrame(-1, -2.0f, 51, 2.0f, 0.0f, 0.0f, 0.0f));
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setClipChildren(false);
        textFieldContainer.addView(frameLayout, LayoutHelper.createLinear(0, -2, 1.0f));
        ImageView imageView = new ImageView(context);
        this.emojiButton = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        this.emojiButton.setPadding(AndroidUtilities.dp(4.0f), AndroidUtilities.dp(1.0f), 0, 0);
        this.emojiButton.setAlpha(0.58f);
        frameLayout.addView(this.emojiButton, LayoutHelper.createFrame(48, 48, 83));
        this.emojiButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.PhotoViewerCaptionEnterView$$ExternalSyntheticLambda3
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PhotoViewerCaptionEnterView.this.m2847x2fe3bd6f(view);
            }
        });
        this.emojiButton.setContentDescription(LocaleController.getString("Emoji", R.string.Emoji));
        ImageView imageView2 = this.emojiButton;
        ReplaceableIconDrawable replaceableIconDrawable = new ReplaceableIconDrawable(context);
        this.emojiIconDrawable = replaceableIconDrawable;
        imageView2.setImageDrawable(replaceableIconDrawable);
        this.emojiIconDrawable.setColorFilter(new PorterDuffColorFilter(-1, PorterDuff.Mode.MULTIPLY));
        this.emojiIconDrawable.setIcon(R.drawable.input_smile, false);
        TextPaint textPaint = new TextPaint(1);
        this.lengthTextPaint = textPaint;
        textPaint.setTextSize(AndroidUtilities.dp(13.0f));
        this.lengthTextPaint.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.lengthTextPaint.setColor(-2500135);
        EditTextCaption editTextCaption = new EditTextCaption(context, null) { // from class: org.telegram.ui.Components.PhotoViewerCaptionEnterView.1
            @Override // org.telegram.ui.Components.EditTextCaption, org.telegram.ui.Components.EditTextBoldCursor, android.widget.TextView, android.view.View
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                try {
                    PhotoViewerCaptionEnterView.this.isInitLineCount = getMeasuredWidth() == 0 && getMeasuredHeight() == 0;
                    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                    if (PhotoViewerCaptionEnterView.this.isInitLineCount) {
                        PhotoViewerCaptionEnterView.this.lineCount = getLineCount();
                    }
                    PhotoViewerCaptionEnterView.this.isInitLineCount = false;
                } catch (Exception e) {
                    setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.dp(51.0f));
                    FileLog.e(e);
                }
            }

            @Override // org.telegram.ui.Components.EditTextEffects, android.widget.TextView
            public void onSelectionChanged(int selStart, int selEnd) {
                super.onSelectionChanged(selStart, selEnd);
                if (selStart != selEnd) {
                    fixHandleView(false);
                } else {
                    fixHandleView(true);
                }
            }

            @Override // org.telegram.ui.Components.EditTextBoldCursor
            protected void extendActionMode(ActionMode actionMode, Menu menu) {
                PhotoViewerCaptionEnterView.this.extendActionMode(actionMode, menu);
            }

            @Override // org.telegram.ui.Components.EditTextBoldCursor
            protected int getActionModeStyle() {
                return 2;
            }

            @Override // android.view.View
            public boolean requestRectangleOnScreen(android.graphics.Rect rectangle) {
                rectangle.bottom += AndroidUtilities.dp(1000.0f);
                return super.requestRectangleOnScreen(rectangle);
            }
        };
        this.messageEditText = editTextCaption;
        editTextCaption.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: org.telegram.ui.Components.PhotoViewerCaptionEnterView$$ExternalSyntheticLambda6
            @Override // android.view.View.OnFocusChangeListener
            public final void onFocusChange(View view, boolean z) {
                PhotoViewerCaptionEnterView.this.m2848x311a104e(view, z);
            }
        });
        this.messageEditText.setSelectAllOnFocus(false);
        this.messageEditText.setDelegate(new EditTextCaption.EditTextCaptionDelegate() { // from class: org.telegram.ui.Components.PhotoViewerCaptionEnterView$$ExternalSyntheticLambda9
            @Override // org.telegram.ui.Components.EditTextCaption.EditTextCaptionDelegate
            public final void onSpansChanged() {
                PhotoViewerCaptionEnterView.this.m2849x3250632d();
            }
        });
        this.messageEditText.setWindowView(this.windowView);
        this.messageEditText.setHint(LocaleController.getString("AddCaption", R.string.AddCaption));
        this.messageEditText.setImeOptions(268435456);
        this.messageEditText.setLinkTextColor(-8994063);
        EditTextCaption editTextCaption2 = this.messageEditText;
        editTextCaption2.setInputType(editTextCaption2.getInputType() | 16384);
        this.messageEditText.setMaxLines(4);
        this.messageEditText.setHorizontallyScrolling(false);
        this.messageEditText.setTextSize(1, 18.0f);
        this.messageEditText.setGravity(80);
        this.messageEditText.setPadding(0, AndroidUtilities.dp(11.0f), 0, AndroidUtilities.dp(12.0f));
        this.messageEditText.setBackgroundDrawable(null);
        this.messageEditText.setCursorColor(-1);
        this.messageEditText.setCursorSize(AndroidUtilities.dp(20.0f));
        this.messageEditText.setTextColor(-1);
        this.messageEditText.setHighlightColor(1342177279);
        this.messageEditText.setHintTextColor(-1291845633);
        frameLayout.addView(this.messageEditText, LayoutHelper.createFrame(-1, -2.0f, 83, 52.0f, 0.0f, 6.0f, 0.0f));
        this.messageEditText.setOnKeyListener(new View.OnKeyListener() { // from class: org.telegram.ui.Components.PhotoViewerCaptionEnterView$$ExternalSyntheticLambda7
            @Override // android.view.View.OnKeyListener
            public final boolean onKey(View view, int i, KeyEvent keyEvent) {
                return PhotoViewerCaptionEnterView.this.m2850x3386b60c(view, i, keyEvent);
            }
        });
        this.messageEditText.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.PhotoViewerCaptionEnterView$$ExternalSyntheticLambda4
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PhotoViewerCaptionEnterView.this.m2851x34bd08eb(view);
            }
        });
        this.messageEditText.addTextChangedListener(new AnonymousClass2());
        this.checkDrawable = context.getResources().getDrawable(R.drawable.input_done).mutate();
        CombinedDrawable combinedDrawable = new CombinedDrawable(this.doneDrawable, this.checkDrawable, 0, AndroidUtilities.dp(1.0f));
        combinedDrawable.setCustomSize(AndroidUtilities.dp(32.0f), AndroidUtilities.dp(32.0f));
        ImageView imageView3 = new ImageView(context);
        this.doneButton = imageView3;
        imageView3.setScaleType(ImageView.ScaleType.CENTER);
        imageView3.setImageDrawable(combinedDrawable);
        textFieldContainer.addView(imageView3, LayoutHelper.createLinear(48, 48, 80));
        imageView3.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.PhotoViewerCaptionEnterView$$ExternalSyntheticLambda5
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PhotoViewerCaptionEnterView.this.m2852x35f35bca(view);
            }
        });
        imageView3.setContentDescription(LocaleController.getString("Done", R.string.Done));
        NumberTextView numberTextView = new NumberTextView(context);
        this.captionLimitView = numberTextView;
        numberTextView.setVisibility(8);
        this.captionLimitView.setTextSize(15);
        this.captionLimitView.setTextColor(-1);
        this.captionLimitView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.captionLimitView.setCenterAlign(true);
        addView(this.captionLimitView, LayoutHelper.createFrame(48, 20.0f, 85, 3.0f, 0.0f, 3.0f, 48.0f));
        this.currentAccount = UserConfig.selectedAccount;
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-PhotoViewerCaptionEnterView */
    public /* synthetic */ void m2847x2fe3bd6f(View view) {
        if (this.keyboardVisible || ((AndroidUtilities.isInMultiwindow || AndroidUtilities.usingHardwareInput) && !isPopupShowing())) {
            showPopup(1, false);
        } else {
            openKeyboardInternal();
        }
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-PhotoViewerCaptionEnterView */
    public /* synthetic */ void m2848x311a104e(View view, boolean focused) {
        if (focused) {
            try {
                EditTextCaption editTextCaption = this.messageEditText;
                editTextCaption.setSelection(editTextCaption.length(), this.messageEditText.length());
            } catch (Exception e) {
            }
        }
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-PhotoViewerCaptionEnterView */
    public /* synthetic */ void m2849x3250632d() {
        this.messageEditText.invalidateEffects();
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-PhotoViewerCaptionEnterView */
    public /* synthetic */ boolean m2850x3386b60c(View view, int i, KeyEvent keyEvent) {
        if (i == 4) {
            if (this.windowView != null && hideActionMode()) {
                return true;
            }
            if (!this.keyboardVisible && isPopupShowing()) {
                if (keyEvent.getAction() == 1) {
                    showPopup(0, true);
                }
                return true;
            }
        }
        return false;
    }

    /* renamed from: lambda$new$4$org-telegram-ui-Components-PhotoViewerCaptionEnterView */
    public /* synthetic */ void m2851x34bd08eb(View view) {
        if (isPopupShowing()) {
            showPopup((AndroidUtilities.isInMultiwindow || AndroidUtilities.usingHardwareInput) ? 0 : 2, false);
        }
    }

    /* renamed from: org.telegram.ui.Components.PhotoViewerCaptionEnterView$2 */
    /* loaded from: classes5.dex */
    public class AnonymousClass2 implements TextWatcher {
        boolean processChange = false;

        AnonymousClass2() {
            PhotoViewerCaptionEnterView.this = this$0;
        }

        @Override // android.text.TextWatcher
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override // android.text.TextWatcher
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            if (PhotoViewerCaptionEnterView.this.lineCount != PhotoViewerCaptionEnterView.this.messageEditText.getLineCount()) {
                if (!PhotoViewerCaptionEnterView.this.isInitLineCount && PhotoViewerCaptionEnterView.this.messageEditText.getMeasuredWidth() > 0) {
                    PhotoViewerCaptionEnterView photoViewerCaptionEnterView = PhotoViewerCaptionEnterView.this;
                    photoViewerCaptionEnterView.onLineCountChanged(photoViewerCaptionEnterView.lineCount, PhotoViewerCaptionEnterView.this.messageEditText.getLineCount());
                }
                PhotoViewerCaptionEnterView photoViewerCaptionEnterView2 = PhotoViewerCaptionEnterView.this;
                photoViewerCaptionEnterView2.lineCount = photoViewerCaptionEnterView2.messageEditText.getLineCount();
            }
            if (!PhotoViewerCaptionEnterView.this.innerTextChange) {
                if (PhotoViewerCaptionEnterView.this.delegate != null) {
                    PhotoViewerCaptionEnterView.this.delegate.onTextChanged(charSequence);
                }
                if (count - before > 1) {
                    this.processChange = true;
                }
            }
        }

        /* JADX WARN: Removed duplicated region for block: B:36:0x0173  */
        /* JADX WARN: Removed duplicated region for block: B:49:? A[RETURN, SYNTHETIC] */
        @Override // android.text.TextWatcher
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void afterTextChanged(android.text.Editable r14) {
            /*
                Method dump skipped, instructions count: 454
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.PhotoViewerCaptionEnterView.AnonymousClass2.afterTextChanged(android.text.Editable):void");
        }

        /* renamed from: lambda$afterTextChanged$0$org-telegram-ui-Components-PhotoViewerCaptionEnterView$2 */
        public /* synthetic */ void m2857x66ab4dd8(ValueAnimator valueAnimator) {
            PhotoViewerCaptionEnterView.this.sendButtonEnabledProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            int color = PhotoViewerCaptionEnterView.this.getThemedColor(Theme.key_dialogFloatingIcon);
            int alpha = Color.alpha(color);
            Theme.setDrawableColor(PhotoViewerCaptionEnterView.this.checkDrawable, ColorUtils.setAlphaComponent(color, (int) (alpha * ((PhotoViewerCaptionEnterView.this.sendButtonEnabledProgress * 0.42f) + 0.58f))));
            PhotoViewerCaptionEnterView.this.doneButton.invalidate();
        }
    }

    /* renamed from: lambda$new$5$org-telegram-ui-Components-PhotoViewerCaptionEnterView */
    public /* synthetic */ void m2852x35f35bca(View view) {
        if (MessagesController.getInstance(this.currentAccount).getCaptionMaxLengthLimit() - this.codePointCount < 0) {
            AndroidUtilities.shakeView(this.captionLimitView, 2.0f, 0);
            Vibrator v = (Vibrator) this.captionLimitView.getContext().getSystemService("vibrator");
            if (v != null) {
                v.vibrate(200L);
                return;
            }
            return;
        }
        this.delegate.onCaptionEnter();
    }

    public void onLineCountChanged(int lineCountOld, int lineCountNew) {
        if (!TextUtils.isEmpty(this.messageEditText.getText())) {
            this.shouldAnimateEditTextWithBounds = true;
            this.messageEditTextPredrawHeigth = this.messageEditText.getMeasuredHeight();
            this.messageEditTextPredrawScrollY = this.messageEditText.getScrollY();
            invalidate();
        } else {
            this.messageEditText.animate().cancel();
            this.messageEditText.setOffsetY(0.0f);
            this.shouldAnimateEditTextWithBounds = false;
        }
        this.chatActivityEnterViewAnimateFromTop = getTop() + this.offset;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();
        canvas.drawRect(0.0f, this.offset, getMeasuredWidth(), getMeasuredHeight(), this.paint);
        canvas.clipRect(0.0f, this.offset, getMeasuredWidth(), getMeasuredHeight());
        super.dispatchDraw(canvas);
        canvas.restore();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        if (this.shouldAnimateEditTextWithBounds) {
            EditTextCaption editTextCaption = this.messageEditText;
            editTextCaption.setOffsetY(editTextCaption.getOffsetY() - ((this.messageEditTextPredrawHeigth - this.messageEditText.getMeasuredHeight()) + (this.messageEditTextPredrawScrollY - this.messageEditText.getScrollY())));
            ValueAnimator a = ValueAnimator.ofFloat(this.messageEditText.getOffsetY(), 0.0f);
            a.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.PhotoViewerCaptionEnterView$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    PhotoViewerCaptionEnterView.this.m2853xcb57dd4c(valueAnimator);
                }
            });
            ValueAnimator valueAnimator = this.messageEditTextAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            this.messageEditTextAnimator = a;
            a.setDuration(200L);
            a.setInterpolator(CubicBezierInterpolator.DEFAULT);
            a.start();
            this.shouldAnimateEditTextWithBounds = false;
        }
        float dy = this.chatActivityEnterViewAnimateFromTop;
        if (dy != 0.0f && dy != getTop() + this.offset) {
            ValueAnimator valueAnimator2 = this.topBackgroundAnimator;
            if (valueAnimator2 != null) {
                valueAnimator2.cancel();
            }
            float top = this.chatActivityEnterViewAnimateFromTop - (getTop() + this.offset);
            this.offset = top;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(top, 0.0f);
            this.topBackgroundAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.PhotoViewerCaptionEnterView$$ExternalSyntheticLambda1
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator3) {
                    PhotoViewerCaptionEnterView.this.m2854xcc8e302b(valueAnimator3);
                }
            });
            this.topBackgroundAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.topBackgroundAnimator.setDuration(200L);
            this.topBackgroundAnimator.start();
            this.chatActivityEnterViewAnimateFromTop = 0.0f;
        }
    }

    /* renamed from: lambda$onDraw$6$org-telegram-ui-Components-PhotoViewerCaptionEnterView */
    public /* synthetic */ void m2853xcb57dd4c(ValueAnimator animation) {
        this.messageEditText.setOffsetY(((Float) animation.getAnimatedValue()).floatValue());
    }

    /* renamed from: lambda$onDraw$7$org-telegram-ui-Components-PhotoViewerCaptionEnterView */
    public /* synthetic */ void m2854xcc8e302b(ValueAnimator valueAnimator) {
        this.offset = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    public void setForceFloatingEmoji(boolean value) {
        this.forceFloatingEmoji = value;
    }

    public void updateColors() {
        Theme.setDrawableColor(this.doneDrawable, getThemedColor(Theme.key_dialogFloatingButton));
        int color = getThemedColor(Theme.key_dialogFloatingIcon);
        int alpha = Color.alpha(color);
        Theme.setDrawableColor(this.checkDrawable, ColorUtils.setAlphaComponent(color, (int) (alpha * ((this.sendButtonEnabledProgress * 0.42f) + 0.58f))));
        EmojiView emojiView = this.emojiView;
        if (emojiView != null) {
            emojiView.updateColors();
        }
    }

    public boolean hideActionMode() {
        return false;
    }

    protected void extendActionMode(ActionMode actionMode, Menu menu) {
    }

    private void onWindowSizeChanged() {
        int size = this.sizeNotifierLayout.getHeight();
        if (!this.keyboardVisible) {
            size -= this.emojiPadding;
        }
        PhotoViewerCaptionEnterViewDelegate photoViewerCaptionEnterViewDelegate = this.delegate;
        if (photoViewerCaptionEnterViewDelegate != null) {
            photoViewerCaptionEnterViewDelegate.onWindowSizeChanged(size);
        }
    }

    public void onCreate() {
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
        this.sizeNotifierLayout.setDelegate(this);
    }

    public void onDestroy() {
        hidePopup();
        if (isKeyboardVisible()) {
            closeKeyboard();
        }
        this.keyboardVisible = false;
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
        SizeNotifierFrameLayoutPhoto sizeNotifierFrameLayoutPhoto = this.sizeNotifierLayout;
        if (sizeNotifierFrameLayoutPhoto != null) {
            sizeNotifierFrameLayoutPhoto.setDelegate(null);
        }
    }

    public void setDelegate(PhotoViewerCaptionEnterViewDelegate delegate) {
        this.delegate = delegate;
    }

    public void setFieldText(CharSequence text) {
        EditTextCaption editTextCaption = this.messageEditText;
        if (editTextCaption == null) {
            return;
        }
        editTextCaption.setText(text);
        EditTextCaption editTextCaption2 = this.messageEditText;
        editTextCaption2.setSelection(editTextCaption2.getText().length());
        PhotoViewerCaptionEnterViewDelegate photoViewerCaptionEnterViewDelegate = this.delegate;
        if (photoViewerCaptionEnterViewDelegate != null) {
            photoViewerCaptionEnterViewDelegate.onTextChanged(this.messageEditText.getText());
        }
    }

    public int getSelectionLength() {
        EditTextCaption editTextCaption = this.messageEditText;
        if (editTextCaption == null) {
            return 0;
        }
        try {
            return editTextCaption.getSelectionEnd() - this.messageEditText.getSelectionStart();
        } catch (Exception e) {
            FileLog.e(e);
            return 0;
        }
    }

    public int getCursorPosition() {
        EditTextCaption editTextCaption = this.messageEditText;
        if (editTextCaption == null) {
            return 0;
        }
        return editTextCaption.getSelectionStart();
    }

    private void createEmojiView() {
        if (this.emojiView != null) {
            return;
        }
        EmojiView emojiView = new EmojiView(false, false, getContext(), false, null, null, null);
        this.emojiView = emojiView;
        emojiView.setDelegate(new EmojiView.EmojiViewDelegate() { // from class: org.telegram.ui.Components.PhotoViewerCaptionEnterView.3
            @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
            public /* synthetic */ boolean canSchedule() {
                return EmojiView.EmojiViewDelegate.CC.$default$canSchedule(this);
            }

            @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
            public /* synthetic */ long getDialogId() {
                return EmojiView.EmojiViewDelegate.CC.$default$getDialogId(this);
            }

            @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
            public /* synthetic */ float getProgressToSearchOpened() {
                return EmojiView.EmojiViewDelegate.CC.$default$getProgressToSearchOpened(this);
            }

            @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
            public /* synthetic */ int getThreadId() {
                return EmojiView.EmojiViewDelegate.CC.$default$getThreadId(this);
            }

            @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
            public /* synthetic */ void invalidateEnterView() {
                EmojiView.EmojiViewDelegate.CC.$default$invalidateEnterView(this);
            }

            @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
            public /* synthetic */ boolean isExpanded() {
                return EmojiView.EmojiViewDelegate.CC.$default$isExpanded(this);
            }

            @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
            public /* synthetic */ boolean isInScheduleMode() {
                return EmojiView.EmojiViewDelegate.CC.$default$isInScheduleMode(this);
            }

            @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
            public /* synthetic */ boolean isSearchOpened() {
                return EmojiView.EmojiViewDelegate.CC.$default$isSearchOpened(this);
            }

            @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
            public /* synthetic */ void onClearEmojiRecent() {
                EmojiView.EmojiViewDelegate.CC.$default$onClearEmojiRecent(this);
            }

            @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
            public /* synthetic */ void onGifSelected(View view, Object obj, String str, Object obj2, boolean z, int i) {
                EmojiView.EmojiViewDelegate.CC.$default$onGifSelected(this, view, obj, str, obj2, z, i);
            }

            @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
            public /* synthetic */ void onSearchOpenClose(int i) {
                EmojiView.EmojiViewDelegate.CC.$default$onSearchOpenClose(this, i);
            }

            @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
            public /* synthetic */ void onShowStickerSet(TLRPC.StickerSet stickerSet, TLRPC.InputStickerSet inputStickerSet) {
                EmojiView.EmojiViewDelegate.CC.$default$onShowStickerSet(this, stickerSet, inputStickerSet);
            }

            @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
            public /* synthetic */ void onStickerSelected(View view, TLRPC.Document document, String str, Object obj, MessageObject.SendAnimationData sendAnimationData, boolean z, int i) {
                EmojiView.EmojiViewDelegate.CC.$default$onStickerSelected(this, view, document, str, obj, sendAnimationData, z, i);
            }

            @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
            public /* synthetic */ void onStickerSetAdd(TLRPC.StickerSetCovered stickerSetCovered) {
                EmojiView.EmojiViewDelegate.CC.$default$onStickerSetAdd(this, stickerSetCovered);
            }

            @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
            public /* synthetic */ void onStickerSetRemove(TLRPC.StickerSetCovered stickerSetCovered) {
                EmojiView.EmojiViewDelegate.CC.$default$onStickerSetRemove(this, stickerSetCovered);
            }

            @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
            public /* synthetic */ void onStickersGroupClick(long j) {
                EmojiView.EmojiViewDelegate.CC.$default$onStickersGroupClick(this, j);
            }

            @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
            public /* synthetic */ void onStickersSettingsClick() {
                EmojiView.EmojiViewDelegate.CC.$default$onStickersSettingsClick(this);
            }

            @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
            public /* synthetic */ void onTabOpened(int i) {
                EmojiView.EmojiViewDelegate.CC.$default$onTabOpened(this, i);
            }

            @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
            public /* synthetic */ void showTrendingStickersAlert(TrendingStickersLayout trendingStickersLayout) {
                EmojiView.EmojiViewDelegate.CC.$default$showTrendingStickersAlert(this, trendingStickersLayout);
            }

            @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
            public boolean onBackspace() {
                if (PhotoViewerCaptionEnterView.this.messageEditText.length() == 0) {
                    return false;
                }
                PhotoViewerCaptionEnterView.this.messageEditText.dispatchKeyEvent(new KeyEvent(0, 67));
                return true;
            }

            @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
            public void onEmojiSelected(String symbol) {
                int i = PhotoViewerCaptionEnterView.this.messageEditText.getSelectionEnd();
                if (i < 0) {
                    i = 0;
                }
                try {
                    try {
                        PhotoViewerCaptionEnterView.this.innerTextChange = true;
                        CharSequence localCharSequence = Emoji.replaceEmoji(symbol, PhotoViewerCaptionEnterView.this.messageEditText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
                        PhotoViewerCaptionEnterView.this.messageEditText.setText(PhotoViewerCaptionEnterView.this.messageEditText.getText().insert(i, localCharSequence));
                        int j = localCharSequence.length() + i;
                        PhotoViewerCaptionEnterView.this.messageEditText.setSelection(j, j);
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                } finally {
                    PhotoViewerCaptionEnterView.this.innerTextChange = false;
                }
            }
        });
        this.sizeNotifierLayout.addView(this.emojiView);
    }

    public void addEmojiToRecent(String code) {
        createEmojiView();
        this.emojiView.addEmojiToRecent(code);
    }

    public void replaceWithText(int start, int len, CharSequence text, boolean parseEmoji) {
        try {
            SpannableStringBuilder builder = new SpannableStringBuilder(this.messageEditText.getText());
            builder.replace(start, start + len, text);
            if (parseEmoji) {
                Emoji.replaceEmoji(builder, this.messageEditText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
            }
            this.messageEditText.setText(builder);
            this.messageEditText.setSelection(Math.min(text.length() + start, this.messageEditText.length()));
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void setFieldFocused(boolean focus) {
        EditTextCaption editTextCaption = this.messageEditText;
        if (editTextCaption == null) {
            return;
        }
        if (focus) {
            if (!editTextCaption.isFocused()) {
                this.messageEditText.postDelayed(new Runnable() { // from class: org.telegram.ui.Components.PhotoViewerCaptionEnterView$$ExternalSyntheticLambda8
                    @Override // java.lang.Runnable
                    public final void run() {
                        PhotoViewerCaptionEnterView.this.m2855xe94970c8();
                    }
                }, 600L);
            }
        } else if (editTextCaption.isFocused() && !this.keyboardVisible) {
            this.messageEditText.clearFocus();
        }
    }

    /* renamed from: lambda$setFieldFocused$8$org-telegram-ui-Components-PhotoViewerCaptionEnterView */
    public /* synthetic */ void m2855xe94970c8() {
        EditTextCaption editTextCaption = this.messageEditText;
        if (editTextCaption != null) {
            try {
                editTextCaption.requestFocus();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public CharSequence getFieldCharSequence() {
        return AndroidUtilities.getTrimmedString(this.messageEditText.getText());
    }

    public int getEmojiPadding() {
        return this.emojiPadding;
    }

    public boolean isPopupView(View view) {
        return view == this.emojiView;
    }

    private void showPopup(int show, boolean animated) {
        EmojiView emojiView;
        this.lastShow = show;
        if (show == 1) {
            if (this.emojiView == null) {
                createEmojiView();
            }
            this.emojiView.setVisibility(0);
            this.delegate.onEmojiViewOpen();
            if (this.keyboardHeight <= 0) {
                this.keyboardHeight = MessagesController.getGlobalEmojiSettings().getInt("kbd_height", AndroidUtilities.dp(200.0f));
            }
            if (this.keyboardHeightLand <= 0) {
                this.keyboardHeightLand = MessagesController.getGlobalEmojiSettings().getInt("kbd_height_land3", AndroidUtilities.dp(200.0f));
            }
            int currentHeight = AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y ? this.keyboardHeightLand : this.keyboardHeight;
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.emojiView.getLayoutParams();
            layoutParams.width = AndroidUtilities.displaySize.x;
            layoutParams.height = currentHeight;
            this.emojiView.setLayoutParams(layoutParams);
            if (!AndroidUtilities.isInMultiwindow && !this.forceFloatingEmoji) {
                AndroidUtilities.hideKeyboard(this.messageEditText);
            }
            SizeNotifierFrameLayoutPhoto sizeNotifierFrameLayoutPhoto = this.sizeNotifierLayout;
            if (sizeNotifierFrameLayoutPhoto != null) {
                this.emojiPadding = currentHeight;
                sizeNotifierFrameLayoutPhoto.requestLayout();
                this.emojiIconDrawable.setIcon(R.drawable.input_keyboard, true);
                onWindowSizeChanged();
                return;
            }
            return;
        }
        if (this.emojiButton != null) {
            this.emojiIconDrawable.setIcon(R.drawable.input_smile, true);
        }
        if (this.sizeNotifierLayout != null) {
            if (animated && SharedConfig.smoothKeyboard && show == 0 && this.emojiView != null) {
                ValueAnimator animator = ValueAnimator.ofFloat(this.emojiPadding, 0.0f);
                final float animateFrom = this.emojiPadding;
                this.popupAnimating = true;
                this.delegate.onEmojiViewCloseStart();
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.PhotoViewerCaptionEnterView$$ExternalSyntheticLambda2
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        PhotoViewerCaptionEnterView.this.m2856x43266e57(animateFrom, valueAnimator);
                    }
                });
                animator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.PhotoViewerCaptionEnterView.4
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        PhotoViewerCaptionEnterView.this.emojiPadding = 0;
                        PhotoViewerCaptionEnterView.this.setTranslationY(0.0f);
                        PhotoViewerCaptionEnterView.this.setAlpha(1.0f);
                        PhotoViewerCaptionEnterView.this.emojiView.setTranslationY(0.0f);
                        PhotoViewerCaptionEnterView.this.popupAnimating = false;
                        PhotoViewerCaptionEnterView.this.delegate.onEmojiViewCloseEnd();
                        PhotoViewerCaptionEnterView.this.emojiView.setVisibility(8);
                        PhotoViewerCaptionEnterView.this.emojiView.setAlpha(1.0f);
                    }
                });
                animator.setDuration(210L);
                animator.setInterpolator(AdjustPanLayoutHelper.keyboardInterpolator);
                animator.start();
            } else if (show == 0) {
                EmojiView emojiView2 = this.emojiView;
                if (emojiView2 != null) {
                    emojiView2.setVisibility(8);
                }
                this.emojiPadding = 0;
            } else if (!SharedConfig.smoothKeyboard && (emojiView = this.emojiView) != null) {
                emojiView.setVisibility(8);
            }
            this.sizeNotifierLayout.requestLayout();
            onWindowSizeChanged();
        }
    }

    /* renamed from: lambda$showPopup$9$org-telegram-ui-Components-PhotoViewerCaptionEnterView */
    public /* synthetic */ void m2856x43266e57(float animateFrom, ValueAnimator animation) {
        float v = ((Float) animation.getAnimatedValue()).floatValue();
        this.emojiPadding = (int) v;
        this.emojiView.setTranslationY(animateFrom - v);
        setTranslationY(animateFrom - v);
        setAlpha(v / animateFrom);
        this.emojiView.setAlpha(v / animateFrom);
    }

    public void hidePopup() {
        if (isPopupShowing()) {
            showPopup(0, true);
        }
    }

    private void openKeyboardInternal() {
        showPopup((AndroidUtilities.isInMultiwindow || AndroidUtilities.usingHardwareInput) ? 0 : 2, false);
        openKeyboard();
    }

    public void openKeyboard() {
        this.messageEditText.requestFocus();
        AndroidUtilities.showKeyboard(this.messageEditText);
        try {
            EditTextCaption editTextCaption = this.messageEditText;
            editTextCaption.setSelection(editTextCaption.length(), this.messageEditText.length());
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public boolean isPopupShowing() {
        EmojiView emojiView = this.emojiView;
        return emojiView != null && emojiView.getVisibility() == 0;
    }

    public boolean isPopupAnimating() {
        return this.popupAnimating;
    }

    public void closeKeyboard() {
        AndroidUtilities.hideKeyboard(this.messageEditText);
        this.messageEditText.clearFocus();
    }

    public boolean isKeyboardVisible() {
        return ((AndroidUtilities.usingHardwareInput || AndroidUtilities.isInMultiwindow) && getTag() != null) || this.keyboardVisible;
    }

    @Override // org.telegram.ui.Components.SizeNotifierFrameLayoutPhoto.SizeNotifierFrameLayoutPhotoDelegate
    public void onSizeChanged(int height, boolean isWidthGreater) {
        boolean z;
        int newHeight;
        if (height > AndroidUtilities.dp(50.0f) && this.keyboardVisible && !AndroidUtilities.isInMultiwindow && !this.forceFloatingEmoji) {
            if (isWidthGreater) {
                this.keyboardHeightLand = height;
                MessagesController.getGlobalEmojiSettings().edit().putInt("kbd_height_land3", this.keyboardHeightLand).commit();
            } else {
                this.keyboardHeight = height;
                MessagesController.getGlobalEmojiSettings().edit().putInt("kbd_height", this.keyboardHeight).commit();
            }
        }
        if (isPopupShowing()) {
            if (isWidthGreater) {
                newHeight = this.keyboardHeightLand;
            } else {
                newHeight = this.keyboardHeight;
            }
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.emojiView.getLayoutParams();
            if (layoutParams.width != AndroidUtilities.displaySize.x || layoutParams.height != newHeight) {
                layoutParams.width = AndroidUtilities.displaySize.x;
                layoutParams.height = newHeight;
                this.emojiView.setLayoutParams(layoutParams);
                if (this.sizeNotifierLayout != null) {
                    this.emojiPadding = layoutParams.height;
                    this.sizeNotifierLayout.requestLayout();
                    onWindowSizeChanged();
                }
            }
        }
        if (this.lastSizeChangeValue1 == height && this.lastSizeChangeValue2 == isWidthGreater) {
            onWindowSizeChanged();
            return;
        }
        this.lastSizeChangeValue1 = height;
        this.lastSizeChangeValue2 = isWidthGreater;
        boolean oldValue = this.keyboardVisible;
        boolean z2 = height > 0;
        this.keyboardVisible = z2;
        if (z2 && isPopupShowing()) {
            showPopup(0, false);
        }
        if (this.emojiPadding != 0 && !(z = this.keyboardVisible) && z != oldValue && !isPopupShowing()) {
            this.emojiPadding = 0;
            this.sizeNotifierLayout.requestLayout();
        }
        onWindowSizeChanged();
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        EmojiView emojiView;
        if (id == NotificationCenter.emojiLoaded && (emojiView = this.emojiView) != null) {
            emojiView.invalidateViews();
        }
    }

    public void setAllowTextEntitiesIntersection(boolean value) {
        this.messageEditText.setAllowTextEntitiesIntersection(value);
    }

    public EditTextCaption getMessageEditText() {
        return this.messageEditText;
    }

    public int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
