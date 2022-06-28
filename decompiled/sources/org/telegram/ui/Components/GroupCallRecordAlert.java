package org.telegram.ui.Components;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Shader;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.google.android.exoplayer2.C;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.GroupCallRecordAlert;
/* loaded from: classes5.dex */
public class GroupCallRecordAlert extends BottomSheet {
    private int currentPage;
    private float pageOffset;
    private TextView positiveButton;
    private TextView[] titles;
    private LinearLayout titlesLayout;
    private ViewPager viewPager;

    public GroupCallRecordAlert(Context context, TLRPC.Chat chat, boolean hasVideo) {
        super(context, false);
        int color = Theme.getColor(Theme.key_voipgroup_inviteMembersBackground);
        this.shadowDrawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
        this.containerView = new FrameLayout(context) { // from class: org.telegram.ui.Components.GroupCallRecordAlert.1
            boolean ignoreLayout;

            @Override // android.widget.FrameLayout, android.view.View
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                boolean isLandscape = View.MeasureSpec.getSize(widthMeasureSpec) > View.MeasureSpec.getSize(heightMeasureSpec);
                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) GroupCallRecordAlert.this.positiveButton.getLayoutParams();
                if (isLandscape) {
                    int dp = AndroidUtilities.dp(80.0f);
                    marginLayoutParams.leftMargin = dp;
                    marginLayoutParams.rightMargin = dp;
                } else {
                    int dp2 = AndroidUtilities.dp(16.0f);
                    marginLayoutParams.leftMargin = dp2;
                    marginLayoutParams.rightMargin = dp2;
                }
                int width = View.MeasureSpec.getSize(widthMeasureSpec);
                int padding = (width - AndroidUtilities.dp(200.0f)) / 2;
                GroupCallRecordAlert.this.viewPager.setPadding(padding, 0, padding, 0);
                super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(370.0f), C.BUFFER_FLAG_ENCRYPTED));
                measureChildWithMargins(GroupCallRecordAlert.this.titlesLayout, View.MeasureSpec.makeMeasureSpec(0, 0), 0, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f), C.BUFFER_FLAG_ENCRYPTED), 0);
            }

            @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                GroupCallRecordAlert.this.updateTitlesLayout();
            }

            @Override // android.view.View, android.view.ViewParent
            public void requestLayout() {
                if (this.ignoreLayout) {
                    return;
                }
                super.requestLayout();
            }
        };
        this.containerView.setWillNotDraw(false);
        this.containerView.setClipChildren(false);
        this.containerView.setBackgroundDrawable(this.shadowDrawable);
        this.containerView.setPadding(this.backgroundPaddingLeft, 0, this.backgroundPaddingLeft, 0);
        TextView titleTextView = new TextView(getContext());
        if (ChatObject.isChannelOrGiga(chat)) {
            titleTextView.setText(LocaleController.getString("VoipChannelRecordVoiceChat", R.string.VoipChannelRecordVoiceChat));
        } else {
            titleTextView.setText(LocaleController.getString("VoipRecordVoiceChat", R.string.VoipRecordVoiceChat));
        }
        titleTextView.setTextColor(-1);
        titleTextView.setTextSize(1, 20.0f);
        titleTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        int i = 5;
        titleTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        this.containerView.addView(titleTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 24.0f, 29.0f, 24.0f, 0.0f));
        TextView infoTextView = new TextView(getContext());
        infoTextView.setText(LocaleController.getString("VoipRecordVoiceChatInfo", R.string.VoipRecordVoiceChatInfo));
        infoTextView.setTextColor(-1);
        infoTextView.setTextSize(1, 14.0f);
        infoTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        this.containerView.addView(infoTextView, LayoutHelper.createFrame(-2, -2.0f, (!LocaleController.isRTL ? 3 : i) | 48, 24.0f, 62.0f, 24.0f, 0.0f));
        this.titles = new TextView[3];
        ViewPager viewPager = new ViewPager(context);
        this.viewPager = viewPager;
        viewPager.setClipChildren(false);
        this.viewPager.setOffscreenPageLimit(4);
        this.viewPager.setClipToPadding(false);
        AndroidUtilities.setViewPagerEdgeEffectColor(this.viewPager, Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
        this.viewPager.setAdapter(new Adapter());
        this.viewPager.setPageMargin(0);
        this.containerView.addView(this.viewPager, LayoutHelper.createFrame(-1, -1.0f, 1, 0.0f, 100.0f, 0.0f, 130.0f));
        this.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() { // from class: org.telegram.ui.Components.GroupCallRecordAlert.2
            @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                GroupCallRecordAlert.this.currentPage = position;
                GroupCallRecordAlert.this.pageOffset = positionOffset;
                GroupCallRecordAlert.this.updateTitlesLayout();
            }

            @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
            public void onPageSelected(int i2) {
            }

            @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
            public void onPageScrollStateChanged(int state) {
            }
        });
        View leftView = new View(getContext());
        leftView.setBackground(new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{color, 0}));
        this.containerView.addView(leftView, LayoutHelper.createFrame(120, -1.0f, 51, 0.0f, 100.0f, 0.0f, 130.0f));
        View rightView = new View(getContext());
        rightView.setBackground(new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{0, color}));
        this.containerView.addView(rightView, LayoutHelper.createFrame(120, -1.0f, 53, 0.0f, 100.0f, 0.0f, 130.0f));
        TextView textView = new TextView(getContext()) { // from class: org.telegram.ui.Components.GroupCallRecordAlert.3
            private Paint[] gradientPaint;

            {
                GroupCallRecordAlert.this = this;
                this.gradientPaint = new Paint[this.titles.length];
                int a = 0;
                while (true) {
                    Paint[] paintArr = this.gradientPaint;
                    if (a < paintArr.length) {
                        paintArr[a] = new Paint(1);
                        a++;
                    } else {
                        return;
                    }
                }
            }

            @Override // android.view.View
            protected void onSizeChanged(int w, int h, int oldw, int oldh) {
                int color3;
                int color2;
                int color1;
                Shader gradient;
                super.onSizeChanged(w, h, oldw, oldh);
                for (int a = 0; a < this.gradientPaint.length; a++) {
                    if (a == 0) {
                        color1 = -11033346;
                        color2 = -9015575;
                        color3 = 0;
                    } else if (a == 1) {
                        color1 = -8919716;
                        color2 = -11089922;
                        color3 = 0;
                    } else {
                        color1 = -9015575;
                        color2 = -1026983;
                        color3 = -1792170;
                    }
                    if (color3 != 0) {
                        gradient = new LinearGradient(0.0f, 0.0f, getMeasuredWidth(), 0.0f, new int[]{color1, color2, color3}, (float[]) null, Shader.TileMode.CLAMP);
                    } else {
                        gradient = new LinearGradient(0.0f, 0.0f, getMeasuredWidth(), 0.0f, new int[]{color1, color2}, (float[]) null, Shader.TileMode.CLAMP);
                    }
                    this.gradientPaint[a].setShader(gradient);
                }
            }

            @Override // android.widget.TextView, android.view.View
            protected void onDraw(Canvas canvas) {
                AndroidUtilities.rectTmp.set(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight());
                this.gradientPaint[GroupCallRecordAlert.this.currentPage].setAlpha(255);
                canvas.drawRoundRect(AndroidUtilities.rectTmp, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), this.gradientPaint[GroupCallRecordAlert.this.currentPage]);
                if (GroupCallRecordAlert.this.pageOffset > 0.0f) {
                    int i2 = GroupCallRecordAlert.this.currentPage + 1;
                    Paint[] paintArr = this.gradientPaint;
                    if (i2 < paintArr.length) {
                        paintArr[GroupCallRecordAlert.this.currentPage + 1].setAlpha((int) (GroupCallRecordAlert.this.pageOffset * 255.0f));
                        canvas.drawRoundRect(AndroidUtilities.rectTmp, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), this.gradientPaint[GroupCallRecordAlert.this.currentPage + 1]);
                    }
                }
                super.onDraw(canvas);
            }
        };
        this.positiveButton = textView;
        textView.setMinWidth(AndroidUtilities.dp(64.0f));
        this.positiveButton.setTag(-1);
        this.positiveButton.setTextSize(1, 14.0f);
        this.positiveButton.setTextColor(Theme.getColor(Theme.key_voipgroup_nameText));
        this.positiveButton.setGravity(17);
        this.positiveButton.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.positiveButton.setText(LocaleController.getString("VoipRecordStart", R.string.VoipRecordStart));
        if (Build.VERSION.SDK_INT >= 23) {
            this.positiveButton.setForeground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), 0, ColorUtils.setAlphaComponent(Theme.getColor(Theme.key_voipgroup_nameText), 76)));
        }
        float f = 12.0f;
        this.positiveButton.setPadding(0, AndroidUtilities.dp(12.0f), 0, AndroidUtilities.dp(12.0f));
        this.positiveButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.GroupCallRecordAlert$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                GroupCallRecordAlert.this.m2662lambda$new$0$orgtelegramuiComponentsGroupCallRecordAlert(view);
            }
        });
        this.containerView.addView(this.positiveButton, LayoutHelper.createFrame(-1, 48.0f, 80, 0.0f, 0.0f, 0.0f, 64.0f));
        this.titlesLayout = new LinearLayout(context);
        this.containerView.addView(this.titlesLayout, LayoutHelper.createFrame(-2, 64, 80));
        int a = 0;
        while (true) {
            TextView[] textViewArr = this.titles;
            if (a >= textViewArr.length) {
                break;
            }
            textViewArr[a] = new TextView(context);
            this.titles[a].setTextSize(1, f);
            this.titles[a].setTextColor(-1);
            this.titles[a].setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            this.titles[a].setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
            this.titles[a].setGravity(16);
            this.titles[a].setSingleLine(true);
            this.titlesLayout.addView(this.titles[a], LayoutHelper.createLinear(-2, -1));
            if (a == 0) {
                this.titles[a].setText(LocaleController.getString("VoipRecordAudio", R.string.VoipRecordAudio));
            } else if (a == 1) {
                this.titles[a].setText(LocaleController.getString("VoipRecordPortrait", R.string.VoipRecordPortrait));
            } else {
                this.titles[a].setText(LocaleController.getString("VoipRecordLandscape", R.string.VoipRecordLandscape));
            }
            final int num = a;
            this.titles[a].setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.GroupCallRecordAlert$$ExternalSyntheticLambda1
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    GroupCallRecordAlert.this.m2663lambda$new$1$orgtelegramuiComponentsGroupCallRecordAlert(num, view);
                }
            });
            a++;
            f = 12.0f;
        }
        if (hasVideo) {
            this.viewPager.setCurrentItem(1);
        }
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-GroupCallRecordAlert */
    public /* synthetic */ void m2662lambda$new$0$orgtelegramuiComponentsGroupCallRecordAlert(View view) {
        onStartRecord(this.currentPage);
        dismiss();
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-GroupCallRecordAlert */
    public /* synthetic */ void m2663lambda$new$1$orgtelegramuiComponentsGroupCallRecordAlert(int num, View view) {
        this.viewPager.setCurrentItem(num, true);
    }

    public void updateTitlesLayout() {
        float scale;
        float alpha;
        View[] viewArr = this.titles;
        int i = this.currentPage;
        View current = viewArr[i];
        View next = i < viewArr.length + (-1) ? viewArr[i + 1] : null;
        float measuredWidth = this.containerView.getMeasuredWidth() / 2;
        float currentCx = current.getLeft() + (current.getMeasuredWidth() / 2);
        float tx = (this.containerView.getMeasuredWidth() / 2) - currentCx;
        if (next != null) {
            float nextCx = next.getLeft() + (next.getMeasuredWidth() / 2);
            tx -= (nextCx - currentCx) * this.pageOffset;
        }
        int a = 0;
        while (true) {
            TextView[] textViewArr = this.titles;
            if (a < textViewArr.length) {
                int i2 = this.currentPage;
                if (a < i2 || a > i2 + 1) {
                    alpha = 0.7f;
                    scale = 0.9f;
                } else if (a == i2) {
                    float f = this.pageOffset;
                    alpha = 1.0f - (0.3f * f);
                    scale = 1.0f - (f * 0.1f);
                } else {
                    float f2 = this.pageOffset;
                    alpha = (0.3f * f2) + 0.7f;
                    scale = (f2 * 0.1f) + 0.9f;
                }
                textViewArr[a].setAlpha(alpha);
                this.titles[a].setScaleX(scale);
                this.titles[a].setScaleY(scale);
                a++;
            } else {
                this.titlesLayout.setTranslationX(tx);
                this.positiveButton.invalidate();
                return;
            }
        }
    }

    public void onStartRecord(int type) {
    }

    /* loaded from: classes5.dex */
    public class Adapter extends PagerAdapter {
        private Adapter() {
            GroupCallRecordAlert.this = r1;
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public int getCount() {
            return GroupCallRecordAlert.this.titles.length;
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public Object instantiateItem(ViewGroup container, final int position) {
            int res;
            ImageView imageView = new ImageView(GroupCallRecordAlert.this.getContext()) { // from class: org.telegram.ui.Components.GroupCallRecordAlert.Adapter.1
                @Override // android.view.View
                public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
                    super.onInitializeAccessibilityEvent(event);
                    if (event.getEventType() == 32768) {
                        GroupCallRecordAlert.this.viewPager.setCurrentItem(position, true);
                    }
                }
            };
            imageView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.GroupCallRecordAlert$Adapter$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    GroupCallRecordAlert.Adapter.this.m2664x107e1182(position, view);
                }
            });
            imageView.setFocusable(true);
            imageView.setTag(Integer.valueOf(position));
            imageView.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(AndroidUtilities.dp(200.0f), -1));
            if (position == 0) {
                imageView.setContentDescription(LocaleController.getString("VoipRecordAudio", R.string.VoipRecordAudio));
            } else if (position == 1) {
                imageView.setContentDescription(LocaleController.getString("VoipRecordPortrait", R.string.VoipRecordPortrait));
            } else {
                imageView.setContentDescription(LocaleController.getString("VoipRecordLandscape", R.string.VoipRecordLandscape));
            }
            if (position == 0) {
                res = R.raw.record_audio;
            } else if (position == 1) {
                res = R.raw.record_video_p;
            } else {
                res = R.raw.record_video_l;
            }
            String svg = RLottieDrawable.readRes(null, res);
            SvgHelper.SvgDrawable drawable = SvgHelper.getDrawable(svg);
            drawable.setAspectFill(false);
            imageView.setImageDrawable(drawable);
            if (imageView.getParent() != null) {
                ViewGroup parent = (ViewGroup) imageView.getParent();
                parent.removeView(imageView);
            }
            container.addView(imageView, 0);
            return imageView;
        }

        /* renamed from: lambda$instantiateItem$0$org-telegram-ui-Components-GroupCallRecordAlert$Adapter */
        public /* synthetic */ void m2664x107e1182(int position, View e) {
            GroupCallRecordAlert.this.onStartRecord(position);
            GroupCallRecordAlert.this.dismiss();
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public Parcelable saveState() {
            return null;
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public void unregisterDataSetObserver(DataSetObserver observer) {
            if (observer != null) {
                super.unregisterDataSetObserver(observer);
            }
        }
    }
}
