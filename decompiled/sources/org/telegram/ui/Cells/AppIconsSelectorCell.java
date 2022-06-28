package org.telegram.ui.Cells;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.C;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.AppIconsSelectorCell;
import org.telegram.ui.Components.ColoredImageSpan;
import org.telegram.ui.Components.Easings;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.LauncherIconController;
/* loaded from: classes4.dex */
public class AppIconsSelectorCell extends RecyclerListView implements NotificationCenter.NotificationCenterDelegate {
    public static final float ICONS_ROUND_RADIUS = 18.0f;
    private List<LauncherIconController.LauncherIcon> availableIcons = new ArrayList();
    private int currentAccount;
    private LinearLayoutManager linearLayoutManager;

    public AppIconsSelectorCell(final Context context, final BaseFragment fragment, int currentAccount) {
        super(context);
        this.currentAccount = currentAccount;
        setPadding(0, AndroidUtilities.dp(12.0f), 0, AndroidUtilities.dp(12.0f));
        setFocusable(false);
        setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        setItemAnimator(null);
        setLayoutAnimation(null);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 0, false);
        this.linearLayoutManager = linearLayoutManager;
        setLayoutManager(linearLayoutManager);
        setAdapter(new RecyclerView.Adapter() { // from class: org.telegram.ui.Cells.AppIconsSelectorCell.1
            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new RecyclerListView.Holder(new IconHolderView(parent.getContext()));
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                IconHolderView holderView = (IconHolderView) holder.itemView;
                LauncherIconController.LauncherIcon icon = (LauncherIconController.LauncherIcon) AppIconsSelectorCell.this.availableIcons.get(position);
                holderView.bind(icon);
                holderView.iconView.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(18.0f), 0, Theme.getColor(Theme.key_listSelector), -16777216));
                holderView.iconView.setForeground(icon.foreground);
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public int getItemCount() {
                return AppIconsSelectorCell.this.availableIcons.size();
            }
        });
        addItemDecoration(new RecyclerView.ItemDecoration() { // from class: org.telegram.ui.Cells.AppIconsSelectorCell.2
            @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int pos = parent.getChildViewHolder(view).getAdapterPosition();
                if (pos == 0) {
                    outRect.left = AndroidUtilities.dp(18.0f);
                }
                if (pos == AppIconsSelectorCell.this.getAdapter().getItemCount() - 1) {
                    outRect.right = AndroidUtilities.dp(18.0f);
                    return;
                }
                int itemCount = AppIconsSelectorCell.this.getAdapter().getItemCount();
                if (itemCount == 4) {
                    outRect.right = ((AppIconsSelectorCell.this.getWidth() - AndroidUtilities.dp(36.0f)) - (AndroidUtilities.dp(58.0f) * itemCount)) / (itemCount - 1);
                } else {
                    outRect.right = AndroidUtilities.dp(24.0f);
                }
            }
        });
        setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.Cells.AppIconsSelectorCell$$ExternalSyntheticLambda0
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i) {
                AppIconsSelectorCell.this.m1631lambda$new$0$orgtelegramuiCellsAppIconsSelectorCell(fragment, context, view, i);
            }
        });
        updateIconsVisibility();
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Cells-AppIconsSelectorCell */
    public /* synthetic */ void m1631lambda$new$0$orgtelegramuiCellsAppIconsSelectorCell(BaseFragment fragment, Context context, View view, int position) {
        IconHolderView holderView = (IconHolderView) view;
        LauncherIconController.LauncherIcon icon = this.availableIcons.get(position);
        if (icon.premium && !UserConfig.hasPremiumOnAccounts()) {
            fragment.showDialog(new PremiumFeatureBottomSheet(fragment, 10, true));
        } else if (!LauncherIconController.isEnabled(icon)) {
            LinearSmoothScroller smoothScroller = new LinearSmoothScroller(context) { // from class: org.telegram.ui.Cells.AppIconsSelectorCell.3
                @Override // androidx.recyclerview.widget.LinearSmoothScroller
                public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference) {
                    return (boxStart - viewStart) + AndroidUtilities.dp(16.0f);
                }

                @Override // androidx.recyclerview.widget.LinearSmoothScroller
                public float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                    return super.calculateSpeedPerPixel(displayMetrics) * 3.0f;
                }
            };
            smoothScroller.setTargetPosition(position);
            this.linearLayoutManager.startSmoothScroll(smoothScroller);
            LauncherIconController.setIcon(icon);
            holderView.setSelected(true, true);
            for (int i = 0; i < getChildCount(); i++) {
                IconHolderView otherView = (IconHolderView) getChildAt(i);
                if (otherView != holderView) {
                    otherView.setSelected(false, true);
                }
            }
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showBulletin, 5, icon);
        }
    }

    private void updateIconsVisibility() {
        this.availableIcons.clear();
        this.availableIcons.addAll(Arrays.asList(LauncherIconController.LauncherIcon.values()));
        if (MessagesController.getInstance(this.currentAccount).premiumLocked) {
            int i = 0;
            while (i < this.availableIcons.size()) {
                if (this.availableIcons.get(i).premium) {
                    this.availableIcons.remove(i);
                    i--;
                }
                i++;
            }
        }
        getAdapter().notifyDataSetChanged();
        invalidateItemDecorations();
        for (int i2 = 0; i2 < this.availableIcons.size(); i2++) {
            LauncherIconController.LauncherIcon icon = this.availableIcons.get(i2);
            if (LauncherIconController.isEnabled(icon)) {
                this.linearLayoutManager.scrollToPositionWithOffset(i2, AndroidUtilities.dp(16.0f));
                return;
            }
        }
    }

    @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        invalidateItemDecorations();
    }

    @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View
    public void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthSpec), C.BUFFER_FLAG_ENCRYPTED), heightSpec);
    }

    @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.premiumStatusChangedGlobal);
    }

    @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.premiumStatusChangedGlobal);
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.premiumStatusChangedGlobal) {
            updateIconsVisibility();
        }
    }

    /* loaded from: classes4.dex */
    public static final class IconHolderView extends LinearLayout {
        private AdaptiveIconImageView iconView;
        private Paint outlinePaint;
        private float progress;
        private TextView titleView;

        private IconHolderView(Context context) {
            super(context);
            this.outlinePaint = new Paint(1);
            setOrientation(1);
            setWillNotDraw(false);
            AdaptiveIconImageView adaptiveIconImageView = new AdaptiveIconImageView(context);
            this.iconView = adaptiveIconImageView;
            adaptiveIconImageView.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
            addView(this.iconView, LayoutHelper.createLinear(58, 58, 1));
            TextView textView = new TextView(context);
            this.titleView = textView;
            textView.setSingleLine();
            this.titleView.setTextSize(1, 13.0f);
            this.titleView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            addView(this.titleView, LayoutHelper.createLinear(-2, -2, 1, 0, 4, 0, 0));
            this.outlinePaint.setStyle(Paint.Style.STROKE);
            this.outlinePaint.setStrokeWidth(Math.max(2, AndroidUtilities.dp(0.5f)));
        }

        @Override // android.view.View
        public void draw(Canvas canvas) {
            super.draw(canvas);
            float stroke = this.outlinePaint.getStrokeWidth();
            AndroidUtilities.rectTmp.set(this.iconView.getLeft() + stroke, this.iconView.getTop() + stroke, this.iconView.getRight() - stroke, this.iconView.getBottom() - stroke);
            canvas.drawRoundRect(AndroidUtilities.rectTmp, AndroidUtilities.dp(18.0f), AndroidUtilities.dp(18.0f), this.outlinePaint);
        }

        private void setProgress(float progress) {
            this.progress = progress;
            this.titleView.setTextColor(ColorUtils.blendARGB(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText), Theme.getColor(Theme.key_windowBackgroundWhiteValueText), progress));
            this.outlinePaint.setColor(ColorUtils.blendARGB(ColorUtils.setAlphaComponent(Theme.getColor(Theme.key_switchTrack), 63), Theme.getColor(Theme.key_windowBackgroundWhiteValueText), progress));
            this.outlinePaint.setStrokeWidth(Math.max(2, AndroidUtilities.dp(AndroidUtilities.lerp(0.5f, 2.0f, progress))));
            invalidate();
        }

        public void setSelected(boolean selected, boolean animate) {
            float to = selected ? 1.0f : 0.0f;
            float f = this.progress;
            if (to == f && animate) {
                return;
            }
            if (animate) {
                ValueAnimator animator = ValueAnimator.ofFloat(f, to).setDuration(250L);
                animator.setInterpolator(Easings.easeInOutQuad);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Cells.AppIconsSelectorCell$IconHolderView$$ExternalSyntheticLambda0
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        AppIconsSelectorCell.IconHolderView.this.m1632xfd226fea(valueAnimator);
                    }
                });
                animator.start();
                return;
            }
            setProgress(to);
        }

        /* renamed from: lambda$setSelected$0$org-telegram-ui-Cells-AppIconsSelectorCell$IconHolderView */
        public /* synthetic */ void m1632xfd226fea(ValueAnimator animation) {
            setProgress(((Float) animation.getAnimatedValue()).floatValue());
        }

        public void bind(LauncherIconController.LauncherIcon icon) {
            this.iconView.setImageResource(icon.background);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) this.titleView.getLayoutParams();
            if (icon.premium && !UserConfig.hasPremiumOnAccounts()) {
                SpannableString str = new SpannableString("d " + LocaleController.getString(icon.title));
                ColoredImageSpan span = new ColoredImageSpan((int) R.drawable.msg_mini_premiumlock);
                span.setTopOffset(1);
                span.setSize(AndroidUtilities.dp(13.0f));
                str.setSpan(span, 0, 1, 33);
                params.rightMargin = AndroidUtilities.dp(4.0f);
                this.titleView.setText(str);
            } else {
                params.rightMargin = 0;
                this.titleView.setText(LocaleController.getString(icon.title));
            }
            setSelected(LauncherIconController.isEnabled(icon), false);
        }
    }

    /* loaded from: classes4.dex */
    public static class AdaptiveIconImageView extends ImageView {
        private Drawable foreground;
        private Path path = new Path();
        private int outerPadding = AndroidUtilities.dp(5.0f);
        private int backgroundOuterPadding = AndroidUtilities.dp(42.0f);

        public AdaptiveIconImageView(Context context) {
            super(context);
        }

        public void setForeground(int res) {
            this.foreground = ContextCompat.getDrawable(getContext(), res);
            invalidate();
        }

        @Override // android.view.View
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            updatePath();
        }

        public void setPadding(int padding) {
            setPadding(padding, padding, padding, padding);
        }

        public void setOuterPadding(int outerPadding) {
            this.outerPadding = outerPadding;
        }

        public void setBackgroundOuterPadding(int backgroundOuterPadding) {
            this.backgroundOuterPadding = backgroundOuterPadding;
        }

        @Override // android.view.View
        public void draw(Canvas canvas) {
            canvas.save();
            canvas.clipPath(this.path);
            canvas.scale((this.backgroundOuterPadding / getWidth()) + 1.0f, (this.backgroundOuterPadding / getHeight()) + 1.0f, getWidth() / 2.0f, getHeight() / 2.0f);
            super.draw(canvas);
            canvas.restore();
            Drawable drawable = this.foreground;
            if (drawable != null) {
                int i = this.outerPadding;
                drawable.setBounds(-i, -i, getWidth() + this.outerPadding, getHeight() + this.outerPadding);
                this.foreground.draw(canvas);
            }
        }

        private void updatePath() {
            this.path.rewind();
            this.path.addCircle(getWidth() / 2.0f, getHeight() / 2.0f, Math.min((getWidth() - getPaddingLeft()) - getPaddingRight(), (getHeight() - getPaddingTop()) - getPaddingBottom()) / 2.0f, Path.Direction.CW);
        }
    }
}
