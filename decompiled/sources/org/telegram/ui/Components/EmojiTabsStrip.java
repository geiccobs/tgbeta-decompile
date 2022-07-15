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
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.EmojiTabsStrip;
import org.telegram.ui.Components.Premium.PremiumLockIconView;
/* loaded from: classes3.dex */
public class EmojiTabsStrip extends ScrollableHorizontalScrollView {
    private static int[] emojiTabsDrawableIds = {R.drawable.msg_emoji_smiles, R.drawable.msg_emoji_cat, R.drawable.msg_emoji_food, R.drawable.msg_emoji_activities, R.drawable.msg_emoji_travel, R.drawable.msg_emoji_objects, R.drawable.msg_emoji_other, R.drawable.msg_emoji_flags};
    private LinearLayout contentView;
    private EmojiTabsView emojiTabs;
    private ArrayList<EmojiTabButton> emojipackTabs;
    private boolean includeAnimated;
    private EmojiTabButton recentTab;
    private Theme.ResourcesProvider resourcesProvider;
    private ValueAnimator selectAnimator;
    private int recentDrawableId = R.drawable.msg_emoji_recent;
    private float selectT = 0.0f;
    private float selectAnimationT = 0.0f;
    private int selected = 0;
    private int wasIndex = 0;

    protected boolean onTabClick(int i) {
        throw null;
    }

