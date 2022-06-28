package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes5.dex */
public class ChatBigEmptyView extends LinearLayout {
    public static final int EMPTY_VIEW_TYPE_GROUP = 1;
    public static final int EMPTY_VIEW_TYPE_SAVED = 2;
    public static final int EMPTY_VIEW_TYPE_SECRET = 0;
    private final Theme.ResourcesProvider resourcesProvider;
    private TextView statusTextView;
    private ArrayList<TextView> textViews = new ArrayList<>();
    private ArrayList<ImageView> imageViews = new ArrayList<>();

    public ChatBigEmptyView(Context context, View parent, int type, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.resourcesProvider = resourcesProvider;
        setBackground(Theme.createServiceDrawable(AndroidUtilities.dp(18.0f), this, parent, getThemedPaint(Theme.key_paint_chatActionBackground)));
        setPadding(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(12.0f));
        setOrientation(1);
        if (type == 0) {
            TextView textView = new TextView(context);
            this.statusTextView = textView;
            textView.setTextSize(1, 15.0f);
            this.statusTextView.setTextColor(getThemedColor(Theme.key_chat_serviceText));
            this.statusTextView.setGravity(1);
            this.statusTextView.setMaxWidth(AndroidUtilities.dp(210.0f));
            this.textViews.add(this.statusTextView);
            addView(this.statusTextView, LayoutHelper.createLinear(-2, -2, 49));
        } else if (type == 1) {
            TextView textView2 = new TextView(context);
            this.statusTextView = textView2;
            textView2.setTextSize(1, 15.0f);
            this.statusTextView.setTextColor(getThemedColor(Theme.key_chat_serviceText));
            this.statusTextView.setGravity(1);
            this.statusTextView.setMaxWidth(AndroidUtilities.dp(210.0f));
            this.textViews.add(this.statusTextView);
            addView(this.statusTextView, LayoutHelper.createLinear(-2, -2, 49));
        } else {
            RLottieImageView imageView = new RLottieImageView(context);
            imageView.setAutoRepeat(true);
            imageView.setAnimation(R.raw.utyan_saved_messages, 120, 120);
            imageView.playAnimation();
            addView(imageView, LayoutHelper.createLinear(-2, -2, 49, 0, 2, 0, 0));
        }
        TextView textView3 = new TextView(context);
        if (type == 0) {
            textView3.setText(LocaleController.getString("EncryptedDescriptionTitle", R.string.EncryptedDescriptionTitle));
            textView3.setTextSize(1, 15.0f);
        } else if (type == 1) {
            textView3.setText(LocaleController.getString("GroupEmptyTitle2", R.string.GroupEmptyTitle2));
            textView3.setTextSize(1, 15.0f);
        } else {
            textView3.setText(LocaleController.getString("ChatYourSelfTitle", R.string.ChatYourSelfTitle));
            textView3.setTextSize(1, 16.0f);
            textView3.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            textView3.setGravity(1);
        }
        textView3.setTextColor(getThemedColor(Theme.key_chat_serviceText));
        this.textViews.add(textView3);
        textView3.setMaxWidth(AndroidUtilities.dp(260.0f));
        int i = 0;
        addView(textView3, LayoutHelper.createLinear(-2, -2, (type != 2 ? LocaleController.isRTL ? 5 : 3 : 1) | 48, 0, 8, 0, type != 2 ? 0 : 8));
        int a = 0;
        while (a < 4) {
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(i);
            addView(linearLayout, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 0, 8, 0, 0));
            ImageView imageView2 = new ImageView(context);
            imageView2.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_chat_serviceText), PorterDuff.Mode.MULTIPLY));
            if (type == 0) {
                imageView2.setImageResource(R.drawable.ic_lock_white);
            } else if (type == 2) {
                imageView2.setImageResource(R.drawable.list_circle);
            } else {
                imageView2.setImageResource(R.drawable.groups_overview_check);
            }
            this.imageViews.add(imageView2);
            TextView textView4 = new TextView(context);
            textView4.setTextSize(1, 15.0f);
            textView4.setTextColor(getThemedColor(Theme.key_chat_serviceText));
            this.textViews.add(textView4);
            textView4.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            textView4.setMaxWidth(AndroidUtilities.dp(260.0f));
            switch (a) {
                case 0:
                    if (type == 0) {
                        textView4.setText(LocaleController.getString("EncryptedDescription1", R.string.EncryptedDescription1));
                        break;
                    } else if (type == 2) {
                        textView4.setText(LocaleController.getString("ChatYourSelfDescription1", R.string.ChatYourSelfDescription1));
                        break;
                    } else {
                        textView4.setText(LocaleController.getString("GroupDescription1", R.string.GroupDescription1));
                        break;
                    }
                case 1:
                    if (type == 0) {
                        textView4.setText(LocaleController.getString("EncryptedDescription2", R.string.EncryptedDescription2));
                        break;
                    } else if (type == 2) {
                        textView4.setText(LocaleController.getString("ChatYourSelfDescription2", R.string.ChatYourSelfDescription2));
                        break;
                    } else {
                        textView4.setText(LocaleController.getString("GroupDescription2", R.string.GroupDescription2));
                        break;
                    }
                case 2:
                    if (type == 0) {
                        textView4.setText(LocaleController.getString("EncryptedDescription3", R.string.EncryptedDescription3));
                        break;
                    } else if (type == 2) {
                        textView4.setText(LocaleController.getString("ChatYourSelfDescription3", R.string.ChatYourSelfDescription3));
                        break;
                    } else {
                        textView4.setText(LocaleController.getString("GroupDescription3", R.string.GroupDescription3));
                        break;
                    }
                case 3:
                    if (type == 0) {
                        textView4.setText(LocaleController.getString("EncryptedDescription4", R.string.EncryptedDescription4));
                        break;
                    } else if (type == 2) {
                        textView4.setText(LocaleController.getString("ChatYourSelfDescription4", R.string.ChatYourSelfDescription4));
                        break;
                    } else {
                        textView4.setText(LocaleController.getString("GroupDescription4", R.string.GroupDescription4));
                        break;
                    }
            }
            if (LocaleController.isRTL) {
                linearLayout.addView(textView4, LayoutHelper.createLinear(-2, -2));
                if (type == 0) {
                    linearLayout.addView(imageView2, LayoutHelper.createLinear(-2, -2, 8.0f, 3.0f, 0.0f, 0.0f));
                } else if (type == 2) {
                    linearLayout.addView(imageView2, LayoutHelper.createLinear(-2, -2, 8.0f, 7.0f, 0.0f, 0.0f));
                } else {
                    linearLayout.addView(imageView2, LayoutHelper.createLinear(-2, -2, 8.0f, 3.0f, 0.0f, 0.0f));
                }
            } else {
                if (type == 0) {
                    linearLayout.addView(imageView2, LayoutHelper.createLinear(-2, -2, 0.0f, 4.0f, 8.0f, 0.0f));
                } else if (type == 2) {
                    linearLayout.addView(imageView2, LayoutHelper.createLinear(-2, -2, 0.0f, 8.0f, 8.0f, 0.0f));
                } else {
                    linearLayout.addView(imageView2, LayoutHelper.createLinear(-2, -2, 0.0f, 4.0f, 8.0f, 0.0f));
                }
                linearLayout.addView(textView4, LayoutHelper.createLinear(-2, -2));
            }
            a++;
            i = 0;
        }
    }

    public void setTextColor(int color) {
        for (int a = 0; a < this.textViews.size(); a++) {
            this.textViews.get(a).setTextColor(color);
        }
        for (int a2 = 0; a2 < this.imageViews.size(); a2++) {
            this.imageViews.get(a2).setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_chat_serviceText), PorterDuff.Mode.MULTIPLY));
        }
    }

    public void setStatusText(CharSequence text) {
        this.statusTextView.setText(text);
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }

    private Paint getThemedPaint(String paintKey) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Paint paint = resourcesProvider != null ? resourcesProvider.getPaint(paintKey) : null;
        return paint != null ? paint : Theme.getThemePaint(paintKey);
    }
}
