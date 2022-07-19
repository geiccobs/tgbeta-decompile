package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.core.graphics.ColorUtils;
import androidx.core.math.MathUtils;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$StickerSet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.EmojiTabsStrip;
import org.telegram.ui.Components.EmojiView;
import org.telegram.ui.Components.Premium.PremiumLockIconView;
/* loaded from: classes3.dex */
public class EmojiTabsStrip extends ScrollableHorizontalScrollView {
    private ValueAnimator appearAnimation;
    private int appearCount;
    private LinearLayout contentView;
    private EmojiTabsView emojiTabs;
    private ArrayList<EmojiTabButton> emojipackTabs;
    private boolean includeAnimated;
    private Runnable onSettingsOpenRunnable;
    private EmojiView parent;
    private EmojiTabButton recentTab;
    private Theme.ResourcesProvider resourcesProvider;
    private ValueAnimator selectAnimator;
    private EmojiTabButton settingsTab;
    private static int[] emojiTabsDrawableIds = {R.drawable.msg_emoji_smiles, R.drawable.msg_emoji_cat, R.drawable.msg_emoji_food, R.drawable.msg_emoji_activities, R.drawable.msg_emoji_travel, R.drawable.msg_emoji_objects, R.drawable.msg_emoji_other, R.drawable.msg_emoji_flags};
    private static int[] emojiTabsAnimatedDrawableIds = {R.raw.msg_emoji_smiles, R.raw.msg_emoji_cat, R.raw.msg_emoji_food, R.raw.msg_emoji_activities, R.raw.msg_emoji_travel, R.raw.msg_emoji_objects, R.raw.msg_emoji_other, R.raw.msg_emoji_flags};
    private int recentDrawableId = R.drawable.msg_emoji_recent;
    private int settingsDrawableId = R.drawable.smiles_tab_settings;
    private float selectT = 0.0f;
    private float selectAnimationT = 0.0f;
    private int selected = 0;
    private int wasIndex = 0;
    boolean first = true;

    protected boolean onTabClick(int i) {
        throw null;
    }

