package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.EmojiData;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.IdenticonDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.URLSpanReplacement;
/* loaded from: classes4.dex */
public class IdenticonActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private AnimatorSet animatorSet;
    private int chat_id;
    private TextView codeTextView;
    private FrameLayout container;
    private boolean emojiSelected;
    private String emojiText;
    private TextView emojiTextView;
    private AnimatorSet hintAnimatorSet;
    private LinearLayout linearLayout;
    private LinearLayout linearLayout1;
    private TextView textView;
    private int textWidth;

    /* loaded from: classes4.dex */
    private static class LinkMovementMethodMy extends LinkMovementMethod {
        private LinkMovementMethodMy() {
        }

        @Override // android.text.method.LinkMovementMethod, android.text.method.ScrollingMovementMethod, android.text.method.BaseMovementMethod, android.text.method.MovementMethod
        public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
            try {
                return super.onTouchEvent(widget, buffer, event);
            } catch (Exception e) {
                FileLog.e(e);
                return false;
            }
        }
    }

    public IdenticonActivity(Bundle args) {
        super(args);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        this.chat_id = getArguments().getInt(ChatReactionsEditActivity.KEY_CHAT_ID);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        return super.onFragmentCreate();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("EncryptionKey", R.string.EncryptionKey));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.IdenticonActivity.1
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int id) {
                if (id == -1) {
                    IdenticonActivity.this.finishFragment();
                }
            }
        });
        this.fragmentView = new FrameLayout(context);
        FrameLayout parentFrameLayout = (FrameLayout) this.fragmentView;
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.fragmentView.setOnTouchListener(IdenticonActivity$$ExternalSyntheticLambda0.INSTANCE);
        LinearLayout linearLayout = new LinearLayout(context);
        this.linearLayout = linearLayout;
        linearLayout.setOrientation(1);
        this.linearLayout.setWeightSum(100.0f);
        parentFrameLayout.addView(this.linearLayout, LayoutHelper.createFrame(-1, -1.0f));
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setPadding(AndroidUtilities.dp(20.0f), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(20.0f));
        this.linearLayout.addView(frameLayout, LayoutHelper.createLinear(-1, -1, 50.0f));
        ImageView identiconView = new ImageView(context);
        identiconView.setScaleType(ImageView.ScaleType.FIT_XY);
        frameLayout.addView(identiconView, LayoutHelper.createFrame(-1, -1.0f));
        FrameLayout frameLayout2 = new FrameLayout(context) { // from class: org.telegram.ui.IdenticonActivity.2
            @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                if (IdenticonActivity.this.codeTextView != null) {
                    int x = (IdenticonActivity.this.codeTextView.getLeft() + (IdenticonActivity.this.codeTextView.getMeasuredWidth() / 2)) - (IdenticonActivity.this.emojiTextView.getMeasuredWidth() / 2);
                    int y = (((IdenticonActivity.this.codeTextView.getMeasuredHeight() - IdenticonActivity.this.emojiTextView.getMeasuredHeight()) / 2) + IdenticonActivity.this.linearLayout1.getTop()) - AndroidUtilities.dp(16.0f);
                    IdenticonActivity.this.emojiTextView.layout(x, y, IdenticonActivity.this.emojiTextView.getMeasuredWidth() + x, IdenticonActivity.this.emojiTextView.getMeasuredHeight() + y);
                }
            }
        };
        this.container = frameLayout2;
        frameLayout2.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.linearLayout.addView(this.container, LayoutHelper.createLinear(-1, -1, 50.0f));
        LinearLayout linearLayout2 = new LinearLayout(context);
        this.linearLayout1 = linearLayout2;
        linearLayout2.setOrientation(1);
        this.linearLayout1.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
        this.container.addView(this.linearLayout1, LayoutHelper.createFrame(-2, -2, 17));
        TextView textView = new TextView(context);
        this.codeTextView = textView;
        textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText4));
        this.codeTextView.setGravity(17);
        this.codeTextView.setTypeface(Typeface.MONOSPACE);
        this.codeTextView.setTextSize(1, 16.0f);
        this.linearLayout1.addView(this.codeTextView, LayoutHelper.createLinear(-2, -2, 1));
        TextView textView2 = new TextView(context);
        this.textView = textView2;
        textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText4));
        this.textView.setLinkTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteLinkText));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLinksClickable(true);
        this.textView.setClickable(true);
        this.textView.setGravity(17);
        this.textView.setMovementMethod(new LinkMovementMethodMy());
        this.linearLayout1.addView(this.textView, LayoutHelper.createFrame(-2, -2, 1));
        TextView textView3 = new TextView(context);
        this.emojiTextView = textView3;
        textView3.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText4));
        this.emojiTextView.setGravity(17);
        this.emojiTextView.setTextSize(1, 32.0f);
        this.container.addView(this.emojiTextView, LayoutHelper.createFrame(-2, -2.0f));
        TLRPC.EncryptedChat encryptedChat = MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf(this.chat_id));
        if (encryptedChat != null) {
            IdenticonDrawable drawable = new IdenticonDrawable();
            identiconView.setImageDrawable(drawable);
            drawable.setEncryptedChat(encryptedChat);
            TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(encryptedChat.user_id));
            SpannableStringBuilder hash = new SpannableStringBuilder();
            StringBuilder emojis = new StringBuilder();
            if (encryptedChat.key_hash.length > 16) {
                String hex = Utilities.bytesToHex(encryptedChat.key_hash);
                for (int a = 0; a < 32; a++) {
                    if (a != 0) {
                        if (a % 8 == 0) {
                            hash.append('\n');
                        } else if (a % 4 == 0) {
                            hash.append(' ');
                        }
                    }
                    hash.append((CharSequence) hex.substring(a * 2, (a * 2) + 2));
                    hash.append(' ');
                }
                hash.append((CharSequence) "\n");
                for (int a2 = 0; a2 < 5; a2++) {
                    int num = ((encryptedChat.key_hash[(a2 * 4) + 16] & Byte.MAX_VALUE) << 24) | ((encryptedChat.key_hash[((a2 * 4) + 16) + 1] & 255) << 16) | ((encryptedChat.key_hash[((a2 * 4) + 16) + 2] & 255) << 8) | (encryptedChat.key_hash[(a2 * 4) + 16 + 3] & 255);
                    if (a2 != 0) {
                        emojis.append(" ");
                    }
                    emojis.append(EmojiData.emojiSecret[num % EmojiData.emojiSecret.length]);
                }
                this.emojiText = emojis.toString();
            }
            this.codeTextView.setText(hash.toString());
            hash.clear();
            hash.append((CharSequence) AndroidUtilities.replaceTags(LocaleController.formatString("EncryptionKeyDescription", R.string.EncryptionKeyDescription, user.first_name, user.first_name)));
            int index = hash.toString().indexOf("telegram.org");
            if (index != -1) {
                hash.setSpan(new URLSpanReplacement(LocaleController.getString("EncryptionKeyLink", R.string.EncryptionKeyLink)), index, "telegram.org".length() + index, 33);
            }
            this.textView.setText(hash);
        }
        updateEmojiButton(false);
        return this.fragmentView;
    }

    public static /* synthetic */ boolean lambda$createView$0(View v, MotionEvent event) {
        return true;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        fixLayout();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        super.onResume();
        fixLayout();
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        TextView textView;
        if (id == NotificationCenter.emojiLoaded && (textView = this.emojiTextView) != null) {
            textView.invalidate();
        }
    }

    private void updateEmojiButton(boolean animated) {
        AnimatorSet animatorSet = this.animatorSet;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.animatorSet = null;
        }
        float f = 1.0f;
        if (animated) {
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.animatorSet = animatorSet2;
            Animator[] animatorArr = new Animator[6];
            TextView textView = this.emojiTextView;
            float[] fArr = new float[1];
            fArr[0] = this.emojiSelected ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(textView, "alpha", fArr);
            TextView textView2 = this.codeTextView;
            float[] fArr2 = new float[1];
            fArr2[0] = this.emojiSelected ? 0.0f : 1.0f;
            animatorArr[1] = ObjectAnimator.ofFloat(textView2, "alpha", fArr2);
            TextView textView3 = this.emojiTextView;
            float[] fArr3 = new float[1];
            fArr3[0] = this.emojiSelected ? 1.0f : 0.0f;
            animatorArr[2] = ObjectAnimator.ofFloat(textView3, "scaleX", fArr3);
            TextView textView4 = this.emojiTextView;
            float[] fArr4 = new float[1];
            fArr4[0] = this.emojiSelected ? 1.0f : 0.0f;
            animatorArr[3] = ObjectAnimator.ofFloat(textView4, "scaleY", fArr4);
            TextView textView5 = this.codeTextView;
            float[] fArr5 = new float[1];
            fArr5[0] = this.emojiSelected ? 0.0f : 1.0f;
            animatorArr[4] = ObjectAnimator.ofFloat(textView5, "scaleX", fArr5);
            TextView textView6 = this.codeTextView;
            float[] fArr6 = new float[1];
            if (this.emojiSelected) {
                f = 0.0f;
            }
            fArr6[0] = f;
            animatorArr[5] = ObjectAnimator.ofFloat(textView6, "scaleY", fArr6);
            animatorSet2.playTogether(animatorArr);
            this.animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.IdenticonActivity.3
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    if (animation.equals(IdenticonActivity.this.animatorSet)) {
                        IdenticonActivity.this.animatorSet = null;
                    }
                }
            });
            this.animatorSet.setInterpolator(new DecelerateInterpolator());
            this.animatorSet.setDuration(150L);
            this.animatorSet.start();
        } else {
            this.emojiTextView.setAlpha(this.emojiSelected ? 1.0f : 0.0f);
            this.codeTextView.setAlpha(this.emojiSelected ? 0.0f : 1.0f);
            this.emojiTextView.setScaleX(this.emojiSelected ? 1.0f : 0.0f);
            this.emojiTextView.setScaleY(this.emojiSelected ? 1.0f : 0.0f);
            this.codeTextView.setScaleX(this.emojiSelected ? 0.0f : 1.0f);
            TextView textView7 = this.codeTextView;
            if (this.emojiSelected) {
                f = 0.0f;
            }
            textView7.setScaleY(f);
        }
        this.emojiTextView.setTag(!this.emojiSelected ? Theme.key_chat_emojiPanelIcon : Theme.key_chat_emojiPanelIconSelected);
    }

    private void fixLayout() {
        ViewTreeObserver obs = this.fragmentView.getViewTreeObserver();
        obs.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: org.telegram.ui.IdenticonActivity.4
            @Override // android.view.ViewTreeObserver.OnPreDrawListener
            public boolean onPreDraw() {
                if (IdenticonActivity.this.fragmentView == null) {
                    return true;
                }
                IdenticonActivity.this.fragmentView.getViewTreeObserver().removeOnPreDrawListener(this);
                WindowManager manager = (WindowManager) ApplicationLoader.applicationContext.getSystemService("window");
                int rotation = manager.getDefaultDisplay().getRotation();
                if (rotation == 3 || rotation == 1) {
                    IdenticonActivity.this.linearLayout.setOrientation(0);
                } else {
                    IdenticonActivity.this.linearLayout.setOrientation(1);
                }
                IdenticonActivity.this.fragmentView.setPadding(IdenticonActivity.this.fragmentView.getPaddingLeft(), 0, IdenticonActivity.this.fragmentView.getPaddingRight(), IdenticonActivity.this.fragmentView.getPaddingBottom());
                return true;
            }
        });
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        String str;
        if (isOpen && !backward && (str = this.emojiText) != null) {
            TextView textView = this.emojiTextView;
            textView.setText(Emoji.replaceEmoji(str, textView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(32.0f), false));
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(this.container, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        themeDescriptions.add(new ThemeDescription(this.textView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText4));
        themeDescriptions.add(new ThemeDescription(this.codeTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText4));
        themeDescriptions.add(new ThemeDescription(this.textView, ThemeDescription.FLAG_LINKCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteLinkText));
        return themeDescriptions;
    }
}
