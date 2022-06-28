package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.exoplayer2.C;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
/* loaded from: classes4.dex */
public class ChatUnreadCell extends FrameLayout {
    private FrameLayout backgroundLayout;
    private ImageView imageView;
    private Theme.ResourcesProvider resourcesProvider;
    private TextView textView;

    public ChatUnreadCell(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.resourcesProvider = resourcesProvider;
        FrameLayout frameLayout = new FrameLayout(context);
        this.backgroundLayout = frameLayout;
        frameLayout.setBackgroundResource(R.drawable.newmsg_divider);
        this.backgroundLayout.getBackground().setColorFilter(new PorterDuffColorFilter(getColor(Theme.key_chat_unreadMessagesStartBackground), PorterDuff.Mode.MULTIPLY));
        addView(this.backgroundLayout, LayoutHelper.createFrame(-1, 27.0f, 51, 0.0f, 7.0f, 0.0f, 0.0f));
        ImageView imageView = new ImageView(context);
        this.imageView = imageView;
        imageView.setImageResource(R.drawable.ic_ab_new);
        this.imageView.setColorFilter(new PorterDuffColorFilter(getColor(Theme.key_chat_unreadMessagesStartArrowIcon), PorterDuff.Mode.MULTIPLY));
        this.imageView.setPadding(0, AndroidUtilities.dp(2.0f), 0, 0);
        this.backgroundLayout.addView(this.imageView, LayoutHelper.createFrame(-2, -2.0f, 21, 0.0f, 0.0f, 10.0f, 0.0f));
        TextView textView = new TextView(context);
        this.textView = textView;
        textView.setPadding(0, 0, 0, AndroidUtilities.dp(1.0f));
        this.textView.setTextSize(1, 14.0f);
        this.textView.setTextColor(getColor(Theme.key_chat_unreadMessagesStartText));
        this.textView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        addView(this.textView, LayoutHelper.createFrame(-2, -2, 17));
    }

    public void setText(String text) {
        this.textView.setText(text);
    }

    public ImageView getImageView() {
        return this.imageView;
    }

    public TextView getTextView() {
        return this.textView;
    }

    public FrameLayout getBackgroundLayout() {
        return this.backgroundLayout;
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(40.0f), C.BUFFER_FLAG_ENCRYPTED));
    }

    private int getColor(String key) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
