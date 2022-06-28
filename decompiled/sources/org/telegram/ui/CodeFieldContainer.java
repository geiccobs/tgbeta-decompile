package org.telegram.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
/* loaded from: classes4.dex */
public class CodeFieldContainer extends LinearLayout {
    public static final int TYPE_PASSCODE = 10;
    public CodeNumberField[] codeField;
    public boolean ignoreOnTextChange;
    public boolean isFocusSuppressed;
    float strokeWidth;
    Paint paint = new Paint(1);
    Paint bitmapPaint = new Paint(1);

    public CodeFieldContainer(Context context) {
        super(context);
        this.paint.setStyle(Paint.Style.STROKE);
        setOrientation(0);
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Paint paint = this.paint;
        float dp = AndroidUtilities.dp(1.5f);
        this.strokeWidth = dp;
        paint.setStrokeWidth(dp);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof CodeNumberField) {
                CodeNumberField codeField = (CodeNumberField) child;
                if (!this.isFocusSuppressed) {
                    if (child.isFocused()) {
                        codeField.animateFocusedProgress(1.0f);
                    } else if (!child.isFocused()) {
                        codeField.animateFocusedProgress(0.0f);
                    }
                }
                float successProgress = codeField.getSuccessProgress();
                int focusClr = ColorUtils.blendARGB(Theme.getColor(Theme.key_windowBackgroundWhiteInputField), Theme.getColor(Theme.key_windowBackgroundWhiteInputFieldActivated), codeField.getFocusedProgress());
                int errorClr = ColorUtils.blendARGB(focusClr, Theme.getColor(Theme.key_dialogTextRed), codeField.getErrorProgress());
                this.paint.setColor(ColorUtils.blendARGB(errorClr, Theme.getColor(Theme.key_checkbox), successProgress));
                AndroidUtilities.rectTmp.set(child.getLeft(), child.getTop(), child.getRight(), child.getBottom());
                RectF rectF = AndroidUtilities.rectTmp;
                float f = this.strokeWidth;
                rectF.inset(f, f);
                if (successProgress != 0.0f) {
                    float offset = -Math.max(0.0f, this.strokeWidth * (codeField.getSuccessScaleProgress() - 1.0f));
                    AndroidUtilities.rectTmp.inset(offset, offset);
                }
                canvas.drawRoundRect(AndroidUtilities.rectTmp, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), this.paint);
            }
        }
        super.dispatchDraw(canvas);
    }

    @Override // android.view.ViewGroup
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        if (child instanceof CodeNumberField) {
            CodeNumberField field = (CodeNumberField) child;
            canvas.save();
            float progress = ((CodeNumberField) child).enterAnimation;
            AndroidUtilities.rectTmp.set(child.getX(), child.getY(), child.getX() + child.getMeasuredWidth(), child.getY() + child.getMeasuredHeight());
            RectF rectF = AndroidUtilities.rectTmp;
            float f = this.strokeWidth;
            rectF.inset(f, f);
            canvas.clipRect(AndroidUtilities.rectTmp);
            if (field.replaceAnimation) {
                float s = (progress * 0.5f) + 0.5f;
                child.setAlpha(progress);
                canvas.scale(s, s, field.getX() + (field.getMeasuredWidth() / 2.0f), field.getY() + (field.getMeasuredHeight() / 2.0f));
            } else {
                child.setAlpha(1.0f);
                canvas.translate(0.0f, child.getMeasuredHeight() * (1.0f - progress));
            }
            super.drawChild(canvas, child, drawingTime);
            canvas.restore();
            float exitProgress = field.exitAnimation;
            if (exitProgress < 1.0f) {
                canvas.save();
                float s2 = ((1.0f - exitProgress) * 0.5f) + 0.5f;
                canvas.scale(s2, s2, field.getX() + (field.getMeasuredWidth() / 2.0f), field.getY() + (field.getMeasuredHeight() / 2.0f));
                this.bitmapPaint.setAlpha((int) ((1.0f - exitProgress) * 255.0f));
                canvas.drawBitmap(field.exitBitmap, field.getX(), field.getY(), this.bitmapPaint);
                canvas.restore();
                return true;
            }
            return true;
        }
        return super.drawChild(canvas, child, drawingTime);
    }

    public void setNumbersCount(final int length, int currentType) {
        int gapSize;
        int height;
        int width;
        int i = currentType;
        CodeNumberField[] codeNumberFieldArr = this.codeField;
        if (codeNumberFieldArr == null || codeNumberFieldArr.length != length) {
            this.codeField = new CodeNumberField[length];
            int a = 0;
            while (a < length) {
                final int num = a;
                this.codeField[a] = new CodeNumberField(getContext()) { // from class: org.telegram.ui.CodeFieldContainer.1
                    @Override // android.view.View
                    public boolean dispatchKeyEvent(KeyEvent event) {
                        if (event.getKeyCode() == 4) {
                            return false;
                        }
                        int keyCode = event.getKeyCode();
                        if (event.getAction() != 1) {
                            return isFocused();
                        }
                        if (keyCode == 67 && CodeFieldContainer.this.codeField[num].length() == 1) {
                            CodeFieldContainer.this.codeField[num].startExitAnimation();
                            CodeFieldContainer.this.codeField[num].setText("");
                            return true;
                        } else if (keyCode == 67 && CodeFieldContainer.this.codeField[num].length() == 0 && num > 0) {
                            CodeFieldContainer.this.codeField[num - 1].setSelection(CodeFieldContainer.this.codeField[num - 1].length());
                            int i2 = 0;
                            while (true) {
                                int i3 = num;
                                if (i2 < i3) {
                                    if (i2 == i3 - 1) {
                                        CodeFieldContainer.this.codeField[num - 1].requestFocus();
                                    } else {
                                        CodeFieldContainer.this.codeField[i2].clearFocus();
                                    }
                                    i2++;
                                } else {
                                    CodeFieldContainer.this.codeField[num - 1].startExitAnimation();
                                    CodeFieldContainer.this.codeField[num - 1].setText("");
                                    return true;
                                }
                            }
                        } else {
                            if (keyCode >= 7 && keyCode <= 16) {
                                String str = Integer.toString(keyCode - 7);
                                if (CodeFieldContainer.this.codeField[num].getText() != null && str.equals(CodeFieldContainer.this.codeField[num].getText().toString())) {
                                    if (num >= length - 1) {
                                        CodeFieldContainer.this.processNextPressed();
                                    } else {
                                        CodeFieldContainer.this.codeField[num + 1].requestFocus();
                                    }
                                    return true;
                                }
                                if (CodeFieldContainer.this.codeField[num].length() > 0) {
                                    CodeFieldContainer.this.codeField[num].startExitAnimation();
                                }
                                CodeFieldContainer.this.codeField[num].setText(str);
                            }
                            return true;
                        }
                    }
                };
                this.codeField[a].setImeOptions(268435461);
                this.codeField[a].setTextSize(1, 20.0f);
                this.codeField[a].setMaxLines(1);
                this.codeField[a].setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
                this.codeField[a].setPadding(0, 0, 0, 0);
                this.codeField[a].setGravity(17);
                if (i != 3) {
                    this.codeField[a].setInputType(3);
                } else {
                    this.codeField[a].setEnabled(false);
                    this.codeField[a].setInputType(0);
                    this.codeField[a].setVisibility(8);
                }
                if (i == 10) {
                    width = 42;
                    height = 47;
                    gapSize = 10;
                } else if (i == 11) {
                    width = 28;
                    height = 34;
                    gapSize = 5;
                } else {
                    width = 34;
                    height = 42;
                    gapSize = 7;
                }
                addView(this.codeField[a], LayoutHelper.createLinear(width, height, 1, 0, 0, a != length + (-1) ? gapSize : 0, 0));
                this.codeField[a].addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.CodeFieldContainer.2
                    @Override // android.text.TextWatcher
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override // android.text.TextWatcher
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override // android.text.TextWatcher
                    public void afterTextChanged(Editable s) {
                        int len;
                        if (!CodeFieldContainer.this.ignoreOnTextChange && (len = s.length()) >= 1) {
                            int n = num;
                            if (len > 1) {
                                String text = s.toString();
                                CodeFieldContainer.this.ignoreOnTextChange = true;
                                for (int a2 = 0; a2 < Math.min(length - num, len); a2++) {
                                    if (a2 == 0) {
                                        s.replace(0, len, text.substring(a2, a2 + 1));
                                    } else {
                                        n++;
                                        CodeFieldContainer.this.codeField[num + a2].setText(text.substring(a2, a2 + 1));
                                    }
                                }
                                CodeFieldContainer.this.ignoreOnTextChange = false;
                            }
                            if (n != length - 1) {
                                CodeFieldContainer.this.codeField[n + 1].setSelection(CodeFieldContainer.this.codeField[n + 1].length());
                                CodeFieldContainer.this.codeField[n + 1].requestFocus();
                            }
                            int i2 = length;
                            if ((n == i2 - 1 || (n == i2 - 2 && len >= 2)) && CodeFieldContainer.this.getCode().length() == length) {
                                CodeFieldContainer.this.processNextPressed();
                            }
                        }
                    }
                });
                this.codeField[a].setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: org.telegram.ui.CodeFieldContainer$$ExternalSyntheticLambda0
                    @Override // android.widget.TextView.OnEditorActionListener
                    public final boolean onEditorAction(TextView textView, int i2, KeyEvent keyEvent) {
                        return CodeFieldContainer.this.m2176lambda$setNumbersCount$0$orgtelegramuiCodeFieldContainer(textView, i2, keyEvent);
                    }
                });
                a++;
                i = currentType;
            }
            return;
        }
        int a2 = 0;
        while (true) {
            CodeNumberField[] codeNumberFieldArr2 = this.codeField;
            if (a2 < codeNumberFieldArr2.length) {
                codeNumberFieldArr2[a2].setText("");
                a2++;
            } else {
                return;
            }
        }
    }

    /* renamed from: lambda$setNumbersCount$0$org-telegram-ui-CodeFieldContainer */
    public /* synthetic */ boolean m2176lambda$setNumbersCount$0$orgtelegramuiCodeFieldContainer(TextView textView, int i, KeyEvent keyEvent) {
        if (i == 5) {
            processNextPressed();
            return true;
        }
        return false;
    }

    protected void processNextPressed() {
    }

    public String getCode() {
        if (this.codeField == null) {
            return "";
        }
        StringBuilder codeBuilder = new StringBuilder();
        int a = 0;
        while (true) {
            CodeNumberField[] codeNumberFieldArr = this.codeField;
            if (a < codeNumberFieldArr.length) {
                codeBuilder.append(PhoneFormat.stripExceptNumbers(codeNumberFieldArr[a].getText().toString()));
                a++;
            } else {
                return codeBuilder.toString();
            }
        }
    }

    public void setCode(String savedCode) {
        this.codeField[0].setText(savedCode);
    }

    public void setText(String code) {
        setText(code, false);
    }

    public void setText(String code, boolean fromPaste) {
        int startFrom = 0;
        if (fromPaste) {
            int i = 0;
            while (true) {
                CodeNumberField[] codeNumberFieldArr = this.codeField;
                if (i < codeNumberFieldArr.length) {
                    if (!codeNumberFieldArr[i].isFocused()) {
                        i++;
                    } else {
                        startFrom = i;
                        break;
                    }
                } else {
                    break;
                }
            }
        }
        for (int i2 = startFrom; i2 < Math.min(this.codeField.length, code.length() + startFrom); i2++) {
            this.codeField[i2].setText(Character.toString(code.charAt(i2 - startFrom)));
        }
    }
}
