package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Magnifier;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.zxing.common.detector.MathUtils;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LanguageDetector;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.FloatingActionMode;
import org.telegram.ui.ActionBar.FloatingToolbar;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ArticleViewer;
import org.telegram.ui.Cells.TextSelectionHelper;
import org.telegram.ui.Cells.TextSelectionHelper.SelectableView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.RestrictedLanguagesSelectActivity;
/* loaded from: classes3.dex */
public abstract class TextSelectionHelper<Cell extends SelectableView> {
    private ActionMode actionMode;
    protected boolean actionsIsShowing;
    private Callback callback;
    protected int capturedX;
    protected int capturedY;
    protected float cornerRadius;
    private TextView deleteView;
    protected float enterProgress;
    private ValueAnimator handleViewAnimator;
    protected float handleViewProgress;
    private boolean isOneTouch;
    int keyboardSize;
    private int lastX;
    private int lastY;
    private Magnifier magnifier;
    private float magnifierDx;
    private float magnifierDy;
    private float magnifierX;
    private float magnifierXanimated;
    private float magnifierY;
    private float magnifierYanimated;
    protected Cell maybeSelectedView;
    protected int maybeTextX;
    protected int maybeTextY;
    protected boolean movingDirectionSettling;
    private boolean movingHandle;
    protected boolean movingHandleStart;
    float movingOffsetX;
    float movingOffsetY;
    protected boolean multiselect;
    private boolean parentIsScrolling;
    protected RecyclerListView parentRecyclerView;
    protected ViewGroup parentView;
    private ActionBarPopupWindow.ActionBarPopupWindowLayout popupLayout;
    private Rect popupRect;
    private ActionBarPopupWindow popupWindow;
    private boolean scrollDown;
    private boolean scrolling;
    protected int selectedCellId;
    protected Cell selectedView;
    private boolean snap;
    protected TextSelectionHelper<Cell>.TextSelectionOverlay textSelectionOverlay;
    protected int textX;
    protected int textY;
    private int topOffset;
    private boolean tryCapture;
    protected int[] tmpCoord = new int[2];
    protected Paint selectionPaint = new Paint(1);
    protected Paint selectionHandlePaint = new Paint(1);
    protected Path selectionPath = new Path();
    protected Path selectionHandlePath = new Path();
    protected int selectionStart = -1;
    protected int selectionEnd = -1;
    private final ActionMode.Callback textSelectActionCallback = createActionCallback();
    protected final Rect textArea = new Rect();
    private RectF startArea = new RectF();
    private RectF endArea = new RectF();
    protected final LayoutBlock layoutBlock = new LayoutBlock();
    private Interpolator interpolator = new OvershootInterpolator();
    protected boolean showActionsAsPopupAlways = false;
    private Runnable scrollRunnable = new Runnable() { // from class: org.telegram.ui.Cells.TextSelectionHelper.1
        @Override // java.lang.Runnable
        public void run() {
            int i;
            int i2;
            int i3;
            if (TextSelectionHelper.this.scrolling) {
                TextSelectionHelper textSelectionHelper = TextSelectionHelper.this;
                if (textSelectionHelper.parentRecyclerView == null) {
                    return;
                }
                if (textSelectionHelper.multiselect && textSelectionHelper.selectedView == null) {
                    i = AndroidUtilities.dp(8.0f);
                } else if (textSelectionHelper.selectedView == null) {
                    return;
                } else {
                    i = textSelectionHelper.getLineHeight() >> 1;
                }
                TextSelectionHelper textSelectionHelper2 = TextSelectionHelper.this;
                if (!textSelectionHelper2.multiselect) {
                    if (textSelectionHelper2.scrollDown) {
                        if (TextSelectionHelper.this.selectedView.getBottom() - i < TextSelectionHelper.this.parentView.getMeasuredHeight() - TextSelectionHelper.this.getParentBottomPadding()) {
                            i3 = TextSelectionHelper.this.selectedView.getBottom() - TextSelectionHelper.this.parentView.getMeasuredHeight();
                            i2 = TextSelectionHelper.this.getParentBottomPadding();
                            i = i3 + i2;
                        }
                    } else if (TextSelectionHelper.this.selectedView.getTop() + i > TextSelectionHelper.this.getParentTopPadding()) {
                        i3 = -TextSelectionHelper.this.selectedView.getTop();
                        i2 = TextSelectionHelper.this.getParentTopPadding();
                        i = i3 + i2;
                    }
                }
                TextSelectionHelper textSelectionHelper3 = TextSelectionHelper.this;
                RecyclerListView recyclerListView = textSelectionHelper3.parentRecyclerView;
                if (!textSelectionHelper3.scrollDown) {
                    i = -i;
                }
                recyclerListView.scrollBy(0, i);
                AndroidUtilities.runOnUIThread(this);
            }
        }
    };
    final Runnable startSelectionRunnable = new Runnable() { // from class: org.telegram.ui.Cells.TextSelectionHelper.2
        @Override // java.lang.Runnable
        public void run() {
            TextSelectionHelper textSelectionHelper = TextSelectionHelper.this;
            Cell cell = textSelectionHelper.maybeSelectedView;
            if (cell == null || textSelectionHelper.textSelectionOverlay == null) {
                return;
            }
            Cell cell2 = textSelectionHelper.selectedView;
            CharSequence text = textSelectionHelper.getText(cell, true);
            RecyclerListView recyclerListView = TextSelectionHelper.this.parentRecyclerView;
            if (recyclerListView != null) {
                recyclerListView.cancelClickRunnables(false);
            }
            TextSelectionHelper textSelectionHelper2 = TextSelectionHelper.this;
            int i = textSelectionHelper2.capturedX;
            int i2 = textSelectionHelper2.capturedY;
            if (!textSelectionHelper2.textArea.isEmpty()) {
                Rect rect = TextSelectionHelper.this.textArea;
                int i3 = rect.right;
                if (i > i3) {
                    i = i3 - 1;
                }
                int i4 = rect.left;
                if (i < i4) {
                    i = i4 + 1;
                }
                int i5 = rect.top;
                if (i2 < i5) {
                    i2 = i5 + 1;
                }
                int i6 = rect.bottom;
                if (i2 > i6) {
                    i2 = i6 - 1;
                }
            }
            int i7 = i;
            TextSelectionHelper textSelectionHelper3 = TextSelectionHelper.this;
            int charOffsetFromCord = textSelectionHelper3.getCharOffsetFromCord(i7, i2, textSelectionHelper3.maybeTextX, textSelectionHelper3.maybeTextY, cell, true);
            if (charOffsetFromCord >= text.length()) {
                TextSelectionHelper textSelectionHelper4 = TextSelectionHelper.this;
                textSelectionHelper4.fillLayoutForOffset(charOffsetFromCord, textSelectionHelper4.layoutBlock, true);
                TextSelectionHelper textSelectionHelper5 = TextSelectionHelper.this;
                StaticLayout staticLayout = textSelectionHelper5.layoutBlock.layout;
                if (staticLayout == null) {
                    textSelectionHelper5.selectionEnd = -1;
                    textSelectionHelper5.selectionStart = -1;
                    return;
                }
                int lineCount = staticLayout.getLineCount() - 1;
                TextSelectionHelper textSelectionHelper6 = TextSelectionHelper.this;
                float f = i7 - textSelectionHelper6.maybeTextX;
                if (f < textSelectionHelper6.layoutBlock.layout.getLineRight(lineCount) + AndroidUtilities.dp(4.0f) && f > TextSelectionHelper.this.layoutBlock.layout.getLineLeft(lineCount)) {
                    charOffsetFromCord = text.length() - 1;
                }
            }
            if (charOffsetFromCord >= 0 && charOffsetFromCord < text.length() && text.charAt(charOffsetFromCord) != '\n') {
                TextSelectionHelper textSelectionHelper7 = TextSelectionHelper.this;
                int i8 = textSelectionHelper7.maybeTextX;
                int i9 = textSelectionHelper7.maybeTextY;
                textSelectionHelper7.clear();
                TextSelectionHelper.this.textSelectionOverlay.setVisibility(0);
                TextSelectionHelper.this.onTextSelected(cell, cell2);
                TextSelectionHelper textSelectionHelper8 = TextSelectionHelper.this;
                textSelectionHelper8.selectionStart = charOffsetFromCord;
                textSelectionHelper8.selectionEnd = charOffsetFromCord;
                if (text instanceof Spanned) {
                    Spanned spanned = (Spanned) text;
                    Emoji.EmojiSpan[] emojiSpanArr = (Emoji.EmojiSpan[]) spanned.getSpans(0, text.length(), Emoji.EmojiSpan.class);
                    int length = emojiSpanArr.length;
                    int i10 = 0;
                    while (true) {
                        if (i10 >= length) {
                            break;
                        }
                        Emoji.EmojiSpan emojiSpan = emojiSpanArr[i10];
                        int spanStart = spanned.getSpanStart(emojiSpan);
                        int spanEnd = spanned.getSpanEnd(emojiSpan);
                        if (charOffsetFromCord >= spanStart && charOffsetFromCord <= spanEnd) {
                            TextSelectionHelper textSelectionHelper9 = TextSelectionHelper.this;
                            textSelectionHelper9.selectionStart = spanStart;
                            textSelectionHelper9.selectionEnd = spanEnd;
                            break;
                        }
                        i10++;
                    }
                }
                TextSelectionHelper textSelectionHelper10 = TextSelectionHelper.this;
                if (textSelectionHelper10.selectionStart == textSelectionHelper10.selectionEnd) {
                    while (true) {
                        int i11 = TextSelectionHelper.this.selectionStart;
                        if (i11 <= 0 || !TextSelectionHelper.isInterruptedCharacter(text.charAt(i11 - 1))) {
                            break;
                        }
                        TextSelectionHelper.this.selectionStart--;
                    }
                    while (TextSelectionHelper.this.selectionEnd < text.length() && TextSelectionHelper.isInterruptedCharacter(text.charAt(TextSelectionHelper.this.selectionEnd))) {
                        TextSelectionHelper.this.selectionEnd++;
                    }
                }
                TextSelectionHelper textSelectionHelper11 = TextSelectionHelper.this;
                textSelectionHelper11.textX = i8;
                textSelectionHelper11.textY = i9;
                textSelectionHelper11.selectedView = cell;
                textSelectionHelper11.textSelectionOverlay.performHapticFeedback(0, 1);
                TextSelectionHelper.this.showActions();
                TextSelectionHelper.this.invalidate();
                if (cell2 != null) {
                    cell2.invalidate();
                }
                if (TextSelectionHelper.this.callback != null) {
                    TextSelectionHelper.this.callback.onStateChanged(true);
                }
                TextSelectionHelper.this.movingHandle = true;
                TextSelectionHelper textSelectionHelper12 = TextSelectionHelper.this;
                textSelectionHelper12.movingDirectionSettling = true;
                textSelectionHelper12.isOneTouch = true;
                TextSelectionHelper textSelectionHelper13 = TextSelectionHelper.this;
                textSelectionHelper13.movingOffsetY = 0.0f;
                textSelectionHelper13.movingOffsetX = 0.0f;
                textSelectionHelper13.onOffsetChanged();
            }
            TextSelectionHelper.this.tryCapture = false;
        }
    };
    private OnTranslateListener onTranslateListener = null;
    private final Runnable hideActionsRunnable = new Runnable() { // from class: org.telegram.ui.Cells.TextSelectionHelper.3
        @Override // java.lang.Runnable
        public void run() {
            if (Build.VERSION.SDK_INT < 23 || TextSelectionHelper.this.actionMode == null) {
                return;
            }
            TextSelectionHelper textSelectionHelper = TextSelectionHelper.this;
            if (textSelectionHelper.actionsIsShowing) {
                return;
            }
            textSelectionHelper.actionMode.hide(Long.MAX_VALUE);
            AndroidUtilities.runOnUIThread(TextSelectionHelper.this.hideActionsRunnable, 1000L);
        }
    };
    private final ScalablePath tempPath2 = new ScalablePath();
    private int longpressDelay = ViewConfiguration.getLongPressTimeout();
    private int touchSlop = ViewConfiguration.get(ApplicationLoader.applicationContext).getScaledTouchSlop();

    /* loaded from: classes3.dex */
    public interface ArticleSelectableView extends SelectableView {
        void fillTextLayoutBlocks(ArrayList<TextLayoutBlock> arrayList);
    }

    /* loaded from: classes3.dex */
    public static class Callback {
        public void onStateChanged(boolean z) {
            throw null;
        }

        public void onTextCopied() {
        }
    }

    /* loaded from: classes3.dex */
    public static class IgnoreCopySpannable {
    }

    /* loaded from: classes3.dex */
    public interface OnTranslateListener {
        void run(CharSequence charSequence, String str, String str2, Runnable runnable);
    }

    /* loaded from: classes3.dex */
    public interface SelectableView {
        int getBottom();

        int getMeasuredWidth();

        int getTop();

        float getX();

        float getY();

        void invalidate();
    }

    /* loaded from: classes3.dex */
    public interface TextLayoutBlock {
        StaticLayout getLayout();

        CharSequence getPrefix();

        int getRow();

        int getX();

        int getY();
    }

    protected abstract void fillLayoutForOffset(int i, LayoutBlock layoutBlock, boolean z);

    protected abstract int getCharOffsetFromCord(int i, int i2, int i3, int i4, Cell cell, boolean z);

    protected abstract int getLineHeight();

    public int getParentBottomPadding() {
        return 0;
    }

    public int getParentTopPadding() {
        return 0;
    }

    protected Theme.ResourcesProvider getResourcesProvider() {
        return null;
    }

    protected abstract CharSequence getText(Cell cell, boolean z);

    protected void onExitSelectionMode(boolean z) {
    }

    protected void onOffsetChanged() {
    }

    protected abstract void onTextSelected(Cell cell, Cell cell2);

    protected void pickEndView() {
    }