    public EmojiTabsStrip(Context context, Theme.ResourcesProvider resourcesProvider, final boolean z, EmojiView emojiView, Runnable runnable) {
        super(context);
        this.parent = emojiView;
        this.includeAnimated = z;
        this.resourcesProvider = resourcesProvider;
        this.onSettingsOpenRunnable = runnable;
        LinearLayout linearLayout = new LinearLayout(context) { // from class: org.telegram.ui.Components.EmojiTabsStrip.1
            private Paint paint = new Paint(1);
            private RectF from = new RectF();
            private RectF to = new RectF();
            private RectF rect = new RectF();
            private Path path = new Path();

            @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
            protected void onLayout(boolean z2, int i, int i2, int i3, int i4) {
                int paddingLeft = getPaddingLeft();
                int i5 = (i4 - i2) / 2;
                for (int i6 = 0; i6 < getChildCount(); i6++) {
                    View childAt = getChildAt(i6);
                    if (childAt != EmojiTabsStrip.this.settingsTab && childAt != null) {
                        childAt.layout(paddingLeft, i5 - (childAt.getMeasuredHeight() / 2), childAt.getMeasuredWidth() + paddingLeft, (childAt.getMeasuredHeight() / 2) + i5);
                        paddingLeft += childAt.getMeasuredWidth() + AndroidUtilities.dp(3.0f);
                    }
                }
                if (EmojiTabsStrip.this.settingsTab != null) {
                    if (EmojiTabsStrip.this.settingsTab.getMeasuredWidth() + paddingLeft + getPaddingRight() <= EmojiTabsStrip.this.getMeasuredWidth()) {
                        int i7 = i3 - i;
                        EmojiTabsStrip.this.settingsTab.layout((i7 - getPaddingRight()) - EmojiTabsStrip.this.settingsTab.getMeasuredWidth(), i5 - (EmojiTabsStrip.this.settingsTab.getMeasuredHeight() / 2), i7 - getPaddingRight(), i5 + (EmojiTabsStrip.this.settingsTab.getMeasuredHeight() / 2));
                        return;
                    }
                    EmojiTabsStrip.this.settingsTab.layout(paddingLeft, i5 - (EmojiTabsStrip.this.settingsTab.getMeasuredHeight() / 2), EmojiTabsStrip.this.settingsTab.getMeasuredWidth() + paddingLeft, i5 + (EmojiTabsStrip.this.settingsTab.getMeasuredHeight() / 2));
                }
            }

            @Override // android.widget.LinearLayout, android.view.View
            protected void onMeasure(int i, int i2) {
                int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(99999999, Integer.MIN_VALUE);
                int paddingLeft = getPaddingLeft() + getPaddingRight();
                for (int i3 = 0; i3 < getChildCount(); i3++) {
                    View childAt = getChildAt(i3);
                    if (childAt != null) {
                        childAt.measure(makeMeasureSpec, i2);
                        paddingLeft += childAt.getMeasuredWidth() + (i3 + 1 < getChildCount() ? AndroidUtilities.dp(3.0f) : 0);
                    }
                }
                if (!z) {
                    setMeasuredDimension(View.MeasureSpec.getSize(i), View.MeasureSpec.getSize(i2));
                } else {
                    setMeasuredDimension(Math.max(paddingLeft, View.MeasureSpec.getSize(i)), View.MeasureSpec.getSize(i2));
                }
            }

            @Override // android.view.ViewGroup, android.view.View
            protected void dispatchDraw(Canvas canvas) {
                int floor = (int) Math.floor(EmojiTabsStrip.this.selectT);
                getChildBounds(floor, this.from);
                getChildBounds((int) Math.ceil(EmojiTabsStrip.this.selectT), this.to);
                AndroidUtilities.lerp(this.from, this.to, EmojiTabsStrip.this.selectT - floor, this.rect);
                float f = 0.0f;
                if (EmojiTabsStrip.this.emojiTabs != null) {
                    f = MathUtils.clamp(1.0f - Math.abs(EmojiTabsStrip.this.selectT - 1.0f), 0.0f, 1.0f);
                }
                float width = (this.rect.width() / 2.0f) * ((EmojiTabsStrip.this.selectAnimationT * 4.0f * (1.0f - EmojiTabsStrip.this.selectAnimationT) * 0.3f) + 1.0f);
                float height = this.rect.height() / 2.0f;
                RectF rectF = this.rect;
                rectF.set(rectF.centerX() - width, this.rect.centerY() - height, this.rect.centerX() + width, this.rect.centerY() + height);
                float dp = AndroidUtilities.dp(AndroidUtilities.lerp(8.0f, 16.0f, f));
                this.paint.setColor(EmojiTabsStrip.this.selectorColor());
                this.path.rewind();
                this.path.addRoundRect(this.rect, dp, dp, Path.Direction.CW);
                canvas.drawPath(this.path, this.paint);
                this.path.addCircle(EmojiTabsStrip.this.emojiTabs.getLeft() + AndroidUtilities.dp(15.0f), (EmojiTabsStrip.this.emojiTabs.getTop() + EmojiTabsStrip.this.emojiTabs.getBottom()) / 2.0f, AndroidUtilities.dp(15.0f), Path.Direction.CW);
                super.dispatchDraw(canvas);
            }

            @Override // android.view.ViewGroup
            protected boolean drawChild(Canvas canvas, View view, long j) {
                if (view == EmojiTabsStrip.this.emojiTabs) {
                    canvas.save();
                    canvas.clipPath(this.path);
                    boolean drawChild = super.drawChild(canvas, view, j);
                    canvas.restore();
                    return drawChild;
                }
                return super.drawChild(canvas, view, j);
            }

            private void getChildBounds(int i, RectF rectF) {
                View childAt = getChildAt(MathUtils.clamp(i, 0, getChildCount() - 1));
                rectF.set(childAt.getLeft(), childAt.getTop(), childAt.getRight(), childAt.getBottom());
            }
        };
        this.contentView = linearLayout;
        linearLayout.setPadding(AndroidUtilities.dp(11.0f), 0, AndroidUtilities.dp(3.0f), 0);
        this.contentView.setOrientation(0);
        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
        addView(this.contentView);
        LinearLayout linearLayout2 = this.contentView;
        EmojiTabButton emojiTabButton = new EmojiTabButton(context, this.recentDrawableId, false, false);
        this.recentTab = emojiTabButton;
        linearLayout2.addView(emojiTabButton);
        if (z) {
            if (runnable != null) {
                this.settingsTab = new EmojiTabButton(context, this.settingsDrawableId, false, true);
            }
            LinearLayout linearLayout3 = this.contentView;
            EmojiTabsView emojiTabsView = new EmojiTabsView(context);
            this.emojiTabs = emojiTabsView;
            linearLayout3.addView(emojiTabsView);
            updateEmojiPacks();
            return;
        }
        int i = 0;
        while (true) {
            int[] iArr = emojiTabsDrawableIds;
            if (i < iArr.length) {
                this.contentView.addView(new EmojiTabButton(context, iArr[i], false, i == 0));
                i++;
            } else {
                updateClickListeners();
                return;
            }
        }
    }

    private boolean isFreeEmojiPack(TLRPC$StickerSet tLRPC$StickerSet, ArrayList<TLRPC$Document> arrayList) {
        if (tLRPC$StickerSet == null || arrayList == null) {
            return false;
        }
        for (int i = 0; i < arrayList.size(); i++) {
            if (!MessageObject.isFreeEmoji(arrayList.get(i))) {
                return false;
            }
        }
        return true;
    }

    private TLRPC$Document getThumbDocument(TLRPC$StickerSet tLRPC$StickerSet, ArrayList<TLRPC$Document> arrayList) {
        if (tLRPC$StickerSet == null) {
            return null;
        }
        if (arrayList != null) {
            for (int i = 0; i < arrayList.size(); i++) {
                TLRPC$Document tLRPC$Document = arrayList.get(i);
                if (tLRPC$Document.id == tLRPC$StickerSet.thumb_document_id) {
                    return tLRPC$Document;
                }
            }
        }
        if (arrayList != null && arrayList.size() >= 1) {
            return arrayList.get(0);
        }
        return null;
    }

