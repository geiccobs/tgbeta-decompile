package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.view.InputDeviceCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.C;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.TextColorThemeCell;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ThemeEditorView;
import org.telegram.ui.Components.WallpaperUpdater;
import org.telegram.ui.LaunchActivity;
/* loaded from: classes5.dex */
public class ThemeEditorView {
    private static volatile ThemeEditorView Instance = null;
    private ArrayList<ThemeDescription> currentThemeDesription;
    private int currentThemeDesriptionPosition;
    private DecelerateInterpolator decelerateInterpolator;
    private EditorAlert editorAlert;
    private boolean hidden;
    private Activity parentActivity;
    private SharedPreferences preferences;
    private Theme.ThemeInfo themeInfo;
    private WallpaperUpdater wallpaperUpdater;
    private WindowManager.LayoutParams windowLayoutParams;
    private WindowManager windowManager;
    private FrameLayout windowView;
    private final int editorWidth = AndroidUtilities.dp(54.0f);
    private final int editorHeight = AndroidUtilities.dp(54.0f);

    public static ThemeEditorView getInstance() {
        return Instance;
    }

    public void destroy() {
        FrameLayout frameLayout;
        this.wallpaperUpdater.cleanup();
        if (this.parentActivity == null || (frameLayout = this.windowView) == null) {
            return;
        }
        try {
            this.windowManager.removeViewImmediate(frameLayout);
            this.windowView = null;
        } catch (Exception e) {
            FileLog.e(e);
        }
        try {
            EditorAlert editorAlert = this.editorAlert;
            if (editorAlert != null) {
                editorAlert.dismiss();
                this.editorAlert = null;
            }
        } catch (Exception e2) {
            FileLog.e(e2);
        }
        this.parentActivity = null;
        Instance = null;
    }

    /* loaded from: classes5.dex */
    public class EditorAlert extends BottomSheet {
        private boolean animationInProgress;
        private FrameLayout bottomLayout;
        private FrameLayout bottomSaveLayout;
        private AnimatorSet colorChangeAnimation;
        private ColorPicker colorPicker;
        private FrameLayout frameLayout;
        private boolean ignoreTextChange;
        private LinearLayoutManager layoutManager;
        private ListAdapter listAdapter;
        private RecyclerListView listView;
        private int previousScrollPosition;
        private TextView saveButton;
        private int scrollOffsetY;
        private SearchAdapter searchAdapter;
        private EmptyTextProgressView searchEmptyView;
        private SearchField searchField;
        private View[] shadow = new View[2];
        private AnimatorSet[] shadowAnimation = new AnimatorSet[2];
        private Drawable shadowDrawable;
        private boolean startedColorChange;
        private int topBeforeSwitch;

