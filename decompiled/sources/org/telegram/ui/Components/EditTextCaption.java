package org.telegram.ui.Components;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.CharacterStyle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import com.google.android.exoplayer2.C;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.TextStyleSpan;
/* loaded from: classes5.dex */
public class EditTextCaption extends EditTextBoldCursor {
    private static final int ACCESSIBILITY_ACTION_SHARE = 268435456;
    private boolean allowTextEntitiesIntersection;
    private String caption;
    private StaticLayout captionLayout;
    private boolean copyPasteShowed;
    private EditTextCaptionDelegate delegate;
    private int hintColor;
    private boolean isInitLineCount;
    private int lineCount;
    private float offsetY;
    private final Theme.ResourcesProvider resourcesProvider;
    private int userNameLength;
    private int xOffset;
    private int yOffset;
    private int triesCount = 0;
    private int selectionStart = -1;
    private int selectionEnd = -1;

    /* loaded from: classes5.dex */
    public interface EditTextCaptionDelegate {
        void onSpansChanged();
    }

    public EditTextCaption(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.resourcesProvider = resourcesProvider;
        addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.Components.EditTextCaption.1
            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable s) {
                if (EditTextCaption.this.lineCount != EditTextCaption.this.getLineCount()) {
                    if (!EditTextCaption.this.isInitLineCount && EditTextCaption.this.getMeasuredWidth() > 0) {
                        EditTextCaption editTextCaption = EditTextCaption.this;
                        editTextCaption.onLineCountChanged(editTextCaption.lineCount, EditTextCaption.this.getLineCount());
                    }
                    EditTextCaption editTextCaption2 = EditTextCaption.this;
                    editTextCaption2.lineCount = editTextCaption2.getLineCount();
                }
            }
        });
    }

    protected void onLineCountChanged(int oldLineCount, int newLineCount) {
    }

    public void setCaption(String value) {
        String str = this.caption;
        if ((str == null || str.length() == 0) && (value == null || value.length() == 0)) {
            return;
        }
        String str2 = this.caption;
        if (str2 != null && str2.equals(value)) {
            return;
        }
        this.caption = value;
        if (value != null) {
            this.caption = value.replace('\n', ' ');
        }
        requestLayout();
    }

    public void setDelegate(EditTextCaptionDelegate editTextCaptionDelegate) {
        this.delegate = editTextCaptionDelegate;
    }

    public void setAllowTextEntitiesIntersection(boolean value) {
        this.allowTextEntitiesIntersection = value;
    }

    public void makeSelectedBold() {
        TextStyleSpan.TextStyleRun run = new TextStyleSpan.TextStyleRun();
        run.flags |= 1;
        applyTextStyleToSelection(new TextStyleSpan(run));
    }

    public void makeSelectedSpoiler() {
        TextStyleSpan.TextStyleRun run = new TextStyleSpan.TextStyleRun();
        run.flags |= 256;
        applyTextStyleToSelection(new TextStyleSpan(run));
    }

    public void makeSelectedItalic() {
        TextStyleSpan.TextStyleRun run = new TextStyleSpan.TextStyleRun();
        run.flags |= 2;
        applyTextStyleToSelection(new TextStyleSpan(run));
    }

    public void makeSelectedMono() {
        TextStyleSpan.TextStyleRun run = new TextStyleSpan.TextStyleRun();
        run.flags |= 4;
        applyTextStyleToSelection(new TextStyleSpan(run));
    }

    public void makeSelectedStrike() {
        TextStyleSpan.TextStyleRun run = new TextStyleSpan.TextStyleRun();
        run.flags |= 8;
        applyTextStyleToSelection(new TextStyleSpan(run));
    }

    public void makeSelectedUnderline() {
        TextStyleSpan.TextStyleRun run = new TextStyleSpan.TextStyleRun();
        run.flags |= 16;
        applyTextStyleToSelection(new TextStyleSpan(run));
    }

    public void makeSelectedUrl() {
        final int end;
        final int start;
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), this.resourcesProvider);
        builder.setTitle(LocaleController.getString("CreateLink", R.string.CreateLink));
        final EditTextBoldCursor editText = new EditTextBoldCursor(getContext()) { // from class: org.telegram.ui.Components.EditTextCaption.2
            @Override // org.telegram.ui.Components.EditTextBoldCursor, android.widget.TextView, android.view.View
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f), C.BUFFER_FLAG_ENCRYPTED));
            }
        };
        editText.setTextSize(1, 18.0f);
        editText.setText("http://");
        editText.setTextColor(getThemedColor(Theme.key_dialogTextBlack));
        editText.setHintText(LocaleController.getString("URL", R.string.URL));
        editText.setHeaderHintColor(getThemedColor(Theme.key_windowBackgroundWhiteBlueHeader));
        editText.setSingleLine(true);
        editText.setFocusable(true);
        editText.setTransformHintToHeader(true);
        editText.setLineColors(getThemedColor(Theme.key_windowBackgroundWhiteInputField), getThemedColor(Theme.key_windowBackgroundWhiteInputFieldActivated), getThemedColor(Theme.key_windowBackgroundWhiteRedText3));
        editText.setImeOptions(6);
        editText.setBackgroundDrawable(null);
        editText.requestFocus();
        editText.setPadding(0, 0, 0, 0);
        builder.setView(editText);
        if (this.selectionStart >= 0 && this.selectionEnd >= 0) {
            start = this.selectionStart;
            end = this.selectionEnd;
            this.selectionEnd = -1;
            this.selectionStart = -1;
        } else {
            start = getSelectionStart();
            end = getSelectionEnd();
        }
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.EditTextCaption$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                EditTextCaption.this.m2560x8e956f7d(start, end, editText, dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        builder.show().setOnShowListener(new DialogInterface.OnShowListener() { // from class: org.telegram.ui.Components.EditTextCaption$$ExternalSyntheticLambda1
            @Override // android.content.DialogInterface.OnShowListener
            public final void onShow(DialogInterface dialogInterface) {
                EditTextCaption.lambda$makeSelectedUrl$1(EditTextBoldCursor.this, dialogInterface);
            }
        });
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) editText.getLayoutParams();
        if (layoutParams != null) {
            if (layoutParams instanceof FrameLayout.LayoutParams) {
                ((FrameLayout.LayoutParams) layoutParams).gravity = 1;
            }
            int dp = AndroidUtilities.dp(24.0f);
            layoutParams.leftMargin = dp;
            layoutParams.rightMargin = dp;
            layoutParams.height = AndroidUtilities.dp(36.0f);
            editText.setLayoutParams(layoutParams);
        }
        editText.setSelection(0, editText.getText().length());
    }

    /* renamed from: lambda$makeSelectedUrl$0$org-telegram-ui-Components-EditTextCaption */
    public /* synthetic */ void m2560x8e956f7d(int start, int end, EditTextBoldCursor editText, DialogInterface dialogInterface, int i) {
        Editable editable = getText();
        CharacterStyle[] spans = (CharacterStyle[]) editable.getSpans(start, end, CharacterStyle.class);
        if (spans != null && spans.length > 0) {
            for (CharacterStyle oldSpan : spans) {
                int spanStart = editable.getSpanStart(oldSpan);
                int spanEnd = editable.getSpanEnd(oldSpan);
                editable.removeSpan(oldSpan);
                if (spanStart < start) {
                    editable.setSpan(oldSpan, spanStart, start, 33);
                }
                if (spanEnd > end) {
                    editable.setSpan(oldSpan, end, spanEnd, 33);
                }
            }
        }
        try {
            editable.setSpan(new URLSpanReplacement(editText.getText().toString()), start, end, 33);
        } catch (Exception e) {
        }
        EditTextCaptionDelegate editTextCaptionDelegate = this.delegate;
        if (editTextCaptionDelegate != null) {
            editTextCaptionDelegate.onSpansChanged();
        }
    }

    public static /* synthetic */ void lambda$makeSelectedUrl$1(EditTextBoldCursor editText, DialogInterface dialog) {
        editText.requestFocus();
        AndroidUtilities.showKeyboard(editText);
    }

    public void makeSelectedRegular() {
        applyTextStyleToSelection(null);
    }

    public void setSelectionOverride(int start, int end) {
        this.selectionStart = start;
        this.selectionEnd = end;
    }

    private void applyTextStyleToSelection(TextStyleSpan span) {
        int end;
        int start;
        if (this.selectionStart >= 0 && this.selectionEnd >= 0) {
            start = this.selectionStart;
            end = this.selectionEnd;
            this.selectionEnd = -1;
            this.selectionStart = -1;
        } else {
            start = getSelectionStart();
            end = getSelectionEnd();
        }
        MediaDataController.addStyleToText(span, start, end, getText(), this.allowTextEntitiesIntersection);
        EditTextCaptionDelegate editTextCaptionDelegate = this.delegate;
        if (editTextCaptionDelegate != null) {
            editTextCaptionDelegate.onSpansChanged();
        }
    }

    @Override // android.widget.TextView, android.view.View
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if (Build.VERSION.SDK_INT < 23 && !hasWindowFocus && this.copyPasteShowed) {
            return;
        }
        try {
            super.onWindowFocusChanged(hasWindowFocus);
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    private ActionMode.Callback overrideCallback(final ActionMode.Callback callback) {
        final ActionMode.Callback wrap = new ActionMode.Callback() { // from class: org.telegram.ui.Components.EditTextCaption.3
            @Override // android.view.ActionMode.Callback
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                EditTextCaption.this.copyPasteShowed = true;
                return callback.onCreateActionMode(mode, menu);
            }

            @Override // android.view.ActionMode.Callback
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return callback.onPrepareActionMode(mode, menu);
            }

            @Override // android.view.ActionMode.Callback
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                if (EditTextCaption.this.performMenuAction(item.getItemId())) {
                    mode.finish();
                    return true;
                }
                try {
                    return callback.onActionItemClicked(mode, item);
                } catch (Exception e) {
                    return true;
                }
            }

            @Override // android.view.ActionMode.Callback
            public void onDestroyActionMode(ActionMode mode) {
                EditTextCaption.this.copyPasteShowed = false;
                callback.onDestroyActionMode(mode);
            }
        };
        if (Build.VERSION.SDK_INT >= 23) {
            return new ActionMode.Callback2() { // from class: org.telegram.ui.Components.EditTextCaption.4
                @Override // android.view.ActionMode.Callback
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    return wrap.onCreateActionMode(mode, menu);
                }

                @Override // android.view.ActionMode.Callback
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return wrap.onPrepareActionMode(mode, menu);
                }

                @Override // android.view.ActionMode.Callback
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    return wrap.onActionItemClicked(mode, item);
                }

                @Override // android.view.ActionMode.Callback
                public void onDestroyActionMode(ActionMode mode) {
                    wrap.onDestroyActionMode(mode);
                }

                @Override // android.view.ActionMode.Callback2
                public void onGetContentRect(ActionMode mode, View view, android.graphics.Rect outRect) {
                    ActionMode.Callback callback2 = callback;
                    if (callback2 instanceof ActionMode.Callback2) {
                        ((ActionMode.Callback2) callback2).onGetContentRect(mode, view, outRect);
                    } else {
                        super.onGetContentRect(mode, view, outRect);
                    }
                }
            };
        }
        return wrap;
    }

    public boolean performMenuAction(int itemId) {
        if (itemId == R.id.menu_regular) {
            makeSelectedRegular();
            return true;
        } else if (itemId == R.id.menu_bold) {
            makeSelectedBold();
            return true;
        } else if (itemId == R.id.menu_italic) {
            makeSelectedItalic();
            return true;
        } else if (itemId == R.id.menu_mono) {
            makeSelectedMono();
            return true;
        } else if (itemId == R.id.menu_link) {
            makeSelectedUrl();
            return true;
        } else if (itemId == R.id.menu_strike) {
            makeSelectedStrike();
            return true;
        } else if (itemId == R.id.menu_underline) {
            makeSelectedUnderline();
            return true;
        } else if (itemId == R.id.menu_spoiler) {
            makeSelectedSpoiler();
            return true;
        } else {
            return false;
        }
    }

    @Override // org.telegram.ui.Components.EditTextBoldCursor, android.view.View
    public ActionMode startActionMode(ActionMode.Callback callback, int type) {
        return super.startActionMode(overrideCallback(callback), type);
    }

    @Override // org.telegram.ui.Components.EditTextBoldCursor, android.view.View
    public ActionMode startActionMode(ActionMode.Callback callback) {
        return super.startActionMode(overrideCallback(callback));
    }

    @Override // org.telegram.ui.Components.EditTextBoldCursor, android.widget.TextView, android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int index;
        try {
            this.isInitLineCount = getMeasuredWidth() == 0 && getMeasuredHeight() == 0;
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            if (this.isInitLineCount) {
                this.lineCount = getLineCount();
            }
            this.isInitLineCount = false;
        } catch (Exception e) {
            setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.dp(51.0f));
            FileLog.e(e);
        }
        this.captionLayout = null;
        String str = this.caption;
        if (str != null && str.length() > 0) {
            CharSequence text = getText();
            if (text.length() > 1 && text.charAt(0) == '@' && (index = TextUtils.indexOf(text, ' ')) != -1) {
                TextPaint paint = getPaint();
                CharSequence str2 = text.subSequence(0, index + 1);
                int size = (int) Math.ceil(paint.measureText(text, 0, index + 1));
                int width = (getMeasuredWidth() - getPaddingLeft()) - getPaddingRight();
                this.userNameLength = str2.length();
                CharSequence captionFinal = TextUtils.ellipsize(this.caption, paint, width - size, TextUtils.TruncateAt.END);
                this.xOffset = size;
                try {
                    StaticLayout staticLayout = new StaticLayout(captionFinal, getPaint(), width - size, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    this.captionLayout = staticLayout;
                    if (staticLayout.getLineCount() > 0) {
                        this.xOffset = (int) (this.xOffset + (-this.captionLayout.getLineLeft(0)));
                    }
                    this.yOffset = ((getMeasuredHeight() - this.captionLayout.getLineBottom(0)) / 2) + AndroidUtilities.dp(0.5f);
                } catch (Exception e2) {
                    FileLog.e(e2);
                }
            }
        }
    }

    public String getCaption() {
        return this.caption;
    }

    @Override // org.telegram.ui.Components.EditTextBoldCursor
    public void setHintColor(int value) {
        super.setHintColor(value);
        this.hintColor = value;
        invalidate();
    }

    public void setOffsetY(float offset) {
        this.offsetY = offset;
        invalidate();
    }

    public float getOffsetY() {
        return this.offsetY;
    }

    @Override // org.telegram.ui.Components.EditTextBoldCursor, org.telegram.ui.Components.EditTextEffects, android.widget.TextView, android.view.View
    public void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(0.0f, this.offsetY);
        super.onDraw(canvas);
        try {
            if (this.captionLayout != null && this.userNameLength == length()) {
                Paint paint = getPaint();
                int oldColor = getPaint().getColor();
                paint.setColor(this.hintColor);
                canvas.save();
                canvas.translate(this.xOffset, this.yOffset);
                this.captionLayout.draw(canvas);
                canvas.restore();
                paint.setColor(oldColor);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        canvas.restore();
    }

    @Override // org.telegram.ui.Components.EditTextBoldCursor, android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        AccessibilityNodeInfoCompat infoCompat = AccessibilityNodeInfoCompat.wrap(info);
        if (!TextUtils.isEmpty(this.caption)) {
            infoCompat.setHintText(this.caption);
        }
        List<AccessibilityNodeInfoCompat.AccessibilityActionCompat> actions = infoCompat.getActionList();
        int i = 0;
        int size = actions.size();
        while (true) {
            if (i >= size) {
                break;
            }
            AccessibilityNodeInfoCompat.AccessibilityActionCompat action = actions.get(i);
            if (action.getId() != 268435456) {
                i++;
            } else {
                infoCompat.removeAction(action);
                break;
            }
        }
        if (hasSelection()) {
            infoCompat.addAction(new AccessibilityNodeInfoCompat.AccessibilityActionCompat(R.id.menu_spoiler, LocaleController.getString("Spoiler", R.string.Spoiler)));
            infoCompat.addAction(new AccessibilityNodeInfoCompat.AccessibilityActionCompat(R.id.menu_bold, LocaleController.getString("Bold", R.string.Bold)));
            infoCompat.addAction(new AccessibilityNodeInfoCompat.AccessibilityActionCompat(R.id.menu_italic, LocaleController.getString("Italic", R.string.Italic)));
            infoCompat.addAction(new AccessibilityNodeInfoCompat.AccessibilityActionCompat(R.id.menu_mono, LocaleController.getString("Mono", R.string.Mono)));
            infoCompat.addAction(new AccessibilityNodeInfoCompat.AccessibilityActionCompat(R.id.menu_strike, LocaleController.getString("Strike", R.string.Strike)));
            infoCompat.addAction(new AccessibilityNodeInfoCompat.AccessibilityActionCompat(R.id.menu_underline, LocaleController.getString("Underline", R.string.Underline)));
            infoCompat.addAction(new AccessibilityNodeInfoCompat.AccessibilityActionCompat(R.id.menu_link, LocaleController.getString("CreateLink", R.string.CreateLink)));
            infoCompat.addAction(new AccessibilityNodeInfoCompat.AccessibilityActionCompat(R.id.menu_regular, LocaleController.getString("Regular", R.string.Regular)));
        }
    }

    @Override // android.view.View
    public boolean performAccessibilityAction(int action, Bundle arguments) {
        return performMenuAction(action) || super.performAccessibilityAction(action, arguments);
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
