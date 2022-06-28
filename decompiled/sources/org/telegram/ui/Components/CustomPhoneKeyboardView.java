package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextPaint;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.core.graphics.ColorUtils;
import androidx.core.view.GestureDetectorCompat;
import com.google.android.exoplayer2.C;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes5.dex */
public class CustomPhoneKeyboardView extends ViewGroup {
    private static final int BUTTON_PADDING = 6;
    public static final int KEYBOARD_HEIGHT_DP = 230;
    private static final int SIDE_PADDING = 10;
    private ImageView backButton;
    private boolean dispatchBackWhenEmpty;
    private EditText editText;
    private boolean postedLongClick;
    private boolean runningLongClick;
    private View[] views = new View[12];
    private Runnable onBackButton = new Runnable() { // from class: org.telegram.ui.Components.CustomPhoneKeyboardView$$ExternalSyntheticLambda2
        @Override // java.lang.Runnable
        public final void run() {
            CustomPhoneKeyboardView.this.m2553lambda$new$0$orgtelegramuiComponentsCustomPhoneKeyboardView();
        }
    };
    private Runnable detectLongClick = new Runnable() { // from class: org.telegram.ui.Components.CustomPhoneKeyboardView$$ExternalSyntheticLambda3
        @Override // java.lang.Runnable
        public final void run() {
            CustomPhoneKeyboardView.this.m2554lambda$new$1$orgtelegramuiComponentsCustomPhoneKeyboardView();
        }
    };

