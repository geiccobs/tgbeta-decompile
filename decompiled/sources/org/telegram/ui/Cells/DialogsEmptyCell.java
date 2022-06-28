package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import androidx.core.util.Consumer;
import com.google.android.exoplayer2.C;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BlurredRecyclerView;
import org.telegram.ui.Components.Easings;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.TextViewSwitcher;
/* loaded from: classes4.dex */
public class DialogsEmptyCell extends LinearLayout {
    public static final int TYPE_FILTER_ADDING_CHATS = 3;
    public static final int TYPE_FILTER_NO_CHATS_TO_DISPLAY = 2;
    private static final int TYPE_UNSPECIFIED = -1;
    public static final int TYPE_WELCOME_NO_CONTACTS = 0;
    public static final int TYPE_WELCOME_WITH_CONTACTS = 1;
    private RLottieImageView imageView;
    private Runnable onUtyanAnimationEndListener;
    private Consumer<Float> onUtyanAnimationUpdateListener;
    private int prevIcon;
    private TextViewSwitcher subtitleView;
    private TextView titleView;
    private boolean utyanAnimationTriggered;
    private ValueAnimator utyanAnimator;
    private float utyanCollapseProgress;
    private int currentType = -1;
    private int currentAccount = UserConfig.selectedAccount;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface EmptyType {
    }