        /* loaded from: classes5.dex */
        public class SearchField extends FrameLayout {
            private View backgroundView;
            private ImageView clearSearchImageView;
            private EditTextBoldCursor searchEditText;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            public SearchField(Context context) {
                super(context);
                EditorAlert.this = r13;
                View searchBackground = new View(context);
                searchBackground.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(18.0f), -854795));
                addView(searchBackground, LayoutHelper.createFrame(-1, 36.0f, 51, 14.0f, 11.0f, 14.0f, 0.0f));
                ImageView searchIconImageView = new ImageView(context);
                searchIconImageView.setScaleType(ImageView.ScaleType.CENTER);
                searchIconImageView.setImageResource(R.drawable.smiles_inputsearch);
                searchIconImageView.setColorFilter(new PorterDuffColorFilter(-6182737, PorterDuff.Mode.MULTIPLY));
                addView(searchIconImageView, LayoutHelper.createFrame(36, 36.0f, 51, 16.0f, 11.0f, 0.0f, 0.0f));
                ImageView imageView = new ImageView(context);
                this.clearSearchImageView = imageView;
                imageView.setScaleType(ImageView.ScaleType.CENTER);
                ImageView imageView2 = this.clearSearchImageView;
                CloseProgressDrawable2 progressDrawable = new CloseProgressDrawable2() { // from class: org.telegram.ui.Components.ThemeEditorView.EditorAlert.SearchField.1
                    @Override // org.telegram.ui.Components.CloseProgressDrawable2
                    public int getCurrentColor() {
                        return -6182737;
                    }
                };
                imageView2.setImageDrawable(progressDrawable);
                progressDrawable.setSide(AndroidUtilities.dp(7.0f));
                this.clearSearchImageView.setScaleX(0.1f);
                this.clearSearchImageView.setScaleY(0.1f);
                this.clearSearchImageView.setAlpha(0.0f);
                addView(this.clearSearchImageView, LayoutHelper.createFrame(36, 36.0f, 53, 14.0f, 11.0f, 14.0f, 0.0f));
                this.clearSearchImageView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ThemeEditorView$EditorAlert$SearchField$$ExternalSyntheticLambda0
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        ThemeEditorView.EditorAlert.SearchField.this.m3142xacc46f88(view);
                    }
                });
                EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context) { // from class: org.telegram.ui.Components.ThemeEditorView.EditorAlert.SearchField.2
                    @Override // org.telegram.ui.Components.EditTextEffects, android.view.View
                    public boolean dispatchTouchEvent(MotionEvent event) {
                        MotionEvent e = MotionEvent.obtain(event);
                        e.setLocation(e.getRawX(), e.getRawY() - EditorAlert.this.containerView.getTranslationY());
                        EditorAlert.this.listView.dispatchTouchEvent(e);
                        e.recycle();
                        return super.dispatchTouchEvent(event);
                    }
                };
                this.searchEditText = editTextBoldCursor;
                editTextBoldCursor.setTextSize(1, 16.0f);
                this.searchEditText.setHintTextColor(-6774617);
                this.searchEditText.setTextColor(-14540254);
                this.searchEditText.setBackgroundDrawable(null);
                this.searchEditText.setPadding(0, 0, 0, 0);
                this.searchEditText.setMaxLines(1);
                this.searchEditText.setLines(1);
                this.searchEditText.setSingleLine(true);
                this.searchEditText.setImeOptions(268435459);
                this.searchEditText.setHint(LocaleController.getString("Search", R.string.Search));
                this.searchEditText.setCursorColor(-11491093);
                this.searchEditText.setCursorSize(AndroidUtilities.dp(20.0f));
                this.searchEditText.setCursorWidth(1.5f);
                addView(this.searchEditText, LayoutHelper.createFrame(-1, 40.0f, 51, 54.0f, 9.0f, 46.0f, 0.0f));
                this.searchEditText.addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.Components.ThemeEditorView.EditorAlert.SearchField.3
                    @Override // android.text.TextWatcher
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override // android.text.TextWatcher
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override // android.text.TextWatcher
                    public void afterTextChanged(Editable s) {
                        boolean showed = true;
                        boolean show = SearchField.this.searchEditText.length() > 0;
                        float f = 0.0f;
                        if (SearchField.this.clearSearchImageView.getAlpha() == 0.0f) {
                            showed = false;
                        }
                        if (show != showed) {
                            ViewPropertyAnimator animate = SearchField.this.clearSearchImageView.animate();
                            float f2 = 1.0f;
                            if (show) {
                                f = 1.0f;
                            }
                            ViewPropertyAnimator scaleX = animate.alpha(f).setDuration(150L).scaleX(show ? 1.0f : 0.1f);
                            if (!show) {
                                f2 = 0.1f;
                            }
                            scaleX.scaleY(f2).start();
                        }
                        String text = SearchField.this.searchEditText.getText().toString();
                        if (text.length() != 0) {
                            if (EditorAlert.this.searchEmptyView != null) {
                                EditorAlert.this.searchEmptyView.setText(LocaleController.getString("NoResult", R.string.NoResult));
                            }
                        } else if (EditorAlert.this.listView.getAdapter() != EditorAlert.this.listAdapter) {
                            int top = EditorAlert.this.getCurrentTop();
                            EditorAlert.this.searchEmptyView.setText(LocaleController.getString("NoChats", R.string.NoChats));
                            EditorAlert.this.searchEmptyView.showTextView();
                            EditorAlert.this.listView.setAdapter(EditorAlert.this.listAdapter);
                            EditorAlert.this.listAdapter.notifyDataSetChanged();
                            if (top > 0) {
                                EditorAlert.this.layoutManager.scrollToPositionWithOffset(0, -top);
                            }
                        }
                        if (EditorAlert.this.searchAdapter != null) {
                            EditorAlert.this.searchAdapter.searchDialogs(text);
                        }
                    }
                });
                this.searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: org.telegram.ui.Components.ThemeEditorView$EditorAlert$SearchField$$ExternalSyntheticLambda1
                    @Override // android.widget.TextView.OnEditorActionListener
                    public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                        return ThemeEditorView.EditorAlert.SearchField.this.m3143x49326be7(textView, i, keyEvent);
                    }
                });
            }

            /* renamed from: lambda$new$0$org-telegram-ui-Components-ThemeEditorView$EditorAlert$SearchField */
            public /* synthetic */ void m3142xacc46f88(View v) {
                this.searchEditText.setText("");
                AndroidUtilities.showKeyboard(this.searchEditText);
            }

            /* renamed from: lambda$new$1$org-telegram-ui-Components-ThemeEditorView$EditorAlert$SearchField */
            public /* synthetic */ boolean m3143x49326be7(TextView v, int actionId, KeyEvent event) {
                if (event != null) {
                    if ((event.getAction() == 1 && event.getKeyCode() == 84) || (event.getAction() == 0 && event.getKeyCode() == 66)) {
                        AndroidUtilities.hideKeyboard(this.searchEditText);
                        return false;
                    }
                    return false;
                }
                return false;
            }

            public void hideKeyboard() {
                AndroidUtilities.hideKeyboard(this.searchEditText);
            }

            public void showKeyboard() {
                this.searchEditText.requestFocus();
                AndroidUtilities.showKeyboard(this.searchEditText);
            }

            @Override // android.view.ViewGroup, android.view.ViewParent
            public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
                super.requestDisallowInterceptTouchEvent(disallowIntercept);
            }
        }

        /* loaded from: classes5.dex */
        public class ColorPicker extends FrameLayout {
            private LinearGradient alphaGradient;
            private boolean alphaPressed;
            private Drawable circleDrawable;
            private boolean circlePressed;
            private LinearGradient colorGradient;
            private boolean colorPressed;
            private Bitmap colorWheelBitmap;
            private Paint colorWheelPaint;
            private int colorWheelRadius;
            private LinearLayout linearLayout;
            private Paint valueSliderPaint;
            private final int paramValueSliderWidth = AndroidUtilities.dp(20.0f);
            private EditTextBoldCursor[] colorEditText = new EditTextBoldCursor[4];
            private float[] colorHSV = {0.0f, 0.0f, 1.0f};
            private float alpha = 1.0f;
            private float[] hsvTemp = new float[3];
            private DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();
            private Paint circlePaint = new Paint(1);

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            public ColorPicker(Context context) {
                super(context);
                EditorAlert.this = r20;
                setWillNotDraw(false);
                this.circleDrawable = context.getResources().getDrawable(R.drawable.knob_shadow).mutate();
                Paint paint = new Paint();
                this.colorWheelPaint = paint;
                paint.setAntiAlias(true);
                this.colorWheelPaint.setDither(true);
                Paint paint2 = new Paint();
                this.valueSliderPaint = paint2;
                paint2.setAntiAlias(true);
                this.valueSliderPaint.setDither(true);
                LinearLayout linearLayout = new LinearLayout(context);
                this.linearLayout = linearLayout;
                linearLayout.setOrientation(0);
                addView(this.linearLayout, LayoutHelper.createFrame(-2, -2, 49));
                int a = 0;
                while (a < 4) {
                    this.colorEditText[a] = new EditTextBoldCursor(context);
                    this.colorEditText[a].setInputType(2);
                    this.colorEditText[a].setTextColor(Theme.MSG_OUT_COLOR_BLACK);
                    this.colorEditText[a].setCursorColor(Theme.MSG_OUT_COLOR_BLACK);
                    this.colorEditText[a].setCursorSize(AndroidUtilities.dp(20.0f));
                    this.colorEditText[a].setCursorWidth(1.5f);
                    this.colorEditText[a].setTextSize(1, 18.0f);
                    this.colorEditText[a].setBackground(null);
                    this.colorEditText[a].setLineColors(Theme.getColor(Theme.key_dialogInputField), Theme.getColor(Theme.key_dialogInputFieldActivated), Theme.getColor(Theme.key_dialogTextRed2));
                    this.colorEditText[a].setMaxLines(1);
                    this.colorEditText[a].setTag(Integer.valueOf(a));
                    this.colorEditText[a].setGravity(17);
                    if (a == 0) {
                        this.colorEditText[a].setHint("red");
                    } else if (a == 1) {
                        this.colorEditText[a].setHint("green");
                    } else if (a == 2) {
                        this.colorEditText[a].setHint("blue");
                    } else if (a == 3) {
                        this.colorEditText[a].setHint("alpha");
                    }
                    this.colorEditText[a].setImeOptions((a == 3 ? 6 : 5) | 268435456);
                    InputFilter[] inputFilters = {new InputFilter.LengthFilter(3)};
                    this.colorEditText[a].setFilters(inputFilters);
                    final int num = a;
                    this.linearLayout.addView(this.colorEditText[a], LayoutHelper.createLinear(55, 36, 0.0f, 0.0f, a != 3 ? 16.0f : 0.0f, 0.0f));
                    this.colorEditText[a].addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.Components.ThemeEditorView.EditorAlert.ColorPicker.1
                        @Override // android.text.TextWatcher
                        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                        }

                        @Override // android.text.TextWatcher
                        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                        }

                        @Override // android.text.TextWatcher
                        public void afterTextChanged(Editable editable) {
                            if (!EditorAlert.this.ignoreTextChange) {
                                EditorAlert.this.ignoreTextChange = true;
                                int color = Utilities.parseInt((CharSequence) editable.toString()).intValue();
                                if (color < 0) {
                                    color = 0;
                                    EditTextBoldCursor editTextBoldCursor = ColorPicker.this.colorEditText[num];
                                    editTextBoldCursor.setText("0");
                                    ColorPicker.this.colorEditText[num].setSelection(ColorPicker.this.colorEditText[num].length());
                                } else if (color > 255) {
                                    color = 255;
                                    EditTextBoldCursor editTextBoldCursor2 = ColorPicker.this.colorEditText[num];
                                    editTextBoldCursor2.setText("255");
                                    ColorPicker.this.colorEditText[num].setSelection(ColorPicker.this.colorEditText[num].length());
                                }
                                int currentColor = ColorPicker.this.getColor();
                                int i = num;
                                if (i == 2) {
                                    currentColor = (currentColor & InputDeviceCompat.SOURCE_ANY) | (color & 255);
                                } else if (i == 1) {
                                    currentColor = ((-65281) & currentColor) | ((color & 255) << 8);
                                } else if (i == 0) {
                                    currentColor = ((-16711681) & currentColor) | ((color & 255) << 16);
                                } else if (i == 3) {
                                    currentColor = (16777215 & currentColor) | ((color & 255) << 24);
                                }
                                ColorPicker.this.setColor(currentColor);
                                for (int a2 = 0; a2 < ThemeEditorView.this.currentThemeDesription.size(); a2++) {
                                    ((ThemeDescription) ThemeEditorView.this.currentThemeDesription.get(a2)).setColor(ColorPicker.this.getColor(), false);
                                }
                                EditorAlert.this.ignoreTextChange = false;
                            }
                        }
                    });
                    this.colorEditText[a].setOnEditorActionListener(ThemeEditorView$EditorAlert$ColorPicker$$ExternalSyntheticLambda0.INSTANCE);
                    a++;
                }
            }

            public static /* synthetic */ boolean lambda$new$0(TextView textView, int i, KeyEvent keyEvent) {
                if (i == 6) {
                    AndroidUtilities.hideKeyboard(textView);
                    return true;
                }
                return false;
            }

            @Override // android.widget.FrameLayout, android.view.View
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
                int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
                int size = Math.min(widthSize, heightSize);
                measureChild(this.linearLayout, widthMeasureSpec, heightMeasureSpec);
                setMeasuredDimension(size, size);
            }

            @Override // android.view.View
            protected void onDraw(Canvas canvas) {
                int centerX = (getWidth() / 2) - (this.paramValueSliderWidth * 2);
                int centerY = (getHeight() / 2) - AndroidUtilities.dp(8.0f);
                Bitmap bitmap = this.colorWheelBitmap;
                int i = this.colorWheelRadius;
                canvas.drawBitmap(bitmap, centerX - i, centerY - i, (Paint) null);
                float hueAngle = (float) Math.toRadians(this.colorHSV[0]);
                double d = this.colorHSV[1];
                Double.isNaN(d);
                double d2 = (-Math.cos(hueAngle)) * d;
                double d3 = this.colorWheelRadius;
                Double.isNaN(d3);
                int colorPointX = ((int) (d2 * d3)) + centerX;
                float[] fArr = this.colorHSV;
                double d4 = fArr[1];
                Double.isNaN(d4);
                double d5 = (-Math.sin(hueAngle)) * d4;
                int i2 = this.colorWheelRadius;
                double d6 = i2;
                Double.isNaN(d6);
                int colorPointY = ((int) (d5 * d6)) + centerY;
                float f = i2 * 0.075f;
                float[] fArr2 = this.hsvTemp;
                fArr2[0] = fArr[0];
                fArr2[1] = fArr[1];
                fArr2[2] = 1.0f;
                drawPointerArrow(canvas, colorPointX, colorPointY, Color.HSVToColor(fArr2));
                int i3 = this.colorWheelRadius;
                int x = centerX + i3 + this.paramValueSliderWidth;
                int y = centerY - i3;
                int width = AndroidUtilities.dp(9.0f);
                int height = this.colorWheelRadius * 2;
                if (this.colorGradient == null) {
                    this.colorGradient = new LinearGradient(x, y, x + width, y + height, new int[]{-16777216, Color.HSVToColor(this.hsvTemp)}, (float[]) null, Shader.TileMode.CLAMP);
                }
                this.valueSliderPaint.setShader(this.colorGradient);
                canvas.drawRect(x, y, x + width, y + height, this.valueSliderPaint);
                float[] fArr3 = this.colorHSV;
                drawPointerArrow(canvas, x + (width / 2), (int) (y + (fArr3[2] * height)), Color.HSVToColor(fArr3));
                int x2 = x + (this.paramValueSliderWidth * 2);
                if (this.alphaGradient == null) {
                    int color = Color.HSVToColor(this.hsvTemp);
                    this.alphaGradient = new LinearGradient(x2, y, x2 + width, y + height, new int[]{color, color & ViewCompat.MEASURED_SIZE_MASK}, (float[]) null, Shader.TileMode.CLAMP);
                }
                this.valueSliderPaint.setShader(this.alphaGradient);
                canvas.drawRect(x2, y, x2 + width, y + height, this.valueSliderPaint);
                drawPointerArrow(canvas, (width / 2) + x2, (int) (y + ((1.0f - this.alpha) * height)), (Color.HSVToColor(this.colorHSV) & ViewCompat.MEASURED_SIZE_MASK) | (((int) (this.alpha * 255.0f)) << 24));
            }

            private void drawPointerArrow(Canvas canvas, int x, int y, int color) {
                int side = AndroidUtilities.dp(13.0f);
                this.circleDrawable.setBounds(x - side, y - side, x + side, y + side);
                this.circleDrawable.draw(canvas);
                this.circlePaint.setColor(-1);
                canvas.drawCircle(x, y, AndroidUtilities.dp(11.0f), this.circlePaint);
                this.circlePaint.setColor(color);
                canvas.drawCircle(x, y, AndroidUtilities.dp(9.0f), this.circlePaint);
            }

            @Override // android.view.View
            protected void onSizeChanged(int width, int height, int oldw, int oldh) {
                int max = Math.max(1, ((width / 2) - (this.paramValueSliderWidth * 2)) - AndroidUtilities.dp(20.0f));
                this.colorWheelRadius = max;
                this.colorWheelBitmap = createColorWheelBitmap(max * 2, max * 2);
                this.colorGradient = null;
                this.alphaGradient = null;
            }

            private Bitmap createColorWheelBitmap(int width, int height) {
                Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                int[] colors = new int[12 + 1];
                float[] hsv = {0.0f, 1.0f, 1.0f};
                for (int i = 0; i < colors.length; i++) {
                    hsv[0] = ((i * 30) + 180) % 360;
                    colors[i] = Color.HSVToColor(hsv);
                }
                int i2 = colors[0];
                colors[12] = i2;
                SweepGradient sweepGradient = new SweepGradient(width / 2, height / 2, colors, (float[]) null);
                RadialGradient radialGradient = new RadialGradient(width / 2, height / 2, this.colorWheelRadius, -1, (int) ViewCompat.MEASURED_SIZE_MASK, Shader.TileMode.CLAMP);
                ComposeShader composeShader = new ComposeShader(sweepGradient, radialGradient, PorterDuff.Mode.SRC_OVER);
                this.colorWheelPaint.setShader(composeShader);
                Canvas canvas = new Canvas(bitmap);
                canvas.drawCircle(width / 2, height / 2, this.colorWheelRadius, this.colorWheelPaint);
                return bitmap;
            }

            private void startColorChange(boolean start) {
                if (EditorAlert.this.startedColorChange != start) {
                    if (EditorAlert.this.colorChangeAnimation != null) {
                        EditorAlert.this.colorChangeAnimation.cancel();
                    }
                    EditorAlert.this.startedColorChange = start;
                    EditorAlert.this.colorChangeAnimation = new AnimatorSet();
                    AnimatorSet animatorSet = EditorAlert.this.colorChangeAnimation;
                    Animator[] animatorArr = new Animator[2];
                    ColorDrawable colorDrawable = EditorAlert.this.backDrawable;
                    Property<ColorDrawable, Integer> property = AnimationProperties.COLOR_DRAWABLE_ALPHA;
                    int[] iArr = new int[1];
                    iArr[0] = start ? 0 : 51;
                    animatorArr[0] = ObjectAnimator.ofInt(colorDrawable, property, iArr);
                    ViewGroup viewGroup = EditorAlert.this.containerView;
                    Property property2 = View.ALPHA;
                    float[] fArr = new float[1];
                    fArr[0] = start ? 0.2f : 1.0f;
                    animatorArr[1] = ObjectAnimator.ofFloat(viewGroup, property2, fArr);
                    animatorSet.playTogether(animatorArr);
                    EditorAlert.this.colorChangeAnimation.setDuration(150L);
                    EditorAlert.this.colorChangeAnimation.setInterpolator(this.decelerateInterpolator);
                    EditorAlert.this.colorChangeAnimation.start();
                }
            }

            /* JADX WARN: Code restructure failed: missing block: B:13:0x005e, code lost:
                if (r10 <= r22.colorWheelRadius) goto L17;
             */
            /* JADX WARN: Code restructure failed: missing block: B:34:0x00cf, code lost:
                if (r4 <= (r17 + r2)) goto L37;
             */
            /* JADX WARN: Code restructure failed: missing block: B:57:0x011e, code lost:
                if (r4 <= (r17 + r2)) goto L58;
             */
            /* JADX WARN: Removed duplicated region for block: B:23:0x00ad  */
            /* JADX WARN: Removed duplicated region for block: B:36:0x00d5  */
            /* JADX WARN: Removed duplicated region for block: B:39:0x00e7  */
            /* JADX WARN: Removed duplicated region for block: B:40:0x00e9  */
            /* JADX WARN: Removed duplicated region for block: B:46:0x00fd  */
            /* JADX WARN: Removed duplicated region for block: B:60:0x0136  */
            /* JADX WARN: Removed duplicated region for block: B:61:0x0139  */
            /* JADX WARN: Removed duplicated region for block: B:67:0x0145  */
            /* JADX WARN: Removed duplicated region for block: B:75:0x0168  */
            /* JADX WARN: Removed duplicated region for block: B:93:0x01dd  */
            /* JADX WARN: Removed duplicated region for block: B:98:0x025e  */
            @Override // android.view.View
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct add '--show-bad-code' argument
            */
            public boolean onTouchEvent(android.view.MotionEvent r23) {
                /*
                    Method dump skipped, instructions count: 628
                    To view this dump add '--comments-level debug' option
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ThemeEditorView.EditorAlert.ColorPicker.onTouchEvent(android.view.MotionEvent):boolean");
            }

            public void setColor(int color) {
                int red = Color.red(color);
                int green = Color.green(color);
                int blue = Color.blue(color);
                int a = Color.alpha(color);
                if (!EditorAlert.this.ignoreTextChange) {
                    EditorAlert.this.ignoreTextChange = true;
                    EditTextBoldCursor editTextBoldCursor = this.colorEditText[0];
                    editTextBoldCursor.setText("" + red);
                    EditTextBoldCursor editTextBoldCursor2 = this.colorEditText[1];
                    editTextBoldCursor2.setText("" + green);
                    EditTextBoldCursor editTextBoldCursor3 = this.colorEditText[2];
                    editTextBoldCursor3.setText("" + blue);
                    EditTextBoldCursor editTextBoldCursor4 = this.colorEditText[3];
                    editTextBoldCursor4.setText("" + a);
                    for (int b = 0; b < 4; b++) {
                        EditTextBoldCursor[] editTextBoldCursorArr = this.colorEditText;
                        editTextBoldCursorArr[b].setSelection(editTextBoldCursorArr[b].length());
                    }
                    EditorAlert.this.ignoreTextChange = false;
                }
                this.alphaGradient = null;
                this.colorGradient = null;
                this.alpha = a / 255.0f;
                Color.colorToHSV(color, this.colorHSV);
                invalidate();
            }

            public int getColor() {
                return (Color.HSVToColor(this.colorHSV) & ViewCompat.MEASURED_SIZE_MASK) | (((int) (this.alpha * 255.0f)) << 24);
            }
        }

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public EditorAlert(Context context, ArrayList<ThemeDescription> items) {
            super(context, true);
            ThemeEditorView.this = this$0;
            this.shadowDrawable = context.getResources().getDrawable(R.drawable.sheet_shadow_round).mutate();
            this.containerView = new FrameLayout(context) { // from class: org.telegram.ui.Components.ThemeEditorView.EditorAlert.1
                private boolean ignoreLayout = false;
                private RectF rect1 = new RectF();

                @Override // android.view.ViewGroup
                public boolean onInterceptTouchEvent(MotionEvent ev) {
                    if (ev.getAction() == 0 && EditorAlert.this.scrollOffsetY != 0 && ev.getY() < EditorAlert.this.scrollOffsetY) {
                        EditorAlert.this.dismiss();
                        return true;
                    }
                    return super.onInterceptTouchEvent(ev);
                }

                @Override // android.view.View
                public boolean onTouchEvent(MotionEvent e) {
                    return !EditorAlert.this.isDismissed() && super.onTouchEvent(e);
                }

                @Override // android.widget.FrameLayout, android.view.View
                protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    int width = View.MeasureSpec.getSize(widthMeasureSpec);
                    int height = View.MeasureSpec.getSize(heightMeasureSpec);
                    if (Build.VERSION.SDK_INT >= 21 && !EditorAlert.this.isFullscreen) {
                        this.ignoreLayout = true;
                        setPadding(EditorAlert.this.backgroundPaddingLeft, AndroidUtilities.statusBarHeight, EditorAlert.this.backgroundPaddingLeft, 0);
                        this.ignoreLayout = false;
                    }
                    int pickerSize = Math.min(width, height - (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0));
                    int padding = ((height - (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0)) + AndroidUtilities.dp(8.0f)) - pickerSize;
                    if (EditorAlert.this.listView.getPaddingTop() != padding) {
                        this.ignoreLayout = true;
                        EditorAlert.this.listView.getPaddingTop();
                        EditorAlert.this.listView.setPadding(0, padding, 0, AndroidUtilities.dp(48.0f));
                        if (EditorAlert.this.colorPicker.getVisibility() == 0) {
                            EditorAlert editorAlert = EditorAlert.this;
                            editorAlert.setScrollOffsetY(editorAlert.listView.getPaddingTop());
                            EditorAlert.this.previousScrollPosition = 0;
                        }
                        this.ignoreLayout = false;
                    }
                    super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(height, C.BUFFER_FLAG_ENCRYPTED));
                }

                @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
                protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                    super.onLayout(changed, left, top, right, bottom);
                    EditorAlert.this.updateLayout();
                }

                @Override // android.view.View, android.view.ViewParent
                public void requestLayout() {
                    if (this.ignoreLayout) {
                        return;
                    }
                    super.requestLayout();
                }

                @Override // android.view.View
                protected void onDraw(Canvas canvas) {
                    float radProgress;
                    int statusBarHeight;
                    int height;
                    int top;
                    int y;
                    int y2 = (EditorAlert.this.scrollOffsetY - EditorAlert.this.backgroundPaddingTop) + AndroidUtilities.dp(6.0f);
                    int top2 = (EditorAlert.this.scrollOffsetY - EditorAlert.this.backgroundPaddingTop) - AndroidUtilities.dp(13.0f);
                    int height2 = getMeasuredHeight() + AndroidUtilities.dp(30.0f) + EditorAlert.this.backgroundPaddingTop;
                    float radProgress2 = 1.0f;
                    if (!EditorAlert.this.isFullscreen && Build.VERSION.SDK_INT >= 21) {
                        int top3 = top2 + AndroidUtilities.statusBarHeight;
                        int y3 = y2 + AndroidUtilities.statusBarHeight;
                        int height3 = height2 - AndroidUtilities.statusBarHeight;
                        if (EditorAlert.this.backgroundPaddingTop + top3 < AndroidUtilities.statusBarHeight * 2) {
                            int diff = Math.min(AndroidUtilities.statusBarHeight, ((AndroidUtilities.statusBarHeight * 2) - top3) - EditorAlert.this.backgroundPaddingTop);
                            top3 -= diff;
                            height3 += diff;
                            radProgress2 = 1.0f - Math.min(1.0f, (diff * 2) / AndroidUtilities.statusBarHeight);
                        }
                        if (EditorAlert.this.backgroundPaddingTop + top3 < AndroidUtilities.statusBarHeight) {
                            y = y3;
                            top = top3;
                            height = height3;
                            statusBarHeight = Math.min(AndroidUtilities.statusBarHeight, (AndroidUtilities.statusBarHeight - top3) - EditorAlert.this.backgroundPaddingTop);
                            radProgress = radProgress2;
                        } else {
                            y = y3;
                            top = top3;
                            height = height3;
                            statusBarHeight = 0;
                            radProgress = radProgress2;
                        }
                    } else {
                        y = y2;
                        top = top2;
                        height = height2;
                        statusBarHeight = 0;
                        radProgress = 1.0f;
                    }
                    EditorAlert.this.shadowDrawable.setBounds(0, top, getMeasuredWidth(), height);
                    EditorAlert.this.shadowDrawable.draw(canvas);
                    if (radProgress != 1.0f) {
                        Theme.dialogs_onlineCirclePaint.setColor(-1);
                        this.rect1.set(EditorAlert.this.backgroundPaddingLeft, EditorAlert.this.backgroundPaddingTop + top, getMeasuredWidth() - EditorAlert.this.backgroundPaddingLeft, EditorAlert.this.backgroundPaddingTop + top + AndroidUtilities.dp(24.0f));
                        canvas.drawRoundRect(this.rect1, AndroidUtilities.dp(12.0f) * radProgress, AndroidUtilities.dp(12.0f) * radProgress, Theme.dialogs_onlineCirclePaint);
                    }
                    int w = AndroidUtilities.dp(36.0f);
                    this.rect1.set((getMeasuredWidth() - w) / 2, y, (getMeasuredWidth() + w) / 2, AndroidUtilities.dp(4.0f) + y);
                    Theme.dialogs_onlineCirclePaint.setColor(-1973016);
                    Theme.dialogs_onlineCirclePaint.setAlpha((int) (EditorAlert.this.listView.getAlpha() * 255.0f));
                    canvas.drawRoundRect(this.rect1, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), Theme.dialogs_onlineCirclePaint);
                    if (statusBarHeight > 0) {
                        int finalColor = Color.argb(255, (int) (Color.red(-1) * 0.8f), (int) (Color.green(-1) * 0.8f), (int) (Color.blue(-1) * 0.8f));
                        Theme.dialogs_onlineCirclePaint.setColor(finalColor);
                        canvas.drawRect(EditorAlert.this.backgroundPaddingLeft, AndroidUtilities.statusBarHeight - statusBarHeight, getMeasuredWidth() - EditorAlert.this.backgroundPaddingLeft, AndroidUtilities.statusBarHeight, Theme.dialogs_onlineCirclePaint);
                    }
                }
            };
            this.containerView.setWillNotDraw(false);
            this.containerView.setPadding(this.backgroundPaddingLeft, 0, this.backgroundPaddingLeft, 0);
            FrameLayout frameLayout = new FrameLayout(context);
            this.frameLayout = frameLayout;
            frameLayout.setBackgroundColor(-1);
            SearchField searchField = new SearchField(context);
            this.searchField = searchField;
            this.frameLayout.addView(searchField, LayoutHelper.createFrame(-1, -1, 51));
            RecyclerListView recyclerListView = new RecyclerListView(context) { // from class: org.telegram.ui.Components.ThemeEditorView.EditorAlert.2
                @Override // org.telegram.ui.Components.RecyclerListView
                protected boolean allowSelectChildAtPosition(float x, float y) {
                    return y >= ((float) ((EditorAlert.this.scrollOffsetY + AndroidUtilities.dp(48.0f)) + (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0)));
                }
            };
            this.listView = recyclerListView;
            recyclerListView.setSelectorDrawableColor(AndroidUtilities.LIGHT_STATUS_BAR_OVERLAY);
            this.listView.setPadding(0, 0, 0, AndroidUtilities.dp(48.0f));
            this.listView.setClipToPadding(false);
            RecyclerListView recyclerListView2 = this.listView;
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            this.layoutManager = linearLayoutManager;
            recyclerListView2.setLayoutManager(linearLayoutManager);
            this.listView.setHorizontalScrollBarEnabled(false);
            this.listView.setVerticalScrollBarEnabled(false);
            this.containerView.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
            RecyclerListView recyclerListView3 = this.listView;
            ListAdapter listAdapter = new ListAdapter(context, items);
            this.listAdapter = listAdapter;
            recyclerListView3.setAdapter(listAdapter);
            this.searchAdapter = new SearchAdapter(context);
            this.listView.setGlowColor(-657673);
            this.listView.setItemAnimator(null);
            this.listView.setLayoutAnimation(null);
            this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.Components.ThemeEditorView$EditorAlert$$ExternalSyntheticLambda5
                @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
                public final void onItemClick(View view, int i) {
                    ThemeEditorView.EditorAlert.this.m3134xc2ffc91a(view, i);
                }
            });
            this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.Components.ThemeEditorView.EditorAlert.3
                @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    EditorAlert.this.updateLayout();
                }
            });
            EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context);
            this.searchEmptyView = emptyTextProgressView;
            emptyTextProgressView.setShowAtCenter(true);
            this.searchEmptyView.showTextView();
            this.searchEmptyView.setText(LocaleController.getString("NoResult", R.string.NoResult));
            this.listView.setEmptyView(this.searchEmptyView);
            this.containerView.addView(this.searchEmptyView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 52.0f, 0.0f, 0.0f));
            FrameLayout.LayoutParams frameLayoutParams = new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight(), 51);
            frameLayoutParams.topMargin = AndroidUtilities.dp(58.0f);
            this.shadow[0] = new View(context);
            this.shadow[0].setBackgroundColor(301989888);
            this.shadow[0].setAlpha(0.0f);
            this.shadow[0].setTag(1);
            this.containerView.addView(this.shadow[0], frameLayoutParams);
            this.containerView.addView(this.frameLayout, LayoutHelper.createFrame(-1, 58, 51));
            ColorPicker colorPicker = new ColorPicker(context);
            this.colorPicker = colorPicker;
            colorPicker.setVisibility(8);
            this.containerView.addView(this.colorPicker, LayoutHelper.createFrame(-1, -1, 1));
            FrameLayout.LayoutParams frameLayoutParams2 = new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight(), 83);
            frameLayoutParams2.bottomMargin = AndroidUtilities.dp(48.0f);
            this.shadow[1] = new View(context);
            this.shadow[1].setBackgroundColor(301989888);
            this.containerView.addView(this.shadow[1], frameLayoutParams2);
            FrameLayout frameLayout2 = new FrameLayout(context);
            this.bottomSaveLayout = frameLayout2;
            frameLayout2.setBackgroundColor(-1);
            this.containerView.addView(this.bottomSaveLayout, LayoutHelper.createFrame(-1, 48, 83));
            TextView closeButton = new TextView(context);
            closeButton.setTextSize(1, 14.0f);
            closeButton.setTextColor(-15095832);
            closeButton.setGravity(17);
            closeButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_AUDIO_SELECTOR_COLOR, 0));
            closeButton.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
            closeButton.setText(LocaleController.getString("CloseEditor", R.string.CloseEditor).toUpperCase());
            closeButton.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            this.bottomSaveLayout.addView(closeButton, LayoutHelper.createFrame(-2, -1, 51));
            closeButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ThemeEditorView$EditorAlert$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    ThemeEditorView.EditorAlert.this.m3135xc4361bf9(view);
                }
            });
            TextView saveButton = new TextView(context);
            saveButton.setTextSize(1, 14.0f);
            saveButton.setTextColor(-15095832);
            saveButton.setGravity(17);
            saveButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_AUDIO_SELECTOR_COLOR, 0));
            saveButton.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
            saveButton.setText(LocaleController.getString("SaveTheme", R.string.SaveTheme).toUpperCase());
            saveButton.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            this.bottomSaveLayout.addView(saveButton, LayoutHelper.createFrame(-2, -1, 53));
            saveButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ThemeEditorView$EditorAlert$$ExternalSyntheticLambda1
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    ThemeEditorView.EditorAlert.this.m3136xc56c6ed8(view);
                }
            });
            FrameLayout frameLayout3 = new FrameLayout(context);
            this.bottomLayout = frameLayout3;
            frameLayout3.setVisibility(8);
            this.bottomLayout.setBackgroundColor(-1);
            this.containerView.addView(this.bottomLayout, LayoutHelper.createFrame(-1, 48, 83));
            TextView cancelButton = new TextView(context);
            cancelButton.setTextSize(1, 14.0f);
            cancelButton.setTextColor(-15095832);
            cancelButton.setGravity(17);
            cancelButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_AUDIO_SELECTOR_COLOR, 0));
            cancelButton.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
            cancelButton.setText(LocaleController.getString("Cancel", R.string.Cancel).toUpperCase());
            cancelButton.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            this.bottomLayout.addView(cancelButton, LayoutHelper.createFrame(-2, -1, 51));
            cancelButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ThemeEditorView$EditorAlert$$ExternalSyntheticLambda2
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    ThemeEditorView.EditorAlert.this.m3137xc6a2c1b7(view);
                }
            });
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(0);
            this.bottomLayout.addView(linearLayout, LayoutHelper.createFrame(-2, -1, 53));
            TextView defaultButtom = new TextView(context);
            defaultButtom.setTextSize(1, 14.0f);
            defaultButtom.setTextColor(-15095832);
            defaultButtom.setGravity(17);
            defaultButtom.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_AUDIO_SELECTOR_COLOR, 0));
            defaultButtom.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
            defaultButtom.setText(LocaleController.getString("Default", R.string.Default).toUpperCase());
            defaultButtom.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            linearLayout.addView(defaultButtom, LayoutHelper.createFrame(-2, -1, 51));
            defaultButtom.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ThemeEditorView$EditorAlert$$ExternalSyntheticLambda3
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    ThemeEditorView.EditorAlert.this.m3138xc7d91496(view);
                }
            });
            TextView saveButton2 = new TextView(context);
            saveButton2.setTextSize(1, 14.0f);
            saveButton2.setTextColor(-15095832);
            saveButton2.setGravity(17);
            saveButton2.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_AUDIO_SELECTOR_COLOR, 0));
            saveButton2.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
            saveButton2.setText(LocaleController.getString("Save", R.string.Save).toUpperCase());
            saveButton2.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            linearLayout.addView(saveButton2, LayoutHelper.createFrame(-2, -1, 51));
            saveButton2.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ThemeEditorView$EditorAlert$$ExternalSyntheticLambda4
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    ThemeEditorView.EditorAlert.this.m3139xc90f6775(view);
                }
            });
        }

        /* renamed from: lambda$new$0$org-telegram-ui-Components-ThemeEditorView$EditorAlert */
        public /* synthetic */ void m3134xc2ffc91a(View view, int position) {
            if (position == 0) {
                return;
            }
            RecyclerView.Adapter adapter = this.listView.getAdapter();
            ListAdapter listAdapter = this.listAdapter;
            if (adapter == listAdapter) {
                ThemeEditorView.this.currentThemeDesription = listAdapter.getItem(position - 1);
            } else {
                ThemeEditorView.this.currentThemeDesription = this.searchAdapter.getItem(position - 1);
            }
            ThemeEditorView.this.currentThemeDesriptionPosition = position;
            for (int a = 0; a < ThemeEditorView.this.currentThemeDesription.size(); a++) {
                ThemeDescription description = (ThemeDescription) ThemeEditorView.this.currentThemeDesription.get(a);
                if (description.getCurrentKey().equals(Theme.key_chat_wallpaper)) {
                    ThemeEditorView.this.wallpaperUpdater.showAlert(true);
                    return;
                }
                description.startEditing();
                if (a == 0) {
                    this.colorPicker.setColor(description.getCurrentColor());
                }
            }
            setColorPickerVisible(true);
        }

        /* renamed from: lambda$new$1$org-telegram-ui-Components-ThemeEditorView$EditorAlert */
        public /* synthetic */ void m3135xc4361bf9(View v) {
            dismiss();
        }

        /* renamed from: lambda$new$2$org-telegram-ui-Components-ThemeEditorView$EditorAlert */
        public /* synthetic */ void m3136xc56c6ed8(View v) {
            Theme.saveCurrentTheme(ThemeEditorView.this.themeInfo, true, false, false);
            setOnDismissListener(null);
            dismiss();
            ThemeEditorView.this.close();
        }

        /* renamed from: lambda$new$3$org-telegram-ui-Components-ThemeEditorView$EditorAlert */
        public /* synthetic */ void m3137xc6a2c1b7(View v) {
            for (int a = 0; a < ThemeEditorView.this.currentThemeDesription.size(); a++) {
                ((ThemeDescription) ThemeEditorView.this.currentThemeDesription.get(a)).setPreviousColor();
            }
            setColorPickerVisible(false);
        }

        /* renamed from: lambda$new$4$org-telegram-ui-Components-ThemeEditorView$EditorAlert */
        public /* synthetic */ void m3138xc7d91496(View v) {
            for (int a = 0; a < ThemeEditorView.this.currentThemeDesription.size(); a++) {
                ((ThemeDescription) ThemeEditorView.this.currentThemeDesription.get(a)).setDefaultColor();
            }
            setColorPickerVisible(false);
        }

        /* renamed from: lambda$new$5$org-telegram-ui-Components-ThemeEditorView$EditorAlert */
        public /* synthetic */ void m3139xc90f6775(View v) {
            setColorPickerVisible(false);
        }

        private void runShadowAnimation(final int num, final boolean show) {
            if ((show && this.shadow[num].getTag() != null) || (!show && this.shadow[num].getTag() == null)) {
                this.shadow[num].setTag(show ? null : 1);
                if (show) {
                    this.shadow[num].setVisibility(0);
                }
                AnimatorSet[] animatorSetArr = this.shadowAnimation;
                if (animatorSetArr[num] != null) {
                    animatorSetArr[num].cancel();
                }
                this.shadowAnimation[num] = new AnimatorSet();
                AnimatorSet animatorSet = this.shadowAnimation[num];
                Animator[] animatorArr = new Animator[1];
                View view = this.shadow[num];
                Property property = View.ALPHA;
                float[] fArr = new float[1];
                fArr[0] = show ? 1.0f : 0.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(view, property, fArr);
                animatorSet.playTogether(animatorArr);
                this.shadowAnimation[num].setDuration(150L);
                this.shadowAnimation[num].addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ThemeEditorView.EditorAlert.4
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        if (EditorAlert.this.shadowAnimation[num] != null && EditorAlert.this.shadowAnimation[num].equals(animation)) {
                            if (!show) {
                                EditorAlert.this.shadow[num].setVisibility(4);
                            }
                            EditorAlert.this.shadowAnimation[num] = null;
                        }
                    }

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationCancel(Animator animation) {
                        if (EditorAlert.this.shadowAnimation[num] != null && EditorAlert.this.shadowAnimation[num].equals(animation)) {
                            EditorAlert.this.shadowAnimation[num] = null;
                        }
                    }
                });
                this.shadowAnimation[num].start();
            }
        }

        @Override // org.telegram.ui.ActionBar.BottomSheet
        public void dismissInternal() {
            super.dismissInternal();
            if (this.searchField.searchEditText.isFocused()) {
                AndroidUtilities.hideKeyboard(this.searchField.searchEditText);
            }
        }

        public void setColorPickerVisible(boolean visible) {
            float f = 0.0f;
            if (!visible) {
                if (ThemeEditorView.this.parentActivity != null) {
                    ((LaunchActivity) ThemeEditorView.this.parentActivity).rebuildAllFragments(false);
                }
                Theme.saveCurrentTheme(ThemeEditorView.this.themeInfo, false, false, false);
                if (this.listView.getAdapter() == this.listAdapter) {
                    AndroidUtilities.hideKeyboard(getCurrentFocus());
                }
                this.animationInProgress = true;
                this.listView.setVisibility(0);
                this.bottomSaveLayout.setVisibility(0);
                this.searchField.setVisibility(0);
                this.listView.setAlpha(0.0f);
                AnimatorSet animatorSet = new AnimatorSet();
                Animator[] animatorArr = new Animator[8];
                animatorArr[0] = ObjectAnimator.ofFloat(this.colorPicker, View.ALPHA, 0.0f);
                animatorArr[1] = ObjectAnimator.ofFloat(this.bottomLayout, View.ALPHA, 0.0f);
                animatorArr[2] = ObjectAnimator.ofFloat(this.listView, View.ALPHA, 1.0f);
                animatorArr[3] = ObjectAnimator.ofFloat(this.frameLayout, View.ALPHA, 1.0f);
                View view = this.shadow[0];
                Property property = View.ALPHA;
                float[] fArr = new float[1];
                if (this.shadow[0].getTag() == null) {
                    f = 1.0f;
                }
                fArr[0] = f;
                animatorArr[4] = ObjectAnimator.ofFloat(view, property, fArr);
                animatorArr[5] = ObjectAnimator.ofFloat(this.searchEmptyView, View.ALPHA, 1.0f);
                animatorArr[6] = ObjectAnimator.ofFloat(this.bottomSaveLayout, View.ALPHA, 1.0f);
                animatorArr[7] = ObjectAnimator.ofInt(this, "scrollOffsetY", this.previousScrollPosition);
                animatorSet.playTogether(animatorArr);
                animatorSet.setDuration(150L);
                animatorSet.setInterpolator(ThemeEditorView.this.decelerateInterpolator);
                animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ThemeEditorView.EditorAlert.6
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        if (EditorAlert.this.listView.getAdapter() == EditorAlert.this.searchAdapter) {
                            EditorAlert.this.searchField.showKeyboard();
                        }
                        EditorAlert.this.colorPicker.setVisibility(8);
                        EditorAlert.this.bottomLayout.setVisibility(8);
                        EditorAlert.this.animationInProgress = false;
                    }
                });
                animatorSet.start();
                this.listView.getAdapter().notifyItemChanged(ThemeEditorView.this.currentThemeDesriptionPosition);
                return;
            }
            this.animationInProgress = true;
            this.colorPicker.setVisibility(0);
            this.bottomLayout.setVisibility(0);
            this.colorPicker.setAlpha(0.0f);
            this.bottomLayout.setAlpha(0.0f);
            this.previousScrollPosition = this.scrollOffsetY;
            AnimatorSet animatorSet2 = new AnimatorSet();
            animatorSet2.playTogether(ObjectAnimator.ofFloat(this.colorPicker, View.ALPHA, 1.0f), ObjectAnimator.ofFloat(this.bottomLayout, View.ALPHA, 1.0f), ObjectAnimator.ofFloat(this.listView, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.frameLayout, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.shadow[0], View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.searchEmptyView, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.bottomSaveLayout, View.ALPHA, 0.0f), ObjectAnimator.ofInt(this, "scrollOffsetY", this.listView.getPaddingTop()));
            animatorSet2.setDuration(150L);
            animatorSet2.setInterpolator(ThemeEditorView.this.decelerateInterpolator);
            animatorSet2.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ThemeEditorView.EditorAlert.5
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    EditorAlert.this.listView.setVisibility(4);
                    EditorAlert.this.searchField.setVisibility(4);
                    EditorAlert.this.bottomSaveLayout.setVisibility(4);
                    EditorAlert.this.animationInProgress = false;
                }
            });
            animatorSet2.start();
        }

        public int getCurrentTop() {
            if (this.listView.getChildCount() != 0) {
                int i = 0;
                View child = this.listView.getChildAt(0);
                RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(child);
                if (holder != null) {
                    int paddingTop = this.listView.getPaddingTop();
                    if (holder.getAdapterPosition() == 0 && child.getTop() >= 0) {
                        i = child.getTop();
                    }
                    return paddingTop - i;
                }
                return -1000;
            }
            return -1000;
        }

        @Override // org.telegram.ui.ActionBar.BottomSheet
        protected boolean canDismissWithSwipe() {
            return false;
        }

        public void updateLayout() {
            int top;
            int newOffset;
            if (this.listView.getChildCount() <= 0 || this.listView.getVisibility() != 0 || this.animationInProgress) {
                return;
            }
            View child = this.listView.getChildAt(0);
            RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(child);
            if (this.listView.getVisibility() != 0 || this.animationInProgress) {
                top = this.listView.getPaddingTop();
            } else {
                top = child.getTop() - AndroidUtilities.dp(8.0f);
            }
            if (top > (-AndroidUtilities.dp(1.0f)) && holder != null && holder.getAdapterPosition() == 0) {
                newOffset = top;
                runShadowAnimation(0, false);
            } else {
                newOffset = 0;
                runShadowAnimation(0, true);
            }
            if (this.scrollOffsetY != newOffset) {
                setScrollOffsetY(newOffset);
            }
        }

        public int getScrollOffsetY() {
            return this.scrollOffsetY;
        }

        public void setScrollOffsetY(int value) {
            RecyclerListView recyclerListView = this.listView;
            this.scrollOffsetY = value;
            recyclerListView.setTopGlowOffset(value);
            this.frameLayout.setTranslationY(this.scrollOffsetY);
            this.colorPicker.setTranslationY(this.scrollOffsetY);
            this.searchEmptyView.setTranslationY(this.scrollOffsetY);
            this.containerView.invalidate();
        }

        /* loaded from: classes5.dex */
        public class SearchAdapter extends RecyclerListView.SelectionAdapter {
            private Context context;
            private int currentCount;
            private int lastSearchId;
            private String lastSearchText;
            private Runnable searchRunnable;
            private ArrayList<ArrayList<ThemeDescription>> searchResult = new ArrayList<>();
            private ArrayList<CharSequence> searchNames = new ArrayList<>();

            public SearchAdapter(Context context) {
                EditorAlert.this = this$1;
                this.context = context;
            }

            public CharSequence generateSearchName(String name, String q) {
                if (TextUtils.isEmpty(name)) {
                    return "";
                }
                SpannableStringBuilder builder = new SpannableStringBuilder();
                String wholeString = name.trim();
                String lower = wholeString.toLowerCase();
                int lastIndex = 0;
                while (true) {
                    int index = lower.indexOf(q, lastIndex);
                    if (index == -1) {
                        break;
                    }
                    int end = q.length() + index;
                    if (lastIndex != 0 && lastIndex != index + 1) {
                        builder.append((CharSequence) wholeString.substring(lastIndex, index));
                    } else if (lastIndex == 0 && index != 0) {
                        builder.append((CharSequence) wholeString.substring(0, index));
                    }
                    String query = wholeString.substring(index, Math.min(wholeString.length(), end));
                    if (query.startsWith(" ")) {
                        builder.append((CharSequence) " ");
                    }
                    String query2 = query.trim();
                    int start = builder.length();
                    builder.append((CharSequence) query2);
                    builder.setSpan(new ForegroundColorSpan(-11697229), start, query2.length() + start, 33);
                    lastIndex = end;
                }
                if (lastIndex != -1 && lastIndex < wholeString.length()) {
                    builder.append((CharSequence) wholeString.substring(lastIndex));
                }
                return builder;
            }

            /* renamed from: searchDialogsInternal */
            public void m3140x282ed99(String query, int searchId) {
                Exception e;
                try {
                    String search1 = query.trim().toLowerCase();
                    if (search1.length() == 0) {
                        this.lastSearchId = -1;
                        updateSearchResults(new ArrayList<>(), new ArrayList<>(), this.lastSearchId);
                        return;
                    }
                    String search2 = LocaleController.getInstance().getTranslitString(search1);
                    if (search1.equals(search2) || search2.length() == 0) {
                        search2 = null;
                    }
                    String[] search = new String[(search2 != null ? 1 : 0) + 1];
                    search[0] = search1;
                    if (search2 != null) {
                        search[1] = search2;
                    }
                    ArrayList<ArrayList<ThemeDescription>> searchResults = new ArrayList<>();
                    ArrayList<CharSequence> names = new ArrayList<>();
                    int N = EditorAlert.this.listAdapter.items.size();
                    for (int a = 0; a < N; a++) {
                        ArrayList<ThemeDescription> themeDescriptions = (ArrayList) EditorAlert.this.listAdapter.items.get(a);
                        String key = themeDescriptions.get(0).getCurrentKey();
                        String name = key.toLowerCase();
                        int length = search.length;
                        int i = 0;
                        while (true) {
                            if (i < length) {
                                String q = search[i];
                                if (!name.contains(q)) {
                                    i++;
                                } else {
                                    searchResults.add(themeDescriptions);
                                    names.add(generateSearchName(key, q));
                                    break;
                                }
                            }
                        }
                    }
                    try {
                        updateSearchResults(searchResults, names, searchId);
                    } catch (Exception e2) {
                        e = e2;
                        FileLog.e(e);
                    }
                } catch (Exception e3) {
                    e = e3;
                }
            }

            private void updateSearchResults(final ArrayList<ArrayList<ThemeDescription>> result, final ArrayList<CharSequence> names, final int searchId) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ThemeEditorView$EditorAlert$SearchAdapter$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        ThemeEditorView.EditorAlert.SearchAdapter.this.m3141x612aaf78(searchId, result, names);
                    }
                });
            }

            /* renamed from: lambda$updateSearchResults$0$org-telegram-ui-Components-ThemeEditorView$EditorAlert$SearchAdapter */
            public /* synthetic */ void m3141x612aaf78(int searchId, ArrayList result, ArrayList names) {
                if (searchId == this.lastSearchId) {
                    if (EditorAlert.this.listView.getAdapter() != EditorAlert.this.searchAdapter) {
                        EditorAlert editorAlert = EditorAlert.this;
                        editorAlert.topBeforeSwitch = editorAlert.getCurrentTop();
                        EditorAlert.this.listView.setAdapter(EditorAlert.this.searchAdapter);
                        EditorAlert.this.searchAdapter.notifyDataSetChanged();
                    }
                    boolean isEmpty = true;
                    boolean becomeEmpty = !this.searchResult.isEmpty() && result.isEmpty();
                    if (!this.searchResult.isEmpty() || !result.isEmpty()) {
                        isEmpty = false;
                    }
                    if (becomeEmpty) {
                        EditorAlert editorAlert2 = EditorAlert.this;
                        editorAlert2.topBeforeSwitch = editorAlert2.getCurrentTop();
                    }
                    this.searchResult = result;
                    this.searchNames = names;
                    notifyDataSetChanged();
                    if (!isEmpty && !becomeEmpty && EditorAlert.this.topBeforeSwitch > 0) {
                        EditorAlert.this.layoutManager.scrollToPositionWithOffset(0, -EditorAlert.this.topBeforeSwitch);
                        EditorAlert.this.topBeforeSwitch = -1000;
                    }
                    EditorAlert.this.searchEmptyView.showTextView();
                }
            }

            public void searchDialogs(final String query) {
                if (query != null && query.equals(this.lastSearchText)) {
                    return;
                }
                this.lastSearchText = query;
                if (this.searchRunnable != null) {
                    Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                    this.searchRunnable = null;
                }
                if (query == null || query.length() == 0) {
                    this.searchResult.clear();
                    EditorAlert editorAlert = EditorAlert.this;
                    editorAlert.topBeforeSwitch = editorAlert.getCurrentTop();
                    this.lastSearchId = -1;
                    notifyDataSetChanged();
                    return;
                }
                final int searchId = this.lastSearchId + 1;
                this.lastSearchId = searchId;
                this.searchRunnable = new Runnable() { // from class: org.telegram.ui.Components.ThemeEditorView$EditorAlert$SearchAdapter$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        ThemeEditorView.EditorAlert.SearchAdapter.this.m3140x282ed99(query, searchId);
                    }
                };
                Utilities.searchQueue.postRunnable(this.searchRunnable, 300L);
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public int getItemCount() {
                if (this.searchResult.isEmpty()) {
                    return 0;
                }
                return this.searchResult.size() + 1;
            }

            public ArrayList<ThemeDescription> getItem(int i) {
                if (i < 0 || i >= this.searchResult.size()) {
                    return null;
                }
                return this.searchResult.get(i);
            }

            @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
            public boolean isEnabled(RecyclerView.ViewHolder holder) {
                return true;
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view;
                switch (viewType) {
                    case 0:
                        view = new TextColorThemeCell(this.context);
                        view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                        break;
                    default:
                        view = new View(this.context);
                        view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(56.0f)));
                        break;
                }
                return new RecyclerListView.Holder(view);
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                int color;
                if (holder.getItemViewType() == 0) {
                    ArrayList<ThemeDescription> arrayList = this.searchResult.get(position - 1);
                    ThemeDescription description = arrayList.get(0);
                    if (description.getCurrentKey().equals(Theme.key_chat_wallpaper)) {
                        color = 0;
                    } else {
                        color = description.getSetColor();
                    }
                    ((TextColorThemeCell) holder.itemView).setTextAndColor(this.searchNames.get(position - 1), color);
                }
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public int getItemViewType(int i) {
                if (i == 0) {
                    return 1;
                }
                return 0;
            }
        }

        /* loaded from: classes5.dex */
        public class ListAdapter extends RecyclerListView.SelectionAdapter {
            private Context context;
            private int currentCount;
            private ArrayList<ArrayList<ThemeDescription>> items = new ArrayList<>();

            public ListAdapter(Context context, ArrayList<ThemeDescription> descriptions) {
                EditorAlert.this = r11;
                this.context = context;
                HashMap<String, ArrayList<ThemeDescription>> itemsMap = new HashMap<>();
                int N = descriptions.size();
                for (int a = 0; a < N; a++) {
                    ThemeDescription description = descriptions.get(a);
                    String key = description.getCurrentKey();
                    ArrayList<ThemeDescription> arrayList = itemsMap.get(key);
                    if (arrayList == null) {
                        arrayList = new ArrayList<>();
                        itemsMap.put(key, arrayList);
                        this.items.add(arrayList);
                    }
                    arrayList.add(description);
                }
                int a2 = Build.VERSION.SDK_INT;
                if (a2 >= 26 && !itemsMap.containsKey(Theme.key_windowBackgroundGray)) {
                    ArrayList<ThemeDescription> arrayList2 = new ArrayList<>();
                    arrayList2.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_windowBackgroundGray));
                    this.items.add(arrayList2);
                }
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public int getItemCount() {
                if (this.items.isEmpty()) {
                    return 0;
                }
                return this.items.size() + 1;
            }

            public ArrayList<ThemeDescription> getItem(int i) {
                if (i < 0 || i >= this.items.size()) {
                    return null;
                }
                return this.items.get(i);
            }

            @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
            public boolean isEnabled(RecyclerView.ViewHolder holder) {
                return true;
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view;
                switch (viewType) {
                    case 0:
                        view = new TextColorThemeCell(this.context);
                        view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                        break;
                    default:
                        view = new View(this.context);
                        view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(56.0f)));
                        break;
                }
                return new RecyclerListView.Holder(view);
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                int color;
                if (holder.getItemViewType() == 0) {
                    ArrayList<ThemeDescription> arrayList = this.items.get(position - 1);
                    ThemeDescription description = arrayList.get(0);
                    if (description.getCurrentKey().equals(Theme.key_chat_wallpaper)) {
                        color = 0;
                    } else {
                        color = description.getSetColor();
                    }
                    ((TextColorThemeCell) holder.itemView).setTextAndColor(description.getTitle(), color);
                }
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public int getItemViewType(int i) {
                if (i == 0) {
                    return 1;
                }
                return 0;
            }
        }
    }

    public void show(Activity activity, Theme.ThemeInfo theme) {
        if (Instance != null) {
            Instance.destroy();
        }
        this.hidden = false;
        this.themeInfo = theme;
        this.windowView = new AnonymousClass1(activity);
        this.windowManager = (WindowManager) activity.getSystemService("window");
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0);
        this.preferences = sharedPreferences;
        int sidex = sharedPreferences.getInt("sidex", 1);
        int sidey = this.preferences.getInt("sidey", 0);
        float px = this.preferences.getFloat("px", 0.0f);
        float py = this.preferences.getFloat("py", 0.0f);
        try {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            this.windowLayoutParams = layoutParams;
            layoutParams.width = this.editorWidth;
            this.windowLayoutParams.height = this.editorHeight;
            this.windowLayoutParams.x = getSideCoord(true, sidex, px, this.editorWidth);
            this.windowLayoutParams.y = getSideCoord(false, sidey, py, this.editorHeight);
            this.windowLayoutParams.format = -3;
            this.windowLayoutParams.gravity = 51;
            this.windowLayoutParams.type = 99;
            this.windowLayoutParams.flags = 16777736;
            this.windowManager.addView(this.windowView, this.windowLayoutParams);
            this.wallpaperUpdater = new WallpaperUpdater(activity, null, new WallpaperUpdater.WallpaperUpdaterDelegate() { // from class: org.telegram.ui.Components.ThemeEditorView.2
                @Override // org.telegram.ui.Components.WallpaperUpdater.WallpaperUpdaterDelegate
                public void didSelectWallpaper(File file, Bitmap bitmap, boolean gallery) {
                    Theme.setThemeWallpaper(ThemeEditorView.this.themeInfo, bitmap, file);
                }

                @Override // org.telegram.ui.Components.WallpaperUpdater.WallpaperUpdaterDelegate
                public void needOpenColorPicker() {
                    for (int a = 0; a < ThemeEditorView.this.currentThemeDesription.size(); a++) {
                        ThemeDescription description = (ThemeDescription) ThemeEditorView.this.currentThemeDesription.get(a);
                        description.startEditing();
                        if (a == 0) {
                            ThemeEditorView.this.editorAlert.colorPicker.setColor(description.getCurrentColor());
                        }
                    }
                    ThemeEditorView.this.editorAlert.setColorPickerVisible(true);
                }
            });
            Instance = this;
            this.parentActivity = activity;
            showWithAnimation();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* renamed from: org.telegram.ui.Components.ThemeEditorView$1 */
    /* loaded from: classes5.dex */
    public class AnonymousClass1 extends FrameLayout {
        private boolean dragging;
        private float startX;
        private float startY;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass1(Context arg0) {
            super(arg0);
            ThemeEditorView.this = this$0;
        }

        @Override // android.view.ViewGroup
        public boolean onInterceptTouchEvent(MotionEvent event) {
            return true;
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent event) {
            BaseFragment fragment;
            ArrayList<ThemeDescription> items;
            float x = event.getRawX();
            float y = event.getRawY();
            if (event.getAction() == 0) {
                this.startX = x;
                this.startY = y;
            } else if (event.getAction() == 2 && !this.dragging) {
                if (Math.abs(this.startX - x) >= AndroidUtilities.getPixelsInCM(0.3f, true) || Math.abs(this.startY - y) >= AndroidUtilities.getPixelsInCM(0.3f, false)) {
                    this.dragging = true;
                    this.startX = x;
                    this.startY = y;
                }
            } else if (event.getAction() == 1 && !this.dragging && ThemeEditorView.this.editorAlert == null) {
                LaunchActivity launchActivity = (LaunchActivity) ThemeEditorView.this.parentActivity;
                ActionBarLayout actionBarLayout = null;
                if (AndroidUtilities.isTablet()) {
                    actionBarLayout = launchActivity.getLayersActionBarLayout();
                    if (actionBarLayout != null && actionBarLayout.fragmentsStack.isEmpty()) {
                        actionBarLayout = null;
                    }
                    if (actionBarLayout == null && (actionBarLayout = launchActivity.getRightActionBarLayout()) != null && actionBarLayout.fragmentsStack.isEmpty()) {
                        actionBarLayout = null;
                    }
                }
                if (actionBarLayout == null) {
                    actionBarLayout = launchActivity.getActionBarLayout();
                }
                if (actionBarLayout != null) {
                    if (!actionBarLayout.fragmentsStack.isEmpty()) {
                        fragment = actionBarLayout.fragmentsStack.get(actionBarLayout.fragmentsStack.size() - 1);
                    } else {
                        fragment = null;
                    }
                    if (fragment != null && (items = fragment.getThemeDescriptions()) != null) {
                        ThemeEditorView themeEditorView = ThemeEditorView.this;
                        ThemeEditorView themeEditorView2 = ThemeEditorView.this;
                        themeEditorView.editorAlert = new EditorAlert(themeEditorView2.parentActivity, items);
                        ThemeEditorView.this.editorAlert.setOnDismissListener(ThemeEditorView$1$$ExternalSyntheticLambda1.INSTANCE);
                        ThemeEditorView.this.editorAlert.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.Components.ThemeEditorView$1$$ExternalSyntheticLambda0
                            @Override // android.content.DialogInterface.OnDismissListener
                            public final void onDismiss(DialogInterface dialogInterface) {
                                ThemeEditorView.AnonymousClass1.this.m3133x37c74607(dialogInterface);
                            }
                        });
                        ThemeEditorView.this.editorAlert.show();
                        ThemeEditorView.this.hide();
                    }
                }
            }
            if (this.dragging) {
                if (event.getAction() == 2) {
                    float dx = x - this.startX;
                    float dy = y - this.startY;
                    WindowManager.LayoutParams layoutParams = ThemeEditorView.this.windowLayoutParams;
                    layoutParams.x = (int) (layoutParams.x + dx);
                    WindowManager.LayoutParams layoutParams2 = ThemeEditorView.this.windowLayoutParams;
                    layoutParams2.y = (int) (layoutParams2.y + dy);
                    int maxDiff = ThemeEditorView.this.editorWidth / 2;
                    if (ThemeEditorView.this.windowLayoutParams.x < (-maxDiff)) {
                        ThemeEditorView.this.windowLayoutParams.x = -maxDiff;
                    } else if (ThemeEditorView.this.windowLayoutParams.x > (AndroidUtilities.displaySize.x - ThemeEditorView.this.windowLayoutParams.width) + maxDiff) {
                        ThemeEditorView.this.windowLayoutParams.x = (AndroidUtilities.displaySize.x - ThemeEditorView.this.windowLayoutParams.width) + maxDiff;
                    }
                    float alpha = 1.0f;
                    if (ThemeEditorView.this.windowLayoutParams.x < 0) {
                        alpha = ((ThemeEditorView.this.windowLayoutParams.x / maxDiff) * 0.5f) + 1.0f;
                    } else if (ThemeEditorView.this.windowLayoutParams.x > AndroidUtilities.displaySize.x - ThemeEditorView.this.windowLayoutParams.width) {
                        alpha = 1.0f - ((((ThemeEditorView.this.windowLayoutParams.x - AndroidUtilities.displaySize.x) + ThemeEditorView.this.windowLayoutParams.width) / maxDiff) * 0.5f);
                    }
                    if (ThemeEditorView.this.windowView.getAlpha() != alpha) {
                        ThemeEditorView.this.windowView.setAlpha(alpha);
                    }
                    if (ThemeEditorView.this.windowLayoutParams.y < (-0)) {
                        ThemeEditorView.this.windowLayoutParams.y = -0;
                    } else if (ThemeEditorView.this.windowLayoutParams.y > (AndroidUtilities.displaySize.y - ThemeEditorView.this.windowLayoutParams.height) + 0) {
                        ThemeEditorView.this.windowLayoutParams.y = (AndroidUtilities.displaySize.y - ThemeEditorView.this.windowLayoutParams.height) + 0;
                    }
                    ThemeEditorView.this.windowManager.updateViewLayout(ThemeEditorView.this.windowView, ThemeEditorView.this.windowLayoutParams);
                    this.startX = x;
                    this.startY = y;
                } else if (event.getAction() == 1) {
                    this.dragging = false;
                    ThemeEditorView.this.animateToBoundsMaybe();
                }
            }
            return true;
        }

        public static /* synthetic */ void lambda$onTouchEvent$0(DialogInterface dialog) {
        }

        /* renamed from: lambda$onTouchEvent$1$org-telegram-ui-Components-ThemeEditorView$1 */
        public /* synthetic */ void m3133x37c74607(DialogInterface dialog) {
            ThemeEditorView.this.editorAlert = null;
            ThemeEditorView.this.show();
        }
    }

    private void showWithAnimation() {
        this.windowView.setBackgroundResource(R.drawable.theme_picker);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(ObjectAnimator.ofFloat(this.windowView, View.ALPHA, 0.0f, 1.0f), ObjectAnimator.ofFloat(this.windowView, View.SCALE_X, 0.0f, 1.0f), ObjectAnimator.ofFloat(this.windowView, View.SCALE_Y, 0.0f, 1.0f));
        animatorSet.setInterpolator(this.decelerateInterpolator);
        animatorSet.setDuration(150L);
        animatorSet.start();
    }

    private static int getSideCoord(boolean isX, int side, float p, int sideSize) {
        int total;
        int result;
        if (isX) {
            total = AndroidUtilities.displaySize.x - sideSize;
        } else {
            total = (AndroidUtilities.displaySize.y - sideSize) - ActionBar.getCurrentActionBarHeight();
        }
        if (side == 0) {
            result = AndroidUtilities.dp(10.0f);
        } else if (side == 1) {
            result = total - AndroidUtilities.dp(10.0f);
        } else {
            result = AndroidUtilities.dp(10.0f) + Math.round((total - AndroidUtilities.dp(20.0f)) * p);
        }
        if (!isX) {
            return result + ActionBar.getCurrentActionBarHeight();
        }
        return result;
    }

    public void hide() {
        if (this.parentActivity == null) {
            return;
        }
        try {
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(ObjectAnimator.ofFloat(this.windowView, View.ALPHA, 1.0f, 0.0f), ObjectAnimator.ofFloat(this.windowView, View.SCALE_X, 1.0f, 0.0f), ObjectAnimator.ofFloat(this.windowView, View.SCALE_Y, 1.0f, 0.0f));
            animatorSet.setInterpolator(this.decelerateInterpolator);
            animatorSet.setDuration(150L);
            animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ThemeEditorView.3
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    if (ThemeEditorView.this.windowView != null) {
                        ThemeEditorView.this.windowView.setBackground(null);
                        ThemeEditorView.this.windowManager.removeView(ThemeEditorView.this.windowView);
                    }
                }
            });
            animatorSet.start();
            this.hidden = true;
        } catch (Exception e) {
        }
    }

    public void show() {
        if (this.parentActivity == null) {
            return;
        }
        try {
            this.windowManager.addView(this.windowView, this.windowLayoutParams);
            this.hidden = false;
            showWithAnimation();
        } catch (Exception e) {
        }
    }

    public void close() {
        try {
            this.windowManager.removeView(this.windowView);
        } catch (Exception e) {
        }
        this.parentActivity = null;
    }

    public void onConfigurationChanged() {
        int sidex = this.preferences.getInt("sidex", 1);
        int sidey = this.preferences.getInt("sidey", 0);
        float px = this.preferences.getFloat("px", 0.0f);
        float py = this.preferences.getFloat("py", 0.0f);
        this.windowLayoutParams.x = getSideCoord(true, sidex, px, this.editorWidth);
        this.windowLayoutParams.y = getSideCoord(false, sidey, py, this.editorHeight);
        try {
            if (this.windowView.getParent() != null) {
                this.windowManager.updateViewLayout(this.windowView, this.windowLayoutParams);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        WallpaperUpdater wallpaperUpdater = this.wallpaperUpdater;
        if (wallpaperUpdater != null) {
            wallpaperUpdater.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void animateToBoundsMaybe() {
        int startX = getSideCoord(true, 0, 0.0f, this.editorWidth);
        int endX = getSideCoord(true, 1, 0.0f, this.editorWidth);
        int startY = getSideCoord(false, 0, 0.0f, this.editorHeight);
        int endY = getSideCoord(false, 1, 0.0f, this.editorHeight);
        ArrayList<Animator> animators = null;
        SharedPreferences.Editor editor = this.preferences.edit();
        int maxDiff = AndroidUtilities.dp(20.0f);
        boolean slideOut = false;
        if (Math.abs(startX - this.windowLayoutParams.x) <= maxDiff || (this.windowLayoutParams.x < 0 && this.windowLayoutParams.x > (-this.editorWidth) / 4)) {
            if (0 == 0) {
                animators = new ArrayList<>();
            }
            editor.putInt("sidex", 0);
            if (this.windowView.getAlpha() != 1.0f) {
                animators.add(ObjectAnimator.ofFloat(this.windowView, View.ALPHA, 1.0f));
            }
            animators.add(ObjectAnimator.ofInt(this, "x", startX));
        } else if (Math.abs(endX - this.windowLayoutParams.x) <= maxDiff || (this.windowLayoutParams.x > AndroidUtilities.displaySize.x - this.editorWidth && this.windowLayoutParams.x < AndroidUtilities.displaySize.x - ((this.editorWidth / 4) * 3))) {
            if (0 == 0) {
                animators = new ArrayList<>();
            }
            editor.putInt("sidex", 1);
            if (this.windowView.getAlpha() != 1.0f) {
                animators.add(ObjectAnimator.ofFloat(this.windowView, View.ALPHA, 1.0f));
            }
            animators.add(ObjectAnimator.ofInt(this, "x", endX));
        } else if (this.windowView.getAlpha() != 1.0f) {
            if (0 == 0) {
                animators = new ArrayList<>();
            }
            if (this.windowLayoutParams.x < 0) {
                animators.add(ObjectAnimator.ofInt(this, "x", -this.editorWidth));
            } else {
                animators.add(ObjectAnimator.ofInt(this, "x", AndroidUtilities.displaySize.x));
            }
            slideOut = true;
        } else {
            editor.putFloat("px", (this.windowLayoutParams.x - startX) / (endX - startX));
            editor.putInt("sidex", 2);
        }
        if (!slideOut) {
            if (Math.abs(startY - this.windowLayoutParams.y) <= maxDiff || this.windowLayoutParams.y <= ActionBar.getCurrentActionBarHeight()) {
                if (animators == null) {
                    animators = new ArrayList<>();
                }
                editor.putInt("sidey", 0);
                animators.add(ObjectAnimator.ofInt(this, "y", startY));
            } else if (Math.abs(endY - this.windowLayoutParams.y) <= maxDiff) {
                if (animators == null) {
                    animators = new ArrayList<>();
                }
                editor.putInt("sidey", 1);
                animators.add(ObjectAnimator.ofInt(this, "y", endY));
            } else {
                editor.putFloat("py", (this.windowLayoutParams.y - startY) / (endY - startY));
                editor.putInt("sidey", 2);
            }
            editor.commit();
        }
        if (animators != null) {
            if (this.decelerateInterpolator == null) {
                this.decelerateInterpolator = new DecelerateInterpolator();
            }
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setInterpolator(this.decelerateInterpolator);
            animatorSet.setDuration(150L);
            if (slideOut) {
                animators.add(ObjectAnimator.ofFloat(this.windowView, View.ALPHA, 0.0f));
                animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ThemeEditorView.4
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        Theme.saveCurrentTheme(ThemeEditorView.this.themeInfo, true, false, false);
                        ThemeEditorView.this.destroy();
                    }
                });
            }
            animatorSet.playTogether(animators);
            animatorSet.start();
        }
    }

    public int getX() {
        return this.windowLayoutParams.x;
    }

    public int getY() {
        return this.windowLayoutParams.y;
    }

    public void setX(int value) {
        this.windowLayoutParams.x = value;
        this.windowManager.updateViewLayout(this.windowView, this.windowLayoutParams);
    }

    public void setY(int value) {
        this.windowLayoutParams.y = value;
        this.windowManager.updateViewLayout(this.windowView, this.windowLayoutParams);
    }
}
