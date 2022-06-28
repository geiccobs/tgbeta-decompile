package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Components.HintView;
/* loaded from: classes5.dex */
public class HintView extends FrameLayout {
    public static final int TYPE_COMMON = 4;
    public static final int TYPE_POLL_VOTE = 5;
    public static final int TYPE_SEARCH_AS_LIST = 3;
    private AnimatorSet animatorSet;
    private ImageView arrowImageView;
    private int bottomOffset;
    private int currentType;
    private View currentView;
    private float extraTranslationY;
    private Runnable hideRunnable;
    private ImageView imageView;
    private boolean isTopArrow;
    private ChatMessageCell messageCell;
    private String overrideText;
    private final Theme.ResourcesProvider resourcesProvider;
    private long showingDuration;
    private int shownY;
    private TextView textView;
    private float translationY;

    public HintView(Context context, int type) {
        this(context, type, false, null);
    }

    public HintView(Context context, int type, boolean topArrow) {
        this(context, type, topArrow, null);
    }

    public HintView(Context context, int type, Theme.ResourcesProvider resourcesProvider) {
        this(context, type, false, resourcesProvider);
    }

    public HintView(Context context, int type, boolean topArrow, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.showingDuration = AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS;
        this.resourcesProvider = resourcesProvider;
        this.currentType = type;
        this.isTopArrow = topArrow;
        CorrectlyMeasuringTextView correctlyMeasuringTextView = new CorrectlyMeasuringTextView(context);
        this.textView = correctlyMeasuringTextView;
        correctlyMeasuringTextView.setTextColor(getThemedColor(Theme.key_chat_gifSaveHintText));
        this.textView.setTextSize(1, 14.0f);
        this.textView.setMaxLines(2);
        if (type == 7 || type == 8 || type == 9) {
            this.textView.setMaxWidth(AndroidUtilities.dp(310.0f));
        } else if (type == 4) {
            this.textView.setMaxWidth(AndroidUtilities.dp(280.0f));
        } else {
            this.textView.setMaxWidth(AndroidUtilities.dp(250.0f));
        }
        if (this.currentType == 3) {
            this.textView.setGravity(19);
            this.textView.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(5.0f), getThemedColor(Theme.key_chat_gifSaveHintBackground)));
            this.textView.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
            addView(this.textView, LayoutHelper.createFrame(-2, 30.0f, 51, 0.0f, topArrow ? 6.0f : 0.0f, 0.0f, topArrow ? 0.0f : 6.0f));
        } else {
            this.textView.setGravity(51);
            this.textView.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(6.0f), getThemedColor(Theme.key_chat_gifSaveHintBackground)));
            this.textView.setPadding(AndroidUtilities.dp(this.currentType == 0 ? 54.0f : 8.0f), AndroidUtilities.dp(7.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
            addView(this.textView, LayoutHelper.createFrame(-2, -2.0f, 51, 0.0f, topArrow ? 6.0f : 0.0f, 0.0f, topArrow ? 0.0f : 6.0f));
        }
        if (type == 0) {
            this.textView.setText(LocaleController.getString("AutoplayVideoInfo", R.string.AutoplayVideoInfo));
            ImageView imageView = new ImageView(context);
            this.imageView = imageView;
            imageView.setImageResource(R.drawable.tooltip_sound);
            this.imageView.setScaleType(ImageView.ScaleType.CENTER);
            this.imageView.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_chat_gifSaveHintText), PorterDuff.Mode.MULTIPLY));
            addView(this.imageView, LayoutHelper.createFrame(38, 34.0f, 51, 7.0f, 7.0f, 0.0f, 0.0f));
        }
        ImageView imageView2 = new ImageView(context);
        this.arrowImageView = imageView2;
        imageView2.setImageResource(topArrow ? R.drawable.tooltip_arrow_up : R.drawable.tooltip_arrow);
        this.arrowImageView.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_chat_gifSaveHintBackground), PorterDuff.Mode.MULTIPLY));
        addView(this.arrowImageView, LayoutHelper.createFrame(14, 6.0f, (topArrow ? 48 : 80) | 3, 0.0f, 0.0f, 0.0f, 0.0f));
    }

    public void setBackgroundColor(int background, int text) {
        this.textView.setTextColor(text);
        this.arrowImageView.setColorFilter(new PorterDuffColorFilter(background, PorterDuff.Mode.MULTIPLY));
        TextView textView = this.textView;
        int i = this.currentType;
        textView.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp((i == 7 || i == 8) ? 6.0f : 3.0f), background));
    }

    public void setOverrideText(String text) {
        this.overrideText = text;
        this.textView.setText(text);
        if (this.messageCell != null) {
            ChatMessageCell cell = this.messageCell;
            this.messageCell = null;
            showForMessageCell(cell, false);
        }
    }

    public void setExtraTranslationY(float value) {
        this.extraTranslationY = value;
        setTranslationY(this.translationY + value);
    }

    public float getBaseTranslationY() {
        return this.translationY;
    }

    public boolean showForMessageCell(ChatMessageCell cell, boolean animated) {
        return showForMessageCell(cell, null, 0, 0, animated);
    }

    public boolean showForMessageCell(ChatMessageCell cell, Object object, int x, int y, boolean animated) {
        int top;
        int centerX;
        int i = this.currentType;
        if (!(i == 5 && y == this.shownY && this.messageCell == cell) && (i == 5 || ((i != 0 || getTag() == null) && this.messageCell != cell))) {
            Runnable runnable = this.hideRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.hideRunnable = null;
            }
            int[] position = new int[2];
            cell.getLocationInWindow(position);
            int top2 = position[1];
            View p = (View) getParent();
            p.getLocationInWindow(position);
            int top3 = top2 - position[1];
            View parentView = (View) cell.getParent();
            int i2 = this.currentType;
            if (i2 == 0) {
                ImageReceiver imageReceiver = cell.getPhotoImage();
                top = (int) (top3 + imageReceiver.getImageY());
                int height = (int) imageReceiver.getImageHeight();
                int bottom = top + height;
                int parentHeight = parentView.getMeasuredHeight();
                if (top <= getMeasuredHeight() + AndroidUtilities.dp(10.0f) || bottom > parentHeight + (height / 4)) {
                    return false;
                }
                centerX = cell.getNoSoundIconCenterX();
            } else if (i2 == 5) {
                Integer count = (Integer) object;
                top = top3 + y;
                this.shownY = y;
                if (count.intValue() == -1) {
                    this.textView.setText(LocaleController.getString("PollSelectOption", R.string.PollSelectOption));
                } else if (cell.getMessageObject().isQuiz()) {
                    if (count.intValue() == 0) {
                        this.textView.setText(LocaleController.getString("NoVotesQuiz", R.string.NoVotesQuiz));
                    } else {
                        this.textView.setText(LocaleController.formatPluralString("Answer", count.intValue(), new Object[0]));
                    }
                } else if (count.intValue() == 0) {
                    this.textView.setText(LocaleController.getString("NoVotes", R.string.NoVotes));
                } else {
                    this.textView.setText(LocaleController.formatPluralString("Vote", count.intValue(), new Object[0]));
                }
                measure(View.MeasureSpec.makeMeasureSpec(1000, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(1000, Integer.MIN_VALUE));
                centerX = x;
            } else {
                MessageObject messageObject = cell.getMessageObject();
                String str = this.overrideText;
                if (str == null) {
                    this.textView.setText(LocaleController.getString("HidAccount", R.string.HidAccount));
                } else {
                    this.textView.setText(str);
                }
                measure(View.MeasureSpec.makeMeasureSpec(1000, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(1000, Integer.MIN_VALUE));
                TLRPC.User user = cell.getCurrentUser();
                if (user != null && user.id == 0) {
                    top = top3 + ((cell.getMeasuredHeight() - Math.max(0, cell.getBottom() - parentView.getMeasuredHeight())) - AndroidUtilities.dp(50.0f));
                } else {
                    top = top3 + AndroidUtilities.dp(22.0f);
                    if (!messageObject.isOutOwner() && cell.isDrawNameLayout()) {
                        top += AndroidUtilities.dp(20.0f);
                    }
                }
                if (!this.isTopArrow && top <= getMeasuredHeight() + AndroidUtilities.dp(10.0f)) {
                    return false;
                }
                centerX = cell.getForwardNameCenterX();
            }
            int parentWidth = parentView.getMeasuredWidth();
            if (this.isTopArrow) {
                float f = this.extraTranslationY;
                float dp = AndroidUtilities.dp(44.0f);
                this.translationY = dp;
                setTranslationY(f + dp);
            } else {
                float f2 = this.extraTranslationY;
                float measuredHeight = top - getMeasuredHeight();
                this.translationY = measuredHeight;
                setTranslationY(f2 + measuredHeight);
            }
            int iconX = cell.getLeft() + centerX;
            int left = AndroidUtilities.dp(19.0f);
            if (this.currentType == 5) {
                int offset = Math.max(0, (centerX - (getMeasuredWidth() / 2)) - AndroidUtilities.dp(19.1f));
                setTranslationX(offset);
                left += offset;
            } else if (iconX <= parentView.getMeasuredWidth() / 2) {
                setTranslationX(0.0f);
            } else {
                int offset2 = (parentWidth - getMeasuredWidth()) - AndroidUtilities.dp(38.0f);
                setTranslationX(offset2);
                left += offset2;
            }
            float arrowX = ((cell.getLeft() + centerX) - left) - (this.arrowImageView.getMeasuredWidth() / 2);
            this.arrowImageView.setTranslationX(arrowX);
            if (iconX > parentView.getMeasuredWidth() / 2) {
                if (arrowX < AndroidUtilities.dp(10.0f)) {
                    float diff = arrowX - AndroidUtilities.dp(10.0f);
                    setTranslationX(getTranslationX() + diff);
                    this.arrowImageView.setTranslationX(arrowX - diff);
                }
            } else if (arrowX > getMeasuredWidth() - AndroidUtilities.dp(24.0f)) {
                float diff2 = (arrowX - getMeasuredWidth()) + AndroidUtilities.dp(24.0f);
                setTranslationX(diff2);
                this.arrowImageView.setTranslationX(arrowX - diff2);
            } else if (arrowX < AndroidUtilities.dp(10.0f)) {
                float diff3 = arrowX - AndroidUtilities.dp(10.0f);
                setTranslationX(getTranslationX() + diff3);
                this.arrowImageView.setTranslationX(arrowX - diff3);
            }
            this.messageCell = cell;
            AnimatorSet animatorSet = this.animatorSet;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.animatorSet = null;
            }
            setTag(1);
            setVisibility(0);
            if (!animated) {
                setAlpha(1.0f);
                return true;
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.animatorSet = animatorSet2;
            animatorSet2.playTogether(ObjectAnimator.ofFloat(this, View.ALPHA, 0.0f, 1.0f));
            this.animatorSet.addListener(new AnonymousClass1());
            this.animatorSet.setDuration(300L);
            this.animatorSet.start();
            return true;
        }
        return false;
    }

    /* renamed from: org.telegram.ui.Components.HintView$1 */
    /* loaded from: classes5.dex */
    public class AnonymousClass1 extends AnimatorListenerAdapter {
        AnonymousClass1() {
            HintView.this = this$0;
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animation) {
            HintView.this.animatorSet = null;
            AndroidUtilities.runOnUIThread(HintView.this.hideRunnable = new Runnable() { // from class: org.telegram.ui.Components.HintView$1$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    HintView.AnonymousClass1.this.m2675lambda$onAnimationEnd$0$orgtelegramuiComponentsHintView$1();
                }
            }, HintView.this.currentType == 0 ? 10000L : AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
        }

        /* renamed from: lambda$onAnimationEnd$0$org-telegram-ui-Components-HintView$1 */
        public /* synthetic */ void m2675lambda$onAnimationEnd$0$orgtelegramuiComponentsHintView$1() {
            HintView.this.hide();
        }
    }

    public boolean showForView(View view, boolean animated) {
        if (this.currentView == view || getTag() != null) {
            if (getTag() != null) {
                updatePosition(view);
            }
            return false;
        }
        Runnable runnable = this.hideRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.hideRunnable = null;
        }
        updatePosition(view);
        this.currentView = view;
        AnimatorSet animatorSet = this.animatorSet;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.animatorSet = null;
        }
        setTag(1);
        setVisibility(0);
        if (animated) {
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.animatorSet = animatorSet2;
            animatorSet2.playTogether(ObjectAnimator.ofFloat(this, View.ALPHA, 0.0f, 1.0f));
            this.animatorSet.addListener(new AnonymousClass2());
            this.animatorSet.setDuration(300L);
            this.animatorSet.start();
        } else {
            setAlpha(1.0f);
        }
        return true;
    }

    /* renamed from: org.telegram.ui.Components.HintView$2 */
    /* loaded from: classes5.dex */
    public class AnonymousClass2 extends AnimatorListenerAdapter {
        AnonymousClass2() {
            HintView.this = this$0;
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animation) {
            HintView.this.animatorSet = null;
            AndroidUtilities.runOnUIThread(HintView.this.hideRunnable = new Runnable() { // from class: org.telegram.ui.Components.HintView$2$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    HintView.AnonymousClass2.this.m2676lambda$onAnimationEnd$0$orgtelegramuiComponentsHintView$2();
                }
            }, HintView.this.showingDuration);
        }

        /* renamed from: lambda$onAnimationEnd$0$org-telegram-ui-Components-HintView$2 */
        public /* synthetic */ void m2676lambda$onAnimationEnd$0$orgtelegramuiComponentsHintView$2() {
            HintView.this.hide();
        }
    }

    private void updatePosition(View view) {
        int centerX;
        int offset;
        int i;
        measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.x, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.x, Integer.MIN_VALUE));
        int[] position = new int[2];
        view.getLocationInWindow(position);
        int top = position[1] - AndroidUtilities.dp(4.0f);
        int i2 = this.currentType;
        if (i2 == 4) {
            top += AndroidUtilities.dp(4.0f);
        } else if (i2 == 6) {
            top += view.getMeasuredHeight() + getMeasuredHeight() + AndroidUtilities.dp(10.0f);
        } else if (i2 == 7 || (i2 == 8 && this.isTopArrow)) {
            top += view.getMeasuredHeight() + getMeasuredHeight() + AndroidUtilities.dp(8.0f);
        } else if (i2 == 8) {
            top -= AndroidUtilities.dp(10.0f);
        }
        int i3 = this.currentType;
        if (i3 == 8 && this.isTopArrow) {
            if (view instanceof SimpleTextView) {
                SimpleTextView textView = (SimpleTextView) view;
                Drawable drawable = textView.getRightDrawable();
                centerX = (position[0] + (drawable != null ? drawable.getBounds().centerX() : textView.getTextWidth() / 2)) - AndroidUtilities.dp(8.0f);
            } else if (view instanceof TextView) {
                centerX = (position[0] + ((TextView) view).getMeasuredWidth()) - AndroidUtilities.dp(16.5f);
            } else {
                centerX = position[0];
            }
        } else if (i3 != 3) {
            centerX = position[0] + (view.getMeasuredWidth() / 2);
        } else {
            centerX = position[0];
        }
        View parentView = (View) getParent();
        parentView.getLocationInWindow(position);
        int centerX2 = centerX - position[0];
        int top2 = (top - position[1]) - this.bottomOffset;
        int parentWidth = parentView.getMeasuredWidth();
        if (this.isTopArrow && (i = this.currentType) != 6 && i != 7 && i != 8) {
            float f = this.extraTranslationY;
            float dp = AndroidUtilities.dp(44.0f);
            this.translationY = dp;
            setTranslationY(f + dp);
        } else {
            float f2 = this.extraTranslationY;
            float measuredHeight = top2 - getMeasuredHeight();
            this.translationY = measuredHeight;
            setTranslationY(f2 + measuredHeight);
        }
        int leftMargin = 0;
        int rightMargin = 0;
        if (getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            leftMargin = ((ViewGroup.MarginLayoutParams) getLayoutParams()).leftMargin;
            rightMargin = ((ViewGroup.MarginLayoutParams) getLayoutParams()).rightMargin;
        }
        if (this.currentType != 8 || this.isTopArrow) {
            if (centerX2 > parentView.getMeasuredWidth() / 2) {
                if (this.currentType == 3) {
                    offset = (int) (parentWidth - (getMeasuredWidth() * 1.5f));
                    if (offset < 0) {
                        offset = 0;
                    }
                } else {
                    offset = (parentWidth - getMeasuredWidth()) - (leftMargin + rightMargin);
                }
            } else if (this.currentType == 3) {
                offset = (centerX2 - (getMeasuredWidth() / 2)) - this.arrowImageView.getMeasuredWidth();
                if (offset < 0) {
                    offset = 0;
                }
            } else {
                offset = 0;
            }
        } else {
            offset = (((parentWidth - leftMargin) - rightMargin) - getMeasuredWidth()) / 2;
        }
        setTranslationX(offset);
        float arrowX = (centerX2 - (leftMargin + offset)) - (this.arrowImageView.getMeasuredWidth() / 2);
        if (this.currentType == 7) {
            arrowX += AndroidUtilities.dp(2.0f);
        }
        this.arrowImageView.setTranslationX(arrowX);
        if (centerX2 > parentView.getMeasuredWidth() / 2) {
            if (arrowX < AndroidUtilities.dp(10.0f)) {
                float diff = arrowX - AndroidUtilities.dp(10.0f);
                setTranslationX(getTranslationX() + diff);
                this.arrowImageView.setTranslationX(arrowX - diff);
            }
        } else if (arrowX > getMeasuredWidth() - AndroidUtilities.dp(24.0f)) {
            float diff2 = (arrowX - getMeasuredWidth()) + AndroidUtilities.dp(24.0f);
            setTranslationX(diff2);
            this.arrowImageView.setTranslationX(arrowX - diff2);
        } else if (arrowX < AndroidUtilities.dp(10.0f)) {
            float diff3 = arrowX - AndroidUtilities.dp(10.0f);
            setTranslationX(getTranslationX() + diff3);
            this.arrowImageView.setTranslationX(arrowX - diff3);
        }
    }

    public void hide() {
        if (getTag() == null) {
            return;
        }
        setTag(null);
        Runnable runnable = this.hideRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.hideRunnable = null;
        }
        AnimatorSet animatorSet = this.animatorSet;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.animatorSet = null;
        }
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.animatorSet = animatorSet2;
        animatorSet2.playTogether(ObjectAnimator.ofFloat(this, View.ALPHA, 0.0f));
        this.animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.HintView.3
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                HintView.this.setVisibility(4);
                HintView.this.currentView = null;
                HintView.this.messageCell = null;
                HintView.this.animatorSet = null;
            }
        });
        this.animatorSet.setDuration(300L);
        this.animatorSet.start();
    }

    public void setText(CharSequence text) {
        this.textView.setText(text);
    }

    public ChatMessageCell getMessageCell() {
        return this.messageCell;
    }

    public void setShowingDuration(long showingDuration) {
        this.showingDuration = showingDuration;
    }

    public void setBottomOffset(int offset) {
        this.bottomOffset = offset;
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