    /* renamed from: lambda$new$0$org-telegram-ui-Components-CustomPhoneKeyboardView */
    public /* synthetic */ void m2553lambda$new$0$orgtelegramuiComponentsCustomPhoneKeyboardView() {
        EditText editText = this.editText;
        if (editText != null) {
            if (editText.length() == 0 && !this.dispatchBackWhenEmpty) {
                return;
            }
            performHapticFeedback(3, 2);
            playSoundEffect(0);
            this.editText.dispatchKeyEvent(new KeyEvent(0, 67));
            this.editText.dispatchKeyEvent(new KeyEvent(1, 67));
            if (this.runningLongClick) {
                postDelayed(this.onBackButton, 50L);
            }
        }
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-CustomPhoneKeyboardView */
    public /* synthetic */ void m2554lambda$new$1$orgtelegramuiComponentsCustomPhoneKeyboardView() {
        this.postedLongClick = false;
        this.runningLongClick = true;
        this.onBackButton.run();
    }

    public CustomPhoneKeyboardView(Context context) {
        super(context);
        String symbols;
        int i = 0;
        while (i < 11) {
            if (i != 9) {
                switch (i) {
                    case 1:
                        symbols = "ABC";
                        break;
                    case 2:
                        symbols = "DEF";
                        break;
                    case 3:
                        symbols = "GHI";
                        break;
                    case 4:
                        symbols = "JKL";
                        break;
                    case 5:
                        symbols = "MNO";
                        break;
                    case 6:
                        symbols = "PQRS";
                        break;
                    case 7:
                        symbols = "TUV";
                        break;
                    case 8:
                        symbols = "WXYZ";
                        break;
                    case 9:
                    default:
                        symbols = "";
                        break;
                    case 10:
                        symbols = "+";
                        break;
                }
                final String num = String.valueOf(i != 10 ? i + 1 : 0);
                this.views[i] = new NumberButtonView(context, num, symbols);
                this.views[i].setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.CustomPhoneKeyboardView$$ExternalSyntheticLambda0
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        CustomPhoneKeyboardView.this.m2555lambda$new$2$orgtelegramuiComponentsCustomPhoneKeyboardView(num, view);
                    }
                });
                addView(this.views[i]);
            }
            i++;
        }
        final GestureDetectorCompat backDetector = setupBackButtonDetector(context);
        ImageView imageView = new ImageView(context) { // from class: org.telegram.ui.Components.CustomPhoneKeyboardView.1
            @Override // android.view.View
            public boolean onTouchEvent(MotionEvent event) {
                if ((event.getAction() == 1 || event.getAction() == 3) && (CustomPhoneKeyboardView.this.postedLongClick || CustomPhoneKeyboardView.this.runningLongClick)) {
                    CustomPhoneKeyboardView.this.postedLongClick = false;
                    CustomPhoneKeyboardView.this.runningLongClick = false;
                    removeCallbacks(CustomPhoneKeyboardView.this.detectLongClick);
                    removeCallbacks(CustomPhoneKeyboardView.this.onBackButton);
                }
                super.onTouchEvent(event);
                return backDetector.onTouchEvent(event);
            }
        };
        this.backButton = imageView;
        imageView.setImageResource(R.drawable.msg_clear_input);
        this.backButton.setColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.backButton.setBackground(getButtonDrawable());
        int pad = AndroidUtilities.dp(11.0f);
        this.backButton.setPadding(pad, pad, pad, pad);
        this.backButton.setOnClickListener(CustomPhoneKeyboardView$$ExternalSyntheticLambda1.INSTANCE);
        View[] viewArr = this.views;
        ImageView imageView2 = this.backButton;
        viewArr[11] = imageView2;
        addView(imageView2);
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-CustomPhoneKeyboardView */
    public /* synthetic */ void m2555lambda$new$2$orgtelegramuiComponentsCustomPhoneKeyboardView(String num, View v) {
        if (this.editText == null) {
            return;
        }
        performHapticFeedback(3, 2);
        EditText editText = this.editText;
        if (editText instanceof EditTextBoldCursor) {
            ((EditTextBoldCursor) editText).setTextWatchersSuppressed(true, false);
        }
        Editable text = this.editText.getText();
        int newSelection = this.editText.getSelectionEnd() == this.editText.length() ? -1 : this.editText.getSelectionStart() + num.length();
        if (this.editText.getSelectionStart() != -1 && this.editText.getSelectionEnd() != -1) {
            EditText editText2 = this.editText;
            editText2.setText(text.replace(editText2.getSelectionStart(), this.editText.getSelectionEnd(), num));
            EditText editText3 = this.editText;
            editText3.setSelection(newSelection == -1 ? editText3.length() : newSelection);
        } else {
            this.editText.setText(num);
            EditText editText4 = this.editText;
            editText4.setSelection(editText4.length());
        }
        EditText editText5 = this.editText;
        if (editText5 instanceof EditTextBoldCursor) {
            ((EditTextBoldCursor) editText5).setTextWatchersSuppressed(false, true);
        }
    }

    public static /* synthetic */ void lambda$new$3(View v) {
    }

    @Override // android.view.View
    public boolean canScrollHorizontally(int direction) {
        return true;
    }

    public void setDispatchBackWhenEmpty(boolean dispatchBackWhenEmpty) {
        this.dispatchBackWhenEmpty = dispatchBackWhenEmpty;
    }

    private GestureDetectorCompat setupBackButtonDetector(Context context) {
        final int touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        return new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() { // from class: org.telegram.ui.Components.CustomPhoneKeyboardView.2
            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public boolean onDown(MotionEvent e) {
                if (CustomPhoneKeyboardView.this.postedLongClick) {
                    CustomPhoneKeyboardView customPhoneKeyboardView = CustomPhoneKeyboardView.this;
                    customPhoneKeyboardView.removeCallbacks(customPhoneKeyboardView.detectLongClick);
                }
                CustomPhoneKeyboardView.this.postedLongClick = true;
                CustomPhoneKeyboardView customPhoneKeyboardView2 = CustomPhoneKeyboardView.this;
                customPhoneKeyboardView2.postDelayed(customPhoneKeyboardView2.detectLongClick, 200L);
                CustomPhoneKeyboardView.this.onBackButton.run();
                return true;
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if ((CustomPhoneKeyboardView.this.postedLongClick || CustomPhoneKeyboardView.this.runningLongClick) && (Math.abs(distanceX) >= touchSlop || Math.abs(distanceY) >= touchSlop)) {
                    CustomPhoneKeyboardView.this.postedLongClick = false;
                    CustomPhoneKeyboardView.this.runningLongClick = false;
                    CustomPhoneKeyboardView customPhoneKeyboardView = CustomPhoneKeyboardView.this;
                    customPhoneKeyboardView.removeCallbacks(customPhoneKeyboardView.detectLongClick);
                    CustomPhoneKeyboardView customPhoneKeyboardView2 = CustomPhoneKeyboardView.this;
                    customPhoneKeyboardView2.removeCallbacks(customPhoneKeyboardView2.onBackButton);
                }
                return false;
            }
        });
    }

    public void setEditText(EditText editText) {
        this.editText = editText;
        this.dispatchBackWhenEmpty = false;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int btnWidth = (getWidth() - AndroidUtilities.dp(32.0f)) / 3;
        int btnHeight = (getHeight() - AndroidUtilities.dp(42.0f)) / 4;
        for (int i = 0; i < this.views.length; i++) {
            int rowX = i % 3;
            int rowY = i / 3;
            int left = ((AndroidUtilities.dp(6.0f) + btnWidth) * rowX) + AndroidUtilities.dp(10.0f);
            int top = ((AndroidUtilities.dp(6.0f) + btnHeight) * rowY) + AndroidUtilities.dp(10.0f);
            View[] viewArr = this.views;
            if (viewArr[i] != null) {
                viewArr[i].layout(left, top, left + btnWidth, top + btnHeight);
            }
        }
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        View[] viewArr;
        setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), View.MeasureSpec.getSize(heightMeasureSpec));
        int btnWidth = (getWidth() - AndroidUtilities.dp(32.0f)) / 3;
        int btnHeight = (getHeight() - AndroidUtilities.dp(42.0f)) / 4;
        for (View v : this.views) {
            if (v != null) {
                v.measure(View.MeasureSpec.makeMeasureSpec(btnWidth, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(btnHeight, C.BUFFER_FLAG_ENCRYPTED));
            }
        }
    }

    public static Drawable getButtonDrawable() {
        return Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), Theme.getColor(Theme.key_listSelector), ColorUtils.setAlphaComponent(Theme.getColor(Theme.key_listSelector), 60));
    }

    public void updateColors() {
        View[] viewArr;
        this.backButton.setColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        for (View v : this.views) {
            if (v != null) {
                v.setBackground(getButtonDrawable());
                if (v instanceof NumberButtonView) {
                    ((NumberButtonView) v).updateColors();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public static final class NumberButtonView extends View {
        private String mNumber;
        private String mSymbols;
        private TextPaint numberTextPaint = new TextPaint(1);
        private TextPaint symbolsTextPaint = new TextPaint(1);
        private android.graphics.Rect rect = new android.graphics.Rect();

        public NumberButtonView(Context context, String number, String symbols) {
            super(context);
            this.mNumber = number;
            this.mSymbols = symbols;
            this.numberTextPaint.setTextSize(AndroidUtilities.dp(24.0f));
            this.symbolsTextPaint.setTextSize(AndroidUtilities.dp(14.0f));
            setBackground(CustomPhoneKeyboardView.getButtonDrawable());
            updateColors();
        }

        public void updateColors() {
            this.numberTextPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.symbolsTextPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            float symbolsWidth = this.symbolsTextPaint.measureText(this.mSymbols);
            float numberWidth = this.numberTextPaint.measureText(this.mNumber);
            TextPaint textPaint = this.numberTextPaint;
            String str = this.mNumber;
            textPaint.getTextBounds(str, 0, str.length(), this.rect);
            float textOffsetNumber = this.rect.height() / 2.0f;
            TextPaint textPaint2 = this.symbolsTextPaint;
            String str2 = this.mSymbols;
            textPaint2.getTextBounds(str2, 0, str2.length(), this.rect);
            float textOffsetSymbols = this.rect.height() / 2.0f;
            canvas.drawText(this.mNumber, (getWidth() * 0.25f) - (numberWidth / 2.0f), (getHeight() / 2.0f) + textOffsetNumber, this.numberTextPaint);
            canvas.drawText(this.mSymbols, (getWidth() * 0.7f) - (symbolsWidth / 2.0f), (getHeight() / 2.0f) + textOffsetSymbols, this.symbolsTextPaint);
        }
    }
}
