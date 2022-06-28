package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import org.telegram.tgnet.TLObject;
/* loaded from: classes5.dex */
public class AvatarsImageView extends View {
    public final AvatarsDarawable avatarsDarawable;

    public AvatarsImageView(Context context, boolean inCall) {
        super(context);
        this.avatarsDarawable = new AvatarsDarawable(this, inCall);
    }

    @Override // android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.avatarsDarawable.width = getMeasuredWidth();
        this.avatarsDarawable.height = getMeasuredHeight();
    }

    @Override // android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.avatarsDarawable.onAttachedToWindow();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.avatarsDarawable.onDraw(canvas);
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.avatarsDarawable.onDetachedFromWindow();
    }

    public void setStyle(int style) {
        this.avatarsDarawable.setStyle(style);
    }

    public void setDelegate(Runnable delegate) {
        this.avatarsDarawable.setDelegate(delegate);
    }

    public void setObject(int a, int currentAccount, TLObject object) {
        this.avatarsDarawable.setObject(a, currentAccount, object);
    }

    public void reset() {
        this.avatarsDarawable.reset();
    }

    public void setCount(int usersCount) {
        this.avatarsDarawable.setCount(usersCount);
    }

    public void commitTransition(boolean animated) {
        this.avatarsDarawable.commitTransition(animated);
    }

    public void updateAfterTransitionEnd() {
        this.avatarsDarawable.updateAfterTransitionEnd();
    }

    public void setCentered(boolean centered) {
        this.avatarsDarawable.setCentered(centered);
    }
}