    protected void pickStartView() {
    }

    protected boolean selectLayout(int i, int i2) {
        return false;
    }

    public TextSelectionHelper() {
        new PathWithSavedBottom();
        new PathCopyTo(this.selectionPath);
        Paint paint = this.selectionPaint;
        float dp = AndroidUtilities.dp(6.0f);
        this.cornerRadius = dp;
        paint.setPathEffect(new CornerPathEffect(dp));
    }

    public void setOnTranslate(OnTranslateListener onTranslateListener) {
        this.onTranslateListener = onTranslateListener;
    }

    public void setParentView(ViewGroup viewGroup) {
        if (viewGroup instanceof RecyclerListView) {
            this.parentRecyclerView = (RecyclerListView) viewGroup;
        }
        this.parentView = viewGroup;
    }

    public void setMaybeTextCord(int i, int i2) {
        this.maybeTextX = i;
        this.maybeTextY = i2;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action == 0) {
            this.capturedX = (int) motionEvent.getX();
            this.capturedY = (int) motionEvent.getY();
            this.tryCapture = false;
            this.textArea.inset(-AndroidUtilities.dp(8.0f), -AndroidUtilities.dp(8.0f));
            if (this.textArea.contains(this.capturedX, this.capturedY)) {
                this.textArea.inset(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
                int i = this.capturedX;
                int i2 = this.capturedY;
                Rect rect = this.textArea;
                int i3 = rect.right;
                if (i > i3) {
                    i = i3 - 1;
                }
                int i4 = rect.left;
                if (i < i4) {
                    i = i4 + 1;
                }
                int i5 = rect.top;
                if (i2 < i5) {
                    i2 = i5 + 1;
                }
                int i6 = rect.bottom;
                int charOffsetFromCord = getCharOffsetFromCord(i, i2 > i6 ? i6 - 1 : i2, this.maybeTextX, this.maybeTextY, this.maybeSelectedView, true);
                CharSequence text = getText(this.maybeSelectedView, true);
                if (charOffsetFromCord >= text.length()) {
                    fillLayoutForOffset(charOffsetFromCord, this.layoutBlock, true);
                    StaticLayout staticLayout = this.layoutBlock.layout;
                    if (staticLayout == null) {
                        this.tryCapture = false;
                        return false;
                    }
                    int lineCount = staticLayout.getLineCount() - 1;
                    float f = i - this.maybeTextX;
                    if (f < this.layoutBlock.layout.getLineRight(lineCount) + AndroidUtilities.dp(4.0f) && f > this.layoutBlock.layout.getLineLeft(lineCount)) {
                        charOffsetFromCord = text.length() - 1;
                    }
                }
                if (charOffsetFromCord >= 0 && charOffsetFromCord < text.length() && text.charAt(charOffsetFromCord) != '\n') {
                    AndroidUtilities.runOnUIThread(this.startSelectionRunnable, this.longpressDelay);
                    this.tryCapture = true;
                }
            }
            return this.tryCapture;
        }
        if (action != 1) {
            if (action == 2) {
                int y = (int) motionEvent.getY();
                int x = (int) motionEvent.getX();
                int i7 = this.capturedY;
                int i8 = this.capturedX;
                if (((i7 - y) * (i7 - y)) + ((i8 - x) * (i8 - x)) > this.touchSlop) {
                    AndroidUtilities.cancelRunOnUIThread(this.startSelectionRunnable);
                    this.tryCapture = false;
                }
                return this.tryCapture;
            } else if (action != 3) {
                return false;
            }
        }
        AndroidUtilities.cancelRunOnUIThread(this.startSelectionRunnable);
        this.tryCapture = false;
        return false;
    }

    public void hideMagnifier() {
        Magnifier magnifier;
        if (Build.VERSION.SDK_INT < 28 || (magnifier = this.magnifier) == null) {
            return;
        }
        magnifier.dismiss();
        this.magnifier = null;
    }

    public void showMagnifier(int i) {
        int i2;
        int i3;
        if (Build.VERSION.SDK_INT < 28 || this.selectedView == null || this.isOneTouch || !this.movingHandle || this.textSelectionOverlay == null) {
            return;
        }
        int i4 = this.movingHandleStart ? this.selectionStart : this.selectionEnd;
        fillLayoutForOffset(i4, this.layoutBlock);
        StaticLayout staticLayout = this.layoutBlock.layout;
        if (staticLayout == null) {
            return;
        }
        int lineForOffset = staticLayout.getLineForOffset(i4);
        int lineBottom = staticLayout.getLineBottom(lineForOffset) - staticLayout.getLineTop(lineForOffset);
        int lineTop = (int) (((((int) ((staticLayout.getLineTop(lineForOffset) + this.textY) + this.selectedView.getY())) - lineBottom) - AndroidUtilities.dp(8.0f)) + this.layoutBlock.yOffset);
        Cell cell = this.selectedView;
        if (cell instanceof ArticleViewer.BlockTableCell) {
            i3 = (int) cell.getX();
            i2 = ((int) this.selectedView.getX()) + this.selectedView.getMeasuredWidth();
        } else {
            i2 = (int) (this.selectedView.getX() + this.textX + staticLayout.getLineRight(lineForOffset));
            i3 = (int) (cell.getX() + this.textX + staticLayout.getLineLeft(lineForOffset));
        }
        if (i < i3) {
            i = i3;
        } else if (i > i2) {
            i = i2;
        }
        float f = lineTop;
        if (this.magnifierY != f) {
            this.magnifierY = f;
            this.magnifierDy = (f - this.magnifierYanimated) / 200.0f;
        }
        float f2 = i;
        if (this.magnifierX != f2) {
            this.magnifierX = f2;
            this.magnifierDx = (f2 - this.magnifierXanimated) / 100.0f;
        }
        if (this.magnifier == null) {
            this.magnifier = new Magnifier(this.textSelectionOverlay);
            this.magnifierYanimated = this.magnifierY;
            this.magnifierXanimated = this.magnifierX;
        }
        float f3 = this.magnifierYanimated;
        float f4 = this.magnifierY;
        if (f3 != f4) {
            this.magnifierYanimated = f3 + (this.magnifierDy * 16.0f);
        }
        float f5 = this.magnifierDy;
        if (f5 > 0.0f && this.magnifierYanimated > f4) {
            this.magnifierYanimated = f4;
        } else if (f5 < 0.0f && this.magnifierYanimated < f4) {
            this.magnifierYanimated = f4;
        }
        float f6 = this.magnifierXanimated;
        float f7 = this.magnifierX;
        if (f6 != f7) {
            this.magnifierXanimated = f6 + (this.magnifierDx * 16.0f);
        }
        float f8 = this.magnifierDx;
        if (f8 > 0.0f && this.magnifierXanimated > f7) {
            this.magnifierXanimated = f7;
        } else if (f8 < 0.0f && this.magnifierXanimated < f7) {
            this.magnifierXanimated = f7;
        }
        this.magnifier.show(this.magnifierXanimated, this.magnifierYanimated + (lineBottom * 1.5f) + AndroidUtilities.dp(8.0f));
        this.magnifier.update();
    }