    public EmojiTabsStrip(Context context, final Theme.ResourcesProvider resourcesProvider, final boolean z) {
        super(context);
        this.includeAnimated = z;
        this.resourcesProvider = resourcesProvider;
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
                    if (childAt != null) {
                        childAt.layout(paddingLeft, i5 - (childAt.getMeasuredHeight() / 2), childAt.getMeasuredWidth() + paddingLeft, (childAt.getMeasuredHeight() / 2) + i5);
                        paddingLeft += childAt.getMeasuredWidth() + AndroidUtilities.dp(8.0f);
                    }
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
                        paddingLeft += childAt.getMeasuredWidth() + (i3 + 1 < getChildCount() ? AndroidUtilities.dp(8.0f) : 0);
                    }
                }
                if (!z) {
                    setMeasuredDimension(View.MeasureSpec.getSize(i), View.MeasureSpec.getSize(i2));
                } else {
                    setMeasuredDimension(paddingLeft, View.MeasureSpec.getSize(i2));
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
                int color = Theme.getColor("chat_emojiSearchBackground", resourcesProvider);
                this.paint.setColor(ColorUtils.blendARGB(Theme.blendOver(color, Theme.isCurrentThemeDark() ? 83886079 : ConnectionsManager.FileTypeFile), color, f));
                this.path.rewind();
                this.path.addRoundRect(this.rect, dp, dp, Path.Direction.CW);
                canvas.drawPath(this.path, this.paint);
                EmojiTabsStrip.this.emojiTabs.draw(canvas, this.path);
                super.dispatchDraw(canvas);
            }

            private void getChildBounds(int i, RectF rectF) {
                View childAt = getChildAt(MathUtils.clamp(i, 0, getChildCount() - 1));
                rectF.set(childAt.getLeft(), childAt.getTop(), childAt.getRight(), childAt.getBottom());
            }
        };
        this.contentView = linearLayout;
        linearLayout.setPadding(AndroidUtilities.dp(11.0f), 0, AndroidUtilities.dp(11.0f), 0);
        this.contentView.setOrientation(0);
        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
        addView(this.contentView);
        LinearLayout linearLayout2 = this.contentView;
        EmojiTabButton emojiTabButton = new EmojiTabButton(context, this.recentDrawableId, false);
        this.recentTab = emojiTabButton;
        linearLayout2.addView(emojiTabButton);
        if (z) {
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
                this.contentView.addView(new EmojiTabButton(context, iArr[i], false));
                i++;
            } else {
                updateClickListeners();
                return;
            }
        }
    }

    private boolean isFreeEmojiPack(TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet) {
        if (tLRPC$TL_messages_stickerSet == null || tLRPC$TL_messages_stickerSet.documents == null) {
            return false;
        }
        for (int i = 0; i < tLRPC$TL_messages_stickerSet.documents.size(); i++) {
            if (!MessageObject.isFreeEmoji(tLRPC$TL_messages_stickerSet.documents.get(i))) {
                return false;
            }
        }
        return true;
    }

    private TLRPC$Document getThumbDocument(TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet) {
        if (tLRPC$TL_messages_stickerSet == null) {
            return null;
        }
        if (tLRPC$TL_messages_stickerSet.set != null && tLRPC$TL_messages_stickerSet.documents != null) {
            for (int i = 0; i < tLRPC$TL_messages_stickerSet.documents.size(); i++) {
                TLRPC$Document tLRPC$Document = tLRPC$TL_messages_stickerSet.documents.get(i);
                if (tLRPC$Document.id == tLRPC$TL_messages_stickerSet.set.thumb_document_id) {
                    return tLRPC$Document;
                }
            }
        }
        ArrayList<TLRPC$Document> arrayList = tLRPC$TL_messages_stickerSet.documents;
        if (arrayList != null && arrayList.size() >= 1) {
            return tLRPC$TL_messages_stickerSet.documents.get(0);
        }
        return null;
    }

    public void updateEmojiPacks() {
        ArrayList<TLRPC$TL_messages_stickerSet> stickerSets;
        if (this.includeAnimated && (stickerSets = MediaDataController.getInstance(UserConfig.selectedAccount).getStickerSets(5)) != null) {
            if (this.emojipackTabs == null) {
                this.emojipackTabs = new ArrayList<>();
            }
            int i = 0;
            while (i < Math.max(this.emojipackTabs.size(), stickerSets.size())) {
                AnimatedEmojiDrawable animatedEmojiDrawable = null;
                EmojiTabButton emojiTabButton = i >= this.emojipackTabs.size() ? null : this.emojipackTabs.get(i);
                if (emojiTabButton != null) {
                    animatedEmojiDrawable = (AnimatedEmojiDrawable) emojiTabButton.getDrawable();
                }
                if (i >= stickerSets.size()) {
                    this.contentView.removeView(this.emojipackTabs.remove(i));
                    i--;
                } else {
                    TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = stickerSets.get(i);
                    TLRPC$Document thumbDocument = getThumbDocument(tLRPC$TL_messages_stickerSet);
                    if (thumbDocument != null && (animatedEmojiDrawable == null || animatedEmojiDrawable.getDocumentId() != thumbDocument.id)) {
                        animatedEmojiDrawable = AnimatedEmojiDrawable.make(3, thumbDocument);
                    }
                    if (emojiTabButton == null) {
                        emojiTabButton = new EmojiTabButton(getContext(), animatedEmojiDrawable, isFreeEmojiPack(tLRPC$TL_messages_stickerSet), false);
                        if (animatedEmojiDrawable != null) {
                            animatedEmojiDrawable.addView(emojiTabButton.imageView);
                        }
                    } else {
                        if (emojiTabButton.getDrawable() instanceof AnimatedEmojiDrawable) {
                            ((AnimatedEmojiDrawable) emojiTabButton.getDrawable()).removeView(emojiTabButton.imageView);
                        }
                        emojiTabButton.setDrawable(animatedEmojiDrawable);
                        if (animatedEmojiDrawable != null) {
                            animatedEmojiDrawable.addView(emojiTabButton.imageView);
                        }
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
            updateClickListeners();
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
                    emojiTabsView.contentView.getChildAt(i3).setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.EmojiTabsStrip$$ExternalSyntheticLambda2
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view) {
                            EmojiTabsStrip.this.lambda$updateClickListeners$0(i2, view);
                        }
                    });
                    i3++;
                    i2++;
                }
                i2--;
            } else if (childAt != null) {
                childAt.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.EmojiTabsStrip$$ExternalSyntheticLambda1
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        EmojiTabsStrip.this.lambda$updateClickListeners$1(i2, view);
                    }
                });
            }
            i++;
            i2++;
        }
    }

    public /* synthetic */ void lambda$updateClickListeners$0(int i, View view) {
        if (onTabClick(i)) {
            select(i);
        }
    }

    public /* synthetic */ void lambda$updateClickListeners$1(int i, View view) {
        if (onTabClick(i)) {
            select(i);
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
                    EmojiTabsStrip.this.lambda$select$2(f, f2, valueAnimator2);
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

    public /* synthetic */ void lambda$select$2(float f, float f2, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.selectAnimationT = floatValue;
        this.selectT = AndroidUtilities.lerp(f, f2, floatValue);
        this.contentView.invalidate();
    }

    /* loaded from: classes3.dex */
    public class EmojiTabButton extends ViewGroup {
        private ImageView imageView;
        private PremiumLockIconView lockView;
        private boolean round;
        private ValueAnimator selectAnimator;
        private float selectT;
        private boolean selected;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public EmojiTabButton(Context context, int i, boolean z) {
            super(context);
            EmojiTabsStrip.this = r2;
            this.round = z;
            if (z) {
                setBackground(Theme.createCircleSelectorDrawable(Theme.getColor("listSelectorSDK21", r2.resourcesProvider), 0, 0));
            }
            ImageView imageView = new ImageView(context);
            this.imageView = imageView;
            imageView.setImageDrawable(context.getResources().getDrawable(i).mutate());
            setColor(Theme.getColor("chat_emojiPanelIcon", r2.resourcesProvider));
            addView(this.imageView);
        }

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public EmojiTabButton(Context context, Drawable drawable, boolean z, boolean z2) {
            super(context);
            EmojiTabsStrip.this = r2;
            this.round = z2;
            if (z2) {
                setBackground(Theme.createCircleSelectorDrawable(Theme.getColor("listSelectorSDK21", r2.resourcesProvider), 0, 0));
            }
            ImageView imageView = new ImageView(context, r2) { // from class: org.telegram.ui.Components.EmojiTabsStrip.EmojiTabButton.1
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
            if (z || UserConfig.getInstance(UserConfig.selectedAccount).isPremium()) {
                return;
            }
            this.lockView = new PremiumLockIconView(context, PremiumLockIconView.TYPE_STICKERS_PREMIUM_LOCKED);
            updateLockImageReceiver();
            this.lockView.setPadding(AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f));
            updateLockImageReceiver();
            addView(this.lockView);
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
                            EmojiTabsStrip.EmojiTabButton.this.lambda$updateSelect$0(valueAnimator2);
                        }
                    });
                    this.selectAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.EmojiTabsStrip.EmojiTabButton.2
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animator) {
                            if (!EmojiTabButton.this.round) {
                                if (z) {
                                    if (EmojiTabButton.this.getBackground() != null) {
                                        return;
                                    }
                                    EmojiTabButton emojiTabButton = EmojiTabButton.this;
                                    emojiTabButton.setBackground(Theme.createRadSelectorDrawable(Theme.getColor("listSelectorSDK21", EmojiTabsStrip.this.resourcesProvider), 8, 8));
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

        public /* synthetic */ void lambda$updateSelect$0(ValueAnimator valueAnimator) {
            this.selectT = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            setColor(ColorUtils.blendARGB(Theme.getColor("chat_emojiPanelIcon", EmojiTabsStrip.this.resourcesProvider), Theme.getColor("chat_emojiPanelIconSelected", EmojiTabsStrip.this.resourcesProvider), this.selectT));
        }

        public void updateColor() {
            Theme.setSelectorDrawableColor(getBackground(), Theme.getColor("listSelectorSDK21", EmojiTabsStrip.this.resourcesProvider), false);
            setColor(ColorUtils.blendARGB(Theme.getColor("chat_emojiPanelIcon", EmojiTabsStrip.this.resourcesProvider), Theme.getColor("chat_emojiPanelIconSelected", EmojiTabsStrip.this.resourcesProvider), this.selectT));
        }

        private void setColor(int i) {
            ImageView imageView = this.imageView;
            if (imageView != null) {
                imageView.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
                this.imageView.invalidate();
            }
        }

        @Override // android.view.View
        public boolean callOnClick() {
            return super.callOnClick();
        }
    }

    /* loaded from: classes3.dex */
    public class EmojiTabsView extends ScrollableHorizontalScrollView {
        private Path circlePath;
        private float circlePathR;
        private LinearLayout contentView;
        private ValueAnimator showAnimator;
        private boolean touching = false;
        private boolean scrollingAnimation = false;
        private boolean shown = false;
        private float showT = 0.0f;

        @Override // android.view.ViewGroup, android.view.View
        protected void dispatchDraw(Canvas canvas) {
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
        }

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public EmojiTabsView(Context context) {
            super(context);
            EmojiTabsStrip.this = r10;
            setSmoothScrollingEnabled(true);
            setHorizontalScrollBarEnabled(false);
            setVerticalScrollBarEnabled(false);
            if (Build.VERSION.SDK_INT >= 21) {
                setNestedScrollingEnabled(true);
            }
            LinearLayout linearLayout = new LinearLayout(context, r10) { // from class: org.telegram.ui.Components.EmojiTabsStrip.EmojiTabsView.1
                @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
                protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
                    int paddingLeft = getPaddingLeft();
                    int i5 = (i4 - i2) / 2;
                    for (int i6 = 0; i6 < getChildCount(); i6++) {
                        View childAt = getChildAt(i6);
                        if (childAt != null) {
                            childAt.layout(paddingLeft, i5 - (childAt.getMeasuredHeight() / 2), childAt.getMeasuredWidth() + paddingLeft, (childAt.getMeasuredHeight() / 2) + i5);
                            paddingLeft += childAt.getMeasuredWidth() + AndroidUtilities.dp(6.0f);
                        }
                    }
                }

                @Override // android.widget.LinearLayout, android.view.View
                protected void onMeasure(int i, int i2) {
                    super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((EmojiTabsView.this.contentView.getChildCount() * 36) - 6), 1073741824), i2);
                }
            };
            this.contentView = linearLayout;
            linearLayout.setOrientation(0);
            addView(this.contentView, new FrameLayout.LayoutParams(-2, -1));
            for (int i = 0; i < EmojiTabsStrip.emojiTabsDrawableIds.length; i++) {
                this.contentView.addView(new EmojiTabButton(context, EmojiTabsStrip.emojiTabsDrawableIds[i], true, r10) { // from class: org.telegram.ui.Components.EmojiTabsStrip.EmojiTabsView.2
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

        @Override // android.view.ViewGroup
        protected boolean drawChild(Canvas canvas, View view, long j) {
            if (view == getChildAt(0)) {
                return false;
            }
            return super.drawChild(canvas, view, j);
        }

        public void draw(Canvas canvas, Path path) {
            LinearLayout linearLayout = this.contentView;
            if (linearLayout == null) {
                return;
            }
            View childAt = linearLayout.getChildAt(0);
            if (childAt != null) {
                canvas.save();
                canvas.translate(getLeft() + this.contentView.getLeft() + childAt.getLeft(), getTop() + this.contentView.getTop() + childAt.getTop());
                if (this.circlePath == null || this.circlePathR != AndroidUtilities.dp(15.0f)) {
                    Path path2 = this.circlePath;
                    if (path2 == null) {
                        this.circlePath = new Path();
                    } else {
                        path2.rewind();
                    }
                    Path path3 = this.circlePath;
                    float dp = AndroidUtilities.dp(15.0f);
                    this.circlePathR = dp;
                    path3.addCircle(dp, AndroidUtilities.dp(15.0f), AndroidUtilities.dp(15.0f), Path.Direction.CW);
                }
                canvas.clipPath(this.circlePath);
                canvas.translate(-getScrollX(), 0.0f);
                childAt.setAlpha(1.0f - this.showT);
                childAt.draw(canvas);
                canvas.restore();
            }
            if (childAt != null) {
                childAt.setAlpha(0.0f);
            }
            if (this.showT <= 0.0f) {
                return;
            }
            canvas.save();
            canvas.clipPath(path);
            canvas.translate(getLeft() + this.contentView.getLeft(), getTop() + this.contentView.getTop());
            canvas.saveLayerAlpha(0.0f, 0.0f, getWidth(), getHeight(), (int) (this.showT * 255.0f), 31);
            canvas.translate(-getScrollX(), 0.0f);
            this.contentView.setAlpha(this.showT);
            this.contentView.draw(canvas);
            canvas.restore();
            canvas.restore();
        }

        @Override // android.widget.HorizontalScrollView, android.widget.FrameLayout, android.view.View
        protected void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.lerp(AndroidUtilities.dp(30.0f), maxWidth(), this.showT), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(30.0f), 1073741824));
        }

        public int maxWidth() {
            return AndroidUtilities.dp((Math.min(4.7f, this.contentView.getChildCount()) * 36.0f) - 6.0f);
        }

        @Override // android.view.View
        protected void onScrollChanged(int i, int i2, int i3, int i4) {
            super.onScrollChanged(i, i2, i3, i4);
            if ((Math.abs(i2 - i4) < 2 || i2 >= getMeasuredHeight() || i2 == 0) && !this.touching) {
                EmojiTabsStrip.this.requestDisallowInterceptTouchEvent(false);
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
            EmojiTabsStrip.this.contentView.invalidate();
        }
    }
}
