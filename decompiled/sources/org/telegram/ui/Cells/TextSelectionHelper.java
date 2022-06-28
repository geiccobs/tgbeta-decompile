package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.ClipData;
import android.content.ClipboardManager;
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
import com.google.firebase.messaging.Constants;
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
/* loaded from: classes4.dex */
public abstract class TextSelectionHelper<Cell extends SelectableView> {
    private static final int TRANSLATE = 3;
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
    protected Integer selectedCellEditDate;
    protected int selectedCellId;
    protected Cell selectedView;
    private boolean snap;
    protected TextSelectionHelper<Cell>.TextSelectionOverlay textSelectionOverlay;
    protected int textX;
    protected int textY;
    private int topOffset;
    private boolean tryCapture;
    protected int[] tmpCoord = new int[2];
    protected PathWithSavedBottom path = new PathWithSavedBottom();
    protected Paint selectionPaint = new Paint(1);
    protected Paint selectionHandlePaint = new Paint(1);
    protected Path selectionPath = new Path();
    protected Path selectionHandlePath = new Path();
    protected PathCopyTo selectionPathMirror = new PathCopyTo(this.selectionPath);
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
            int dy;
            if (TextSelectionHelper.this.scrolling && TextSelectionHelper.this.parentRecyclerView != null) {
                if (TextSelectionHelper.this.multiselect && TextSelectionHelper.this.selectedView == null) {
                    dy = AndroidUtilities.dp(8.0f);
                } else if (TextSelectionHelper.this.selectedView != null) {
                    dy = TextSelectionHelper.this.getLineHeight() >> 1;
                } else {
                    return;
                }
                if (!TextSelectionHelper.this.multiselect) {
                    if (TextSelectionHelper.this.scrollDown) {
                        if (TextSelectionHelper.this.selectedView.getBottom() - dy < TextSelectionHelper.this.parentView.getMeasuredHeight() - TextSelectionHelper.this.getParentBottomPadding()) {
                            dy = (TextSelectionHelper.this.selectedView.getBottom() - TextSelectionHelper.this.parentView.getMeasuredHeight()) + TextSelectionHelper.this.getParentBottomPadding();
                        }
                    } else if (TextSelectionHelper.this.selectedView.getTop() + dy > TextSelectionHelper.this.getParentTopPadding()) {
                        dy = (-TextSelectionHelper.this.selectedView.getTop()) + TextSelectionHelper.this.getParentTopPadding();
                    }
                }
                TextSelectionHelper.this.parentRecyclerView.scrollBy(0, TextSelectionHelper.this.scrollDown ? dy : -dy);
                AndroidUtilities.runOnUIThread(this);
            }
        }
    };
    final Runnable startSelectionRunnable = new Runnable() { // from class: org.telegram.ui.Cells.TextSelectionHelper.2
        @Override // java.lang.Runnable
        public void run() {
            int y;
            int x;
            if (TextSelectionHelper.this.maybeSelectedView == null || TextSelectionHelper.this.textSelectionOverlay == null) {
                return;
            }
            Cell oldView = TextSelectionHelper.this.selectedView;
            Cell newView = TextSelectionHelper.this.maybeSelectedView;
            TextSelectionHelper textSelectionHelper = TextSelectionHelper.this;
            CharSequence text = textSelectionHelper.getText(textSelectionHelper.maybeSelectedView, true);
            if (TextSelectionHelper.this.parentRecyclerView != null) {
                TextSelectionHelper.this.parentRecyclerView.cancelClickRunnables(false);
            }
            int x2 = TextSelectionHelper.this.capturedX;
            int y2 = TextSelectionHelper.this.capturedY;
            if (TextSelectionHelper.this.textArea.isEmpty()) {
                x = x2;
                y = y2;
            } else {
                if (x2 > TextSelectionHelper.this.textArea.right) {
                    x2 = TextSelectionHelper.this.textArea.right - 1;
                }
                if (x2 < TextSelectionHelper.this.textArea.left) {
                    x2 = TextSelectionHelper.this.textArea.left + 1;
                }
                if (y2 < TextSelectionHelper.this.textArea.top) {
                    y2 = TextSelectionHelper.this.textArea.top + 1;
                }
                if (y2 > TextSelectionHelper.this.textArea.bottom) {
                    y2 = TextSelectionHelper.this.textArea.bottom - 1;
                }
                x = x2;
                y = y2;
            }
            TextSelectionHelper textSelectionHelper2 = TextSelectionHelper.this;
            int offset = textSelectionHelper2.getCharOffsetFromCord(x, y, textSelectionHelper2.maybeTextX, TextSelectionHelper.this.maybeTextY, newView, true);
            if (offset >= text.length()) {
                TextSelectionHelper textSelectionHelper3 = TextSelectionHelper.this;
                textSelectionHelper3.fillLayoutForOffset(offset, textSelectionHelper3.layoutBlock, true);
                if (TextSelectionHelper.this.layoutBlock.layout == null) {
                    TextSelectionHelper textSelectionHelper4 = TextSelectionHelper.this;
                    textSelectionHelper4.selectionEnd = -1;
                    textSelectionHelper4.selectionStart = -1;
                    return;
                }
                int endLine = TextSelectionHelper.this.layoutBlock.layout.getLineCount() - 1;
                int x3 = x - TextSelectionHelper.this.maybeTextX;
                if (x3 < TextSelectionHelper.this.layoutBlock.layout.getLineRight(endLine) + AndroidUtilities.dp(4.0f) && x3 > TextSelectionHelper.this.layoutBlock.layout.getLineLeft(endLine)) {
                    offset = text.length() - 1;
                }
            }
            if (offset >= 0 && offset < text.length() && text.charAt(offset) != '\n') {
                int maybeTextX = TextSelectionHelper.this.maybeTextX;
                int maybeTextY = TextSelectionHelper.this.maybeTextY;
                TextSelectionHelper.this.clear();
                TextSelectionHelper.this.textSelectionOverlay.setVisibility(0);
                TextSelectionHelper.this.onTextSelected(newView, oldView);
                TextSelectionHelper.this.selectionStart = offset;
                TextSelectionHelper textSelectionHelper5 = TextSelectionHelper.this;
                textSelectionHelper5.selectionEnd = textSelectionHelper5.selectionStart;
                if (text instanceof Spanned) {
                    Emoji.EmojiSpan[] spans = (Emoji.EmojiSpan[]) ((Spanned) text).getSpans(0, text.length(), Emoji.EmojiSpan.class);
                    int length = spans.length;
                    int i = 0;
                    while (true) {
                        if (i >= length) {
                            break;
                        }
                        Emoji.EmojiSpan emojiSpan = spans[i];
                        int s = ((Spanned) text).getSpanStart(emojiSpan);
                        int e = ((Spanned) text).getSpanEnd(emojiSpan);
                        if (offset < s || offset > e) {
                            i++;
                        } else {
                            TextSelectionHelper.this.selectionStart = s;
                            TextSelectionHelper.this.selectionEnd = e;
                            break;
                        }
                    }
                }
                if (TextSelectionHelper.this.selectionStart == TextSelectionHelper.this.selectionEnd) {
                    while (TextSelectionHelper.this.selectionStart > 0 && TextSelectionHelper.isInterruptedCharacter(text.charAt(TextSelectionHelper.this.selectionStart - 1))) {
                        TextSelectionHelper.this.selectionStart--;
                    }
                    while (TextSelectionHelper.this.selectionEnd < text.length() && TextSelectionHelper.isInterruptedCharacter(text.charAt(TextSelectionHelper.this.selectionEnd))) {
                        TextSelectionHelper.this.selectionEnd++;
                    }
                }
                TextSelectionHelper.this.textX = maybeTextX;
                TextSelectionHelper.this.textY = maybeTextY;
                TextSelectionHelper.this.selectedView = newView;
                TextSelectionHelper.this.textSelectionOverlay.performHapticFeedback(0, 1);
                TextSelectionHelper.this.showActions();
                TextSelectionHelper.this.invalidate();
                if (oldView != null) {
                    oldView.invalidate();
                }
                if (TextSelectionHelper.this.callback != null) {
                    TextSelectionHelper.this.callback.onStateChanged(true);
                }
                TextSelectionHelper.this.movingHandle = true;
                TextSelectionHelper.this.movingDirectionSettling = true;
                TextSelectionHelper.this.isOneTouch = true;
                TextSelectionHelper.this.movingOffsetY = 0.0f;
                TextSelectionHelper.this.movingOffsetX = 0.0f;
                TextSelectionHelper.this.onOffsetChanged();
            }
            TextSelectionHelper.this.tryCapture = false;
        }
    };
    private OnTranslateListener onTranslateListener = null;
    private final Runnable hideActionsRunnable = new Runnable() { // from class: org.telegram.ui.Cells.TextSelectionHelper.3
        @Override // java.lang.Runnable
        public void run() {
            if (Build.VERSION.SDK_INT >= 23 && TextSelectionHelper.this.actionMode != null && !TextSelectionHelper.this.actionsIsShowing) {
                TextSelectionHelper.this.actionMode.hide(Long.MAX_VALUE);
                AndroidUtilities.runOnUIThread(TextSelectionHelper.this.hideActionsRunnable, 1000L);
            }
        }
    };
    private final ScalablePath tempPath2 = new ScalablePath();
    private int longpressDelay = ViewConfiguration.getLongPressTimeout();
    private int touchSlop = ViewConfiguration.get(ApplicationLoader.applicationContext).getScaledTouchSlop();

    /* loaded from: classes4.dex */
    public interface ArticleSelectableView extends SelectableView {
        void fillTextLayoutBlocks(ArrayList<TextLayoutBlock> arrayList);
    }

    /* loaded from: classes4.dex */
    public static class IgnoreCopySpannable {
    }

    /* loaded from: classes4.dex */
    public interface OnTranslateListener {
        void run(CharSequence charSequence, String str, String str2, Runnable runnable);
    }

    /* loaded from: classes4.dex */
    public interface SelectableView {
        int getBottom();

        int getMeasuredWidth();

        int getTop();

        float getX();

        float getY();

        void invalidate();
    }

    protected abstract void fillLayoutForOffset(int i, LayoutBlock layoutBlock, boolean z);

    protected abstract int getCharOffsetFromCord(int i, int i2, int i3, int i4, Cell cell, boolean z);

    protected abstract int getLineHeight();

    protected abstract CharSequence getText(Cell cell, boolean z);

    protected abstract void onTextSelected(Cell cell, Cell cell2);

    public TextSelectionHelper() {
        Paint paint = this.selectionPaint;
        float dp = AndroidUtilities.dp(6.0f);
        this.cornerRadius = dp;
        paint.setPathEffect(new CornerPathEffect(dp));
    }

    public void setOnTranslate(OnTranslateListener listener) {
        this.onTranslateListener = listener;
    }

    public void setParentView(ViewGroup view) {
        if (view instanceof RecyclerListView) {
            this.parentRecyclerView = (RecyclerListView) view;
        }
        this.parentView = view;
    }

    public void setMaybeTextCord(int x, int y) {
        this.maybeTextX = x;
        this.maybeTextY = y;
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case 0:
                this.capturedX = (int) event.getX();
                this.capturedY = (int) event.getY();
                this.tryCapture = false;
                this.textArea.inset(-AndroidUtilities.dp(8.0f), -AndroidUtilities.dp(8.0f));
                if (this.textArea.contains(this.capturedX, this.capturedY)) {
                    this.textArea.inset(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
                    int x = this.capturedX;
                    int y = this.capturedY;
                    if (x > this.textArea.right) {
                        x = this.textArea.right - 1;
                    }
                    if (x < this.textArea.left) {
                        x = this.textArea.left + 1;
                    }
                    if (y < this.textArea.top) {
                        y = this.textArea.top + 1;
                    }
                    if (y > this.textArea.bottom) {
                        y = this.textArea.bottom - 1;
                    }
                    int offset = getCharOffsetFromCord(x, y, this.maybeTextX, this.maybeTextY, this.maybeSelectedView, true);
                    CharSequence text = getText(this.maybeSelectedView, true);
                    if (offset >= text.length()) {
                        fillLayoutForOffset(offset, this.layoutBlock, true);
                        if (this.layoutBlock.layout == null) {
                            this.tryCapture = false;
                            return false;
                        }
                        int endLine = this.layoutBlock.layout.getLineCount() - 1;
                        int x2 = x - this.maybeTextX;
                        if (x2 < this.layoutBlock.layout.getLineRight(endLine) + AndroidUtilities.dp(4.0f) && x2 > this.layoutBlock.layout.getLineLeft(endLine)) {
                            offset = text.length() - 1;
                        }
                    }
                    if (offset >= 0 && offset < text.length() && text.charAt(offset) != '\n') {
                        AndroidUtilities.runOnUIThread(this.startSelectionRunnable, this.longpressDelay);
                        this.tryCapture = true;
                    }
                }
                return this.tryCapture;
            case 1:
            case 3:
                AndroidUtilities.cancelRunOnUIThread(this.startSelectionRunnable);
                this.tryCapture = false;
                return false;
            case 2:
                int y2 = (int) event.getY();
                int x3 = (int) event.getX();
                int i = this.capturedY;
                int i2 = (i - y2) * (i - y2);
                int i3 = this.capturedX;
                int r = i2 + ((i3 - x3) * (i3 - x3));
                if (r > this.touchSlop) {
                    AndroidUtilities.cancelRunOnUIThread(this.startSelectionRunnable);
                    this.tryCapture = false;
                }
                return this.tryCapture;
            default:
                return false;
        }
    }

    public void hideMagnifier() {
        Magnifier magnifier;
        if (Build.VERSION.SDK_INT >= 28 && (magnifier = this.magnifier) != null) {
            magnifier.dismiss();
            this.magnifier = null;
        }
    }

    public void showMagnifier(int x) {
        int endLine;
        int startLine;
        if (Build.VERSION.SDK_INT < 28 || this.selectedView == null || this.isOneTouch || !this.movingHandle || this.textSelectionOverlay == null) {
            return;
        }
        int offset = this.movingHandleStart ? this.selectionStart : this.selectionEnd;
        fillLayoutForOffset(offset, this.layoutBlock);
        StaticLayout layout = this.layoutBlock.layout;
        if (layout == null) {
            return;
        }
        int line = layout.getLineForOffset(offset);
        int lineHeight = layout.getLineBottom(line) - layout.getLineTop(line);
        int newY = (int) (((((int) ((layout.getLineTop(line) + this.textY) + this.selectedView.getY())) - lineHeight) - AndroidUtilities.dp(8.0f)) + this.layoutBlock.yOffset);
        Cell cell = this.selectedView;
        if (cell instanceof ArticleViewer.BlockTableCell) {
            startLine = (int) cell.getX();
            endLine = ((int) this.selectedView.getX()) + this.selectedView.getMeasuredWidth();
        } else {
            startLine = (int) (cell.getX() + this.textX + layout.getLineLeft(line));
            endLine = (int) (this.selectedView.getX() + this.textX + layout.getLineRight(line));
        }
        if (x < startLine) {
            x = startLine;
        } else if (x > endLine) {
            x = endLine;
        }
        if (this.magnifierY != newY) {
            this.magnifierY = newY;
            this.magnifierDy = (newY - this.magnifierYanimated) / 200.0f;
        }
        if (this.magnifierX != x) {
            this.magnifierX = x;
            this.magnifierDx = (x - this.magnifierXanimated) / 100.0f;
        }
        if (this.magnifier == null) {
            this.magnifier = new Magnifier(this.textSelectionOverlay);
            this.magnifierYanimated = this.magnifierY;
            this.magnifierXanimated = this.magnifierX;
        }
        float f = this.magnifierYanimated;
        float f2 = this.magnifierY;
        if (f != f2) {
            this.magnifierYanimated = f + (this.magnifierDy * 16.0f);
        }
        float f3 = this.magnifierDy;
        if (f3 > 0.0f && this.magnifierYanimated > f2) {
            this.magnifierYanimated = f2;
        } else if (f3 < 0.0f && this.magnifierYanimated < f2) {
            this.magnifierYanimated = f2;
        }
        float f4 = this.magnifierXanimated;
        float f5 = this.magnifierX;
        if (f4 != f5) {
            this.magnifierXanimated = f4 + (this.magnifierDx * 16.0f);
        }
        float f6 = this.magnifierDx;
        if (f6 > 0.0f && this.magnifierXanimated > f5) {
            this.magnifierXanimated = f5;
        } else if (f6 < 0.0f && this.magnifierXanimated < f5) {
            this.magnifierXanimated = f5;
        }
        this.magnifier.show(this.magnifierXanimated, this.magnifierYanimated + (lineHeight * 1.5f) + AndroidUtilities.dp(8.0f));
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
                TextSelectionHelper.this.m1676xb705f8cf(valueAnimator2);
            }
        });
        this.handleViewAnimator.setDuration(Math.abs(1.0f - this.handleViewProgress) * 250.0f);
        this.handleViewAnimator.start();
    }

    /* renamed from: lambda$showHandleViews$0$org-telegram-ui-Cells-TextSelectionHelper */
    public /* synthetic */ void m1676xb705f8cf(ValueAnimator animation) {
        this.handleViewProgress = ((Float) animation.getAnimatedValue()).floatValue();
        this.textSelectionOverlay.invalidate();
    }

    public boolean isSelectionMode() {
        return this.selectionStart >= 0 && this.selectionEnd >= 0;
    }

    public void showActions() {
        if (this.textSelectionOverlay == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= 23) {
            if (!this.movingHandle && isSelectionMode() && canShowActions()) {
                if (!this.actionsIsShowing) {
                    if (this.actionMode == null) {
                        FloatingToolbar floatingToolbar = new FloatingToolbar(this.textSelectionOverlay.getContext(), this.textSelectionOverlay, 1, getResourcesProvider());
                        FloatingActionMode floatingActionMode = new FloatingActionMode(this.textSelectionOverlay.getContext(), (ActionMode.Callback2) this.textSelectActionCallback, this.textSelectionOverlay, floatingToolbar);
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
            }
        } else if (!this.showActionsAsPopupAlways) {
            if (this.actionMode == null && isSelectionMode()) {
                this.actionMode = this.textSelectionOverlay.startActionMode(this.textSelectActionCallback);
            }
        } else if (!this.movingHandle && isSelectionMode() && canShowActions()) {
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
                        return TextSelectionHelper.this.m1674lambda$showActions$1$orgtelegramuiCellsTextSelectionHelper(view, motionEvent);
                    }
                });
                this.popupLayout.setShownFromBottom(false);
                TextView textView = new TextView(this.textSelectionOverlay.getContext());
                this.deleteView = textView;
                textView.setBackgroundDrawable(Theme.createSelectorDrawable(getThemedColor(Theme.key_listSelector), 2));
                this.deleteView.setGravity(16);
                this.deleteView.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
                this.deleteView.setTextSize(1, 15.0f);
                this.deleteView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
                this.deleteView.setText(this.textSelectionOverlay.getContext().getString(17039361));
                this.deleteView.setTextColor(getThemedColor(Theme.key_actionBarDefaultSubmenuItem));
                this.deleteView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Cells.TextSelectionHelper$$ExternalSyntheticLambda1
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        TextSelectionHelper.this.m1675lambda$showActions$2$orgtelegramuiCellsTextSelectionHelper(view);
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
                    actionBarPopupWindowLayout2.setBackgroundColor(getThemedColor(Theme.key_actionBarDefaultSubmenuBackground));
                }
            }
            int y = 0;
            if (this.selectedView != null) {
                int lineHeight = -getLineHeight();
                int[] coords = offsetToCord(this.selectionStart);
                y = (((int) ((coords[1] + this.textY) + this.selectedView.getY())) + (lineHeight / 2)) - AndroidUtilities.dp(4.0f);
                if (y < 0) {
                    y = 0;
                }
            }
            this.popupWindow.showAtLocation(this.textSelectionOverlay, 48, 0, y - AndroidUtilities.dp(48.0f));
            this.popupWindow.startAnimation();
        }
    }

    /* renamed from: lambda$showActions$1$org-telegram-ui-Cells-TextSelectionHelper */
    public /* synthetic */ boolean m1674lambda$showActions$1$orgtelegramuiCellsTextSelectionHelper(View v, MotionEvent event) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (event.getActionMasked() == 0 && (actionBarPopupWindow = this.popupWindow) != null && actionBarPopupWindow.isShowing()) {
            v.getHitRect(this.popupRect);
            return false;
        }
        return false;
    }

    /* renamed from: lambda$showActions$2$org-telegram-ui-Cells-TextSelectionHelper */
    public /* synthetic */ void m1675lambda$showActions$2$orgtelegramuiCellsTextSelectionHelper(View v) {
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

    public void checkSelectionCancel(MotionEvent e) {
        if (e.getAction() == 1 || e.getAction() == 3) {
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

    public void clear(boolean instant) {
        onExitSelectionMode(instant);
        this.selectionStart = -1;
        this.selectionEnd = -1;
        hideMagnifier();
        hideActions();
        invalidate();
        this.selectedView = null;
        this.selectedCellId = 0;
        this.selectedCellEditDate = null;
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

    protected void onExitSelectionMode(boolean didAction) {
    }

    public void setCallback(Callback listener) {
        this.callback = listener;
    }

    public boolean isTryingSelect() {
        return this.tryCapture;
    }

    public void onParentScrolled() {
        TextSelectionHelper<Cell>.TextSelectionOverlay textSelectionOverlay;
        if (isSelectionMode() && (textSelectionOverlay = this.textSelectionOverlay) != null) {
            this.parentIsScrolling = true;
            textSelectionOverlay.invalidate();
            hideActions();
        }
    }

    public void stopScrolling() {
        this.parentIsScrolling = false;
        showActions();
    }

    public static boolean isInterruptedCharacter(char c) {
        return Character.isLetter(c) || Character.isDigit(c) || c == '_';
    }

    public void setTopOffset(int topOffset) {
        this.topOffset = topOffset;
    }

    /* loaded from: classes4.dex */
    public class TextSelectionOverlay extends View {
        float pressedX;
        float pressedY;
        Paint handleViewPaint = new Paint(1);
        long pressedTime = 0;
        Path path = new Path();

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public TextSelectionOverlay(Context context) {
            super(context);
            TextSelectionHelper.this = this$0;
            this.handleViewPaint.setStyle(Paint.Style.FILL);
        }

        public boolean checkOnTap(MotionEvent event) {
            if (!TextSelectionHelper.this.isSelectionMode() || TextSelectionHelper.this.movingHandle) {
                return false;
            }
            switch (event.getAction()) {
                case 0:
                    this.pressedX = event.getX();
                    this.pressedY = event.getY();
                    this.pressedTime = System.currentTimeMillis();
                    break;
                case 1:
                    if (System.currentTimeMillis() - this.pressedTime < 200 && MathUtils.distance((int) this.pressedX, (int) this.pressedY, (int) event.getX(), (int) event.getY()) < TextSelectionHelper.this.touchSlop) {
                        TextSelectionHelper.this.hideActions();
                        TextSelectionHelper.this.clear();
                        return true;
                    }
                    break;
            }
            return false;
        }

        /* JADX WARN: Removed duplicated region for block: B:236:0x05ae  */
        /* JADX WARN: Removed duplicated region for block: B:241:0x05bb  */
        /* JADX WARN: Removed duplicated region for block: B:271:0x062e  */
        /* JADX WARN: Removed duplicated region for block: B:272:0x0644  */
        /* JADX WARN: Removed duplicated region for block: B:275:0x064d  */
        @Override // android.view.View
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public boolean onTouchEvent(android.view.MotionEvent r34) {
            /*
                Method dump skipped, instructions count: 2134
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.TextSelectionHelper.TextSelectionOverlay.onTouchEvent(android.view.MotionEvent):boolean");
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            int count;
            if (!TextSelectionHelper.this.isSelectionMode()) {
                return;
            }
            int handleViewSize = AndroidUtilities.dp(22.0f);
            int count2 = 0;
            int top = TextSelectionHelper.this.topOffset;
            TextSelectionHelper.this.pickEndView();
            if (TextSelectionHelper.this.selectedView != null) {
                canvas.save();
                float yOffset = TextSelectionHelper.this.selectedView.getY() + TextSelectionHelper.this.textY;
                float xOffset = TextSelectionHelper.this.selectedView.getX() + TextSelectionHelper.this.textX;
                canvas.translate(xOffset, yOffset);
                MessageObject msg = TextSelectionHelper.this.selectedView instanceof ChatMessageCell ? ((ChatMessageCell) TextSelectionHelper.this.selectedView).getMessageObject() : null;
                if (msg == null || !msg.isOutOwner()) {
                    this.handleViewPaint.setColor(TextSelectionHelper.this.getThemedColor(Theme.key_chat_TextSelectionCursor));
                } else {
                    this.handleViewPaint.setColor(TextSelectionHelper.this.getThemedColor(Theme.key_chat_outTextSelectionCursor));
                }
                TextSelectionHelper textSelectionHelper = TextSelectionHelper.this;
                int len = textSelectionHelper.getText(textSelectionHelper.selectedView, false).length();
                if (TextSelectionHelper.this.selectionEnd < 0 || TextSelectionHelper.this.selectionEnd > len) {
                    count = 0;
                } else {
                    TextSelectionHelper textSelectionHelper2 = TextSelectionHelper.this;
                    textSelectionHelper2.fillLayoutForOffset(textSelectionHelper2.selectionEnd, TextSelectionHelper.this.layoutBlock);
                    StaticLayout layout = TextSelectionHelper.this.layoutBlock.layout;
                    if (layout == null) {
                        count = 0;
                    } else {
                        int end = TextSelectionHelper.this.selectionEnd;
                        int textLen = layout.getText().length();
                        if (end > textLen) {
                            end = textLen;
                        }
                        int line = layout.getLineForOffset(end);
                        float x = layout.getPrimaryHorizontal(end);
                        int y = (int) (layout.getLineBottom(line) + TextSelectionHelper.this.layoutBlock.yOffset);
                        float x2 = x + TextSelectionHelper.this.layoutBlock.xOffset;
                        if (y + yOffset <= TextSelectionHelper.this.keyboardSize + top || y + yOffset >= TextSelectionHelper.this.parentView.getMeasuredHeight()) {
                            count = 0;
                            TextSelectionHelper.this.endArea.setEmpty();
                        } else if (!layout.isRtlCharAt(TextSelectionHelper.this.selectionEnd)) {
                            canvas.save();
                            canvas.translate(x2, y);
                            float v = TextSelectionHelper.this.interpolator.getInterpolation(TextSelectionHelper.this.handleViewProgress);
                            canvas.scale(v, v, handleViewSize / 2.0f, handleViewSize / 2.0f);
                            this.path.reset();
                            this.path.addCircle(handleViewSize / 2.0f, handleViewSize / 2.0f, handleViewSize / 2.0f, Path.Direction.CCW);
                            this.path.addRect(0.0f, 0.0f, handleViewSize / 2.0f, handleViewSize / 2.0f, Path.Direction.CCW);
                            canvas.drawPath(this.path, this.handleViewPaint);
                            canvas.restore();
                            TextSelectionHelper.this.endArea.set(xOffset + x2, (y + yOffset) - handleViewSize, xOffset + x2 + handleViewSize, y + yOffset + handleViewSize);
                            TextSelectionHelper.this.endArea.inset(-AndroidUtilities.dp(8.0f), -AndroidUtilities.dp(8.0f));
                            count2 = 0 + 1;
                            canvas.restore();
                        } else {
                            canvas.save();
                            canvas.translate(x2 - handleViewSize, y);
                            float v2 = TextSelectionHelper.this.interpolator.getInterpolation(TextSelectionHelper.this.handleViewProgress);
                            canvas.scale(v2, v2, handleViewSize / 2.0f, handleViewSize / 2.0f);
                            this.path.reset();
                            this.path.addCircle(handleViewSize / 2.0f, handleViewSize / 2.0f, handleViewSize / 2.0f, Path.Direction.CCW);
                            this.path.addRect(handleViewSize / 2.0f, 0.0f, handleViewSize, handleViewSize / 2.0f, Path.Direction.CCW);
                            canvas.drawPath(this.path, this.handleViewPaint);
                            canvas.restore();
                            count = 0;
                            TextSelectionHelper.this.endArea.set((xOffset + x2) - handleViewSize, (y + yOffset) - handleViewSize, xOffset + x2, y + yOffset + handleViewSize);
                            TextSelectionHelper.this.endArea.inset(-AndroidUtilities.dp(8.0f), -AndroidUtilities.dp(8.0f));
                        }
                    }
                }
                count2 = count;
                canvas.restore();
            }
            TextSelectionHelper.this.pickStartView();
            if (TextSelectionHelper.this.selectedView != null) {
                canvas.save();
                float yOffset2 = TextSelectionHelper.this.selectedView.getY() + TextSelectionHelper.this.textY;
                float xOffset2 = TextSelectionHelper.this.selectedView.getX() + TextSelectionHelper.this.textX;
                canvas.translate(xOffset2, yOffset2);
                TextSelectionHelper textSelectionHelper3 = TextSelectionHelper.this;
                int len2 = textSelectionHelper3.getText(textSelectionHelper3.selectedView, false).length();
                if (TextSelectionHelper.this.selectionStart >= 0 && TextSelectionHelper.this.selectionStart <= len2) {
                    TextSelectionHelper textSelectionHelper4 = TextSelectionHelper.this;
                    textSelectionHelper4.fillLayoutForOffset(textSelectionHelper4.selectionStart, TextSelectionHelper.this.layoutBlock);
                    StaticLayout layout2 = TextSelectionHelper.this.layoutBlock.layout;
                    if (layout2 != null) {
                        int line2 = layout2.getLineForOffset(TextSelectionHelper.this.selectionStart);
                        float x3 = layout2.getPrimaryHorizontal(TextSelectionHelper.this.selectionStart);
                        int y2 = (int) (layout2.getLineBottom(line2) + TextSelectionHelper.this.layoutBlock.yOffset);
                        float x4 = x3 + TextSelectionHelper.this.layoutBlock.xOffset;
                        if (y2 + yOffset2 > TextSelectionHelper.this.keyboardSize + top && y2 + yOffset2 < TextSelectionHelper.this.parentView.getMeasuredHeight()) {
                            if (!layout2.isRtlCharAt(TextSelectionHelper.this.selectionStart)) {
                                canvas.save();
                                canvas.translate(x4 - handleViewSize, y2);
                                float v3 = TextSelectionHelper.this.interpolator.getInterpolation(TextSelectionHelper.this.handleViewProgress);
                                canvas.scale(v3, v3, handleViewSize / 2.0f, handleViewSize / 2.0f);
                                this.path.reset();
                                this.path.addCircle(handleViewSize / 2.0f, handleViewSize / 2.0f, handleViewSize / 2.0f, Path.Direction.CCW);
                                this.path.addRect(handleViewSize / 2.0f, 0.0f, handleViewSize, handleViewSize / 2.0f, Path.Direction.CCW);
                                canvas.drawPath(this.path, this.handleViewPaint);
                                canvas.restore();
                                TextSelectionHelper.this.startArea.set((xOffset2 + x4) - handleViewSize, (y2 + yOffset2) - handleViewSize, xOffset2 + x4, y2 + yOffset2 + handleViewSize);
                                TextSelectionHelper.this.startArea.inset(-AndroidUtilities.dp(8.0f), -AndroidUtilities.dp(8.0f));
                                count2++;
                            } else {
                                canvas.save();
                                canvas.translate(x4, y2);
                                float v4 = TextSelectionHelper.this.interpolator.getInterpolation(TextSelectionHelper.this.handleViewProgress);
                                canvas.scale(v4, v4, handleViewSize / 2.0f, handleViewSize / 2.0f);
                                this.path.reset();
                                this.path.addCircle(handleViewSize / 2.0f, handleViewSize / 2.0f, handleViewSize / 2.0f, Path.Direction.CCW);
                                this.path.addRect(0.0f, 0.0f, handleViewSize / 2.0f, handleViewSize / 2.0f, Path.Direction.CCW);
                                canvas.drawPath(this.path, this.handleViewPaint);
                                canvas.restore();
                                TextSelectionHelper.this.startArea.set(xOffset2 + x4, (y2 + yOffset2) - handleViewSize, xOffset2 + x4 + handleViewSize, y2 + yOffset2 + handleViewSize);
                                TextSelectionHelper.this.startArea.inset(-AndroidUtilities.dp(8.0f), -AndroidUtilities.dp(8.0f));
                            }
                        } else {
                            if (y2 + yOffset2 > 0.0f && (y2 + yOffset2) - TextSelectionHelper.this.getLineHeight() < TextSelectionHelper.this.parentView.getMeasuredHeight()) {
                                count2++;
                            }
                            TextSelectionHelper.this.startArea.setEmpty();
                        }
                    }
                }
                canvas.restore();
            }
            if (count2 != 0 && TextSelectionHelper.this.movingHandle) {
                if (!TextSelectionHelper.this.movingHandleStart) {
                    TextSelectionHelper.this.pickEndView();
                }
                TextSelectionHelper textSelectionHelper5 = TextSelectionHelper.this;
                textSelectionHelper5.showMagnifier(textSelectionHelper5.lastX);
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
            if (TextSelectionHelper.this.isOneTouch) {
                invalidate();
            }
        }
    }

    protected void jumpToLine(int newSelection, int nextWhitespace, boolean viewChanged, float newYoffset, float oldYoffset, Cell oldSelectedView) {
        int i;
        if (this.movingHandleStart) {
            this.selectionStart = nextWhitespace;
            if (!viewChanged && nextWhitespace > this.selectionEnd) {
                int k = this.selectionEnd;
                this.selectionEnd = nextWhitespace;
                this.selectionStart = k;
                this.movingHandleStart = false;
            }
            this.snap = true;
            return;
        }
        this.selectionEnd = nextWhitespace;
        if (!viewChanged && (i = this.selectionStart) > nextWhitespace) {
            int k2 = this.selectionEnd;
            this.selectionEnd = i;
            this.selectionStart = k2;
            this.movingHandleStart = true;
        }
        this.snap = true;
    }

    protected boolean canSelect(int newSelection) {
        return (newSelection == this.selectionStart || newSelection == this.selectionEnd) ? false : true;
    }

    protected boolean selectLayout(int x, int y) {
        return false;
    }

    protected void onOffsetChanged() {
    }

    protected void pickEndView() {
    }

    protected void pickStartView() {
    }

    protected boolean isSelectable(View child) {
        return true;
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
    /* loaded from: classes4.dex */
    public class AnonymousClass4 implements ActionMode.Callback {
        private String translateFromLanguage = null;

        AnonymousClass4() {
            TextSelectionHelper.this = this$0;
        }

        @Override // android.view.ActionMode.Callback
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            menu.add(0, 16908321, 0, 17039361);
            menu.add(0, 16908319, 1, 17039373);
            menu.add(0, 3, 2, LocaleController.getString("TranslateMessage", R.string.TranslateMessage));
            return true;
        }

        @Override // android.view.ActionMode.Callback
        public boolean onPrepareActionMode(ActionMode mode, final Menu menu) {
            if (TextSelectionHelper.this.selectedView != null) {
                TextSelectionHelper textSelectionHelper = TextSelectionHelper.this;
                CharSequence charSequence = textSelectionHelper.getText(textSelectionHelper.selectedView, false);
                if (TextSelectionHelper.this.multiselect || (TextSelectionHelper.this.selectionStart <= 0 && TextSelectionHelper.this.selectionEnd >= charSequence.length() - 1)) {
                    menu.getItem(1).setVisible(false);
                } else {
                    menu.getItem(1).setVisible(true);
                }
            }
            if (TextSelectionHelper.this.onTranslateListener != null && LanguageDetector.hasSupport() && TextSelectionHelper.this.getSelectedText() != null) {
                LanguageDetector.detectLanguage(TextSelectionHelper.this.getSelectedText().toString(), new LanguageDetector.StringCallback() { // from class: org.telegram.ui.Cells.TextSelectionHelper$4$$ExternalSyntheticLambda2
                    @Override // org.telegram.messenger.LanguageDetector.StringCallback
                    public final void run(String str) {
                        TextSelectionHelper.AnonymousClass4.this.m1678xfe5e6977(menu, str);
                    }
                }, new LanguageDetector.ExceptionCallback() { // from class: org.telegram.ui.Cells.TextSelectionHelper$4$$ExternalSyntheticLambda1
                    @Override // org.telegram.messenger.LanguageDetector.ExceptionCallback
                    public final void run(Exception exc) {
                        TextSelectionHelper.AnonymousClass4.this.m1679x98ff2bf8(menu, exc);
                    }
                });
            } else {
                this.translateFromLanguage = null;
                updateTranslateButton(menu);
            }
            return true;
        }

        /* renamed from: lambda$onPrepareActionMode$0$org-telegram-ui-Cells-TextSelectionHelper$4 */
        public /* synthetic */ void m1678xfe5e6977(Menu menu, String lng) {
            this.translateFromLanguage = lng;
            updateTranslateButton(menu);
        }

        /* renamed from: lambda$onPrepareActionMode$1$org-telegram-ui-Cells-TextSelectionHelper$4 */
        public /* synthetic */ void m1679x98ff2bf8(Menu menu, Exception err) {
            FileLog.e("mlkit: failed to detect language in selection");
            FileLog.e(err);
            this.translateFromLanguage = null;
            updateTranslateButton(menu);
        }

        private void updateTranslateButton(Menu menu) {
            String str;
            String translateToLanguage = LocaleController.getInstance().getCurrentLocale().getLanguage();
            menu.getItem(2).setVisible(TextSelectionHelper.this.onTranslateListener != null && (((str = this.translateFromLanguage) != null && ((!str.equals(translateToLanguage) || this.translateFromLanguage.equals("und")) && !RestrictedLanguagesSelectActivity.getRestrictedLanguages().contains(this.translateFromLanguage))) || !LanguageDetector.hasSupport()));
        }

        @Override // android.view.ActionMode.Callback
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (!TextSelectionHelper.this.isSelectionMode()) {
                return true;
            }
            switch (item.getItemId()) {
                case 3:
                    if (TextSelectionHelper.this.onTranslateListener != null) {
                        String translateToLanguage = LocaleController.getInstance().getCurrentLocale().getLanguage();
                        TextSelectionHelper.this.onTranslateListener.run(TextSelectionHelper.this.getSelectedText(), this.translateFromLanguage, translateToLanguage, new Runnable() { // from class: org.telegram.ui.Cells.TextSelectionHelper$4$$ExternalSyntheticLambda0
                            @Override // java.lang.Runnable
                            public final void run() {
                                TextSelectionHelper.AnonymousClass4.this.m1677x2de5aa17();
                            }
                        });
                    }
                    TextSelectionHelper.this.hideActions();
                    return true;
                case 16908319:
                    TextSelectionHelper textSelectionHelper = TextSelectionHelper.this;
                    CharSequence text = textSelectionHelper.getText(textSelectionHelper.selectedView, false);
                    if (text == null) {
                        return true;
                    }
                    TextSelectionHelper.this.selectionStart = 0;
                    TextSelectionHelper.this.selectionEnd = text.length();
                    TextSelectionHelper.this.hideActions();
                    TextSelectionHelper.this.invalidate();
                    TextSelectionHelper.this.showActions();
                    return true;
                case 16908321:
                    TextSelectionHelper.this.copyText();
                    return true;
                default:
                    TextSelectionHelper.this.clear();
                    return true;
            }
        }

        /* renamed from: lambda$onActionItemClicked$2$org-telegram-ui-Cells-TextSelectionHelper$4 */
        public /* synthetic */ void m1677x2de5aa17() {
            TextSelectionHelper.this.showActions();
        }

        @Override // android.view.ActionMode.Callback
        public void onDestroyActionMode(ActionMode mode) {
            if (Build.VERSION.SDK_INT < 23) {
                TextSelectionHelper.this.clear();
            }
        }
    }

    private ActionMode.Callback createActionCallback() {
        final ActionMode.Callback callback = new AnonymousClass4();
        if (Build.VERSION.SDK_INT >= 23) {
            ActionMode.Callback2 callback2 = new ActionMode.Callback2() { // from class: org.telegram.ui.Cells.TextSelectionHelper.5
                @Override // android.view.ActionMode.Callback
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    return callback.onCreateActionMode(mode, menu);
                }

                @Override // android.view.ActionMode.Callback
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return callback.onPrepareActionMode(mode, menu);
                }

                @Override // android.view.ActionMode.Callback
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    return callback.onActionItemClicked(mode, item);
                }

                @Override // android.view.ActionMode.Callback
                public void onDestroyActionMode(ActionMode mode) {
                    callback.onDestroyActionMode(mode);
                }

                @Override // android.view.ActionMode.Callback2
                public void onGetContentRect(ActionMode mode, View view, Rect outRect) {
                    if (!TextSelectionHelper.this.isSelectionMode()) {
                        return;
                    }
                    TextSelectionHelper.this.pickStartView();
                    int x1 = 0;
                    int y1 = 1;
                    if (TextSelectionHelper.this.selectedView != null) {
                        int lineHeight = -TextSelectionHelper.this.getLineHeight();
                        TextSelectionHelper textSelectionHelper = TextSelectionHelper.this;
                        int[] coords = textSelectionHelper.offsetToCord(textSelectionHelper.selectionStart);
                        x1 = coords[0] + TextSelectionHelper.this.textX;
                        y1 = (((int) ((coords[1] + TextSelectionHelper.this.textY) + TextSelectionHelper.this.selectedView.getY())) + (lineHeight / 2)) - AndroidUtilities.dp(4.0f);
                        if (y1 < 1) {
                            y1 = 1;
                        }
                    }
                    int x2 = TextSelectionHelper.this.parentView.getWidth();
                    TextSelectionHelper.this.pickEndView();
                    if (TextSelectionHelper.this.selectedView != null) {
                        TextSelectionHelper textSelectionHelper2 = TextSelectionHelper.this;
                        x2 = textSelectionHelper2.offsetToCord(textSelectionHelper2.selectionEnd)[0] + TextSelectionHelper.this.textX;
                    }
                    outRect.set(Math.min(x1, x2), y1, Math.max(x1, x2), y1 + 1);
                }
            };
            return callback2;
        }
        return callback;
    }

    public void copyText() {
        CharSequence str;
        if (!isSelectionMode() || (str = getSelectedText()) == null) {
            return;
        }
        ClipboardManager clipboard = (ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard");
        ClipData clip = ClipData.newPlainText(Constants.ScionAnalytics.PARAM_LABEL, str);
        clipboard.setPrimaryClip(clip);
        hideActions();
        clear(true);
        Callback callback = this.callback;
        if (callback != null) {
            callback.onTextCopied();
        }
    }

    private void translateText() {
        if (!isSelectionMode()) {
            return;
        }
        getSelectedText();
    }

    protected CharSequence getSelectedText() {
        CharSequence text = getText(this.selectedView, false);
        if (text != null) {
            return text.subSequence(this.selectionStart, this.selectionEnd);
        }
        return null;
    }

    protected int[] offsetToCord(int offset) {
        fillLayoutForOffset(offset, this.layoutBlock);
        StaticLayout layout = this.layoutBlock.layout;
        if (layout == null || offset > layout.getText().length()) {
            return this.tmpCoord;
        }
        int line = layout.getLineForOffset(offset);
        this.tmpCoord[0] = (int) (layout.getPrimaryHorizontal(offset) + this.layoutBlock.xOffset);
        this.tmpCoord[1] = layout.getLineBottom(line);
        int[] iArr = this.tmpCoord;
        iArr[1] = (int) (iArr[1] + this.layoutBlock.yOffset);
        return this.tmpCoord;
    }

    protected void drawSelection(Canvas canvas, StaticLayout layout, int selectionStart, int selectionEnd, boolean hasStart, boolean hasEnd) {
        int end;
        Rect rect;
        int endIndex;
        this.selectionPath.reset();
        this.selectionHandlePath.reset();
        float f = this.cornerRadius;
        float R = f * 1.65f;
        int halfR = (int) (f / 2.0f);
        int startLine = layout.getLineForOffset(selectionStart);
        int endLine = layout.getLineForOffset(selectionEnd);
        if (startLine == endLine) {
            drawLine(layout, startLine, selectionStart, selectionEnd, !hasStart, !hasEnd);
        } else {
            int end2 = layout.getLineEnd(startLine);
            if (layout.getParagraphDirection(startLine) != -1 && end2 > 0) {
                int end3 = end2 - 1;
                CharSequence text = layout.getText();
                int s = (int) layout.getPrimaryHorizontal(end3);
                if (layout.isRtlCharAt(end3)) {
                    int endIndex2 = end3;
                    while (layout.isRtlCharAt(endIndex2) && endIndex2 != 0) {
                        endIndex2--;
                    }
                    endIndex = (int) (layout.getLineForOffset(endIndex2) == layout.getLineForOffset(end3) ? layout.getPrimaryHorizontal(endIndex2 + 1) : layout.getLineLeft(startLine));
                } else {
                    endIndex = (int) layout.getLineRight(startLine);
                }
                int l = Math.min(s, endIndex);
                int r = Math.max(s, endIndex);
                if (end3 <= 0 || end3 >= text.length() || Character.isWhitespace(text.charAt(end3 - 1))) {
                    end = end3;
                    rect = null;
                } else {
                    end = end3;
                    rect = new Rect(l, layout.getLineTop(startLine), r + halfR, layout.getLineBottom(startLine));
                }
            } else {
                end = end2;
                rect = null;
            }
            drawLine(layout, startLine, selectionStart, end, !hasStart, true);
            if (rect != null) {
                AndroidUtilities.rectTmp.set(rect);
                this.selectionPath.addRect(AndroidUtilities.rectTmp, Path.Direction.CW);
            }
            for (int i = startLine + 1; i < endLine; i++) {
                int s2 = (int) layout.getLineLeft(i);
                int e = (int) layout.getLineRight(i);
                int l2 = Math.min(s2, e);
                int r2 = Math.max(s2, e);
                int l3 = layout.getLineBottom(i) + 1;
                this.selectionPath.addRect(l2 - halfR, layout.getLineTop(i), r2 + halfR, l3, Path.Direction.CW);
            }
            drawLine(layout, endLine, layout.getLineStart(endLine), selectionEnd, true, !hasEnd);
        }
        boolean restore = Build.VERSION.SDK_INT >= 26;
        if (restore) {
            canvas.save();
        }
        float startLeft = layout.getPrimaryHorizontal(selectionStart);
        float endRight = layout.getPrimaryHorizontal(selectionEnd);
        float startBottom = layout.getLineBottom(startLine);
        float endBottom = layout.getLineBottom(endLine);
        if (hasStart && hasEnd && startBottom == endBottom && Math.abs(endRight - startLeft) < R) {
            float left = Math.min(startLeft, endRight);
            float right = Math.max(startLeft, endRight);
            int i2 = (int) left;
            float left2 = startBottom - R;
            AndroidUtilities.rectTmp2.set(i2, (int) left2, (int) right, (int) startBottom);
            AndroidUtilities.rectTmp.set(AndroidUtilities.rectTmp2);
            this.selectionHandlePath.addRect(AndroidUtilities.rectTmp, Path.Direction.CW);
            if (Build.VERSION.SDK_INT >= 26) {
                canvas.clipOutRect(AndroidUtilities.rectTmp2);
            }
        } else {
            if (hasStart) {
                AndroidUtilities.rectTmp2.set((int) startLeft, (int) (startBottom - R), (int) Math.min(startLeft + R, layout.getLineRight(startLine)), (int) startBottom);
                AndroidUtilities.rectTmp.set(AndroidUtilities.rectTmp2);
                this.selectionHandlePath.addRect(AndroidUtilities.rectTmp, Path.Direction.CW);
                if (Build.VERSION.SDK_INT >= 26) {
                    AndroidUtilities.rectTmp2.set(AndroidUtilities.rectTmp2.left - ((int) R), AndroidUtilities.rectTmp2.top, AndroidUtilities.rectTmp2.right, AndroidUtilities.rectTmp2.bottom);
                    canvas.clipOutRect(AndroidUtilities.rectTmp2);
                }
            }
            if (hasEnd) {
                AndroidUtilities.rectTmp2.set((int) Math.max(endRight - R, layout.getLineLeft(endLine)), (int) (endBottom - R), (int) endRight, (int) endBottom);
                AndroidUtilities.rectTmp.set(AndroidUtilities.rectTmp2);
                this.selectionHandlePath.addRect(AndroidUtilities.rectTmp, Path.Direction.CW);
                if (Build.VERSION.SDK_INT >= 26) {
                    canvas.clipOutRect(AndroidUtilities.rectTmp2);
                }
            }
        }
        canvas.drawPath(this.selectionPath, this.selectionPaint);
        if (restore) {
            canvas.restore();
            canvas.drawPath(this.selectionHandlePath, this.selectionHandlePaint);
        }
    }

    private void drawLine(StaticLayout layout, int line, int start, int end, boolean padAtStart, boolean padAtEnd) {
        this.tempPath2.reset();
        layout.getSelectionPath(start, end, this.tempPath2);
        float sy = 1.0f;
        float cy = 0.0f;
        if (this.tempPath2.lastBottom < layout.getLineBottom(line)) {
            int lineTop = layout.getLineTop(line);
            int lineBottom = layout.getLineBottom(line);
            float lineH = lineBottom - lineTop;
            float lineHWithoutSpacing = this.tempPath2.lastBottom - lineTop;
            sy = lineH / lineHWithoutSpacing;
            cy = lineTop;
        }
        for (int i = 0; i < this.tempPath2.rectsCount; i++) {
            RectF rect = (RectF) this.tempPath2.rects.get(i);
            float f = 0.0f;
            float f2 = (int) (rect.left - (padAtStart ? this.cornerRadius / 2.0f : 0.0f));
            float f3 = (int) (((rect.top - cy) * sy) + cy);
            float f4 = rect.right;
            if (padAtEnd) {
                f = this.cornerRadius / 2.0f;
            }
            rect.set(f2, f3, (int) (f4 + f), (int) (((rect.bottom - cy) * sy) + cy));
            this.selectionPath.addRect(rect.left, rect.top, rect.right, rect.bottom, Path.Direction.CW);
        }
        if (this.tempPath2.rectsCount == 0 && !padAtEnd) {
            int left = (int) layout.getPrimaryHorizontal(start);
            int right = (int) layout.getPrimaryHorizontal(end);
            int top = layout.getLineTop(line);
            int bottom = layout.getLineBottom(line);
            Path path = this.selectionPath;
            float f5 = this.cornerRadius;
            path.addRect(left - (f5 / 2.0f), top, right + (f5 / 4.0f), bottom, Path.Direction.CW);
        }
    }

    /* loaded from: classes4.dex */
    public static class LayoutBlock {
        StaticLayout layout;
        float xOffset;
        float yOffset;

        private LayoutBlock() {
        }
    }

    /* loaded from: classes4.dex */
    public static class Callback {
        public void onStateChanged(boolean isSelected) {
        }

        public void onTextCopied() {
        }
    }

    protected void fillLayoutForOffset(int offset, LayoutBlock layoutBlock) {
        fillLayoutForOffset(offset, layoutBlock, false);
    }

    /* loaded from: classes4.dex */
    public static class ChatListTextSelectionHelper extends TextSelectionHelper<ChatMessageCell> {
        SparseArray<Animator> animatorSparseArray = new SparseArray<>();
        private boolean isDescription;
        private boolean maybeIsDescription;
        public static int TYPE_MESSAGE = 0;
        public static int TYPE_CAPTION = 1;
        public static int TYPE_DESCRIPTION = 2;

        @Override // org.telegram.ui.Cells.TextSelectionHelper
        protected int getLineHeight() {
            if (this.selectedView == null || ((ChatMessageCell) this.selectedView).getMessageObject() == null) {
                return 0;
            }
            MessageObject object = ((ChatMessageCell) this.selectedView).getMessageObject();
            StaticLayout layout = null;
            if (this.isDescription) {
                layout = ((ChatMessageCell) this.selectedView).getDescriptionlayout();
            } else if (((ChatMessageCell) this.selectedView).hasCaptionLayout()) {
                layout = ((ChatMessageCell) this.selectedView).getCaptionLayout();
            } else if (object.textLayoutBlocks != null) {
                layout = object.textLayoutBlocks.get(0).textLayout;
            }
            if (layout == null) {
                return 0;
            }
            int lineHeight = layout.getLineBottom(0) - layout.getLineTop(0);
            return lineHeight;
        }

        public void setMessageObject(ChatMessageCell chatMessageCell) {
            this.maybeSelectedView = chatMessageCell;
            MessageObject messageObject = chatMessageCell.getMessageObject();
            if (this.maybeIsDescription && chatMessageCell.getDescriptionlayout() != null) {
                this.textArea.set(this.maybeTextX, this.maybeTextY, this.maybeTextX + chatMessageCell.getDescriptionlayout().getWidth(), this.maybeTextY + chatMessageCell.getDescriptionlayout().getHeight());
            } else if (chatMessageCell.hasCaptionLayout()) {
                this.textArea.set(this.maybeTextX, this.maybeTextY, this.maybeTextX + chatMessageCell.getCaptionLayout().getWidth(), this.maybeTextY + chatMessageCell.getCaptionLayout().getHeight());
            } else if (messageObject != null && messageObject.textLayoutBlocks != null && messageObject.textLayoutBlocks.size() > 0) {
                MessageObject.TextLayoutBlock block = messageObject.textLayoutBlocks.get(messageObject.textLayoutBlocks.size() - 1);
                this.textArea.set(this.maybeTextX, this.maybeTextY, this.maybeTextX + block.textLayout.getWidth(), (int) (this.maybeTextY + block.textYOffset + block.textLayout.getHeight()));
            }
        }

        public CharSequence getText(ChatMessageCell cell, boolean maybe) {
            if (cell == null || cell.getMessageObject() == null) {
                return null;
            }
            if (!maybe ? this.isDescription : this.maybeIsDescription) {
                return cell.getDescriptionlayout().getText();
            }
            if (cell.hasCaptionLayout()) {
                return cell.getCaptionLayout().getText();
            }
            return cell.getMessageObject().messageText;
        }

        public void onTextSelected(ChatMessageCell newView, ChatMessageCell oldView) {
            final boolean idChanged = oldView == null || !(oldView.getMessageObject() == null || oldView.getMessageObject().getId() == newView.getMessageObject().getId());
            this.selectedCellId = newView.getMessageObject().getId();
            try {
                this.selectedCellEditDate = Integer.valueOf(newView.getMessageObject().messageOwner.edit_date);
            } catch (Exception e) {
                this.selectedCellEditDate = null;
            }
            this.enterProgress = 0.0f;
            this.isDescription = this.maybeIsDescription;
            Animator oldAnimator = this.animatorSparseArray.get(this.selectedCellId);
            if (oldAnimator != null) {
                oldAnimator.removeAllListeners();
                oldAnimator.cancel();
            }
            ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Cells.TextSelectionHelper$ChatListTextSelectionHelper$$ExternalSyntheticLambda1
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    TextSelectionHelper.ChatListTextSelectionHelper.this.m1680x6ff5dada(idChanged, valueAnimator);
                }
            });
            animator.setDuration(250L);
            animator.start();
            this.animatorSparseArray.put(this.selectedCellId, animator);
            if (!idChanged) {
                newView.setSelectedBackgroundProgress(0.0f);
            }
            SharedConfig.removeTextSelectionHint();
        }

        /* renamed from: lambda$onTextSelected$0$org-telegram-ui-Cells-TextSelectionHelper$ChatListTextSelectionHelper */
        public /* synthetic */ void m1680x6ff5dada(boolean idChanged, ValueAnimator animation) {
            this.enterProgress = ((Float) animation.getAnimatedValue()).floatValue();
            if (this.textSelectionOverlay != null) {
                this.textSelectionOverlay.invalidate();
            }
            if (this.selectedView != null && ((ChatMessageCell) this.selectedView).getCurrentMessagesGroup() == null && idChanged) {
                ((ChatMessageCell) this.selectedView).setSelectedBackgroundProgress(1.0f - this.enterProgress);
            }
        }

        public void draw(MessageObject messageObject, MessageObject.TextLayoutBlock block, Canvas canvas) {
            MessageObject selectedMessageObject;
            if (this.selectedView != null && ((ChatMessageCell) this.selectedView).getMessageObject() != null && !this.isDescription && (selectedMessageObject = ((ChatMessageCell) this.selectedView).getMessageObject()) != null && selectedMessageObject.textLayoutBlocks != null && messageObject.getId() == this.selectedCellId) {
                int selectionStart = this.selectionStart;
                int selectionEnd = this.selectionEnd;
                if (selectedMessageObject.textLayoutBlocks.size() > 1) {
                    if (selectionStart < block.charactersOffset) {
                        selectionStart = block.charactersOffset;
                    }
                    if (selectionStart > block.charactersEnd) {
                        selectionStart = block.charactersEnd;
                    }
                    if (selectionEnd < block.charactersOffset) {
                        selectionEnd = block.charactersOffset;
                    }
                    if (selectionEnd > block.charactersEnd) {
                        selectionEnd = block.charactersEnd;
                    }
                }
                if (selectionStart != selectionEnd) {
                    if (selectedMessageObject.isOutOwner()) {
                        this.selectionPaint.setColor(getThemedColor(Theme.key_chat_outTextSelectionHighlight));
                        this.selectionHandlePaint.setColor(getThemedColor(Theme.key_chat_outTextSelectionHighlight));
                    } else {
                        this.selectionPaint.setColor(getThemedColor(Theme.key_chat_inTextSelectionHighlight));
                        this.selectionHandlePaint.setColor(getThemedColor(Theme.key_chat_inTextSelectionHighlight));
                    }
                    drawSelection(canvas, block.textLayout, selectionStart, selectionEnd, true, true);
                }
            }
        }

        public int getCharOffsetFromCord(int x, int y, int offsetX, int offsetY, ChatMessageCell cell, boolean maybe) {
            StaticLayout lastLayout;
            float yOffset;
            int y2;
            if (cell == null) {
                return 0;
            }
            int line = -1;
            int x2 = x - offsetX;
            int y3 = y - offsetY;
            boolean isDescription = maybe ? this.maybeIsDescription : this.isDescription;
            if (isDescription) {
                yOffset = 0.0f;
                lastLayout = cell.getDescriptionlayout();
            } else if (cell.hasCaptionLayout()) {
                yOffset = 0.0f;
                lastLayout = cell.getCaptionLayout();
            } else {
                MessageObject.TextLayoutBlock lastBlock = cell.getMessageObject().textLayoutBlocks.get(cell.getMessageObject().textLayoutBlocks.size() - 1);
                StaticLayout lastLayout2 = lastBlock.textLayout;
                yOffset = lastBlock.textYOffset;
                lastLayout = lastLayout2;
            }
            if (y3 < 0) {
                y3 = 1;
            }
            if (y3 <= lastLayout.getLineBottom(lastLayout.getLineCount() - 1) + yOffset) {
                y2 = y3;
            } else {
                int y4 = (int) ((lastLayout.getLineBottom(lastLayout.getLineCount() - 1) + yOffset) - 1.0f);
                y2 = y4;
            }
            fillLayoutForCoords(x2, y2, cell, this.layoutBlock, maybe);
            if (this.layoutBlock.layout == null) {
                return -1;
            }
            StaticLayout layout = this.layoutBlock.layout;
            int x3 = (int) (x2 - this.layoutBlock.xOffset);
            int i = 0;
            while (true) {
                if (i < layout.getLineCount()) {
                    if (y2 <= this.layoutBlock.yOffset + layout.getLineTop(i) || y2 >= this.layoutBlock.yOffset + layout.getLineBottom(i)) {
                        i++;
                    } else {
                        line = i;
                        break;
                    }
                } else {
                    break;
                }
            }
            if (line < 0) {
                return -1;
            }
            return layout.getOffsetForHorizontal(line, x3);
        }

        private void fillLayoutForCoords(int x, int y, ChatMessageCell cell, LayoutBlock layoutBlock, boolean maybe) {
            if (cell == null) {
                return;
            }
            MessageObject messageObject = cell.getMessageObject();
            if (!maybe ? this.isDescription : this.maybeIsDescription) {
                layoutBlock.layout = cell.getDescriptionlayout();
                layoutBlock.xOffset = 0.0f;
                layoutBlock.yOffset = 0.0f;
            } else if (cell.hasCaptionLayout()) {
                layoutBlock.layout = cell.getCaptionLayout();
                layoutBlock.xOffset = 0.0f;
                layoutBlock.yOffset = 0.0f;
            } else {
                for (int i = 0; i < messageObject.textLayoutBlocks.size(); i++) {
                    MessageObject.TextLayoutBlock block = messageObject.textLayoutBlocks.get(i);
                    if (y >= block.textYOffset && y <= block.textYOffset + block.height) {
                        layoutBlock.layout = block.textLayout;
                        layoutBlock.yOffset = block.textYOffset;
                        layoutBlock.xOffset = -(block.isRtl() ? (int) Math.ceil(messageObject.textXOffset) : 0);
                        return;
                    }
                }
            }
        }

        @Override // org.telegram.ui.Cells.TextSelectionHelper
        protected void fillLayoutForOffset(int offset, LayoutBlock layoutBlock, boolean maybe) {
            ChatMessageCell selectedView = (ChatMessageCell) (maybe ? this.maybeSelectedView : this.selectedView);
            if (selectedView == null) {
                layoutBlock.layout = null;
                return;
            }
            MessageObject messageObject = selectedView.getMessageObject();
            if (this.isDescription) {
                layoutBlock.layout = selectedView.getDescriptionlayout();
                layoutBlock.yOffset = 0.0f;
                layoutBlock.xOffset = 0.0f;
            } else if (selectedView.hasCaptionLayout()) {
                layoutBlock.layout = selectedView.getCaptionLayout();
                layoutBlock.yOffset = 0.0f;
                layoutBlock.xOffset = 0.0f;
            } else if (messageObject.textLayoutBlocks == null) {
                layoutBlock.layout = null;
            } else {
                int i = 0;
                if (messageObject.textLayoutBlocks.size() == 1) {
                    layoutBlock.layout = messageObject.textLayoutBlocks.get(0).textLayout;
                    layoutBlock.yOffset = 0.0f;
                    if (messageObject.textLayoutBlocks.get(0).isRtl()) {
                        i = (int) Math.ceil(messageObject.textXOffset);
                    }
                    layoutBlock.xOffset = -i;
                    return;
                }
                for (int i2 = 0; i2 < messageObject.textLayoutBlocks.size(); i2++) {
                    MessageObject.TextLayoutBlock block = messageObject.textLayoutBlocks.get(i2);
                    if (offset >= block.charactersOffset && offset <= block.charactersEnd) {
                        layoutBlock.layout = messageObject.textLayoutBlocks.get(i2).textLayout;
                        layoutBlock.yOffset = messageObject.textLayoutBlocks.get(i2).textYOffset;
                        if (block.isRtl()) {
                            i = (int) Math.ceil(messageObject.textXOffset);
                        }
                        layoutBlock.xOffset = -i;
                        return;
                    }
                }
                layoutBlock.layout = null;
            }
        }

        @Override // org.telegram.ui.Cells.TextSelectionHelper
        protected void onExitSelectionMode(boolean instant) {
            if (this.selectedView != null && ((ChatMessageCell) this.selectedView).isDrawingSelectionBackground() && !instant) {
                final ChatMessageCell cell = (ChatMessageCell) this.selectedView;
                final int id = ((ChatMessageCell) this.selectedView).getMessageObject().getId();
                Animator oldAnimator = this.animatorSparseArray.get(id);
                if (oldAnimator != null) {
                    oldAnimator.removeAllListeners();
                    oldAnimator.cancel();
                }
                cell.setSelectedBackgroundProgress(0.01f);
                ValueAnimator animator = ValueAnimator.ofFloat(0.01f, 1.0f);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Cells.TextSelectionHelper$ChatListTextSelectionHelper$$ExternalSyntheticLambda0
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        TextSelectionHelper.ChatListTextSelectionHelper.lambda$onExitSelectionMode$1(ChatMessageCell.this, id, valueAnimator);
                    }
                });
                animator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Cells.TextSelectionHelper.ChatListTextSelectionHelper.1
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        cell.setSelectedBackgroundProgress(0.0f);
                    }
                });
                animator.setDuration(300L);
                animator.start();
                this.animatorSparseArray.put(id, animator);
            }
        }

        public static /* synthetic */ void lambda$onExitSelectionMode$1(ChatMessageCell cell, int id, ValueAnimator animation) {
            float exit = ((Float) animation.getAnimatedValue()).floatValue();
            if (cell.getMessageObject() != null && cell.getMessageObject().getId() == id) {
                cell.setSelectedBackgroundProgress(exit);
            }
        }

        public void onChatMessageCellAttached(ChatMessageCell chatMessageCell) {
            if (chatMessageCell.getMessageObject() != null && chatMessageCell.getMessageObject().getId() == this.selectedCellId) {
                this.selectedView = chatMessageCell;
            }
        }

        public void onChatMessageCellDetached(ChatMessageCell chatMessageCell) {
            if (chatMessageCell.getMessageObject() != null && chatMessageCell.getMessageObject().getId() == this.selectedCellId) {
                this.selectedView = null;
            }
        }

        public void drawCaption(boolean isOut, StaticLayout captionLayout, Canvas canvas) {
            if (this.isDescription) {
                return;
            }
            if (isOut) {
                this.selectionPaint.setColor(getThemedColor(Theme.key_chat_outTextSelectionHighlight));
                this.selectionHandlePaint.setColor(getThemedColor(Theme.key_chat_outTextSelectionHighlight));
            } else {
                this.selectionPaint.setColor(getThemedColor(Theme.key_chat_inTextSelectionHighlight));
                this.selectionHandlePaint.setColor(getThemedColor(Theme.key_chat_inTextSelectionHighlight));
            }
            drawSelection(canvas, captionLayout, this.selectionStart, this.selectionEnd, true, true);
        }

        public void drawDescription(boolean isOut, StaticLayout layout, Canvas canvas) {
            if (!this.isDescription) {
                return;
            }
            if (isOut) {
                this.selectionPaint.setColor(getThemedColor(Theme.key_chat_outTextSelectionHighlight));
                this.selectionHandlePaint.setColor(getThemedColor(Theme.key_chat_outTextSelectionHighlight));
            } else {
                this.selectionPaint.setColor(getThemedColor(Theme.key_chat_inTextSelectionHighlight));
                this.selectionHandlePaint.setColor(getThemedColor(Theme.key_chat_inTextSelectionHighlight));
            }
            drawSelection(canvas, layout, this.selectionStart, this.selectionEnd, true, true);
        }

        @Override // org.telegram.ui.Cells.TextSelectionHelper
        public void invalidate() {
            super.invalidate();
            if (this.selectedView != null && ((ChatMessageCell) this.selectedView).getCurrentMessagesGroup() != null) {
                this.parentView.invalidate();
            }
        }

        public void cancelAllAnimators() {
            for (int i = 0; i < this.animatorSparseArray.size(); i++) {
                SparseArray<Animator> sparseArray = this.animatorSparseArray;
                Animator animator = sparseArray.get(sparseArray.keyAt(i));
                animator.cancel();
            }
            this.animatorSparseArray.clear();
        }

        public void setIsDescription(boolean b) {
            this.maybeIsDescription = b;
        }

        @Override // org.telegram.ui.Cells.TextSelectionHelper
        public void clear(boolean instant) {
            super.clear(instant);
            this.isDescription = false;
        }

        public int getTextSelectionType(ChatMessageCell cell) {
            if (this.isDescription) {
                return TYPE_DESCRIPTION;
            }
            if (cell.hasCaptionLayout()) {
                return TYPE_CAPTION;
            }
            return TYPE_MESSAGE;
        }

        public void updateTextPosition(int textX, int textY) {
            if (this.textX != textX || this.textY != textY) {
                this.textX = textX;
                this.textY = textY;
                invalidate();
            }
        }

        public void checkDataChanged(MessageObject messageObject) {
            try {
                Integer.valueOf(messageObject.messageOwner.edit_date);
            } catch (Exception e) {
            }
            if (this.selectedCellId == messageObject.getId()) {
                clear(true);
            }
        }
    }

    /* loaded from: classes4.dex */
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

        public CharSequence getText(ArticleSelectableView view, boolean maybe) {
            int i;
            this.arrayList.clear();
            view.fillTextLayoutBlocks(this.arrayList);
            if (maybe) {
                i = this.maybeTextIndex;
            } else {
                i = this.startPeek ? this.startViewChildPosition : this.endViewChildPosition;
            }
            if (this.arrayList.isEmpty() || i < 0) {
                return "";
            }
            return this.arrayList.get(i).getLayout().getText();
        }

        public int getCharOffsetFromCord(int x, int y, int offsetX, int offsetY, ArticleSelectableView view, boolean maybe) {
            int childIndex;
            if (view == null) {
                return -1;
            }
            int line = -1;
            int x2 = x - offsetX;
            int y2 = y - offsetY;
            this.arrayList.clear();
            view.fillTextLayoutBlocks(this.arrayList);
            if (maybe) {
                childIndex = this.maybeTextIndex;
            } else {
                childIndex = this.startPeek ? this.startViewChildPosition : this.endViewChildPosition;
            }
            StaticLayout layout = this.arrayList.get(childIndex).getLayout();
            if (x2 < 0) {
                x2 = 1;
            }
            if (y2 < 0) {
                y2 = 1;
            }
            if (x2 > layout.getWidth()) {
                x2 = layout.getWidth();
            }
            if (y2 > layout.getLineBottom(layout.getLineCount() - 1)) {
                y2 = layout.getLineBottom(layout.getLineCount() - 1) - 1;
            }
            int i = 0;
            while (true) {
                if (i < layout.getLineCount()) {
                    if (y2 <= layout.getLineTop(i) || y2 >= layout.getLineBottom(i)) {
                        i++;
                    } else {
                        line = i;
                        break;
                    }
                } else {
                    break;
                }
            }
            if (line < 0) {
                return -1;
            }
            return layout.getOffsetForHorizontal(line, x2);
        }

        @Override // org.telegram.ui.Cells.TextSelectionHelper
        protected void fillLayoutForOffset(int offset, LayoutBlock layoutBlock, boolean maybe) {
            this.arrayList.clear();
            ArticleSelectableView selectedView = (ArticleSelectableView) (maybe ? this.maybeSelectedView : this.selectedView);
            if (selectedView == null) {
                layoutBlock.layout = null;
                return;
            }
            selectedView.fillTextLayoutBlocks(this.arrayList);
            if (maybe) {
                layoutBlock.layout = this.arrayList.get(this.maybeTextIndex).getLayout();
            } else {
                int index = this.startPeek ? this.startViewChildPosition : this.endViewChildPosition;
                if (index < 0 || index >= this.arrayList.size()) {
                    layoutBlock.layout = null;
                    return;
                }
                layoutBlock.layout = this.arrayList.get(index).getLayout();
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
            int index = this.startPeek ? this.startViewChildPosition : this.endViewChildPosition;
            if (index < 0 || index >= this.arrayList.size()) {
                return 0;
            }
            StaticLayout layout = this.arrayList.get(index).getLayout();
            int min = Integer.MAX_VALUE;
            for (int i = 0; i < layout.getLineCount(); i++) {
                int h = layout.getLineBottom(i) - layout.getLineTop(i);
                if (h < min) {
                    min = h;
                }
            }
            return min;
        }

        public void trySelect(View view) {
            if (this.maybeSelectedView != null) {
                this.startSelectionRunnable.run();
            }
        }

        public void setMaybeView(int x, int y, View parentView) {
            if (parentView instanceof ArticleSelectableView) {
                this.capturedX = x;
                this.capturedY = y;
                this.maybeSelectedView = (ArticleSelectableView) parentView;
                int findClosestLayoutIndex = findClosestLayoutIndex(x, y, (ArticleSelectableView) this.maybeSelectedView);
                this.maybeTextIndex = findClosestLayoutIndex;
                if (findClosestLayoutIndex < 0) {
                    this.maybeSelectedView = null;
                    return;
                }
                this.maybeTextX = this.arrayList.get(findClosestLayoutIndex).getX();
                this.maybeTextY = this.arrayList.get(this.maybeTextIndex).getY();
            }
        }

        private int findClosestLayoutIndex(int x, int y, ArticleSelectableView maybeSelectedView) {
            if (maybeSelectedView instanceof ViewGroup) {
                ViewGroup parent = (ViewGroup) maybeSelectedView;
                for (int i = 0; i < parent.getChildCount(); i++) {
                    View child = parent.getChildAt(i);
                    if ((child instanceof ArticleSelectableView) && y > child.getY() && y < child.getY() + child.getHeight()) {
                        return findClosestLayoutIndex((int) (x - child.getX()), (int) (y - child.getY()), (ArticleSelectableView) child);
                    }
                }
            }
            this.arrayList.clear();
            maybeSelectedView.fillTextLayoutBlocks(this.arrayList);
            if (this.arrayList.isEmpty()) {
                return -1;
            }
            int minDistance = Integer.MAX_VALUE;
            int minIndex = -1;
            int i2 = this.arrayList.size() - 1;
            while (true) {
                if (i2 < 0) {
                    break;
                }
                TextLayoutBlock block = this.arrayList.get(i2);
                int top = block.getY();
                int bottom = block.getLayout().getHeight() + top;
                if (y >= top && y < bottom) {
                    minDistance = 0;
                    minIndex = i2;
                    break;
                }
                int d = Math.min(Math.abs(y - top), Math.abs(y - bottom));
                if (d < minDistance) {
                    minDistance = d;
                    minIndex = i2;
                }
                i2--;
            }
            if (minIndex < 0) {
                return -1;
            }
            int row = this.arrayList.get(minIndex).getRow();
            if (row > 0 && minDistance < AndroidUtilities.dp(24.0f)) {
                int minDistanceX = Integer.MAX_VALUE;
                int minIndexX = minIndex;
                for (int i3 = this.arrayList.size() - 1; i3 >= 0; i3--) {
                    TextLayoutBlock block2 = this.arrayList.get(i3);
                    if (block2.getRow() == row) {
                        int left = block2.getX();
                        int right = block2.getX() + block2.getLayout().getWidth();
                        if (x >= left && x <= right) {
                            return i3;
                        }
                        int d2 = Math.min(Math.abs(x - left), Math.abs(x - right));
                        if (d2 < minDistanceX) {
                            minDistanceX = d2;
                            minIndexX = i3;
                        }
                    }
                }
                return minIndexX;
            }
            return minIndex;
        }

        public void draw(Canvas canvas, ArticleSelectableView view, int i) {
            this.selectionPaint.setColor(getThemedColor(Theme.key_chat_inTextSelectionHighlight));
            this.selectionHandlePaint.setColor(getThemedColor(Theme.key_chat_inTextSelectionHighlight));
            int position = getAdapterPosition(view);
            if (position < 0) {
                return;
            }
            this.arrayList.clear();
            view.fillTextLayoutBlocks(this.arrayList);
            if (!this.arrayList.isEmpty()) {
                TextLayoutBlock layoutBlock = this.arrayList.get(i);
                int endOffset = this.endViewOffset;
                int textLen = layoutBlock.getLayout().getText().length();
                if (endOffset > textLen) {
                    endOffset = textLen;
                }
                int i2 = this.startViewPosition;
                if (position == i2 && position == this.endViewPosition) {
                    int i3 = this.startViewChildPosition;
                    int i4 = this.endViewChildPosition;
                    if (i3 == i4 && i3 == i) {
                        drawSelection(canvas, layoutBlock.getLayout(), this.startViewOffset, endOffset, true, true);
                    } else if (i == i3) {
                        drawSelection(canvas, layoutBlock.getLayout(), this.startViewOffset, textLen, true, false);
                    } else if (i == i4) {
                        drawSelection(canvas, layoutBlock.getLayout(), 0, endOffset, false, true);
                    } else if (i > i3 && i < i4) {
                        drawSelection(canvas, layoutBlock.getLayout(), 0, textLen, false, false);
                    }
                } else if (position == i2 && this.startViewChildPosition == i) {
                    drawSelection(canvas, layoutBlock.getLayout(), this.startViewOffset, textLen, true, false);
                } else {
                    int i5 = this.endViewPosition;
                    if (position == i5 && this.endViewChildPosition == i) {
                        drawSelection(canvas, layoutBlock.getLayout(), 0, endOffset, false, true);
                    } else if ((position > i2 && position < i5) || ((position == i2 && i > this.startViewChildPosition) || (position == i5 && i < this.endViewChildPosition))) {
                        drawSelection(canvas, layoutBlock.getLayout(), 0, textLen, false, false);
                    }
                }
            }
        }

        private int getAdapterPosition(ArticleSelectableView view) {
            View child = (View) view;
            ViewParent parent = child.getParent();
            while (true) {
                if (parent != this.parentView && parent != null) {
                    if (parent instanceof View) {
                        child = (View) parent;
                        parent = child.getParent();
                    } else {
                        parent = null;
                        break;
                    }
                } else {
                    break;
                }
            }
            if (parent != null) {
                if (this.parentRecyclerView != null) {
                    return this.parentRecyclerView.getChildAdapterPosition(child);
                }
                return this.parentView.indexOfChild(child);
            }
            return -1;
        }

        @Override // org.telegram.ui.Cells.TextSelectionHelper
        public boolean isSelectable(View child) {
            if (child instanceof ArticleSelectableView) {
                this.arrayList.clear();
                ((ArticleSelectableView) child).fillTextLayoutBlocks(this.arrayList);
                if (!(child instanceof ArticleViewer.BlockTableCell)) {
                    return !this.arrayList.isEmpty();
                }
                return true;
            }
            return false;
        }

        public void onTextSelected(ArticleSelectableView newView, ArticleSelectableView oldView) {
            int position = getAdapterPosition(newView);
            if (position < 0) {
                return;
            }
            this.endViewPosition = position;
            this.startViewPosition = position;
            int i = this.maybeTextIndex;
            this.endViewChildPosition = i;
            this.startViewChildPosition = i;
            this.arrayList.clear();
            newView.fillTextLayoutBlocks(this.arrayList);
            int n = this.arrayList.size();
            this.childCountByPosition.put(position, n);
            for (int i2 = 0; i2 < n; i2++) {
                this.textByPosition.put((i2 << 16) + position, this.arrayList.get(i2).getLayout().getText());
                this.prefixTextByPosition.put((i2 << 16) + position, this.arrayList.get(i2).getPrefix());
            }
        }

        protected void onNewViewSelected(ArticleSelectableView oldView, ArticleSelectableView newView, int childPosition) {
            int i;
            int position = getAdapterPosition(newView);
            int oldPosition = -1;
            if (oldView != null) {
                oldPosition = getAdapterPosition(oldView);
            }
            invalidate();
            if (this.movingDirectionSettling && (i = this.startViewPosition) == this.endViewPosition) {
                if (position == i) {
                    if (childPosition < this.startViewChildPosition) {
                        this.startViewChildPosition = childPosition;
                        pickStartView();
                        this.movingHandleStart = true;
                        this.startViewOffset = this.selectionEnd;
                        this.selectionStart = this.selectionEnd - 1;
                    } else {
                        this.endViewChildPosition = childPosition;
                        pickEndView();
                        this.movingHandleStart = false;
                        this.endViewOffset = 0;
                    }
                } else if (position < i) {
                    this.startViewPosition = position;
                    this.startViewChildPosition = childPosition;
                    pickStartView();
                    this.movingHandleStart = true;
                    this.startViewOffset = this.selectionEnd;
                    this.selectionStart = this.selectionEnd - 1;
                } else {
                    this.endViewPosition = position;
                    this.endViewChildPosition = childPosition;
                    pickEndView();
                    this.movingHandleStart = false;
                    this.endViewOffset = 0;
                }
            } else if (this.movingHandleStart) {
                if (position == oldPosition) {
                    int i2 = this.endViewChildPosition;
                    if (childPosition <= i2 || position < this.endViewPosition) {
                        this.startViewPosition = position;
                        this.startViewChildPosition = childPosition;
                        pickStartView();
                        this.startViewOffset = this.selectionEnd;
                    } else {
                        this.endViewPosition = position;
                        this.startViewChildPosition = i2;
                        this.endViewChildPosition = childPosition;
                        this.startViewOffset = this.endViewOffset;
                        pickEndView();
                        this.endViewOffset = 0;
                        this.movingHandleStart = false;
                    }
                } else if (position <= this.endViewPosition) {
                    this.startViewPosition = position;
                    this.startViewChildPosition = childPosition;
                    pickStartView();
                    this.startViewOffset = this.selectionEnd;
                } else {
                    this.endViewPosition = position;
                    this.startViewChildPosition = this.endViewChildPosition;
                    this.endViewChildPosition = childPosition;
                    this.startViewOffset = this.endViewOffset;
                    pickEndView();
                    this.endViewOffset = 0;
                    this.movingHandleStart = false;
                }
            } else if (position == oldPosition) {
                int i3 = this.startViewChildPosition;
                if (childPosition >= i3 || position > this.startViewPosition) {
                    this.endViewPosition = position;
                    this.endViewChildPosition = childPosition;
                    pickEndView();
                    this.endViewOffset = 0;
                } else {
                    this.startViewPosition = position;
                    this.endViewChildPosition = i3;
                    this.startViewChildPosition = childPosition;
                    this.endViewOffset = this.startViewOffset;
                    pickStartView();
                    this.movingHandleStart = true;
                    this.startViewOffset = this.selectionEnd;
                }
            } else if (position >= this.startViewPosition) {
                this.endViewPosition = position;
                this.endViewChildPosition = childPosition;
                pickEndView();
                this.endViewOffset = 0;
            } else {
                this.startViewPosition = position;
                this.endViewChildPosition = this.startViewChildPosition;
                this.startViewChildPosition = childPosition;
                this.endViewOffset = this.startViewOffset;
                pickStartView();
                this.movingHandleStart = true;
                this.startViewOffset = this.selectionEnd;
            }
            this.arrayList.clear();
            newView.fillTextLayoutBlocks(this.arrayList);
            int n = this.arrayList.size();
            this.childCountByPosition.put(position, n);
            for (int i4 = 0; i4 < n; i4++) {
                this.textByPosition.put((i4 << 16) + position, this.arrayList.get(i4).getLayout().getText());
                this.prefixTextByPosition.put((i4 << 16) + position, this.arrayList.get(i4).getPrefix());
            }
        }

        @Override // org.telegram.ui.Cells.TextSelectionHelper
        protected void pickEndView() {
            if (!isSelectionMode()) {
                return;
            }
            this.startPeek = false;
            int i = this.endViewPosition;
            if (i >= 0) {
                ArticleSelectableView view = null;
                LinearLayoutManager linearLayoutManager = this.layoutManager;
                if (linearLayoutManager != null) {
                    view = (ArticleSelectableView) linearLayoutManager.findViewByPosition(i);
                } else if (i < this.parentView.getChildCount()) {
                    view = (ArticleSelectableView) this.parentView.getChildAt(this.endViewPosition);
                }
                if (view == null) {
                    this.selectedView = null;
                    return;
                }
                this.selectedView = view;
                if (this.startViewPosition != this.endViewPosition) {
                    this.selectionStart = 0;
                } else if (this.startViewChildPosition != this.endViewChildPosition) {
                    this.selectionStart = 0;
                } else {
                    this.selectionStart = this.startViewOffset;
                }
                this.selectionEnd = this.endViewOffset;
                CharSequence text = getText((ArticleSelectableView) this.selectedView, false);
                if (this.selectionEnd > text.length()) {
                    this.selectionEnd = text.length();
                }
                this.arrayList.clear();
                ((ArticleSelectableView) this.selectedView).fillTextLayoutBlocks(this.arrayList);
                if (!this.arrayList.isEmpty()) {
                    this.textX = this.arrayList.get(this.endViewChildPosition).getX();
                    this.textY = this.arrayList.get(this.endViewChildPosition).getY();
                }
            }
        }

        @Override // org.telegram.ui.Cells.TextSelectionHelper
        protected void pickStartView() {
            if (!isSelectionMode()) {
                return;
            }
            this.startPeek = true;
            int i = this.startViewPosition;
            if (i >= 0) {
                ArticleSelectableView view = null;
                LinearLayoutManager linearLayoutManager = this.layoutManager;
                if (linearLayoutManager != null) {
                    view = (ArticleSelectableView) linearLayoutManager.findViewByPosition(i);
                } else if (this.endViewPosition < this.parentView.getChildCount()) {
                    view = (ArticleSelectableView) this.parentView.getChildAt(this.startViewPosition);
                }
                if (view == null) {
                    this.selectedView = null;
                    return;
                }
                this.selectedView = view;
                if (this.startViewPosition != this.endViewPosition) {
                    this.selectionEnd = getText((ArticleSelectableView) this.selectedView, false).length();
                } else if (this.startViewChildPosition != this.endViewChildPosition) {
                    this.selectionEnd = getText((ArticleSelectableView) this.selectedView, false).length();
                } else {
                    this.selectionEnd = this.endViewOffset;
                }
                this.selectionStart = this.startViewOffset;
                this.arrayList.clear();
                ((ArticleSelectableView) this.selectedView).fillTextLayoutBlocks(this.arrayList);
                if (!this.arrayList.isEmpty()) {
                    this.textX = this.arrayList.get(this.startViewChildPosition).getX();
                    this.textY = this.arrayList.get(this.startViewChildPosition).getY();
                }
            }
        }

        @Override // org.telegram.ui.Cells.TextSelectionHelper
        protected void onOffsetChanged() {
            int position = getAdapterPosition((ArticleSelectableView) this.selectedView);
            int childPosition = this.startPeek ? this.startViewChildPosition : this.endViewChildPosition;
            if (position == this.startViewPosition && childPosition == this.startViewChildPosition) {
                this.startViewOffset = this.selectionStart;
            }
            if (position == this.endViewPosition && childPosition == this.endViewChildPosition) {
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
        public void clear(boolean instant) {
            super.clear(instant);
            this.startViewPosition = -1;
            this.endViewPosition = -1;
            this.startViewChildPosition = -1;
            this.endViewChildPosition = -1;
            this.textByPosition.clear();
            this.childCountByPosition.clear();
        }

        @Override // org.telegram.ui.Cells.TextSelectionHelper
        protected CharSequence getSelectedText() {
            SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
            int i = this.startViewPosition;
            while (true) {
                int i2 = this.endViewPosition;
                if (i > i2) {
                    break;
                }
                int i3 = this.startViewPosition;
                if (i == i3) {
                    int n = i3 == i2 ? this.endViewChildPosition : this.childCountByPosition.get(i) - 1;
                    for (int k = this.startViewChildPosition; k <= n; k++) {
                        CharSequence text = this.textByPosition.get((k << 16) + i);
                        if (text != null) {
                            int i4 = this.startViewPosition;
                            int i5 = this.endViewPosition;
                            if (i4 == i5 && k == this.endViewChildPosition && k == this.startViewChildPosition) {
                                int e = this.endViewOffset;
                                int s = this.startViewOffset;
                                if (e < s) {
                                    s = e;
                                    e = s;
                                }
                                int tmp = text.length();
                                if (s < tmp) {
                                    if (e > text.length()) {
                                        e = text.length();
                                    }
                                    stringBuilder.append(text.subSequence(s, e));
                                    stringBuilder.append('\n');
                                }
                            } else if (i4 == i5 && k == this.endViewChildPosition) {
                                CharSequence prefix = this.prefixTextByPosition.get((k << 16) + i);
                                if (prefix != null) {
                                    stringBuilder.append(prefix).append(' ');
                                }
                                int e2 = this.endViewOffset;
                                if (e2 > text.length()) {
                                    e2 = text.length();
                                }
                                stringBuilder.append(text.subSequence(0, e2));
                                stringBuilder.append('\n');
                            } else if (k == this.startViewChildPosition) {
                                int s2 = this.startViewOffset;
                                if (s2 < text.length()) {
                                    stringBuilder.append(text.subSequence(s2, text.length()));
                                    stringBuilder.append('\n');
                                }
                            } else {
                                CharSequence prefix2 = this.prefixTextByPosition.get((k << 16) + i);
                                if (prefix2 != null) {
                                    stringBuilder.append(prefix2).append(' ');
                                }
                                stringBuilder.append(text);
                                stringBuilder.append('\n');
                            }
                        }
                    }
                } else if (i == i2) {
                    for (int k2 = 0; k2 <= this.endViewChildPosition; k2++) {
                        CharSequence text2 = this.textByPosition.get((k2 << 16) + i);
                        if (text2 != null) {
                            if (this.startViewPosition == this.endViewPosition && k2 == this.endViewChildPosition && k2 == this.startViewChildPosition) {
                                int e3 = this.endViewOffset;
                                int s3 = this.startViewOffset;
                                if (s3 < text2.length()) {
                                    if (e3 > text2.length()) {
                                        e3 = text2.length();
                                    }
                                    stringBuilder.append(text2.subSequence(s3, e3));
                                    stringBuilder.append('\n');
                                }
                            } else if (k2 == this.endViewChildPosition) {
                                CharSequence prefix3 = this.prefixTextByPosition.get((k2 << 16) + i);
                                if (prefix3 != null) {
                                    stringBuilder.append(prefix3).append(' ');
                                }
                                int e4 = this.endViewOffset;
                                if (e4 > text2.length()) {
                                    e4 = text2.length();
                                }
                                stringBuilder.append(text2.subSequence(0, e4));
                                stringBuilder.append('\n');
                            } else {
                                CharSequence prefix4 = this.prefixTextByPosition.get((k2 << 16) + i);
                                if (prefix4 != null) {
                                    stringBuilder.append(prefix4).append(' ');
                                }
                                stringBuilder.append(text2);
                                stringBuilder.append('\n');
                            }
                        }
                    }
                } else {
                    int n2 = this.childCountByPosition.get(i);
                    for (int k3 = this.startViewChildPosition; k3 < n2; k3++) {
                        CharSequence prefix5 = this.prefixTextByPosition.get((k3 << 16) + i);
                        if (prefix5 != null) {
                            stringBuilder.append(prefix5).append(' ');
                        }
                        stringBuilder.append(this.textByPosition.get((k3 << 16) + i));
                        stringBuilder.append('\n');
                    }
                }
                i++;
            }
            int i6 = stringBuilder.length();
            if (i6 > 0) {
                IgnoreCopySpannable[] spans = (IgnoreCopySpannable[]) stringBuilder.getSpans(0, stringBuilder.length() - 1, IgnoreCopySpannable.class);
                for (IgnoreCopySpannable span : spans) {
                    int end = stringBuilder.getSpanEnd(span);
                    int start = stringBuilder.getSpanStart(span);
                    stringBuilder.delete(start, end);
                }
                return stringBuilder.subSequence(0, stringBuilder.length() - 1);
            }
            return null;
        }

        @Override // org.telegram.ui.Cells.TextSelectionHelper
        protected boolean selectLayout(int x, int y) {
            if (!this.multiselect) {
                return false;
            }
            if (y <= ((ArticleSelectableView) this.selectedView).getTop() || y >= ((ArticleSelectableView) this.selectedView).getBottom()) {
                int n = this.parentView.getChildCount();
                for (int i = 0; i < n; i++) {
                    if (isSelectable(this.parentView.getChildAt(i))) {
                        ArticleSelectableView child = (ArticleSelectableView) this.parentView.getChildAt(i);
                        if (y > child.getTop() && y < child.getBottom()) {
                            int index = findClosestLayoutIndex((int) (x - child.getX()), (int) (y - child.getY()), child);
                            if (index < 0) {
                                return false;
                            }
                            onNewViewSelected((ArticleSelectableView) this.selectedView, child, index);
                            this.selectedView = child;
                            return true;
                        }
                    }
                }
                return false;
            }
            int currentChildPosition = this.startPeek ? this.startViewChildPosition : this.endViewChildPosition;
            int k = findClosestLayoutIndex((int) (x - ((ArticleSelectableView) this.selectedView).getX()), (int) (y - ((ArticleSelectableView) this.selectedView).getY()), (ArticleSelectableView) this.selectedView);
            if (k == currentChildPosition || k < 0) {
                return false;
            }
            onNewViewSelected((ArticleSelectableView) this.selectedView, (ArticleSelectableView) this.selectedView, k);
            return true;
        }

        @Override // org.telegram.ui.Cells.TextSelectionHelper
        protected boolean canSelect(int newSelection) {
            if (this.startViewPosition == this.endViewPosition && this.startViewChildPosition == this.endViewChildPosition) {
                return super.canSelect(newSelection);
            }
            return true;
        }

        public void jumpToLine(int newSelection, int nextWhitespace, boolean viewChanged, float newYoffset, float oldYoffset, ArticleSelectableView oldSelectedView) {
            if (viewChanged && oldSelectedView == this.selectedView && oldYoffset == newYoffset) {
                if (this.movingHandleStart) {
                    this.selectionStart = newSelection;
                    return;
                } else {
                    this.selectionEnd = newSelection;
                    return;
                }
            }
            super.jumpToLine(newSelection, nextWhitespace, viewChanged, newYoffset, oldYoffset, (float) oldSelectedView);
        }

        @Override // org.telegram.ui.Cells.TextSelectionHelper
        protected boolean canShowActions() {
            LinearLayoutManager linearLayoutManager = this.layoutManager;
            if (linearLayoutManager == null) {
                return true;
            }
            int firstV = linearLayoutManager.findFirstVisibleItemPosition();
            int lastV = this.layoutManager.findLastVisibleItemPosition();
            int i = this.startViewPosition;
            if ((firstV >= i && firstV <= this.endViewPosition) || (lastV >= i && lastV <= this.endViewPosition)) {
                return true;
            }
            return i >= firstV && this.endViewPosition <= lastV;
        }
    }

    /* loaded from: classes4.dex */
    public interface TextLayoutBlock {
        StaticLayout getLayout();

        CharSequence getPrefix();

        int getRow();

        int getX();

        int getY();

        /* renamed from: org.telegram.ui.Cells.TextSelectionHelper$TextLayoutBlock$-CC */
        /* loaded from: classes4.dex */
        public final /* synthetic */ class CC {
            public static CharSequence $default$getPrefix(TextLayoutBlock _this) {
                return null;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class PathCopyTo extends Path {
        private Path destination;

        public PathCopyTo(Path destination) {
            this.destination = destination;
        }

        @Override // android.graphics.Path
        public void reset() {
            super.reset();
        }

        @Override // android.graphics.Path
        public void addRect(float left, float top, float right, float bottom, Path.Direction dir) {
            this.destination.addRect(left, top, right, bottom, dir);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
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
        public void addRect(float left, float top, float right, float bottom, Path.Direction dir) {
            super.addRect(left, top, right, bottom, dir);
            if (bottom > this.lastBottom) {
                this.lastBottom = bottom;
            }
        }
    }

    /* loaded from: classes4.dex */
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
        public void addRect(float left, float top, float right, float bottom, Path.Direction dir) {
            RectF rectF;
            ArrayList<RectF> arrayList = recycled;
            if (arrayList != null && arrayList.size() > 0) {
                rectF = recycled.remove(0);
            } else {
                rectF = new RectF();
            }
            rectF.set(left, top, right, bottom);
            this.rects.add(rectF);
            this.rectsCount++;
            super.addRect(left, top, right, bottom, dir);
            if (bottom > this.lastBottom) {
                this.lastBottom = bottom;
            }
        }
    }

    public void setKeyboardSize(int keyboardSize) {
        this.keyboardSize = keyboardSize;
        invalidate();
    }

    public int getParentTopPadding() {
        return 0;
    }

    public int getParentBottomPadding() {
        return 0;
    }

    public int getThemedColor(String key) {
        return Theme.getColor(key);
    }

    protected Theme.ResourcesProvider getResourcesProvider() {
        return null;
    }
}