    public void updateEmojiPacks() {
        ArrayList<EmojiTabButton> arrayList;
        if (!this.includeAnimated) {
            return;
        }
        if (this.first && !MediaDataController.getInstance(UserConfig.selectedAccount).areStickersLoaded(5)) {
            return;
        }
        this.first = false;
        ArrayList<EmojiView.EmojiPack> emojipacks = this.parent.getEmojipacks();
        if (emojipacks == null) {
            return;
        }
        if (this.emojipackTabs == null) {
            this.emojipackTabs = new ArrayList<>();
        }
        this.contentView.removeView(this.settingsTab);
        boolean z = this.emojipackTabs.size() == 0 && emojipacks.size() > 0 && this.appearCount != emojipacks.size();
        if (this.appearAnimation != null && this.appearCount != emojipacks.size()) {
            this.appearAnimation.cancel();
            this.appearAnimation = null;
        }
        this.appearCount = emojipacks.size();
        boolean isPremium = UserConfig.getInstance(UserConfig.selectedAccount).isPremium();
        int i = 0;
        while (i < Math.max(this.emojipackTabs.size(), emojipacks.size())) {
            EmojiTabButton emojiTabButton = i >= this.emojipackTabs.size() ? null : this.emojipackTabs.get(i);
            AnimatedEmojiDrawable animatedEmojiDrawable = emojiTabButton == null ? null : (AnimatedEmojiDrawable) emojiTabButton.getDrawable();
            if (i >= emojipacks.size()) {
                if (animatedEmojiDrawable != null) {
                    animatedEmojiDrawable.removeView(emojiTabButton);
                }
                this.contentView.removeView(this.emojipackTabs.remove(i));
                i--;
            } else {
                EmojiView.EmojiPack emojiPack = emojipacks.get(i);
                TLRPC$Document thumbDocument = getThumbDocument(emojiPack.set, emojiPack.documents);
                if (thumbDocument != null && (animatedEmojiDrawable == null || animatedEmojiDrawable.getDocumentId() != thumbDocument.id)) {
                    animatedEmojiDrawable = AnimatedEmojiDrawable.make(UserConfig.selectedAccount, 3, thumbDocument);
                }
                AnimatedEmojiDrawable animatedEmojiDrawable2 = animatedEmojiDrawable;
                boolean isFreeEmojiPack = isFreeEmojiPack(emojiPack.set, emojiPack.documents);
                if (emojiTabButton == null) {
                    EmojiTabButton emojiTabButton2 = new EmojiTabButton(getContext(), (Drawable) animatedEmojiDrawable2, isFreeEmojiPack, false, false);
                    if (animatedEmojiDrawable2 != null) {
                        animatedEmojiDrawable2.addView(emojiTabButton2.imageView);
                    }
                    emojiTabButton = emojiTabButton2;
                } else {
                    if (emojiTabButton.getDrawable() instanceof AnimatedEmojiDrawable) {
                        ((AnimatedEmojiDrawable) emojiTabButton.getDrawable()).removeView(emojiTabButton.imageView);
                    }
                    emojiTabButton.setDrawable(animatedEmojiDrawable2);
                    if (animatedEmojiDrawable2 != null) {
                        animatedEmojiDrawable2.addView(emojiTabButton.imageView);
                    }
                }
                if (!isPremium && !isFreeEmojiPack) {
                    emojiTabButton.setLock(Boolean.TRUE);
                } else if (!emojiPack.installed) {
                    emojiTabButton.setLock(Boolean.FALSE);
                } else {
                    emojiTabButton.setLock(null);
                }
                if (i >= this.emojipackTabs.size()) {
                    this.emojipackTabs.add(emojiTabButton);
                    this.contentView.addView(emojiTabButton);
                } else {
                    this.emojipackTabs.set(i, emojiTabButton);
                }
            }
            i++;
        }
        EmojiTabButton emojiTabButton3 = this.settingsTab;
        if (emojiTabButton3 != null) {
            this.contentView.addView(emojiTabButton3);
        }
        if (z) {
            if (this.emojipackTabs != null) {
                for (int i2 = 0; i2 < this.emojipackTabs.size(); i2++) {
                    this.emojipackTabs.get(i2).setScaleX(0.0f);
                    this.emojipackTabs.get(i2).setScaleY(0.0f);
                }
            }
            this.appearAnimation = ValueAnimator.ofFloat(0.0f, 1.0f);
            final OvershootInterpolator overshootInterpolator = new OvershootInterpolator(3.0f);
            this.appearAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.EmojiTabsStrip$$ExternalSyntheticLambda1
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    EmojiTabsStrip.this.lambda$updateEmojiPacks$0(overshootInterpolator, valueAnimator);
                }
            });
            this.appearAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.EmojiTabsStrip.2
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animator) {
                    if (EmojiTabsStrip.this.emojipackTabs == null) {
                        return;
                    }
                    for (int i3 = 0; i3 < EmojiTabsStrip.this.emojipackTabs.size(); i3++) {
                        ((EmojiTabButton) EmojiTabsStrip.this.emojipackTabs.get(i3)).setScaleX(1.0f);
                        ((EmojiTabButton) EmojiTabsStrip.this.emojipackTabs.get(i3)).setScaleY(1.0f);
                    }
                }
            });
            this.appearAnimation.setStartDelay(150L);
            this.appearAnimation.setDuration((this.emojipackTabs == null ? 0 : arrayList.size()) * 75);
            this.appearAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            this.appearAnimation.start();
        }
        updateClickListeners();
    }

    public /* synthetic */ void lambda$updateEmojiPacks$0(OvershootInterpolator overshootInterpolator, ValueAnimator valueAnimator) {
        if (this.emojipackTabs == null) {
            return;
        }
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        int size = this.emojipackTabs.size();
        float f = size;
        float f2 = (1.0f / f) * 4.5f;
        for (int i = 0; i < size; i++) {
            float interpolation = overshootInterpolator.getInterpolation(MathUtils.clamp((floatValue - ((i / f) * (1.0f - f2))) / f2, 0.0f, 1.0f));
            this.emojipackTabs.get(i).setScaleX(interpolation);
            this.emojipackTabs.get(i).setScaleY(interpolation);
        }
    }

    public void updateClickListeners() {
        int i = 0;
        final int i2 = 0;
        while (i < this.contentView.getChildCount()) {
            View childAt = this.contentView.getChildAt(i);
            if (childAt instanceof EmojiTabsView) {
                EmojiTabsView emojiTabsView = (EmojiTabsView) childAt;
                int i3 = 0;
                while (i3 < emojiTabsView.contentView.getChildCount()) {
                    emojiTabsView.contentView.getChildAt(i3).setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.EmojiTabsStrip$$ExternalSyntheticLambda3
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view) {
                            EmojiTabsStrip.this.lambda$updateClickListeners$1(i2, view);
                        }
                    });
                    i3++;
                    i2++;
                }
                i2--;
            } else if (childAt != null) {
                childAt.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.EmojiTabsStrip$$ExternalSyntheticLambda4
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        EmojiTabsStrip.this.lambda$updateClickListeners$2(i2, view);
                    }
                });
            }
            i++;
            i2++;
        }
        EmojiTabButton emojiTabButton = this.settingsTab;
        if (emojiTabButton != null) {
            emojiTabButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.EmojiTabsStrip$$ExternalSyntheticLambda2
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    EmojiTabsStrip.this.lambda$updateClickListeners$3(view);
                }
            });
        }
    }

    public /* synthetic */ void lambda$updateClickListeners$1(int i, View view) {
        if (onTabClick(i)) {
            select(i);
        }
    }

    public /* synthetic */ void lambda$updateClickListeners$2(int i, View view) {
        if (onTabClick(i)) {
            select(i);
        }
    }

    public /* synthetic */ void lambda$updateClickListeners$3(View view) {
        Runnable runnable = this.onSettingsOpenRunnable;
        if (runnable != null) {
            runnable.run();
        }
    }

    @Override // android.widget.HorizontalScrollView, android.widget.FrameLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        this.contentView.setPadding(AndroidUtilities.dp(11.0f), 0, AndroidUtilities.dp(11.0f), 0);
        super.onMeasure(i, i2);
    }

    public void updateColors() {
        EmojiTabButton emojiTabButton = this.recentTab;
        if (emojiTabButton != null) {
            emojiTabButton.updateColor();
        }
    }

    public void select(int i) {
        int i2;
        int i3;
        int i4 = this.selected;
        int i5 = 0;
        int i6 = 0;
        while (i5 < this.contentView.getChildCount()) {
            View childAt = this.contentView.getChildAt(i5);
            if (childAt instanceof EmojiTabsView) {
                EmojiTabsView emojiTabsView = (EmojiTabsView) childAt;
                int i7 = i6;
                int i8 = 0;
                while (i8 < emojiTabsView.contentView.getChildCount()) {
                    View childAt2 = emojiTabsView.contentView.getChildAt(i8);
                    if (childAt2 instanceof EmojiTabButton) {
                        ((EmojiTabButton) childAt2).updateSelect(i == i7, true);
                    }
                    i8++;
                    i7++;
                }
                i3 = i7 - 1;
            } else {
                if (childAt instanceof EmojiTabButton) {
                    ((EmojiTabButton) childAt).updateSelect(i == i6, true);
                }
                i3 = i6;
            }
            if (i >= i6 && i <= i3) {
                this.selected = i5;
            }
            i5++;
            i6 = i3 + 1;
        }
        if (i4 != this.selected) {
            ValueAnimator valueAnimator = this.selectAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            final float f = this.selectT;
            final float f2 = this.selected;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.selectAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.EmojiTabsStrip$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    EmojiTabsStrip.this.lambda$select$4(f, f2, valueAnimator2);
                }
            });
            this.selectAnimator.setDuration(350L);
            this.selectAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            this.selectAnimator.start();
            EmojiTabsView emojiTabsView2 = this.emojiTabs;
            if (emojiTabsView2 != null) {
                emojiTabsView2.show(this.selected == 1);
            }
            View childAt3 = this.contentView.getChildAt(this.selected);
            if (this.selected >= 2) {
                scrollToVisible(childAt3.getLeft(), childAt3.getRight());
            } else {
                scrollTo(0);
            }
        }
        if (this.wasIndex != i) {
            EmojiTabsView emojiTabsView3 = this.emojiTabs;
            if (emojiTabsView3 != null && this.selected == 1 && i >= 1 && i <= emojiTabsView3.contentView.getChildCount() + 1) {
                this.emojiTabs.scrollToVisible(AndroidUtilities.dp(((i - 1) * 36) - 6), AndroidUtilities.dp(i2 + 30));
            }
            this.wasIndex = i;
        }
    }

    public /* synthetic */ void lambda$select$4(float f, float f2, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.selectAnimationT = floatValue;
        this.selectT = AndroidUtilities.lerp(f, f2, floatValue);
        this.contentView.invalidate();
    }

    public int selectorColor() {
        return Theme.getColor("chat_emojiPanelIcon", this.resourcesProvider) & 788529151;
    }

    /* loaded from: classes3.dex */
    public class EmojiTabButton extends ViewGroup {
        private boolean forceSelector;
        private ImageView imageView;
        private ValueAnimator lockAnimator;
        private float lockT;
        private PremiumLockIconView lockView;
        private RLottieDrawable lottieDrawable;
        private boolean round;
        private ValueAnimator selectAnimator;
        private float selectT;
        private boolean selected;
        private boolean wasVisible;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public EmojiTabButton(Context context, int i, int i2, boolean z, boolean z2) {
            super(context);
            EmojiTabsStrip.this = r8;
            this.round = z;
            this.forceSelector = z2;
            if (z) {
                setBackground(Theme.createCircleSelectorDrawable(r8.selectorColor(), 0, 0));
            } else if (z2) {
                setBackground(Theme.createRadSelectorDrawable(r8.selectorColor(), 8, 8));
            }
            if (Build.VERSION.SDK_INT >= 23) {
                RLottieDrawable rLottieDrawable = new RLottieDrawable(i2, "" + i2, AndroidUtilities.dp(24.0f), AndroidUtilities.dp(24.0f), false, null);
                this.lottieDrawable = rLottieDrawable;
                rLottieDrawable.setBounds(AndroidUtilities.dp(3.0f), AndroidUtilities.dp(3.0f), AndroidUtilities.dp(27.0f), AndroidUtilities.dp(27.0f));
                this.lottieDrawable.setMasterParent(this);
                this.lottieDrawable.setAllowDecodeSingleFrame(true);
                this.lottieDrawable.start();
            } else {
                ImageView imageView = new ImageView(context);
                this.imageView = imageView;
                imageView.setImageDrawable(context.getResources().getDrawable(i).mutate());
                addView(this.imageView);
            }
            setColor(Theme.getColor("chat_emojiPanelIcon", r8.resourcesProvider));
        }

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public EmojiTabButton(Context context, int i, boolean z, boolean z2) {
            super(context);
            EmojiTabsStrip.this = r1;
            this.round = z;
            this.forceSelector = z2;
            if (z) {
                setBackground(Theme.createCircleSelectorDrawable(r1.selectorColor(), 0, 0));
            } else if (z2) {
                setBackground(Theme.createRadSelectorDrawable(r1.selectorColor(), 8, 8));
            }
            ImageView imageView = new ImageView(context);
            this.imageView = imageView;
            imageView.setImageDrawable(context.getResources().getDrawable(i).mutate());
            setColor(Theme.getColor("chat_emojiPanelIcon", r1.resourcesProvider));
            addView(this.imageView);
        }

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public EmojiTabButton(Context context, Drawable drawable, boolean z, boolean z2, boolean z3) {
            super(context);
            EmojiTabsStrip.this = r1;
            this.round = z2;
            this.forceSelector = z3;
            if (z2) {
                setBackground(Theme.createCircleSelectorDrawable(r1.selectorColor(), 0, 0));
            } else if (z3) {
                setBackground(Theme.createRadSelectorDrawable(r1.selectorColor(), 8, 8));
            }
            ImageView imageView = new ImageView(context, r1) { // from class: org.telegram.ui.Components.EmojiTabsStrip.EmojiTabButton.1
                @Override // android.view.View
                public void invalidate() {
                    super.invalidate();
                    EmojiTabButton.this.updateLockImageReceiver();
                }

                @Override // android.view.View
                protected void dispatchDraw(Canvas canvas) {
                    if (getDrawable() != null) {
                        getDrawable().setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
                        getDrawable().setAlpha(255);
                        getDrawable().draw(canvas);
                    }
                }
            };
            this.imageView = imageView;
            imageView.setImageDrawable(drawable);
            addView(this.imageView);
            PremiumLockIconView premiumLockIconView = new PremiumLockIconView(context, PremiumLockIconView.TYPE_STICKERS_PREMIUM_LOCKED);
            this.lockView = premiumLockIconView;
            premiumLockIconView.setAlpha(0.0f);
            this.lockView.setScaleX(0.0f);
            this.lockView.setScaleY(0.0f);
            updateLockImageReceiver();
            this.lockView.setPadding(AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f));
            updateLockImageReceiver();
            addView(this.lockView);
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void dispatchDraw(Canvas canvas) {
            super.dispatchDraw(canvas);
            RLottieDrawable rLottieDrawable = this.lottieDrawable;
            if (rLottieDrawable != null) {
                rLottieDrawable.draw(canvas);
            }
        }

        @Override // android.view.View
        public boolean performClick() {
            RLottieDrawable rLottieDrawable = this.lottieDrawable;
            if (rLottieDrawable != null) {
                rLottieDrawable.setProgress(0.0f);
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.EmojiTabsStrip$EmojiTabButton$$ExternalSyntheticLambda3
                    @Override // java.lang.Runnable
                    public final void run() {
                        EmojiTabsStrip.EmojiTabButton.this.lambda$performClick$0();
                    }
                }, 75L);
            }
            return super.performClick();
        }

        public /* synthetic */ void lambda$performClick$0() {
            this.lottieDrawable.start();
        }

        public void updateVisibilityInbounds(boolean z) {
            RLottieDrawable rLottieDrawable;
            if (!this.wasVisible && z && (rLottieDrawable = this.lottieDrawable) != null && !rLottieDrawable.isRunning()) {
                this.lottieDrawable.setProgress(0.0f);
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.EmojiTabsStrip$EmojiTabButton$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        EmojiTabsStrip.EmojiTabButton.this.lambda$updateVisibilityInbounds$1();
                    }
                }, 75L);
            }
            this.wasVisible = z;
        }

        public /* synthetic */ void lambda$updateVisibilityInbounds$1() {
            this.lottieDrawable.start();
        }

        public void setLock(Boolean bool) {
            if (this.lockView == null) {
                return;
            }
            if (bool == null) {
                updateLock(false);
                return;
            }
            updateLock(true);
            if (bool.booleanValue()) {
                this.lockView.setImageResource(R.drawable.msg_mini_premiumlock);
                return;
            }
            Drawable mutate = getResources().getDrawable(R.drawable.zoom_plus).mutate();
            mutate.setColorFilter(new PorterDuffColorFilter(-1, PorterDuff.Mode.MULTIPLY));
            this.lockView.setImageDrawable(mutate);
        }

        private void updateLock(final boolean z) {
            ValueAnimator valueAnimator = this.lockAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            float f = 1.0f;
            if (Math.abs(this.lockT - (z ? 1.0f : 0.0f)) < 0.01f) {
                return;
            }
            this.lockView.setVisibility(0);
            float[] fArr = new float[2];
            fArr[0] = this.lockT;
            if (!z) {
                f = 0.0f;
            }
            fArr[1] = f;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
            this.lockAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.EmojiTabsStrip$EmojiTabButton$$ExternalSyntheticLambda1
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    EmojiTabsStrip.EmojiTabButton.this.lambda$updateLock$2(valueAnimator2);
                }
            });
            this.lockAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.EmojiTabsStrip.EmojiTabButton.2
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    if (!z) {
                        EmojiTabButton.this.lockView.setVisibility(8);
                    }
                }
            });
            this.lockAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.lockAnimator.setDuration(200L);
            this.lockAnimator.start();
        }

        public /* synthetic */ void lambda$updateLock$2(ValueAnimator valueAnimator) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            this.lockT = floatValue;
            this.lockView.setScaleX(floatValue);
            this.lockView.setScaleY(this.lockT);
            this.lockView.setAlpha(this.lockT);
        }

        public void updateLockImageReceiver() {
            ImageReceiver imageReceiver;
            PremiumLockIconView premiumLockIconView = this.lockView;
            if (premiumLockIconView == null || premiumLockIconView.ready() || !(getDrawable() instanceof AnimatedEmojiDrawable) || (imageReceiver = ((AnimatedEmojiDrawable) getDrawable()).getImageReceiver()) == null) {
                return;
            }
            this.lockView.setImageReceiver(imageReceiver);
            this.lockView.invalidate();
        }

        @Override // android.view.View
        protected void onMeasure(int i, int i2) {
            setMeasuredDimension(AndroidUtilities.dp(30.0f), AndroidUtilities.dp(30.0f));
            ImageView imageView = this.imageView;
            if (imageView != null) {
                imageView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0f), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0f), 1073741824));
            }
            PremiumLockIconView premiumLockIconView = this.lockView;
            if (premiumLockIconView != null) {
                premiumLockIconView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(12.0f), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(12.0f), 1073741824));
            }
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            ImageView imageView = this.imageView;
            if (imageView != null) {
                int i5 = (i3 - i) / 2;
                int i6 = (i4 - i2) / 2;
                imageView.layout(i5 - (imageView.getMeasuredWidth() / 2), i6 - (this.imageView.getMeasuredHeight() / 2), i5 + (this.imageView.getMeasuredWidth() / 2), i6 + (this.imageView.getMeasuredHeight() / 2));
            }
            PremiumLockIconView premiumLockIconView = this.lockView;
            if (premiumLockIconView != null) {
                int i7 = i3 - i;
                int i8 = i4 - i2;
                premiumLockIconView.layout(i7 - AndroidUtilities.dp(12.0f), i8 - AndroidUtilities.dp(12.0f), i7, i8);
            }
        }

        public Drawable getDrawable() {
            ImageView imageView = this.imageView;
            if (imageView != null) {
                return imageView.getDrawable();
            }
            return null;
        }

        public void setDrawable(Drawable drawable) {
            ImageReceiver imageReceiver;
            if (this.lockView != null && (drawable instanceof AnimatedEmojiDrawable) && (imageReceiver = ((AnimatedEmojiDrawable) drawable).getImageReceiver()) != null) {
                this.lockView.setImageReceiver(imageReceiver);
            }
            ImageView imageView = this.imageView;
            if (imageView != null) {
                imageView.setImageDrawable(drawable);
            }
        }

        public void updateSelect(final boolean z, boolean z2) {
            ImageView imageView = this.imageView;
            if ((imageView == null || imageView.getDrawable() != null) && this.selected != z) {
                this.selected = z;
                ValueAnimator valueAnimator = this.selectAnimator;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                    this.selectAnimator = null;
                }
                float f = 1.0f;
                if (z2) {
                    float[] fArr = new float[2];
                    fArr[0] = this.selectT;
                    if (!z) {
                        f = 0.0f;
                    }
                    fArr[1] = f;
                    ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
                    this.selectAnimator = ofFloat;
                    ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.EmojiTabsStrip$EmojiTabButton$$ExternalSyntheticLambda0
                        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                        public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                            EmojiTabsStrip.EmojiTabButton.this.lambda$updateSelect$3(valueAnimator2);
                        }
                    });
                    this.selectAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.EmojiTabsStrip.EmojiTabButton.3
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animator) {
                            if (!EmojiTabButton.this.round) {
                                if (z || EmojiTabButton.this.forceSelector) {
                                    if (EmojiTabButton.this.getBackground() != null) {
                                        return;
                                    }
                                    EmojiTabButton emojiTabButton = EmojiTabButton.this;
                                    emojiTabButton.setBackground(Theme.createRadSelectorDrawable(EmojiTabsStrip.this.selectorColor(), 8, 8));
                                    return;
                                }
                                EmojiTabButton.this.setBackground(null);
                            }
                        }
                    });
                    this.selectAnimator.setDuration(350L);
                    this.selectAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                    this.selectAnimator.start();
                    return;
                }
                if (!z) {
                    f = 0.0f;
                }
                this.selectT = f;
                updateColor();
            }
        }

        public /* synthetic */ void lambda$updateSelect$3(ValueAnimator valueAnimator) {
            this.selectT = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            setColor(ColorUtils.blendARGB(Theme.getColor("chat_emojiPanelIcon", EmojiTabsStrip.this.resourcesProvider), Theme.getColor("chat_emojiPanelIconSelected", EmojiTabsStrip.this.resourcesProvider), this.selectT));
        }

        public void updateColor() {
            Theme.setSelectorDrawableColor(getBackground(), EmojiTabsStrip.this.selectorColor(), false);
            setColor(ColorUtils.blendARGB(Theme.getColor("chat_emojiPanelIcon", EmojiTabsStrip.this.resourcesProvider), Theme.getColor("chat_emojiPanelIconSelected", EmojiTabsStrip.this.resourcesProvider), this.selectT));
        }

        private void setColor(int i) {
            PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY);
            ImageView imageView = this.imageView;
            if (imageView != null) {
                imageView.setColorFilter(porterDuffColorFilter);
                this.imageView.invalidate();
            }
            RLottieDrawable rLottieDrawable = this.lottieDrawable;
            if (rLottieDrawable != null) {
                rLottieDrawable.setColorFilter(porterDuffColorFilter);
                invalidate();
            }
        }
    }

    /* loaded from: classes3.dex */
    public class EmojiTabsView extends ScrollableHorizontalScrollView {
        private LinearLayout contentView;
        private ValueAnimator showAnimator;
        private boolean touching = false;
        private boolean scrollingAnimation = false;
        private boolean shown = false;
        private float showT = 0.0f;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public EmojiTabsView(Context context) {
            super(context);
            EmojiTabsStrip.this = r12;
            setSmoothScrollingEnabled(true);
            setHorizontalScrollBarEnabled(false);
            setVerticalScrollBarEnabled(false);
            if (Build.VERSION.SDK_INT >= 21) {
                setNestedScrollingEnabled(true);
            }
            LinearLayout linearLayout = new LinearLayout(context, r12) { // from class: org.telegram.ui.Components.EmojiTabsStrip.EmojiTabsView.1
                @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
                protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
                    int paddingLeft = getPaddingLeft();
                    int i5 = (i4 - i2) / 2;
                    for (int i6 = 0; i6 < getChildCount(); i6++) {
                        View childAt = getChildAt(i6);
                        if (childAt != EmojiTabsStrip.this.settingsTab && childAt != null) {
                            childAt.layout(paddingLeft, i5 - (childAt.getMeasuredHeight() / 2), childAt.getMeasuredWidth() + paddingLeft, (childAt.getMeasuredHeight() / 2) + i5);
                            paddingLeft += childAt.getMeasuredWidth() + AndroidUtilities.dp(2.0f);
                        }
                    }
                }

                @Override // android.widget.LinearLayout, android.view.View
                protected void onMeasure(int i, int i2) {
                    super.onMeasure(Math.max(View.MeasureSpec.getSize(i), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((EmojiTabsView.this.contentView.getChildCount() * 32) - 2), 1073741824)), i2);
                }
            };
            this.contentView = linearLayout;
            linearLayout.setOrientation(0);
            addView(this.contentView, new FrameLayout.LayoutParams(-2, -1));
            for (int i = 0; i < EmojiTabsStrip.emojiTabsDrawableIds.length; i++) {
                this.contentView.addView(new EmojiTabButton(context, EmojiTabsStrip.emojiTabsDrawableIds[i], EmojiTabsStrip.emojiTabsAnimatedDrawableIds[i], true, false, r12) { // from class: org.telegram.ui.Components.EmojiTabsStrip.EmojiTabsView.2
                    {
                        EmojiTabsView.this = this;
                        EmojiTabsStrip emojiTabsStrip = EmojiTabsStrip.this;
                    }

                    @Override // android.view.View
                    public boolean onTouchEvent(MotionEvent motionEvent) {
                        EmojiTabsView.this.intercept(motionEvent);
                        return super.onTouchEvent(motionEvent);
                    }
                });
            }
        }

        @Override // android.widget.HorizontalScrollView, android.widget.FrameLayout, android.view.View
        protected void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.lerp(AndroidUtilities.dp(30.0f), maxWidth(), this.showT), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(30.0f), 1073741824));
        }

        public int maxWidth() {
            return AndroidUtilities.dp((Math.min(5.7f, this.contentView.getChildCount()) * 32.0f) - 2.0f);
        }

        @Override // android.view.View
        protected void onScrollChanged(int i, int i2, int i3, int i4) {
            super.onScrollChanged(i, i2, i3, i4);
            if ((Math.abs(i2 - i4) < 2 || i2 >= getMeasuredHeight() || i2 == 0) && !this.touching) {
                EmojiTabsStrip.this.requestDisallowInterceptTouchEvent(false);
            }
            updateButtonsVisibility();
        }

        private void updateButtonsVisibility() {
            int childCount = this.contentView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.contentView.getChildAt(i);
                if (childAt instanceof EmojiTabButton) {
                    ((EmojiTabButton) childAt).updateVisibilityInbounds(childAt.getRight() - getScrollX() > 0 && childAt.getLeft() - getScrollX() < getMeasuredWidth());
                }
            }
        }

        public void intercept(MotionEvent motionEvent) {
            if (!this.shown || this.scrollingAnimation) {
                return;
            }
            int action = motionEvent.getAction();
            if (action != 0) {
                if (action == 1) {
                    this.touching = false;
                    return;
                } else if (action != 2) {
                    return;
                }
            }
            this.touching = true;
            if (!this.scrollingAnimation) {
                resetScrollTo();
            }
            EmojiTabsStrip.this.requestDisallowInterceptTouchEvent(true);
        }

        @Override // android.widget.HorizontalScrollView, android.view.View
        public boolean onTouchEvent(MotionEvent motionEvent) {
            intercept(motionEvent);
            return super.onTouchEvent(motionEvent);
        }

        public void show(boolean z) {
            if (z == this.shown) {
                return;
            }
            this.shown = z;
            if (!z) {
                scrollTo(0);
            }
            ValueAnimator valueAnimator = this.showAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            float[] fArr = new float[2];
            fArr[0] = this.showT;
            fArr[1] = z ? 1.0f : 0.0f;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
            this.showAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.EmojiTabsStrip$EmojiTabsView$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    EmojiTabsStrip.EmojiTabsView.this.lambda$show$0(valueAnimator2);
                }
            });
            this.showAnimator.setDuration(475L);
            this.showAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            this.showAnimator.start();
        }

        public /* synthetic */ void lambda$show$0(ValueAnimator valueAnimator) {
            this.showT = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            invalidate();
            requestLayout();
            updateButtonsVisibility();
            EmojiTabsStrip.this.contentView.invalidate();
        }
    }
}
