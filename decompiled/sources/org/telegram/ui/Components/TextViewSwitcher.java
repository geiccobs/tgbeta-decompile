package org.telegram.ui.Components;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ViewSwitcher;
/* loaded from: classes5.dex */
public class TextViewSwitcher extends ViewSwitcher {
    public TextViewSwitcher(Context context) {
        super(context);
    }

    public void setText(CharSequence text) {
        setText(text, true);
    }

    public void setText(CharSequence text, boolean animated) {
        if (!TextUtils.equals(text, getCurrentView().getText())) {
            if (animated) {
                getNextView().setText(text);
                showNext();
                return;
            }
            getCurrentView().setText(text);
        }
    }

    @Override // android.widget.ViewSwitcher, android.widget.ViewAnimator, android.view.ViewGroup
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (!(child instanceof TextView)) {
            throw new IllegalArgumentException();
        }
        super.addView(child, index, params);
    }

    @Override // android.widget.ViewAnimator
    public TextView getCurrentView() {
        return (TextView) super.getCurrentView();
    }

    @Override // android.widget.ViewSwitcher
    public TextView getNextView() {
        return (TextView) super.getNextView();
    }

    public void invalidateViews() {
        getCurrentView().invalidate();
        getNextView().invalidate();
    }
}