    public DialogsEmptyCell(final Context context) {
        super(context);
        setGravity(17);
        setOrientation(1);
        setOnTouchListener(DialogsEmptyCell$$ExternalSyntheticLambda3.INSTANCE);
        RLottieImageView rLottieImageView = new RLottieImageView(context);
        this.imageView = rLottieImageView;
        rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
        addView(this.imageView, LayoutHelper.createFrame(100, 100.0f, 17, 52.0f, 4.0f, 52.0f, 0.0f));
        this.imageView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Cells.DialogsEmptyCell$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                DialogsEmptyCell.this.m1644lambda$new$1$orgtelegramuiCellsDialogsEmptyCell(view);
            }
        });
        TextView textView = new TextView(context);
        this.titleView = textView;
        textView.setTextColor(Theme.getColor(Theme.key_chats_nameMessage_threeLines));
        this.titleView.setTextSize(1, 20.0f);
        this.titleView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.titleView.setGravity(17);
        addView(this.titleView, LayoutHelper.createFrame(-1, -2.0f, 51, 52.0f, 10.0f, 52.0f, 0.0f));
        TextViewSwitcher textViewSwitcher = new TextViewSwitcher(context);
        this.subtitleView = textViewSwitcher;
        textViewSwitcher.setFactory(new ViewSwitcher.ViewFactory() { // from class: org.telegram.ui.Cells.DialogsEmptyCell$$ExternalSyntheticLambda4
            @Override // android.widget.ViewSwitcher.ViewFactory
            public final View makeView() {
                return DialogsEmptyCell.lambda$new$2(context);
            }
        });
        this.subtitleView.setInAnimation(context, R.anim.alpha_in);
        this.subtitleView.setOutAnimation(context, R.anim.alpha_out);
        addView(this.subtitleView, LayoutHelper.createFrame(-1, -2.0f, 51, 52.0f, 7.0f, 52.0f, 0.0f));
    }

    public static /* synthetic */ boolean lambda$new$0(View v, MotionEvent event) {
        return true;
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Cells-DialogsEmptyCell */
    public /* synthetic */ void m1644lambda$new$1$orgtelegramuiCellsDialogsEmptyCell(View v) {
        if (!this.imageView.isPlaying()) {
            this.imageView.setProgress(0.0f);
            this.imageView.playAnimation();
        }
    }

    public static /* synthetic */ View lambda$new$2(Context context) {
        TextView tv = new TextView(context);
        tv.setTextColor(Theme.getColor(Theme.key_chats_message));
        tv.setTextSize(1, 14.0f);
        tv.setGravity(17);
        tv.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
        return tv;
    }

    public void setOnUtyanAnimationEndListener(Runnable onUtyanAnimationEndListener) {
        this.onUtyanAnimationEndListener = onUtyanAnimationEndListener;
    }

    public void setOnUtyanAnimationUpdateListener(Consumer<Float> onUtyanAnimationUpdateListener) {
        this.onUtyanAnimationUpdateListener = onUtyanAnimationUpdateListener;
    }

    public void setType(int value) {
        String help;
        int icon;
        if (this.currentType == value) {
            return;
        }
        this.currentType = value;
        switch (value) {
            case 0:
            case 1:
                icon = R.raw.utyan_newborn;
                help = LocaleController.getString("NoChatsHelp", R.string.NoChatsHelp);
                this.titleView.setText(LocaleController.getString("NoChats", R.string.NoChats));
                break;
            case 2:
                this.imageView.setAutoRepeat(false);
                icon = R.raw.filter_no_chats;
                help = LocaleController.getString("FilterNoChatsToDisplayInfo", R.string.FilterNoChatsToDisplayInfo);
                this.titleView.setText(LocaleController.getString("FilterNoChatsToDisplay", R.string.FilterNoChatsToDisplay));
                break;
            default:
                this.imageView.setAutoRepeat(true);
                icon = R.raw.filter_new;
                help = LocaleController.getString("FilterAddingChatsInfo", R.string.FilterAddingChatsInfo);
                this.titleView.setText(LocaleController.getString("FilterAddingChats", R.string.FilterAddingChats));
                break;
        }
        if (icon != 0) {
            this.imageView.setVisibility(0);
            if (this.currentType == 1) {
                if (isUtyanAnimationTriggered()) {
                    this.utyanCollapseProgress = 1.0f;
                    String noChatsContactsHelp = LocaleController.getString("NoChatsContactsHelp", R.string.NoChatsContactsHelp);
                    if (AndroidUtilities.isTablet() && !AndroidUtilities.isSmallTablet()) {
                        noChatsContactsHelp = noChatsContactsHelp.replace('\n', ' ');
                    }
                    this.subtitleView.setText(noChatsContactsHelp, true);
                    requestLayout();
                } else {
                    startUtyanCollapseAnimation(true);
                }
            }
            if (this.prevIcon != icon) {
                this.imageView.setAnimation(icon, 100, 100);
                this.imageView.playAnimation();
                this.prevIcon = icon;
            }
        } else {
            this.imageView.setVisibility(8);
        }
        if (AndroidUtilities.isTablet() && !AndroidUtilities.isSmallTablet()) {
            help = help.replace('\n', ' ');
        }
        this.subtitleView.setText(help, false);
    }

    public boolean isUtyanAnimationTriggered() {
        return this.utyanAnimationTriggered;
    }

    public void startUtyanExpandAnimation() {
        ValueAnimator valueAnimator = this.utyanAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        this.utyanAnimationTriggered = false;
        ValueAnimator duration = ValueAnimator.ofFloat(this.utyanCollapseProgress, 0.0f).setDuration(250L);
        this.utyanAnimator = duration;
        duration.setInterpolator(Easings.easeOutQuad);
        this.utyanAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Cells.DialogsEmptyCell$$ExternalSyntheticLambda1
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                DialogsEmptyCell.this.m1646x3a8422e3(valueAnimator2);
            }
        });
        this.utyanAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Cells.DialogsEmptyCell.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                if (DialogsEmptyCell.this.onUtyanAnimationEndListener != null) {
                    DialogsEmptyCell.this.onUtyanAnimationEndListener.run();
                }
                if (animation == DialogsEmptyCell.this.utyanAnimator) {
                    DialogsEmptyCell.this.utyanAnimator = null;
                }
            }
        });
        this.utyanAnimator.start();
    }

    /* renamed from: lambda$startUtyanExpandAnimation$3$org-telegram-ui-Cells-DialogsEmptyCell */
    public /* synthetic */ void m1646x3a8422e3(ValueAnimator animation) {
        this.utyanCollapseProgress = ((Float) animation.getAnimatedValue()).floatValue();
        requestLayout();
        Consumer<Float> consumer = this.onUtyanAnimationUpdateListener;
        if (consumer != null) {
            consumer.accept(Float.valueOf(this.utyanCollapseProgress));
        }
    }

    public void startUtyanCollapseAnimation(boolean changeContactsHelp) {
        ValueAnimator valueAnimator = this.utyanAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        this.utyanAnimationTriggered = true;
        if (changeContactsHelp) {
            String noChatsContactsHelp = LocaleController.getString("NoChatsContactsHelp", R.string.NoChatsContactsHelp);
            if (AndroidUtilities.isTablet() && !AndroidUtilities.isSmallTablet()) {
                noChatsContactsHelp = noChatsContactsHelp.replace('\n', ' ');
            }
            this.subtitleView.setText(noChatsContactsHelp, true);
        }
        ValueAnimator duration = ValueAnimator.ofFloat(this.utyanCollapseProgress, 1.0f).setDuration(250L);
        this.utyanAnimator = duration;
        duration.setInterpolator(Easings.easeOutQuad);
        this.utyanAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Cells.DialogsEmptyCell$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                DialogsEmptyCell.this.m1645x5deb8595(valueAnimator2);
            }
        });
        this.utyanAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Cells.DialogsEmptyCell.2
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                if (DialogsEmptyCell.this.onUtyanAnimationEndListener != null) {
                    DialogsEmptyCell.this.onUtyanAnimationEndListener.run();
                }
                if (animation == DialogsEmptyCell.this.utyanAnimator) {
                    DialogsEmptyCell.this.utyanAnimator = null;
                }
            }
        });
        this.utyanAnimator.start();
    }

    /* renamed from: lambda$startUtyanCollapseAnimation$4$org-telegram-ui-Cells-DialogsEmptyCell */
    public /* synthetic */ void m1645x5deb8595(ValueAnimator animation) {
        this.utyanCollapseProgress = ((Float) animation.getAnimatedValue()).floatValue();
        requestLayout();
        Consumer<Float> consumer = this.onUtyanAnimationUpdateListener;
        if (consumer != null) {
            consumer.accept(Float.valueOf(this.utyanCollapseProgress));
        }
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        updateLayout();
    }

    @Override // android.view.View
    public void offsetTopAndBottom(int offset) {
        super.offsetTopAndBottom(offset);
        updateLayout();
    }

    public void updateLayout() {
        int i;
        int offset = 0;
        if ((getParent() instanceof View) && ((i = this.currentType) == 2 || i == 3)) {
            View view = (View) getParent();
            int paddingTop = view.getPaddingTop();
            if (paddingTop != 0) {
                offset = 0 - (getTop() / 2);
            }
        }
        int i2 = this.currentType;
        if (i2 == 0 || i2 == 1) {
            offset = (int) (offset - (((int) (ActionBar.getCurrentActionBarHeight() / 2.0f)) * (1.0f - this.utyanCollapseProgress)));
        }
        this.imageView.setTranslationY(offset);
        this.titleView.setTranslationY(offset);
        this.subtitleView.setTranslationY(offset);
    }

    private int measureUtyanHeight(int heightMeasureSpec) {
        int totalHeight;
        if (getParent() instanceof View) {
            View view = (View) getParent();
            totalHeight = view.getMeasuredHeight();
            if (view.getPaddingTop() != 0 && Build.VERSION.SDK_INT >= 21) {
                totalHeight -= AndroidUtilities.statusBarHeight;
            }
        } else {
            totalHeight = View.MeasureSpec.getSize(heightMeasureSpec);
        }
        if (totalHeight == 0) {
            totalHeight = (AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight()) - (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
        }
        if (getParent() instanceof BlurredRecyclerView) {
            totalHeight -= ((BlurredRecyclerView) getParent()).blurTopPadding;
        }
        return (int) (totalHeight + ((AndroidUtilities.dp(320.0f) - totalHeight) * this.utyanCollapseProgress));
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int totalHeight;
        int i = this.currentType;
        if (i == 0 || i == 1) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(measureUtyanHeight(heightMeasureSpec), C.BUFFER_FLAG_ENCRYPTED));
        } else if (i == 2 || i == 3) {
            if (getParent() instanceof View) {
                View view = (View) getParent();
                totalHeight = view.getMeasuredHeight();
                if (view.getPaddingTop() != 0 && Build.VERSION.SDK_INT >= 21) {
                    totalHeight -= AndroidUtilities.statusBarHeight;
                }
            } else {
                totalHeight = View.MeasureSpec.getSize(heightMeasureSpec);
            }
            if (totalHeight == 0) {
                totalHeight = (AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight()) - (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
            }
            if (getParent() instanceof BlurredRecyclerView) {
                totalHeight -= ((BlurredRecyclerView) getParent()).blurTopPadding;
            }
            ArrayList<TLRPC.RecentMeUrl> arrayList = MessagesController.getInstance(this.currentAccount).hintDialogs;
            if (!arrayList.isEmpty()) {
                totalHeight -= (((AndroidUtilities.dp(72.0f) * arrayList.size()) + arrayList.size()) - 1) + AndroidUtilities.dp(50.0f);
            }
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(totalHeight, C.BUFFER_FLAG_ENCRYPTED));
        } else {
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(166.0f), C.BUFFER_FLAG_ENCRYPTED));
        }
    }
}