    protected void showHandleViews() {
        if (this.handleViewProgress == 1.0f || this.textSelectionOverlay == null) {
            return;
        }
        ValueAnimator valueAnimator = this.handleViewAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        ValueAnimator ofFloat = ValueAnimator.ofFloat(this.handleViewProgress, 1.0f);
        this.handleViewAnimator = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Cells.TextSelectionHelper$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                TextSelectionHelper.this.lambda$showHandleViews$0(valueAnimator2);
            }
        });
        this.handleViewAnimator.setDuration(Math.abs(1.0f - this.handleViewProgress) * 250.0f);
        this.handleViewAnimator.start();
    }

    public /* synthetic */ void lambda$showHandleViews$0(ValueAnimator valueAnimator) {
        this.handleViewProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.textSelectionOverlay.invalidate();
    }

    public boolean isSelectionMode() {
        return this.selectionStart >= 0 && this.selectionEnd >= 0;
    }

    public void showActions() {
        int i;
        if (this.textSelectionOverlay == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= 23) {
            if (this.movingHandle || !isSelectionMode() || !canShowActions()) {
                return;
            }
            if (!this.actionsIsShowing) {
                if (this.actionMode == null) {
                    FloatingActionMode floatingActionMode = new FloatingActionMode(this.textSelectionOverlay.getContext(), (ActionMode.Callback2) this.textSelectActionCallback, this.textSelectionOverlay, new FloatingToolbar(this.textSelectionOverlay.getContext(), this.textSelectionOverlay, 1, getResourcesProvider()));
                    this.actionMode = floatingActionMode;
                    this.textSelectActionCallback.onCreateActionMode(floatingActionMode, floatingActionMode.getMenu());
                }
                ActionMode.Callback callback = this.textSelectActionCallback;
                ActionMode actionMode = this.actionMode;
                callback.onPrepareActionMode(actionMode, actionMode.getMenu());
                this.actionMode.hide(1L);
            }
            AndroidUtilities.cancelRunOnUIThread(this.hideActionsRunnable);
            this.actionsIsShowing = true;
        } else if (!this.showActionsAsPopupAlways) {
            if (this.actionMode != null || !isSelectionMode()) {
                return;
            }
            this.actionMode = this.textSelectionOverlay.startActionMode(this.textSelectActionCallback);
        } else if (this.movingHandle || !isSelectionMode() || !canShowActions()) {
        } else {
            if (this.popupLayout == null) {
                this.popupRect = new Rect();
                ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(this.textSelectionOverlay.getContext());
                this.popupLayout = actionBarPopupWindowLayout;
                actionBarPopupWindowLayout.setPadding(AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f));
                this.popupLayout.setBackgroundDrawable(this.textSelectionOverlay.getContext().getResources().getDrawable(R.drawable.menu_copy));
                this.popupLayout.setAnimationEnabled(false);
                this.popupLayout.setOnTouchListener(new View.OnTouchListener() { // from class: org.telegram.ui.Cells.TextSelectionHelper$$ExternalSyntheticLambda2
                    @Override // android.view.View.OnTouchListener
                    public final boolean onTouch(View view, MotionEvent motionEvent) {
                        boolean lambda$showActions$1;
                        lambda$showActions$1 = TextSelectionHelper.this.lambda$showActions$1(view, motionEvent);
                        return lambda$showActions$1;
                    }
                });
                this.popupLayout.setShownFromBottom(false);
                TextView textView = new TextView(this.textSelectionOverlay.getContext());
                this.deleteView = textView;
                textView.setBackgroundDrawable(Theme.createSelectorDrawable(getThemedColor("listSelectorSDK21"), 2));
                this.deleteView.setGravity(16);
                this.deleteView.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
                this.deleteView.setTextSize(1, 15.0f);
                this.deleteView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
                this.deleteView.setText(this.textSelectionOverlay.getContext().getString(17039361));
                this.deleteView.setTextColor(getThemedColor("actionBarDefaultSubmenuItem"));
                this.deleteView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Cells.TextSelectionHelper$$ExternalSyntheticLambda1
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        TextSelectionHelper.this.lambda$showActions$2(view);
                    }
                });
                this.popupLayout.addView(this.deleteView, LayoutHelper.createFrame(-2, 48.0f));
                ActionBarPopupWindow actionBarPopupWindow = new ActionBarPopupWindow(this.popupLayout, -2, -2);
                this.popupWindow = actionBarPopupWindow;
                actionBarPopupWindow.setAnimationEnabled(false);
                this.popupWindow.setAnimationStyle(R.style.PopupContextAnimation);
                this.popupWindow.setOutsideTouchable(true);
                ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout2 = this.popupLayout;
                if (actionBarPopupWindowLayout2 != null) {
                    actionBarPopupWindowLayout2.setBackgroundColor(getThemedColor("actionBarDefaultSubmenuBackground"));
                }
            }
            if (this.selectedView == null || (i = (((int) ((offsetToCord(this.selectionStart)[1] + this.textY) + this.selectedView.getY())) + ((-getLineHeight()) / 2)) - AndroidUtilities.dp(4.0f)) < 0) {
                i = 0;
            }
            this.popupWindow.showAtLocation(this.textSelectionOverlay, 48, 0, i - AndroidUtilities.dp(48.0f));
            this.popupWindow.startAnimation();
        }
    }

    public /* synthetic */ boolean lambda$showActions$1(View view, MotionEvent motionEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (motionEvent.getActionMasked() != 0 || (actionBarPopupWindow = this.popupWindow) == null || !actionBarPopupWindow.isShowing()) {
            return false;
        }
        view.getHitRect(this.popupRect);
        return false;
    }

    public /* synthetic */ void lambda$showActions$2(View view) {
        copyText();
    }

    protected boolean canShowActions() {
        return this.selectedView != null;
    }

    public void hideActions() {
        ActionMode actionMode;
        if (Build.VERSION.SDK_INT >= 23) {
            if (this.actionMode != null && this.actionsIsShowing) {
                this.actionsIsShowing = false;
                this.hideActionsRunnable.run();
            }
            this.actionsIsShowing = false;
        }
        if (!isSelectionMode() && (actionMode = this.actionMode) != null) {
            actionMode.finish();
            this.actionMode = null;
        }
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null) {
            actionBarPopupWindow.dismiss();
        }
    }

    public TextSelectionHelper<Cell>.TextSelectionOverlay getOverlayView(Context context) {
        if (this.textSelectionOverlay == null) {
            this.textSelectionOverlay = new TextSelectionOverlay(context);
        }
        return this.textSelectionOverlay;
    }

    public boolean isSelected(MessageObject messageObject) {
        return messageObject != null && this.selectedCellId == messageObject.getId();
    }

    public void checkSelectionCancel(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
            cancelTextSelectionRunnable();
        }
    }

    public void cancelTextSelectionRunnable() {
        AndroidUtilities.cancelRunOnUIThread(this.startSelectionRunnable);
        this.tryCapture = false;
    }

    public void clear() {
        clear(false);
    }

    public void clear(boolean z) {
        onExitSelectionMode(z);
        this.selectionStart = -1;
        this.selectionEnd = -1;
        hideMagnifier();
        hideActions();
        invalidate();
        this.selectedView = null;
        this.selectedCellId = 0;
        AndroidUtilities.cancelRunOnUIThread(this.startSelectionRunnable);
        this.tryCapture = false;
        TextSelectionHelper<Cell>.TextSelectionOverlay textSelectionOverlay = this.textSelectionOverlay;
        if (textSelectionOverlay != null) {
            textSelectionOverlay.setVisibility(8);
        }
        this.handleViewProgress = 0.0f;
        Callback callback = this.callback;
        if (callback != null) {
            callback.onStateChanged(false);
        }
        this.capturedX = -1;
        this.capturedY = -1;
        this.maybeTextX = -1;
        this.maybeTextY = -1;
        this.movingOffsetX = 0.0f;
        this.movingOffsetY = 0.0f;
        this.movingHandle = false;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public boolean isTryingSelect() {
        return this.tryCapture;
    }

    public void onParentScrolled() {
        TextSelectionHelper<Cell>.TextSelectionOverlay textSelectionOverlay;
        if (!isSelectionMode() || (textSelectionOverlay = this.textSelectionOverlay) == null) {
            return;
        }
        this.parentIsScrolling = true;
        textSelectionOverlay.invalidate();
        hideActions();
    }

    public void stopScrolling() {
        this.parentIsScrolling = false;
        showActions();
    }

    public static boolean isInterruptedCharacter(char c) {
        return Character.isLetter(c) || Character.isDigit(c) || c == '_';
    }

    public void setTopOffset(int i) {
        this.topOffset = i;
    }

    /* loaded from: classes3.dex */
    public class TextSelectionOverlay extends View {
        float pressedX;
        float pressedY;
        Paint handleViewPaint = new Paint(1);
        long pressedTime = 0;
        Path path = new Path();

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public TextSelectionOverlay(Context context) {
            super(context);
            TextSelectionHelper.this = r1;
            this.handleViewPaint.setStyle(Paint.Style.FILL);
        }

        public boolean checkOnTap(MotionEvent motionEvent) {
            if (TextSelectionHelper.this.isSelectionMode() && !TextSelectionHelper.this.movingHandle) {
                int action = motionEvent.getAction();
                if (action == 0) {
                    this.pressedX = motionEvent.getX();
                    this.pressedY = motionEvent.getY();
                    this.pressedTime = System.currentTimeMillis();
                } else if (action == 1 && System.currentTimeMillis() - this.pressedTime < 200 && MathUtils.distance((int) this.pressedX, (int) this.pressedY, (int) motionEvent.getX(), (int) motionEvent.getY()) < TextSelectionHelper.this.touchSlop) {
                    TextSelectionHelper.this.hideActions();
                    TextSelectionHelper.this.clear();
                    return true;
                }
            }
            return false;
        }

        /* JADX WARN: Code restructure failed: missing block: B:14:0x004f, code lost:
            if (r4 != 3) goto L294;
         */
        /* JADX WARN: Removed duplicated region for block: B:45:0x0120  */
        /* JADX WARN: Removed duplicated region for block: B:52:0x0139 A[ADDED_TO_REGION] */
        /* JADX WARN: Removed duplicated region for block: B:59:0x015d  */
        /* JADX WARN: Removed duplicated region for block: B:62:0x0172  */
        /* JADX WARN: Removed duplicated region for block: B:63:0x0189  */
        /* JADX WARN: Removed duplicated region for block: B:67:0x01b0  */
        @Override // android.view.View
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public boolean onTouchEvent(android.view.MotionEvent r22) {
            /*
                Method dump skipped, instructions count: 1560
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.TextSelectionHelper.TextSelectionOverlay.onTouchEvent(android.view.MotionEvent):boolean");
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            int i;
            TextSelectionHelper textSelectionHelper;
            TextSelectionHelper textSelectionHelper2;
            TextSelectionHelper textSelectionHelper3;
            TextSelectionHelper textSelectionHelper4;
            if (!TextSelectionHelper.this.isSelectionMode()) {
                return;
            }
            int dp = AndroidUtilities.dp(22.0f);
            int i2 = TextSelectionHelper.this.topOffset;
            TextSelectionHelper.this.pickEndView();
            if (TextSelectionHelper.this.selectedView != null) {
                canvas.save();
                float y = TextSelectionHelper.this.selectedView.getY();
                float f = y + textSelectionHelper3.textY;
                float x = TextSelectionHelper.this.selectedView.getX() + TextSelectionHelper.this.textX;
                canvas.translate(x, f);
                Cell cell = TextSelectionHelper.this.selectedView;
                MessageObject messageObject = cell instanceof ChatMessageCell ? ((ChatMessageCell) cell).getMessageObject() : null;
                if (messageObject != null && messageObject.isOutOwner()) {
                    this.handleViewPaint.setColor(TextSelectionHelper.this.getThemedColor("chat_outTextSelectionCursor"));
                } else {
                    this.handleViewPaint.setColor(TextSelectionHelper.this.getThemedColor("chat_TextSelectionCursor"));
                }
                TextSelectionHelper textSelectionHelper5 = TextSelectionHelper.this;
                int length = textSelectionHelper5.getText(textSelectionHelper5.selectedView, false).length();
                TextSelectionHelper textSelectionHelper6 = TextSelectionHelper.this;
                int i3 = textSelectionHelper6.selectionEnd;
                if (i3 >= 0 && i3 <= length) {
                    textSelectionHelper6.fillLayoutForOffset(i3, textSelectionHelper6.layoutBlock);
                    TextSelectionHelper textSelectionHelper7 = TextSelectionHelper.this;
                    StaticLayout staticLayout = textSelectionHelper7.layoutBlock.layout;
                    if (staticLayout != null) {
                        int i4 = textSelectionHelper7.selectionEnd;
                        int length2 = staticLayout.getText().length();
                        if (i4 > length2) {
                            i4 = length2;
                        }
                        int lineForOffset = staticLayout.getLineForOffset(i4);
                        float primaryHorizontal = staticLayout.getPrimaryHorizontal(i4);
                        LayoutBlock layoutBlock = TextSelectionHelper.this.layoutBlock;
                        float f2 = primaryHorizontal + layoutBlock.xOffset;
                        float lineBottom = (int) (staticLayout.getLineBottom(lineForOffset) + layoutBlock.yOffset);
                        float f3 = f + lineBottom;
                        if (f3 <= textSelectionHelper4.keyboardSize + i2 || f3 >= textSelectionHelper4.parentView.getMeasuredHeight()) {
                            TextSelectionHelper.this.endArea.setEmpty();
                        } else if (!staticLayout.isRtlCharAt(TextSelectionHelper.this.selectionEnd)) {
                            canvas.save();
                            canvas.translate(f2, lineBottom);
                            float interpolation = TextSelectionHelper.this.interpolator.getInterpolation(TextSelectionHelper.this.handleViewProgress);
                            float f4 = dp;
                            float f5 = f4 / 2.0f;
                            canvas.scale(interpolation, interpolation, f5, f5);
                            this.path.reset();
                            this.path.addCircle(f5, f5, f5, Path.Direction.CCW);
                            this.path.addRect(0.0f, 0.0f, f5, f5, Path.Direction.CCW);
                            canvas.drawPath(this.path, this.handleViewPaint);
                            canvas.restore();
                            float f6 = x + f2;
                            TextSelectionHelper.this.endArea.set(f6, f3 - f4, f6 + f4, f3 + f4);
                            TextSelectionHelper.this.endArea.inset(-AndroidUtilities.dp(8.0f), -AndroidUtilities.dp(8.0f));
                            i = 1;
                            canvas.restore();
                        } else {
                            canvas.save();
                            float f7 = dp;
                            canvas.translate(f2 - f7, lineBottom);
                            float interpolation2 = TextSelectionHelper.this.interpolator.getInterpolation(TextSelectionHelper.this.handleViewProgress);
                            float f8 = f7 / 2.0f;
                            canvas.scale(interpolation2, interpolation2, f8, f8);
                            this.path.reset();
                            this.path.addCircle(f8, f8, f8, Path.Direction.CCW);
                            this.path.addRect(f8, 0.0f, f7, f8, Path.Direction.CCW);
                            canvas.drawPath(this.path, this.handleViewPaint);
                            canvas.restore();
                            float f9 = x + f2;
                            TextSelectionHelper.this.endArea.set(f9 - f7, f3 - f7, f9, f3 + f7);
                            TextSelectionHelper.this.endArea.inset(-AndroidUtilities.dp(8.0f), -AndroidUtilities.dp(8.0f));
                        }
                    }
                }
                i = 0;
                canvas.restore();
            } else {
                i = 0;
            }
            TextSelectionHelper.this.pickStartView();
            if (TextSelectionHelper.this.selectedView != null) {
                canvas.save();
                float y2 = TextSelectionHelper.this.selectedView.getY();
                float f10 = y2 + textSelectionHelper.textY;
                float x2 = TextSelectionHelper.this.selectedView.getX() + TextSelectionHelper.this.textX;
                canvas.translate(x2, f10);
                TextSelectionHelper textSelectionHelper8 = TextSelectionHelper.this;
                int length3 = textSelectionHelper8.getText(textSelectionHelper8.selectedView, false).length();
                TextSelectionHelper textSelectionHelper9 = TextSelectionHelper.this;
                int i5 = textSelectionHelper9.selectionStart;
                if (i5 >= 0 && i5 <= length3) {
                    textSelectionHelper9.fillLayoutForOffset(i5, textSelectionHelper9.layoutBlock);
                    TextSelectionHelper textSelectionHelper10 = TextSelectionHelper.this;
                    StaticLayout staticLayout2 = textSelectionHelper10.layoutBlock.layout;
                    if (staticLayout2 != null) {
                        int lineForOffset2 = staticLayout2.getLineForOffset(textSelectionHelper10.selectionStart);
                        float primaryHorizontal2 = staticLayout2.getPrimaryHorizontal(TextSelectionHelper.this.selectionStart);
                        LayoutBlock layoutBlock2 = TextSelectionHelper.this.layoutBlock;
                        float f11 = primaryHorizontal2 + layoutBlock2.xOffset;
                        float lineBottom2 = (int) (staticLayout2.getLineBottom(lineForOffset2) + layoutBlock2.yOffset);
                        float f12 = f10 + lineBottom2;
                        if (f12 > i2 + textSelectionHelper2.keyboardSize && f12 < textSelectionHelper2.parentView.getMeasuredHeight()) {
                            if (!staticLayout2.isRtlCharAt(TextSelectionHelper.this.selectionStart)) {
                                canvas.save();
                                float f13 = dp;
                                canvas.translate(f11 - f13, lineBottom2);
                                float interpolation3 = TextSelectionHelper.this.interpolator.getInterpolation(TextSelectionHelper.this.handleViewProgress);
                                float f14 = f13 / 2.0f;
                                canvas.scale(interpolation3, interpolation3, f14, f14);
                                this.path.reset();
                                this.path.addCircle(f14, f14, f14, Path.Direction.CCW);
                                this.path.addRect(f14, 0.0f, f13, f14, Path.Direction.CCW);
                                canvas.drawPath(this.path, this.handleViewPaint);
                                canvas.restore();
                                float f15 = x2 + f11;
                                TextSelectionHelper.this.startArea.set(f15 - f13, f12 - f13, f15, f12 + f13);
                                TextSelectionHelper.this.startArea.inset(-AndroidUtilities.dp(8.0f), -AndroidUtilities.dp(8.0f));
                                i++;
                            } else {
                                canvas.save();
                                canvas.translate(f11, lineBottom2);
                                float interpolation4 = TextSelectionHelper.this.interpolator.getInterpolation(TextSelectionHelper.this.handleViewProgress);
                                float f16 = dp;
                                float f17 = f16 / 2.0f;
                                canvas.scale(interpolation4, interpolation4, f17, f17);
                                this.path.reset();
                                this.path.addCircle(f17, f17, f17, Path.Direction.CCW);
                                this.path.addRect(0.0f, 0.0f, f17, f17, Path.Direction.CCW);
                                canvas.drawPath(this.path, this.handleViewPaint);
                                canvas.restore();
                                float f18 = x2 + f11;
                                TextSelectionHelper.this.startArea.set(f18, f12 - f16, f18 + f16, f12 + f16);
                                TextSelectionHelper.this.startArea.inset(-AndroidUtilities.dp(8.0f), -AndroidUtilities.dp(8.0f));
                            }
                        } else {
                            if (f12 > 0.0f && f12 - TextSelectionHelper.this.getLineHeight() < TextSelectionHelper.this.parentView.getMeasuredHeight()) {
                                i++;
                            }
                            TextSelectionHelper.this.startArea.setEmpty();
                        }
                    }
                }
                canvas.restore();
            }
            if (i != 0 && TextSelectionHelper.this.movingHandle) {
                TextSelectionHelper textSelectionHelper11 = TextSelectionHelper.this;
                if (!textSelectionHelper11.movingHandleStart) {
                    textSelectionHelper11.pickEndView();
                }
                TextSelectionHelper textSelectionHelper12 = TextSelectionHelper.this;
                textSelectionHelper12.showMagnifier(textSelectionHelper12.lastX);
                if (TextSelectionHelper.this.magnifierY != TextSelectionHelper.this.magnifierYanimated || TextSelectionHelper.this.magnifierX != TextSelectionHelper.this.magnifierXanimated) {
                    invalidate();
                }
            }
            if (!TextSelectionHelper.this.parentIsScrolling) {
                TextSelectionHelper.this.showActions();
            }
            if (Build.VERSION.SDK_INT >= 23 && TextSelectionHelper.this.actionMode != null) {
                TextSelectionHelper.this.actionMode.invalidateContentRect();
                if (TextSelectionHelper.this.actionMode != null) {
                    ((FloatingActionMode) TextSelectionHelper.this.actionMode).updateViewLocationInWindow();
                }
            }
            if (!TextSelectionHelper.this.isOneTouch) {
                return;
            }
            invalidate();
        }
    }

    protected void jumpToLine(int i, int i2, boolean z, float f, float f2, Cell cell) {
        int i3;
        int i4;
        if (this.movingHandleStart) {
            this.selectionStart = i2;
            if (!z && i2 > (i4 = this.selectionEnd)) {
                this.selectionEnd = i2;
                this.selectionStart = i4;
                this.movingHandleStart = false;
            }
            this.snap = true;
            return;
        }
        this.selectionEnd = i2;
        if (!z && (i3 = this.selectionStart) > i2) {
            this.selectionEnd = i3;
            this.selectionStart = i2;
            this.movingHandleStart = true;
        }
        this.snap = true;
    }

    protected boolean canSelect(int i) {
        return (i == this.selectionStart || i == this.selectionEnd) ? false : true;
    }

    public void invalidate() {
        Cell cell = this.selectedView;
        if (cell != null) {
            cell.invalidate();
        }
        TextSelectionHelper<Cell>.TextSelectionOverlay textSelectionOverlay = this.textSelectionOverlay;
        if (textSelectionOverlay != null) {
            textSelectionOverlay.invalidate();
        }
    }

    /* renamed from: org.telegram.ui.Cells.TextSelectionHelper$4 */
    /* loaded from: classes3.dex */
    public class AnonymousClass4 implements ActionMode.Callback {
        private String translateFromLanguage = null;

        AnonymousClass4() {
            TextSelectionHelper.this = r1;
        }

        @Override // android.view.ActionMode.Callback
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            menu.add(0, 16908321, 0, 17039361);
            menu.add(0, 16908319, 1, 17039373);
            menu.add(0, 3, 2, LocaleController.getString("TranslateMessage", R.string.TranslateMessage));
            return true;
        }

        @Override // android.view.ActionMode.Callback
        public boolean onPrepareActionMode(ActionMode actionMode, final Menu menu) {
            TextSelectionHelper textSelectionHelper = TextSelectionHelper.this;
            Cell cell = textSelectionHelper.selectedView;
            if (cell != null) {
                CharSequence text = textSelectionHelper.getText(cell, false);
                TextSelectionHelper textSelectionHelper2 = TextSelectionHelper.this;
                if (textSelectionHelper2.multiselect || (textSelectionHelper2.selectionStart <= 0 && textSelectionHelper2.selectionEnd >= text.length() - 1)) {
                    menu.getItem(1).setVisible(false);
                } else {
                    menu.getItem(1).setVisible(true);
                }
            }
            if (TextSelectionHelper.this.onTranslateListener != null && LanguageDetector.hasSupport() && TextSelectionHelper.this.getSelectedText() != null) {
                LanguageDetector.detectLanguage(TextSelectionHelper.this.getSelectedText().toString(), new LanguageDetector.StringCallback() { // from class: org.telegram.ui.Cells.TextSelectionHelper$4$$ExternalSyntheticLambda2
                    @Override // org.telegram.messenger.LanguageDetector.StringCallback
                    public final void run(String str) {
                        TextSelectionHelper.AnonymousClass4.this.lambda$onPrepareActionMode$0(menu, str);
                    }
                }, new LanguageDetector.ExceptionCallback() { // from class: org.telegram.ui.Cells.TextSelectionHelper$4$$ExternalSyntheticLambda1
                    @Override // org.telegram.messenger.LanguageDetector.ExceptionCallback
                    public final void run(Exception exc) {
                        TextSelectionHelper.AnonymousClass4.this.lambda$onPrepareActionMode$1(menu, exc);
                    }
                });
            } else {
                this.translateFromLanguage = null;
                updateTranslateButton(menu);
            }
            return true;
        }

        public /* synthetic */ void lambda$onPrepareActionMode$0(Menu menu, String str) {
            this.translateFromLanguage = str;
            updateTranslateButton(menu);
        }

        public /* synthetic */ void lambda$onPrepareActionMode$1(Menu menu, Exception exc) {
            FileLog.e("mlkit: failed to detect language in selection");
            FileLog.e(exc);
            this.translateFromLanguage = null;
            updateTranslateButton(menu);
        }

        private void updateTranslateButton(Menu menu) {
            String str;
            menu.getItem(2).setVisible(TextSelectionHelper.this.onTranslateListener != null && (((str = this.translateFromLanguage) != null && ((!str.equals(LocaleController.getInstance().getCurrentLocale().getLanguage()) || this.translateFromLanguage.equals("und")) && !RestrictedLanguagesSelectActivity.getRestrictedLanguages().contains(this.translateFromLanguage))) || !LanguageDetector.hasSupport()));
        }

        @Override // android.view.ActionMode.Callback
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            if (!TextSelectionHelper.this.isSelectionMode()) {
                return true;
            }
            int itemId = menuItem.getItemId();
            if (itemId == 3) {
                if (TextSelectionHelper.this.onTranslateListener != null) {
                    TextSelectionHelper.this.onTranslateListener.run(TextSelectionHelper.this.getSelectedText(), this.translateFromLanguage, LocaleController.getInstance().getCurrentLocale().getLanguage(), new Runnable() { // from class: org.telegram.ui.Cells.TextSelectionHelper$4$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            TextSelectionHelper.AnonymousClass4.this.lambda$onActionItemClicked$2();
                        }
                    });
                }
                TextSelectionHelper.this.hideActions();
                return true;
            } else if (itemId != 16908319) {
                if (itemId == 16908321) {
                    TextSelectionHelper.this.copyText();
                    return true;
                }
                TextSelectionHelper.this.clear();
                return true;
            } else {
                TextSelectionHelper textSelectionHelper = TextSelectionHelper.this;
                CharSequence text = textSelectionHelper.getText(textSelectionHelper.selectedView, false);
                if (text == null) {
                    return true;
                }
                TextSelectionHelper textSelectionHelper2 = TextSelectionHelper.this;
                textSelectionHelper2.selectionStart = 0;
                textSelectionHelper2.selectionEnd = text.length();
                TextSelectionHelper.this.hideActions();
                TextSelectionHelper.this.invalidate();
                TextSelectionHelper.this.showActions();
                return true;
            }
        }

        public /* synthetic */ void lambda$onActionItemClicked$2() {
            TextSelectionHelper.this.showActions();
        }

        @Override // android.view.ActionMode.Callback
        public void onDestroyActionMode(ActionMode actionMode) {
            if (Build.VERSION.SDK_INT < 23) {
                TextSelectionHelper.this.clear();
            }
        }
    }

    private ActionMode.Callback createActionCallback() {
        final AnonymousClass4 anonymousClass4 = new AnonymousClass4();
        return Build.VERSION.SDK_INT >= 23 ? new ActionMode.Callback2() { // from class: org.telegram.ui.Cells.TextSelectionHelper.5
            @Override // android.view.ActionMode.Callback
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                return anonymousClass4.onCreateActionMode(actionMode, menu);
            }

            @Override // android.view.ActionMode.Callback
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return anonymousClass4.onPrepareActionMode(actionMode, menu);
            }

            @Override // android.view.ActionMode.Callback
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                return anonymousClass4.onActionItemClicked(actionMode, menuItem);
            }

            @Override // android.view.ActionMode.Callback
            public void onDestroyActionMode(ActionMode actionMode) {
                anonymousClass4.onDestroyActionMode(actionMode);
            }

            @Override // android.view.ActionMode.Callback2
            public void onGetContentRect(ActionMode actionMode, View view, Rect rect) {
                int i;
                if (!TextSelectionHelper.this.isSelectionMode()) {
                    return;
                }
                TextSelectionHelper.this.pickStartView();
                TextSelectionHelper textSelectionHelper = TextSelectionHelper.this;
                int i2 = 1;
                if (textSelectionHelper.selectedView != null) {
                    TextSelectionHelper textSelectionHelper2 = TextSelectionHelper.this;
                    int[] offsetToCord = textSelectionHelper2.offsetToCord(textSelectionHelper2.selectionStart);
                    int i3 = offsetToCord[0];
                    TextSelectionHelper textSelectionHelper3 = TextSelectionHelper.this;
                    i = i3 + textSelectionHelper3.textX;
                    int y = (((int) ((offsetToCord[1] + textSelectionHelper3.textY) + textSelectionHelper3.selectedView.getY())) + ((-textSelectionHelper.getLineHeight()) / 2)) - AndroidUtilities.dp(4.0f);
                    if (y >= 1) {
                        i2 = y;
                    }
                } else {
                    i = 0;
                }
                int width = TextSelectionHelper.this.parentView.getWidth();
                TextSelectionHelper.this.pickEndView();
                TextSelectionHelper textSelectionHelper4 = TextSelectionHelper.this;
                if (textSelectionHelper4.selectedView != null) {
                    width = textSelectionHelper4.offsetToCord(textSelectionHelper4.selectionEnd)[0] + TextSelectionHelper.this.textX;
                }
                rect.set(Math.min(i, width), i2, Math.max(i, width), i2 + 1);
            }
        } : anonymousClass4;
    }

    public void copyText() {
        CharSequence selectedText;
        if (isSelectionMode() && (selectedText = getSelectedText()) != null) {
            AndroidUtilities.addToClipboard(selectedText);
            hideActions();
            clear(true);
            Callback callback = this.callback;
            if (callback == null) {
                return;
            }
            callback.onTextCopied();
        }
    }

    protected CharSequence getSelectedText() {
        CharSequence text = getText(this.selectedView, false);
        if (text != null) {
            return text.subSequence(this.selectionStart, this.selectionEnd);
        }
        return null;
    }

    protected int[] offsetToCord(int i) {
        fillLayoutForOffset(i, this.layoutBlock);
        StaticLayout staticLayout = this.layoutBlock.layout;
        if (staticLayout == null || i > staticLayout.getText().length()) {
            return this.tmpCoord;
        }
        int lineForOffset = staticLayout.getLineForOffset(i);
        this.tmpCoord[0] = (int) (staticLayout.getPrimaryHorizontal(i) + this.layoutBlock.xOffset);
        this.tmpCoord[1] = staticLayout.getLineBottom(lineForOffset);
        int[] iArr = this.tmpCoord;
        iArr[1] = (int) (iArr[1] + this.layoutBlock.yOffset);
        return iArr;
    }

    protected void drawSelection(Canvas canvas, StaticLayout staticLayout, int i, int i2, boolean z, boolean z2) {
        float f;
        this.selectionPath.reset();
        this.selectionHandlePath.reset();
        float f2 = this.cornerRadius;
        float f3 = f2 * 1.65f;
        int i3 = (int) (f2 / 2.0f);
        int lineForOffset = staticLayout.getLineForOffset(i);
        int lineForOffset2 = staticLayout.getLineForOffset(i2);
        boolean z3 = true;
        if (lineForOffset == lineForOffset2) {
            drawLine(staticLayout, lineForOffset, i, i2, !z, !z2);
        } else {
            int lineEnd = staticLayout.getLineEnd(lineForOffset);
            Rect rect = null;
            if (staticLayout.getParagraphDirection(lineForOffset) != -1 && lineEnd > 0) {
                lineEnd--;
                CharSequence text = staticLayout.getText();
                int primaryHorizontal = (int) staticLayout.getPrimaryHorizontal(lineEnd);
                if (staticLayout.isRtlCharAt(lineEnd)) {
                    int i4 = lineEnd;
                    while (staticLayout.isRtlCharAt(i4) && i4 != 0) {
                        i4--;
                    }
                    f = staticLayout.getLineForOffset(i4) == staticLayout.getLineForOffset(lineEnd) ? staticLayout.getPrimaryHorizontal(i4 + 1) : staticLayout.getLineLeft(lineForOffset);
                } else {
                    f = staticLayout.getLineRight(lineForOffset);
                }
                int i5 = (int) f;
                int min = Math.min(primaryHorizontal, i5);
                int max = Math.max(primaryHorizontal, i5);
                if (lineEnd > 0 && lineEnd < text.length() && !Character.isWhitespace(text.charAt(lineEnd - 1))) {
                    rect = new Rect(min, staticLayout.getLineTop(lineForOffset), max + i3, staticLayout.getLineBottom(lineForOffset));
                }
            }
            Rect rect2 = rect;
            drawLine(staticLayout, lineForOffset, i, lineEnd, !z, true);
            if (rect2 != null) {
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set(rect2);
                this.selectionPath.addRect(rectF, Path.Direction.CW);
            }
            for (int i6 = lineForOffset + 1; i6 < lineForOffset2; i6++) {
                int lineLeft = (int) staticLayout.getLineLeft(i6);
                int lineRight = (int) staticLayout.getLineRight(i6);
                this.selectionPath.addRect(Math.min(lineLeft, lineRight) - i3, staticLayout.getLineTop(i6), Math.max(lineLeft, lineRight) + i3, staticLayout.getLineBottom(i6) + 1, Path.Direction.CW);
            }
            z3 = true;
            drawLine(staticLayout, lineForOffset2, staticLayout.getLineStart(lineForOffset2), i2, true, !z2);
        }
        int i7 = Build.VERSION.SDK_INT;
        if (i7 < 26) {
            z3 = false;
        }
        if (z3) {
            canvas.save();
        }
        float primaryHorizontal2 = staticLayout.getPrimaryHorizontal(i);
        float primaryHorizontal3 = staticLayout.getPrimaryHorizontal(i2);
        float lineBottom = staticLayout.getLineBottom(lineForOffset);
        float lineBottom2 = staticLayout.getLineBottom(lineForOffset2);
        if (!z || !z2 || lineBottom != lineBottom2 || Math.abs(primaryHorizontal3 - primaryHorizontal2) >= f3) {
            if (z) {
                Rect rect3 = AndroidUtilities.rectTmp2;
                rect3.set((int) primaryHorizontal2, (int) (lineBottom - f3), (int) Math.min(primaryHorizontal2 + f3, staticLayout.getLineRight(lineForOffset)), (int) lineBottom);
                RectF rectF2 = AndroidUtilities.rectTmp;
                rectF2.set(rect3);
                this.selectionHandlePath.addRect(rectF2, Path.Direction.CW);
                if (i7 >= 26) {
                    rect3.set(rect3.left - ((int) f3), rect3.top, rect3.right, rect3.bottom);
                    canvas.clipOutRect(rect3);
                }
            }
            if (z2) {
                Rect rect4 = AndroidUtilities.rectTmp2;
                rect4.set((int) Math.max(primaryHorizontal3 - f3, staticLayout.getLineLeft(lineForOffset2)), (int) (lineBottom2 - f3), (int) primaryHorizontal3, (int) lineBottom2);
                RectF rectF3 = AndroidUtilities.rectTmp;
                rectF3.set(rect4);
                this.selectionHandlePath.addRect(rectF3, Path.Direction.CW);
                if (i7 >= 26) {
                    canvas.clipOutRect(rect4);
                }
            }
        } else {
            float min2 = Math.min(primaryHorizontal2, primaryHorizontal3);
            float max2 = Math.max(primaryHorizontal2, primaryHorizontal3);
            Rect rect5 = AndroidUtilities.rectTmp2;
            rect5.set((int) min2, (int) (lineBottom - f3), (int) max2, (int) lineBottom);
            RectF rectF4 = AndroidUtilities.rectTmp;
            rectF4.set(rect5);
            this.selectionHandlePath.addRect(rectF4, Path.Direction.CW);
            if (i7 >= 26) {
                canvas.clipOutRect(rect5);
            }
        }
        canvas.drawPath(this.selectionPath, this.selectionPaint);
        if (z3) {
            canvas.restore();
            canvas.drawPath(this.selectionHandlePath, this.selectionHandlePaint);
        }
    }

    private void drawLine(StaticLayout staticLayout, int i, int i2, int i3, boolean z, boolean z2) {
        float f;
        float f2;
        int lineTop;
        this.tempPath2.reset();
        staticLayout.getSelectionPath(i2, i3, this.tempPath2);
        if (this.tempPath2.lastBottom < staticLayout.getLineBottom(i)) {
            f2 = staticLayout.getLineTop(i);
            f = (staticLayout.getLineBottom(i) - lineTop) / (this.tempPath2.lastBottom - f2);
        } else {
            f = 1.0f;
            f2 = 0.0f;
        }
        for (int i4 = 0; i4 < this.tempPath2.rectsCount; i4++) {
            RectF rectF = (RectF) this.tempPath2.rects.get(i4);
            rectF.set((int) (rectF.left - (z ? this.cornerRadius / 2.0f : 0.0f)), (int) (((rectF.top - f2) * f) + f2), (int) (rectF.right + (z2 ? this.cornerRadius / 2.0f : 0.0f)), (int) (((rectF.bottom - f2) * f) + f2));
            this.selectionPath.addRect(rectF.left, rectF.top, rectF.right, rectF.bottom, Path.Direction.CW);
        }
        if (this.tempPath2.rectsCount != 0 || z2) {
            return;
        }
        int lineTop2 = staticLayout.getLineTop(i);
        int lineBottom = staticLayout.getLineBottom(i);
        Path path = this.selectionPath;
        float f3 = this.cornerRadius;
        path.addRect(((int) staticLayout.getPrimaryHorizontal(i2)) - (f3 / 2.0f), lineTop2, ((int) staticLayout.getPrimaryHorizontal(i3)) + (f3 / 4.0f), lineBottom, Path.Direction.CW);
    }

    /* loaded from: classes3.dex */
    public static class LayoutBlock {
        StaticLayout layout;
        float xOffset;
        float yOffset;

        private LayoutBlock() {
        }
    }

    protected void fillLayoutForOffset(int i, LayoutBlock layoutBlock) {
        fillLayoutForOffset(i, layoutBlock, false);
    }

    /* loaded from: classes3.dex */
    public static class ChatListTextSelectionHelper extends TextSelectionHelper<ChatMessageCell> {
        public static int TYPE_CAPTION = 1;
        public static int TYPE_DESCRIPTION = 2;
        public static int TYPE_MESSAGE;
        SparseArray<Animator> animatorSparseArray = new SparseArray<>();
        private boolean isDescription;
        private boolean maybeIsDescription;

        @Override // org.telegram.ui.Cells.TextSelectionHelper
        protected int getLineHeight() {
            Cell cell = this.selectedView;
            if (cell == null || ((ChatMessageCell) cell).getMessageObject() == null) {
                return 0;
            }
            MessageObject messageObject = ((ChatMessageCell) this.selectedView).getMessageObject();
            StaticLayout staticLayout = null;
            if (this.isDescription) {
                staticLayout = ((ChatMessageCell) this.selectedView).getDescriptionlayout();
            } else if (((ChatMessageCell) this.selectedView).hasCaptionLayout()) {
                staticLayout = ((ChatMessageCell) this.selectedView).getCaptionLayout();
            } else {
                ArrayList<MessageObject.TextLayoutBlock> arrayList = messageObject.textLayoutBlocks;
                if (arrayList != null) {
                    staticLayout = arrayList.get(0).textLayout;
                }
            }
            if (staticLayout != null) {
                return staticLayout.getLineBottom(0) - staticLayout.getLineTop(0);
            }
            return 0;
        }

        public void setMessageObject(ChatMessageCell chatMessageCell) {
            ArrayList<MessageObject.TextLayoutBlock> arrayList;
            this.maybeSelectedView = chatMessageCell;
            MessageObject messageObject = chatMessageCell.getMessageObject();
            if (this.maybeIsDescription && chatMessageCell.getDescriptionlayout() != null) {
                Rect rect = this.textArea;
                int i = this.maybeTextX;
                rect.set(i, this.maybeTextY, chatMessageCell.getDescriptionlayout().getWidth() + i, this.maybeTextY + chatMessageCell.getDescriptionlayout().getHeight());
            } else if (chatMessageCell.hasCaptionLayout()) {
                Rect rect2 = this.textArea;
                int i2 = this.maybeTextX;
                rect2.set(i2, this.maybeTextY, chatMessageCell.getCaptionLayout().getWidth() + i2, this.maybeTextY + chatMessageCell.getCaptionLayout().getHeight());
            } else if (messageObject == null || (arrayList = messageObject.textLayoutBlocks) == null || arrayList.size() <= 0) {
            } else {
                ArrayList<MessageObject.TextLayoutBlock> arrayList2 = messageObject.textLayoutBlocks;
                MessageObject.TextLayoutBlock textLayoutBlock = arrayList2.get(arrayList2.size() - 1);
                Rect rect3 = this.textArea;
                int i3 = this.maybeTextX;
                rect3.set(i3, this.maybeTextY, textLayoutBlock.textLayout.getWidth() + i3, (int) (this.maybeTextY + textLayoutBlock.textYOffset + textLayoutBlock.textLayout.getHeight()));
            }
        }

        public CharSequence getText(ChatMessageCell chatMessageCell, boolean z) {
            if (chatMessageCell == null || chatMessageCell.getMessageObject() == null) {
                return null;
            }
            if (!z ? this.isDescription : this.maybeIsDescription) {
                return chatMessageCell.getDescriptionlayout().getText();
            }
            if (chatMessageCell.hasCaptionLayout()) {
                return chatMessageCell.getCaptionLayout().getText();
            }
            return chatMessageCell.getMessageObject().messageText;
        }

        public void onTextSelected(ChatMessageCell chatMessageCell, ChatMessageCell chatMessageCell2) {
            final boolean z = chatMessageCell2 == null || !(chatMessageCell2.getMessageObject() == null || chatMessageCell2.getMessageObject().getId() == chatMessageCell.getMessageObject().getId());
            this.selectedCellId = chatMessageCell.getMessageObject().getId();
            try {
                int i = chatMessageCell.getMessageObject().messageOwner.edit_date;
            } catch (Exception unused) {
            }
            this.enterProgress = 0.0f;
            this.isDescription = this.maybeIsDescription;
            Animator animator = this.animatorSparseArray.get(this.selectedCellId);
            if (animator != null) {
                animator.removeAllListeners();
                animator.cancel();
            }
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Cells.TextSelectionHelper$ChatListTextSelectionHelper$$ExternalSyntheticLambda1
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    TextSelectionHelper.ChatListTextSelectionHelper.this.lambda$onTextSelected$0(z, valueAnimator);
                }
            });
            ofFloat.setDuration(250L);
            ofFloat.start();
            this.animatorSparseArray.put(this.selectedCellId, ofFloat);
            if (!z) {
                chatMessageCell.setSelectedBackgroundProgress(0.0f);
            }
            SharedConfig.removeTextSelectionHint();
        }

        public /* synthetic */ void lambda$onTextSelected$0(boolean z, ValueAnimator valueAnimator) {
            this.enterProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            TextSelectionHelper<Cell>.TextSelectionOverlay textSelectionOverlay = this.textSelectionOverlay;
            if (textSelectionOverlay != null) {
                textSelectionOverlay.invalidate();
            }
            Cell cell = this.selectedView;
            if (cell == null || ((ChatMessageCell) cell).getCurrentMessagesGroup() != null || !z) {
                return;
            }
            ((ChatMessageCell) this.selectedView).setSelectedBackgroundProgress(1.0f - this.enterProgress);
        }

        /* JADX WARN: Removed duplicated region for block: B:28:0x004d  */
        /* JADX WARN: Removed duplicated region for block: B:38:? A[RETURN, SYNTHETIC] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void draw(org.telegram.messenger.MessageObject r12, org.telegram.messenger.MessageObject.TextLayoutBlock r13, android.graphics.Canvas r14) {
            /*
                r11 = this;
                Cell extends org.telegram.ui.Cells.TextSelectionHelper$SelectableView r0 = r11.selectedView
                if (r0 == 0) goto L85
                org.telegram.ui.Cells.ChatMessageCell r0 = (org.telegram.ui.Cells.ChatMessageCell) r0
                org.telegram.messenger.MessageObject r0 = r0.getMessageObject()
                if (r0 == 0) goto L85
                boolean r0 = r11.isDescription
                if (r0 == 0) goto L12
                goto L85
            L12:
                Cell extends org.telegram.ui.Cells.TextSelectionHelper$SelectableView r0 = r11.selectedView
                org.telegram.ui.Cells.ChatMessageCell r0 = (org.telegram.ui.Cells.ChatMessageCell) r0
                org.telegram.messenger.MessageObject r0 = r0.getMessageObject()
                if (r0 == 0) goto L85
                java.util.ArrayList<org.telegram.messenger.MessageObject$TextLayoutBlock> r1 = r0.textLayoutBlocks
                if (r1 != 0) goto L22
                goto L85
            L22:
                int r12 = r12.getId()
                int r1 = r11.selectedCellId
                if (r12 != r1) goto L85
                int r12 = r11.selectionStart
                int r1 = r11.selectionEnd
                java.util.ArrayList<org.telegram.messenger.MessageObject$TextLayoutBlock> r2 = r0.textLayoutBlocks
                int r2 = r2.size()
                r3 = 1
                if (r2 <= r3) goto L49
                int r2 = r13.charactersOffset
                if (r12 >= r2) goto L3c
                r12 = r2
            L3c:
                int r3 = r13.charactersEnd
                if (r12 <= r3) goto L41
                r12 = r3
            L41:
                if (r1 >= r2) goto L44
                r1 = r2
            L44:
                if (r1 <= r3) goto L49
                r7 = r12
                r8 = r3
                goto L4b
            L49:
                r7 = r12
                r8 = r1
            L4b:
                if (r7 == r8) goto L85
                boolean r12 = r0.isOutOwner()
                if (r12 == 0) goto L68
                android.graphics.Paint r12 = r11.selectionPaint
                java.lang.String r0 = "chat_outTextSelectionHighlight"
                int r1 = r11.getThemedColor(r0)
                r12.setColor(r1)
                android.graphics.Paint r12 = r11.selectionHandlePaint
                int r0 = r11.getThemedColor(r0)
                r12.setColor(r0)
                goto L7c
            L68:
                android.graphics.Paint r12 = r11.selectionPaint
                java.lang.String r0 = "chat_inTextSelectionHighlight"
                int r1 = r11.getThemedColor(r0)
                r12.setColor(r1)
                android.graphics.Paint r12 = r11.selectionHandlePaint
                int r0 = r11.getThemedColor(r0)
                r12.setColor(r0)
            L7c:
                android.text.StaticLayout r6 = r13.textLayout
                r9 = 1
                r10 = 1
                r4 = r11
                r5 = r14
                r4.drawSelection(r5, r6, r7, r8, r9, r10)
            L85:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.TextSelectionHelper.ChatListTextSelectionHelper.draw(org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject$TextLayoutBlock, android.graphics.Canvas):void");
        }

        public int getCharOffsetFromCord(int i, int i2, int i3, int i4, ChatMessageCell chatMessageCell, boolean z) {
            StaticLayout staticLayout;
            int i5 = 0;
            if (chatMessageCell == null) {
                return 0;
            }
            int i6 = i - i3;
            int i7 = i2 - i4;
            float f = 0.0f;
            if (z ? this.maybeIsDescription : this.isDescription) {
                staticLayout = chatMessageCell.getDescriptionlayout();
            } else if (chatMessageCell.hasCaptionLayout()) {
                staticLayout = chatMessageCell.getCaptionLayout();
            } else {
                MessageObject.TextLayoutBlock textLayoutBlock = chatMessageCell.getMessageObject().textLayoutBlocks.get(chatMessageCell.getMessageObject().textLayoutBlocks.size() - 1);
                staticLayout = textLayoutBlock.textLayout;
                f = textLayoutBlock.textYOffset;
            }
            if (i7 < 0) {
                i7 = 1;
            }
            if (i7 > staticLayout.getLineBottom(staticLayout.getLineCount() - 1) + f) {
                i7 = (int) ((f + staticLayout.getLineBottom(staticLayout.getLineCount() - 1)) - 1.0f);
            }
            fillLayoutForCoords(i6, i7, chatMessageCell, this.layoutBlock, z);
            LayoutBlock layoutBlock = this.layoutBlock;
            StaticLayout staticLayout2 = layoutBlock.layout;
            if (staticLayout2 == null) {
                return -1;
            }
            int i8 = (int) (i6 - layoutBlock.xOffset);
            while (true) {
                if (i5 >= staticLayout2.getLineCount()) {
                    i5 = -1;
                    break;
                }
                float f2 = i7;
                if (f2 > this.layoutBlock.yOffset + staticLayout2.getLineTop(i5) && f2 < this.layoutBlock.yOffset + staticLayout2.getLineBottom(i5)) {
                    break;
                }
                i5++;
            }
            if (i5 < 0) {
                return -1;
            }
            return staticLayout2.getOffsetForHorizontal(i5, i8);
        }

        private void fillLayoutForCoords(int i, int i2, ChatMessageCell chatMessageCell, LayoutBlock layoutBlock, boolean z) {
            if (chatMessageCell == null) {
                return;
            }
            MessageObject messageObject = chatMessageCell.getMessageObject();
            if (!z ? this.isDescription : this.maybeIsDescription) {
                layoutBlock.layout = chatMessageCell.getDescriptionlayout();
                layoutBlock.xOffset = 0.0f;
                layoutBlock.yOffset = 0.0f;
            } else if (chatMessageCell.hasCaptionLayout()) {
                layoutBlock.layout = chatMessageCell.getCaptionLayout();
                layoutBlock.xOffset = 0.0f;
                layoutBlock.yOffset = 0.0f;
            } else {
                int i3 = 0;
                for (int i4 = 0; i4 < messageObject.textLayoutBlocks.size(); i4++) {
                    MessageObject.TextLayoutBlock textLayoutBlock = messageObject.textLayoutBlocks.get(i4);
                    float f = i2;
                    float f2 = textLayoutBlock.textYOffset;
                    if (f >= f2 && f <= textLayoutBlock.height + f2) {
                        layoutBlock.layout = textLayoutBlock.textLayout;
                        layoutBlock.yOffset = f2;
                        if (textLayoutBlock.isRtl()) {
                            i3 = (int) Math.ceil(messageObject.textXOffset);
                        }
                        layoutBlock.xOffset = -i3;
                        return;
                    }
                }
            }
        }

        @Override // org.telegram.ui.Cells.TextSelectionHelper
        protected void fillLayoutForOffset(int i, LayoutBlock layoutBlock, boolean z) {
            ChatMessageCell chatMessageCell = (ChatMessageCell) (z ? this.maybeSelectedView : this.selectedView);
            if (chatMessageCell == null) {
                layoutBlock.layout = null;
                return;
            }
            MessageObject messageObject = chatMessageCell.getMessageObject();
            if (this.isDescription) {
                layoutBlock.layout = chatMessageCell.getDescriptionlayout();
                layoutBlock.yOffset = 0.0f;
                layoutBlock.xOffset = 0.0f;
            } else if (chatMessageCell.hasCaptionLayout()) {
                layoutBlock.layout = chatMessageCell.getCaptionLayout();
                layoutBlock.yOffset = 0.0f;
                layoutBlock.xOffset = 0.0f;
            } else {
                ArrayList<MessageObject.TextLayoutBlock> arrayList = messageObject.textLayoutBlocks;
                if (arrayList == null) {
                    layoutBlock.layout = null;
                    return;
                }
                int i2 = 0;
                if (arrayList.size() == 1) {
                    layoutBlock.layout = messageObject.textLayoutBlocks.get(0).textLayout;
                    layoutBlock.yOffset = 0.0f;
                    if (messageObject.textLayoutBlocks.get(0).isRtl()) {
                        i2 = (int) Math.ceil(messageObject.textXOffset);
                    }
                    layoutBlock.xOffset = -i2;
                    return;
                }
                for (int i3 = 0; i3 < messageObject.textLayoutBlocks.size(); i3++) {
                    MessageObject.TextLayoutBlock textLayoutBlock = messageObject.textLayoutBlocks.get(i3);
                    if (i >= textLayoutBlock.charactersOffset && i <= textLayoutBlock.charactersEnd) {
                        layoutBlock.layout = messageObject.textLayoutBlocks.get(i3).textLayout;
                        layoutBlock.yOffset = messageObject.textLayoutBlocks.get(i3).textYOffset;
                        if (textLayoutBlock.isRtl()) {
                            i2 = (int) Math.ceil(messageObject.textXOffset);
                        }
                        layoutBlock.xOffset = -i2;
                        return;
                    }
                }
                layoutBlock.layout = null;
            }
        }

        @Override // org.telegram.ui.Cells.TextSelectionHelper
        protected void onExitSelectionMode(boolean z) {
            Cell cell = this.selectedView;
            if (cell == null || !((ChatMessageCell) cell).isDrawingSelectionBackground() || z) {
                return;
            }
            Cell cell2 = this.selectedView;
            final ChatMessageCell chatMessageCell = (ChatMessageCell) cell2;
            final int id = ((ChatMessageCell) cell2).getMessageObject().getId();
            Animator animator = this.animatorSparseArray.get(id);
            if (animator != null) {
                animator.removeAllListeners();
                animator.cancel();
            }
            chatMessageCell.setSelectedBackgroundProgress(0.01f);
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.01f, 1.0f);
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Cells.TextSelectionHelper$ChatListTextSelectionHelper$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    TextSelectionHelper.ChatListTextSelectionHelper.lambda$onExitSelectionMode$1(ChatMessageCell.this, id, valueAnimator);
                }
            });
            ofFloat.addListener(new AnimatorListenerAdapter(this) { // from class: org.telegram.ui.Cells.TextSelectionHelper.ChatListTextSelectionHelper.1
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator2) {
                    chatMessageCell.setSelectedBackgroundProgress(0.0f);
                }
            });
            ofFloat.setDuration(300L);
            ofFloat.start();
            this.animatorSparseArray.put(id, ofFloat);
        }

        public static /* synthetic */ void lambda$onExitSelectionMode$1(ChatMessageCell chatMessageCell, int i, ValueAnimator valueAnimator) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            if (chatMessageCell.getMessageObject() == null || chatMessageCell.getMessageObject().getId() != i) {
                return;
            }
            chatMessageCell.setSelectedBackgroundProgress(floatValue);
        }

        public void onChatMessageCellAttached(ChatMessageCell chatMessageCell) {
            if (chatMessageCell.getMessageObject() == null || chatMessageCell.getMessageObject().getId() != this.selectedCellId) {
                return;
            }
            this.selectedView = chatMessageCell;
        }

        public void onChatMessageCellDetached(ChatMessageCell chatMessageCell) {
            if (chatMessageCell.getMessageObject() == null || chatMessageCell.getMessageObject().getId() != this.selectedCellId) {
                return;
            }
            this.selectedView = null;
        }

        public void drawCaption(boolean z, StaticLayout staticLayout, Canvas canvas) {
            if (this.isDescription) {
                return;
            }
            if (z) {
                this.selectionPaint.setColor(getThemedColor("chat_outTextSelectionHighlight"));
                this.selectionHandlePaint.setColor(getThemedColor("chat_outTextSelectionHighlight"));
            } else {
                this.selectionPaint.setColor(getThemedColor("chat_inTextSelectionHighlight"));
                this.selectionHandlePaint.setColor(getThemedColor("chat_inTextSelectionHighlight"));
            }
            drawSelection(canvas, staticLayout, this.selectionStart, this.selectionEnd, true, true);
        }

        public void drawDescription(boolean z, StaticLayout staticLayout, Canvas canvas) {
            if (!this.isDescription) {
                return;
            }
            if (z) {
                this.selectionPaint.setColor(getThemedColor("chat_outTextSelectionHighlight"));
                this.selectionHandlePaint.setColor(getThemedColor("chat_outTextSelectionHighlight"));
            } else {
                this.selectionPaint.setColor(getThemedColor("chat_inTextSelectionHighlight"));
                this.selectionHandlePaint.setColor(getThemedColor("chat_inTextSelectionHighlight"));
            }
            drawSelection(canvas, staticLayout, this.selectionStart, this.selectionEnd, true, true);
        }

        @Override // org.telegram.ui.Cells.TextSelectionHelper
        public void invalidate() {
            super.invalidate();
            Cell cell = this.selectedView;
            if (cell == null || ((ChatMessageCell) cell).getCurrentMessagesGroup() == null) {
                return;
            }
            this.parentView.invalidate();
        }

        public void cancelAllAnimators() {
            for (int i = 0; i < this.animatorSparseArray.size(); i++) {
                SparseArray<Animator> sparseArray = this.animatorSparseArray;
                sparseArray.get(sparseArray.keyAt(i)).cancel();
            }
            this.animatorSparseArray.clear();
        }

        public void setIsDescription(boolean z) {
            this.maybeIsDescription = z;
        }

        @Override // org.telegram.ui.Cells.TextSelectionHelper
        public void clear(boolean z) {
            super.clear(z);
            this.isDescription = false;
        }

        public int getTextSelectionType(ChatMessageCell chatMessageCell) {
            if (this.isDescription) {
                return TYPE_DESCRIPTION;
            }
            if (chatMessageCell.hasCaptionLayout()) {
                return TYPE_CAPTION;
            }
            return TYPE_MESSAGE;
        }

        public void updateTextPosition(int i, int i2) {
            if (this.textX == i && this.textY == i2) {
                return;
            }
            this.textX = i;
            this.textY = i2;
            invalidate();
        }

        public void checkDataChanged(MessageObject messageObject) {
            try {
                int i = messageObject.messageOwner.edit_date;
            } catch (Exception unused) {
            }
            if (this.selectedCellId == messageObject.getId()) {
                clear(true);
            }
        }
    }

    /* loaded from: classes3.dex */
    public static class ArticleTextSelectionHelper extends TextSelectionHelper<ArticleSelectableView> {
        int endViewOffset;
        public LinearLayoutManager layoutManager;
        boolean startPeek;
        int startViewOffset;
        int startViewPosition = -1;
        int startViewChildPosition = -1;
        int endViewPosition = -1;
        int endViewChildPosition = -1;
        int maybeTextIndex = -1;
        SparseArray<CharSequence> textByPosition = new SparseArray<>();
        SparseArray<CharSequence> prefixTextByPosition = new SparseArray<>();
        SparseIntArray childCountByPosition = new SparseIntArray();
        public ArrayList<TextLayoutBlock> arrayList = new ArrayList<>();

        public ArticleTextSelectionHelper() {
            this.multiselect = true;
            this.showActionsAsPopupAlways = true;
        }

        public CharSequence getText(ArticleSelectableView articleSelectableView, boolean z) {
            int i;
            this.arrayList.clear();
            articleSelectableView.fillTextLayoutBlocks(this.arrayList);
            if (z) {
                i = this.maybeTextIndex;
            } else {
                i = this.startPeek ? this.startViewChildPosition : this.endViewChildPosition;
            }
            return (this.arrayList.isEmpty() || i < 0) ? "" : this.arrayList.get(i).getLayout().getText();
        }

        public int getCharOffsetFromCord(int i, int i2, int i3, int i4, ArticleSelectableView articleSelectableView, boolean z) {
            int i5;
            if (articleSelectableView == null) {
                return -1;
            }
            int i6 = i - i3;
            int i7 = i2 - i4;
            this.arrayList.clear();
            articleSelectableView.fillTextLayoutBlocks(this.arrayList);
            if (z) {
                i5 = this.maybeTextIndex;
            } else {
                i5 = this.startPeek ? this.startViewChildPosition : this.endViewChildPosition;
            }
            StaticLayout layout = this.arrayList.get(i5).getLayout();
            if (i6 < 0) {
                i6 = 1;
            }
            if (i7 < 0) {
                i7 = 1;
            }
            if (i6 > layout.getWidth()) {
                i6 = layout.getWidth();
            }
            if (i7 > layout.getLineBottom(layout.getLineCount() - 1)) {
                i7 = layout.getLineBottom(layout.getLineCount() - 1) - 1;
            }
            int i8 = 0;
            while (true) {
                if (i8 >= layout.getLineCount()) {
                    i8 = -1;
                    break;
                } else if (i7 > layout.getLineTop(i8) && i7 < layout.getLineBottom(i8)) {
                    break;
                } else {
                    i8++;
                }
            }
            if (i8 < 0) {
                return -1;
            }
            return layout.getOffsetForHorizontal(i8, i6);
        }

        @Override // org.telegram.ui.Cells.TextSelectionHelper
        protected void fillLayoutForOffset(int i, LayoutBlock layoutBlock, boolean z) {
            this.arrayList.clear();
            ArticleSelectableView articleSelectableView = (ArticleSelectableView) (z ? this.maybeSelectedView : this.selectedView);
            if (articleSelectableView == null) {
                layoutBlock.layout = null;
                return;
            }
            articleSelectableView.fillTextLayoutBlocks(this.arrayList);
            if (z) {
                layoutBlock.layout = this.arrayList.get(this.maybeTextIndex).getLayout();
            } else {
                int i2 = this.startPeek ? this.startViewChildPosition : this.endViewChildPosition;
                if (i2 < 0 || i2 >= this.arrayList.size()) {
                    layoutBlock.layout = null;
                    return;
                }
                layoutBlock.layout = this.arrayList.get(i2).getLayout();
            }
            layoutBlock.yOffset = 0.0f;
            layoutBlock.xOffset = 0.0f;
        }

        @Override // org.telegram.ui.Cells.TextSelectionHelper
        protected int getLineHeight() {
            if (this.selectedView == null) {
                return 0;
            }
            this.arrayList.clear();
            ((ArticleSelectableView) this.selectedView).fillTextLayoutBlocks(this.arrayList);
            int i = this.startPeek ? this.startViewChildPosition : this.endViewChildPosition;
            if (i < 0 || i >= this.arrayList.size()) {
                return 0;
            }
            StaticLayout layout = this.arrayList.get(i).getLayout();
            int i2 = Integer.MAX_VALUE;
            for (int i3 = 0; i3 < layout.getLineCount(); i3++) {
                int lineBottom = layout.getLineBottom(i3) - layout.getLineTop(i3);
                if (lineBottom < i2) {
                    i2 = lineBottom;
                }
            }
            return i2;
        }

        public void trySelect(View view) {
            if (this.maybeSelectedView != null) {
                this.startSelectionRunnable.run();
            }
        }

        public void setMaybeView(int i, int i2, View view) {
            if (view instanceof ArticleSelectableView) {
                this.capturedX = i;
                this.capturedY = i2;
                ArticleSelectableView articleSelectableView = (ArticleSelectableView) view;
                this.maybeSelectedView = articleSelectableView;
                int findClosestLayoutIndex = findClosestLayoutIndex(i, i2, articleSelectableView);
                this.maybeTextIndex = findClosestLayoutIndex;
                if (findClosestLayoutIndex < 0) {
                    this.maybeSelectedView = null;
                    return;
                }
                this.maybeTextX = this.arrayList.get(findClosestLayoutIndex).getX();
                this.maybeTextY = this.arrayList.get(this.maybeTextIndex).getY();
            }
        }

        private int findClosestLayoutIndex(int i, int i2, ArticleSelectableView articleSelectableView) {
            int i3 = 0;
            if (articleSelectableView instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) articleSelectableView;
                for (int i4 = 0; i4 < viewGroup.getChildCount(); i4++) {
                    View childAt = viewGroup.getChildAt(i4);
                    if (childAt instanceof ArticleSelectableView) {
                        float f = i2;
                        if (f > childAt.getY() && f < childAt.getY() + childAt.getHeight()) {
                            return findClosestLayoutIndex((int) (i - childAt.getX()), (int) (f - childAt.getY()), (ArticleSelectableView) childAt);
                        }
                    }
                }
            }
            this.arrayList.clear();
            articleSelectableView.fillTextLayoutBlocks(this.arrayList);
            if (this.arrayList.isEmpty()) {
                return -1;
            }
            int size = this.arrayList.size() - 1;
            int i5 = Integer.MAX_VALUE;
            int i6 = Integer.MAX_VALUE;
            int i7 = -1;
            while (true) {
                if (size < 0) {
                    i3 = i6;
                    size = i7;
                    break;
                }
                TextLayoutBlock textLayoutBlock = this.arrayList.get(size);
                int y = textLayoutBlock.getY();
                int height = textLayoutBlock.getLayout().getHeight() + y;
                if (i2 >= y && i2 < height) {
                    break;
                }
                int min = Math.min(Math.abs(i2 - y), Math.abs(i2 - height));
                if (min < i6) {
                    i7 = size;
                    i6 = min;
                }
                size--;
            }
            if (size < 0) {
                return -1;
            }
            int row = this.arrayList.get(size).getRow();
            if (row > 0 && i3 < AndroidUtilities.dp(24.0f)) {
                for (int size2 = this.arrayList.size() - 1; size2 >= 0; size2--) {
                    TextLayoutBlock textLayoutBlock2 = this.arrayList.get(size2);
                    if (textLayoutBlock2.getRow() == row) {
                        int x = textLayoutBlock2.getX();
                        int x2 = textLayoutBlock2.getX() + textLayoutBlock2.getLayout().getWidth();
                        if (i >= x && i <= x2) {
                            return size2;
                        }
                        int min2 = Math.min(Math.abs(i - x), Math.abs(i - x2));
                        if (min2 < i5) {
                            size = size2;
                            i5 = min2;
                        }
                    }
                }
            }
            return size;
        }

        public void draw(Canvas canvas, ArticleSelectableView articleSelectableView, int i) {
            this.selectionPaint.setColor(getThemedColor("chat_inTextSelectionHighlight"));
            this.selectionHandlePaint.setColor(getThemedColor("chat_inTextSelectionHighlight"));
            int adapterPosition = getAdapterPosition(articleSelectableView);
            if (adapterPosition < 0) {
                return;
            }
            this.arrayList.clear();
            articleSelectableView.fillTextLayoutBlocks(this.arrayList);
            if (this.arrayList.isEmpty()) {
                return;
            }
            TextLayoutBlock textLayoutBlock = this.arrayList.get(i);
            int i2 = this.endViewOffset;
            int length = textLayoutBlock.getLayout().getText().length();
            int i3 = i2 > length ? length : i2;
            int i4 = this.startViewPosition;
            if (adapterPosition == i4 && adapterPosition == this.endViewPosition) {
                int i5 = this.startViewChildPosition;
                int i6 = this.endViewChildPosition;
                if (i5 == i6 && i5 == i) {
                    drawSelection(canvas, textLayoutBlock.getLayout(), this.startViewOffset, i3, true, true);
                } else if (i == i5) {
                    drawSelection(canvas, textLayoutBlock.getLayout(), this.startViewOffset, length, true, false);
                } else if (i == i6) {
                    drawSelection(canvas, textLayoutBlock.getLayout(), 0, i3, false, true);
                } else if (i <= i5 || i >= i6) {
                } else {
                    drawSelection(canvas, textLayoutBlock.getLayout(), 0, length, false, false);
                }
            } else if (adapterPosition == i4 && this.startViewChildPosition == i) {
                drawSelection(canvas, textLayoutBlock.getLayout(), this.startViewOffset, length, true, false);
            } else {
                int i7 = this.endViewPosition;
                if (adapterPosition == i7 && this.endViewChildPosition == i) {
                    drawSelection(canvas, textLayoutBlock.getLayout(), 0, i3, false, true);
                } else if ((adapterPosition <= i4 || adapterPosition >= i7) && ((adapterPosition != i4 || i <= this.startViewChildPosition) && (adapterPosition != i7 || i >= this.endViewChildPosition))) {
                } else {
                    drawSelection(canvas, textLayoutBlock.getLayout(), 0, length, false, false);
                }
            }
        }

        private int getAdapterPosition(ArticleSelectableView articleSelectableView) {
            ViewGroup viewGroup;
            View view = (View) articleSelectableView;
            ViewParent parent = view.getParent();
            while (true) {
                viewGroup = this.parentView;
                if (parent != viewGroup && parent != null) {
                    if (!(parent instanceof View)) {
                        parent = null;
                        break;
                    }
                    view = (View) parent;
                    parent = view.getParent();
                } else {
                    break;
                }
            }
            if (parent != null) {
                RecyclerListView recyclerListView = this.parentRecyclerView;
                if (recyclerListView != null) {
                    return recyclerListView.getChildAdapterPosition(view);
                }
                return viewGroup.indexOfChild(view);
            }
            return -1;
        }

        public boolean isSelectable(View view) {
            if (view instanceof ArticleSelectableView) {
                this.arrayList.clear();
                ((ArticleSelectableView) view).fillTextLayoutBlocks(this.arrayList);
                if (!(view instanceof ArticleViewer.BlockTableCell)) {
                    return !this.arrayList.isEmpty();
                }
                return true;
            }
            return false;
        }

        public void onTextSelected(ArticleSelectableView articleSelectableView, ArticleSelectableView articleSelectableView2) {
            int adapterPosition = getAdapterPosition(articleSelectableView);
            if (adapterPosition < 0) {
                return;
            }
            this.endViewPosition = adapterPosition;
            this.startViewPosition = adapterPosition;
            int i = this.maybeTextIndex;
            this.endViewChildPosition = i;
            this.startViewChildPosition = i;
            this.arrayList.clear();
            articleSelectableView.fillTextLayoutBlocks(this.arrayList);
            int size = this.arrayList.size();
            this.childCountByPosition.put(adapterPosition, size);
            for (int i2 = 0; i2 < size; i2++) {
                int i3 = (i2 << 16) + adapterPosition;
                this.textByPosition.put(i3, this.arrayList.get(i2).getLayout().getText());
                this.prefixTextByPosition.put(i3, this.arrayList.get(i2).getPrefix());
            }
        }

        protected void onNewViewSelected(ArticleSelectableView articleSelectableView, ArticleSelectableView articleSelectableView2, int i) {
            int i2;
            int adapterPosition = getAdapterPosition(articleSelectableView2);
            int adapterPosition2 = articleSelectableView != null ? getAdapterPosition(articleSelectableView) : -1;
            invalidate();
            if (!this.movingDirectionSettling || (i2 = this.startViewPosition) != this.endViewPosition) {
                if (this.movingHandleStart) {
                    if (adapterPosition == adapterPosition2) {
                        int i3 = this.endViewChildPosition;
                        if (i <= i3 || adapterPosition < this.endViewPosition) {
                            this.startViewPosition = adapterPosition;
                            this.startViewChildPosition = i;
                            pickStartView();
                            this.startViewOffset = this.selectionEnd;
                        } else {
                            this.endViewPosition = adapterPosition;
                            this.startViewChildPosition = i3;
                            this.endViewChildPosition = i;
                            this.startViewOffset = this.endViewOffset;
                            pickEndView();
                            this.endViewOffset = 0;
                            this.movingHandleStart = false;
                        }
                    } else if (adapterPosition <= this.endViewPosition) {
                        this.startViewPosition = adapterPosition;
                        this.startViewChildPosition = i;
                        pickStartView();
                        this.startViewOffset = this.selectionEnd;
                    } else {
                        this.endViewPosition = adapterPosition;
                        this.startViewChildPosition = this.endViewChildPosition;
                        this.endViewChildPosition = i;
                        this.startViewOffset = this.endViewOffset;
                        pickEndView();
                        this.endViewOffset = 0;
                        this.movingHandleStart = false;
                    }
                } else if (adapterPosition == adapterPosition2) {
                    int i4 = this.startViewChildPosition;
                    if (i >= i4 || adapterPosition > this.startViewPosition) {
                        this.endViewPosition = adapterPosition;
                        this.endViewChildPosition = i;
                        pickEndView();
                        this.endViewOffset = 0;
                    } else {
                        this.startViewPosition = adapterPosition;
                        this.endViewChildPosition = i4;
                        this.startViewChildPosition = i;
                        this.endViewOffset = this.startViewOffset;
                        pickStartView();
                        this.movingHandleStart = true;
                        this.startViewOffset = this.selectionEnd;
                    }
                } else if (adapterPosition >= this.startViewPosition) {
                    this.endViewPosition = adapterPosition;
                    this.endViewChildPosition = i;
                    pickEndView();
                    this.endViewOffset = 0;
                } else {
                    this.startViewPosition = adapterPosition;
                    this.endViewChildPosition = this.startViewChildPosition;
                    this.startViewChildPosition = i;
                    this.endViewOffset = this.startViewOffset;
                    pickStartView();
                    this.movingHandleStart = true;
                    this.startViewOffset = this.selectionEnd;
                }
            } else if (adapterPosition == i2) {
                if (i < this.startViewChildPosition) {
                    this.startViewChildPosition = i;
                    pickStartView();
                    this.movingHandleStart = true;
                    int i5 = this.selectionEnd;
                    this.startViewOffset = i5;
                    this.selectionStart = i5 - 1;
                } else {
                    this.endViewChildPosition = i;
                    pickEndView();
                    this.movingHandleStart = false;
                    this.endViewOffset = 0;
                }
            } else if (adapterPosition < i2) {
                this.startViewPosition = adapterPosition;
                this.startViewChildPosition = i;
                pickStartView();
                this.movingHandleStart = true;
                int i6 = this.selectionEnd;
                this.startViewOffset = i6;
                this.selectionStart = i6 - 1;
            } else {
                this.endViewPosition = adapterPosition;
                this.endViewChildPosition = i;
                pickEndView();
                this.movingHandleStart = false;
                this.endViewOffset = 0;
            }
            this.arrayList.clear();
            articleSelectableView2.fillTextLayoutBlocks(this.arrayList);
            int size = this.arrayList.size();
            this.childCountByPosition.put(adapterPosition, size);
            for (int i7 = 0; i7 < size; i7++) {
                int i8 = (i7 << 16) + adapterPosition;
                this.textByPosition.put(i8, this.arrayList.get(i7).getLayout().getText());
                this.prefixTextByPosition.put(i8, this.arrayList.get(i7).getPrefix());
            }
        }

        @Override // org.telegram.ui.Cells.TextSelectionHelper
        protected void pickEndView() {
            ArticleSelectableView articleSelectableView;
            if (!isSelectionMode()) {
                return;
            }
            this.startPeek = false;
            int i = this.endViewPosition;
            if (i < 0) {
                return;
            }
            LinearLayoutManager linearLayoutManager = this.layoutManager;
            if (linearLayoutManager != null) {
                articleSelectableView = (ArticleSelectableView) linearLayoutManager.findViewByPosition(i);
            } else {
                articleSelectableView = i < this.parentView.getChildCount() ? (ArticleSelectableView) this.parentView.getChildAt(this.endViewPosition) : null;
            }
            if (articleSelectableView == null) {
                this.selectedView = null;
                return;
            }
            this.selectedView = articleSelectableView;
            if (this.startViewPosition != this.endViewPosition) {
                this.selectionStart = 0;
            } else if (this.startViewChildPosition != this.endViewChildPosition) {
                this.selectionStart = 0;
            } else {
                this.selectionStart = this.startViewOffset;
            }
            this.selectionEnd = this.endViewOffset;
            CharSequence text = getText(articleSelectableView, false);
            if (this.selectionEnd > text.length()) {
                this.selectionEnd = text.length();
            }
            this.arrayList.clear();
            ((ArticleSelectableView) this.selectedView).fillTextLayoutBlocks(this.arrayList);
            if (this.arrayList.isEmpty()) {
                return;
            }
            this.textX = this.arrayList.get(this.endViewChildPosition).getX();
            this.textY = this.arrayList.get(this.endViewChildPosition).getY();
        }

        @Override // org.telegram.ui.Cells.TextSelectionHelper
        protected void pickStartView() {
            ArticleSelectableView articleSelectableView;
            if (!isSelectionMode()) {
                return;
            }
            this.startPeek = true;
            int i = this.startViewPosition;
            if (i < 0) {
                return;
            }
            LinearLayoutManager linearLayoutManager = this.layoutManager;
            if (linearLayoutManager != null) {
                articleSelectableView = (ArticleSelectableView) linearLayoutManager.findViewByPosition(i);
            } else {
                articleSelectableView = this.endViewPosition < this.parentView.getChildCount() ? (ArticleSelectableView) this.parentView.getChildAt(this.startViewPosition) : null;
            }
            if (articleSelectableView == null) {
                this.selectedView = null;
                return;
            }
            this.selectedView = articleSelectableView;
            if (this.startViewPosition != this.endViewPosition) {
                this.selectionEnd = getText(articleSelectableView, false).length();
            } else if (this.startViewChildPosition != this.endViewChildPosition) {
                this.selectionEnd = getText(articleSelectableView, false).length();
            } else {
                this.selectionEnd = this.endViewOffset;
            }
            this.selectionStart = this.startViewOffset;
            this.arrayList.clear();
            ((ArticleSelectableView) this.selectedView).fillTextLayoutBlocks(this.arrayList);
            if (this.arrayList.isEmpty()) {
                return;
            }
            this.textX = this.arrayList.get(this.startViewChildPosition).getX();
            this.textY = this.arrayList.get(this.startViewChildPosition).getY();
        }

        @Override // org.telegram.ui.Cells.TextSelectionHelper
        protected void onOffsetChanged() {
            int adapterPosition = getAdapterPosition((ArticleSelectableView) this.selectedView);
            int i = this.startPeek ? this.startViewChildPosition : this.endViewChildPosition;
            if (adapterPosition == this.startViewPosition && i == this.startViewChildPosition) {
                this.startViewOffset = this.selectionStart;
            }
            if (adapterPosition == this.endViewPosition && i == this.endViewChildPosition) {
                this.endViewOffset = this.selectionEnd;
            }
        }

        @Override // org.telegram.ui.Cells.TextSelectionHelper
        public void invalidate() {
            super.invalidate();
            for (int i = 0; i < this.parentView.getChildCount(); i++) {
                this.parentView.getChildAt(i).invalidate();
            }
        }

        @Override // org.telegram.ui.Cells.TextSelectionHelper
        public void clear(boolean z) {
            super.clear(z);
            this.startViewPosition = -1;
            this.endViewPosition = -1;
            this.startViewChildPosition = -1;
            this.endViewChildPosition = -1;
            this.textByPosition.clear();
            this.childCountByPosition.clear();
        }

        @Override // org.telegram.ui.Cells.TextSelectionHelper
        protected CharSequence getSelectedText() {
            IgnoreCopySpannable[] ignoreCopySpannableArr;
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            int i = this.startViewPosition;
            while (true) {
                int i2 = this.endViewPosition;
                if (i > i2) {
                    break;
                }
                int i3 = this.startViewPosition;
                if (i == i3) {
                    int i4 = i3 == i2 ? this.endViewChildPosition : this.childCountByPosition.get(i) - 1;
                    for (int i5 = this.startViewChildPosition; i5 <= i4; i5++) {
                        int i6 = (i5 << 16) + i;
                        CharSequence charSequence = this.textByPosition.get(i6);
                        if (charSequence != null) {
                            int i7 = this.startViewPosition;
                            int i8 = this.endViewPosition;
                            if (i7 == i8 && i5 == this.endViewChildPosition && i5 == this.startViewChildPosition) {
                                int i9 = this.endViewOffset;
                                int i10 = this.startViewOffset;
                                if (i9 >= i10) {
                                    i10 = i9;
                                    i9 = i10;
                                }
                                if (i9 < charSequence.length()) {
                                    if (i10 > charSequence.length()) {
                                        i10 = charSequence.length();
                                    }
                                    spannableStringBuilder.append(charSequence.subSequence(i9, i10));
                                    spannableStringBuilder.append('\n');
                                }
                            } else if (i7 == i8 && i5 == this.endViewChildPosition) {
                                CharSequence charSequence2 = this.prefixTextByPosition.get(i6);
                                if (charSequence2 != null) {
                                    spannableStringBuilder.append(charSequence2).append(' ');
                                }
                                int i11 = this.endViewOffset;
                                if (i11 > charSequence.length()) {
                                    i11 = charSequence.length();
                                }
                                spannableStringBuilder.append(charSequence.subSequence(0, i11));
                                spannableStringBuilder.append('\n');
                            } else if (i5 == this.startViewChildPosition) {
                                int i12 = this.startViewOffset;
                                if (i12 < charSequence.length()) {
                                    spannableStringBuilder.append(charSequence.subSequence(i12, charSequence.length()));
                                    spannableStringBuilder.append('\n');
                                }
                            } else {
                                CharSequence charSequence3 = this.prefixTextByPosition.get(i6);
                                if (charSequence3 != null) {
                                    spannableStringBuilder.append(charSequence3).append(' ');
                                }
                                spannableStringBuilder.append(charSequence);
                                spannableStringBuilder.append('\n');
                            }
                        }
                    }
                } else if (i == i2) {
                    for (int i13 = 0; i13 <= this.endViewChildPosition; i13++) {
                        int i14 = (i13 << 16) + i;
                        CharSequence charSequence4 = this.textByPosition.get(i14);
                        if (charSequence4 != null) {
                            if (this.startViewPosition == this.endViewPosition && i13 == this.endViewChildPosition && i13 == this.startViewChildPosition) {
                                int i15 = this.endViewOffset;
                                int i16 = this.startViewOffset;
                                if (i16 < charSequence4.length()) {
                                    if (i15 > charSequence4.length()) {
                                        i15 = charSequence4.length();
                                    }
                                    spannableStringBuilder.append(charSequence4.subSequence(i16, i15));
                                    spannableStringBuilder.append('\n');
                                }
                            } else if (i13 == this.endViewChildPosition) {
                                CharSequence charSequence5 = this.prefixTextByPosition.get(i14);
                                if (charSequence5 != null) {
                                    spannableStringBuilder.append(charSequence5).append(' ');
                                }
                                int i17 = this.endViewOffset;
                                if (i17 > charSequence4.length()) {
                                    i17 = charSequence4.length();
                                }
                                spannableStringBuilder.append(charSequence4.subSequence(0, i17));
                                spannableStringBuilder.append('\n');
                            } else {
                                CharSequence charSequence6 = this.prefixTextByPosition.get(i14);
                                if (charSequence6 != null) {
                                    spannableStringBuilder.append(charSequence6).append(' ');
                                }
                                spannableStringBuilder.append(charSequence4);
                                spannableStringBuilder.append('\n');
                            }
                        }
                    }
                } else {
                    int i18 = this.childCountByPosition.get(i);
                    for (int i19 = this.startViewChildPosition; i19 < i18; i19++) {
                        int i20 = (i19 << 16) + i;
                        CharSequence charSequence7 = this.prefixTextByPosition.get(i20);
                        if (charSequence7 != null) {
                            spannableStringBuilder.append(charSequence7).append(' ');
                        }
                        spannableStringBuilder.append(this.textByPosition.get(i20));
                        spannableStringBuilder.append('\n');
                    }
                }
                i++;
            }
            if (spannableStringBuilder.length() > 0) {
                for (IgnoreCopySpannable ignoreCopySpannable : (IgnoreCopySpannable[]) spannableStringBuilder.getSpans(0, spannableStringBuilder.length() - 1, IgnoreCopySpannable.class)) {
                    spannableStringBuilder.delete(spannableStringBuilder.getSpanStart(ignoreCopySpannable), spannableStringBuilder.getSpanEnd(ignoreCopySpannable));
                }
                return spannableStringBuilder.subSequence(0, spannableStringBuilder.length() - 1);
            }
            return null;
        }

        @Override // org.telegram.ui.Cells.TextSelectionHelper
        protected boolean selectLayout(int i, int i2) {
            if (!this.multiselect) {
                return false;
            }
            if (i2 <= ((ArticleSelectableView) this.selectedView).getTop() || i2 >= ((ArticleSelectableView) this.selectedView).getBottom()) {
                int childCount = this.parentView.getChildCount();
                for (int i3 = 0; i3 < childCount; i3++) {
                    if (isSelectable(this.parentView.getChildAt(i3))) {
                        ArticleSelectableView articleSelectableView = (ArticleSelectableView) this.parentView.getChildAt(i3);
                        if (i2 > articleSelectableView.getTop() && i2 < articleSelectableView.getBottom()) {
                            int findClosestLayoutIndex = findClosestLayoutIndex((int) (i - articleSelectableView.getX()), (int) (i2 - articleSelectableView.getY()), articleSelectableView);
                            if (findClosestLayoutIndex < 0) {
                                return false;
                            }
                            onNewViewSelected((ArticleSelectableView) this.selectedView, articleSelectableView, findClosestLayoutIndex);
                            this.selectedView = articleSelectableView;
                            return true;
                        }
                    }
                }
                return false;
            }
            int i4 = this.startPeek ? this.startViewChildPosition : this.endViewChildPosition;
            int findClosestLayoutIndex2 = findClosestLayoutIndex((int) (i - ((ArticleSelectableView) this.selectedView).getX()), (int) (i2 - ((ArticleSelectableView) this.selectedView).getY()), (ArticleSelectableView) this.selectedView);
            if (findClosestLayoutIndex2 == i4 || findClosestLayoutIndex2 < 0) {
                return false;
            }
            Cell cell = this.selectedView;
            onNewViewSelected((ArticleSelectableView) cell, (ArticleSelectableView) cell, findClosestLayoutIndex2);
            return true;
        }

        @Override // org.telegram.ui.Cells.TextSelectionHelper
        protected boolean canSelect(int i) {
            if (this.startViewPosition == this.endViewPosition && this.startViewChildPosition == this.endViewChildPosition) {
                return super.canSelect(i);
            }
            return true;
        }

        public void jumpToLine(int i, int i2, boolean z, float f, float f2, ArticleSelectableView articleSelectableView) {
            if (z && articleSelectableView == this.selectedView && f2 == f) {
                if (this.movingHandleStart) {
                    this.selectionStart = i;
                    return;
                } else {
                    this.selectionEnd = i;
                    return;
                }
            }
            super.jumpToLine(i, i2, z, f, f2, (float) articleSelectableView);
        }

        @Override // org.telegram.ui.Cells.TextSelectionHelper
        protected boolean canShowActions() {
            LinearLayoutManager linearLayoutManager = this.layoutManager;
            if (linearLayoutManager == null) {
                return true;
            }
            int findFirstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
            int findLastVisibleItemPosition = this.layoutManager.findLastVisibleItemPosition();
            int i = this.startViewPosition;
            if ((findFirstVisibleItemPosition >= i && findFirstVisibleItemPosition <= this.endViewPosition) || (findLastVisibleItemPosition >= i && findLastVisibleItemPosition <= this.endViewPosition)) {
                return true;
            }
            return i >= findFirstVisibleItemPosition && this.endViewPosition <= findLastVisibleItemPosition;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class PathCopyTo extends Path {
        private Path destination;

        public PathCopyTo(Path path) {
            this.destination = path;
        }

        @Override // android.graphics.Path
        public void reset() {
            super.reset();
        }

        @Override // android.graphics.Path
        public void addRect(float f, float f2, float f3, float f4, Path.Direction direction) {
            this.destination.addRect(f, f2, f3, f4, direction);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class PathWithSavedBottom extends Path {
        float lastBottom;

        private PathWithSavedBottom() {
            this.lastBottom = 0.0f;
        }

        @Override // android.graphics.Path
        public void reset() {
            super.reset();
            this.lastBottom = 0.0f;
        }

        @Override // android.graphics.Path
        public void addRect(float f, float f2, float f3, float f4, Path.Direction direction) {
            super.addRect(f, f2, f3, f4, direction);
            if (f4 > this.lastBottom) {
                this.lastBottom = f4;
            }
        }
    }

    /* loaded from: classes3.dex */
    public static class ScalablePath extends Path {
        private static ArrayList<RectF> recycled;
        float lastBottom;
        private ArrayList<RectF> rects;
        private int rectsCount;

        private ScalablePath() {
            this.lastBottom = 0.0f;
            this.rects = new ArrayList<>(1);
            this.rectsCount = 0;
        }

        @Override // android.graphics.Path
        public void reset() {
            super.reset();
            if (recycled == null) {
                recycled = new ArrayList<>(this.rects.size());
            }
            recycled.addAll(this.rects);
            this.rects.clear();
            this.rectsCount = 0;
            this.lastBottom = 0.0f;
        }

        @Override // android.graphics.Path
        public void addRect(float f, float f2, float f3, float f4, Path.Direction direction) {
            RectF rectF;
            ArrayList<RectF> arrayList = recycled;
            if (arrayList != null && arrayList.size() > 0) {
                rectF = recycled.remove(0);
            } else {
                rectF = new RectF();
            }
            rectF.set(f, f2, f3, f4);
            this.rects.add(rectF);
            this.rectsCount++;
            super.addRect(f, f2, f3, f4, direction);
            if (f4 > this.lastBottom) {
                this.lastBottom = f4;
            }
        }
    }

    public void setKeyboardSize(int i) {
        this.keyboardSize = i;
        invalidate();
    }

    public int getThemedColor(String str) {
        return Theme.getColor(str);
    }
}
