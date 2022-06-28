package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
/* loaded from: classes5.dex */
public class SearchField extends FrameLayout {
    private ImageView clearSearchImageView;
    private CloseProgressDrawable2 progressDrawable;
    private final Theme.ResourcesProvider resourcesProvider;
    private View searchBackground;
    private EditTextBoldCursor searchEditText;
    private ImageView searchIconImageView;

    public SearchField(Context context, boolean supportRtl, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        FrameLayout.LayoutParams lp;
        FrameLayout.LayoutParams lp2;
        FrameLayout.LayoutParams lp3;
        FrameLayout.LayoutParams lp4;
        this.resourcesProvider = resourcesProvider;
        View view = new View(context);
        this.searchBackground = view;
        view.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(18.0f), getThemedColor(Theme.key_dialogSearchBackground)));
        if (supportRtl) {
            lp = LayoutHelper.createFrameRelatively(-1.0f, 36.0f, 8388659, 14.0f, 11.0f, 14.0f, 0.0f);
        } else {
            lp = LayoutHelper.createFrame(-1, 36.0f, 51, 14.0f, 11.0f, 14.0f, 0.0f);
        }
        addView(this.searchBackground, lp);
        ImageView imageView = new ImageView(context);
        this.searchIconImageView = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        this.searchIconImageView.setImageResource(R.drawable.smiles_inputsearch);
        this.searchIconImageView.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_dialogSearchIcon), PorterDuff.Mode.MULTIPLY));
        if (supportRtl) {
            lp2 = LayoutHelper.createFrameRelatively(36.0f, 36.0f, 8388659, 16.0f, 11.0f, 0.0f, 0.0f);
        } else {
            lp2 = LayoutHelper.createFrame(36, 36.0f, 51, 16.0f, 11.0f, 0.0f, 0.0f);
        }
        addView(this.searchIconImageView, lp2);
        ImageView imageView2 = new ImageView(context);
        this.clearSearchImageView = imageView2;
        imageView2.setScaleType(ImageView.ScaleType.CENTER);
        ImageView imageView3 = this.clearSearchImageView;
        CloseProgressDrawable2 closeProgressDrawable2 = new CloseProgressDrawable2() { // from class: org.telegram.ui.Components.SearchField.1
            @Override // org.telegram.ui.Components.CloseProgressDrawable2
            protected int getCurrentColor() {
                return SearchField.this.getThemedColor(Theme.key_dialogSearchIcon);
            }
        };
        this.progressDrawable = closeProgressDrawable2;
        imageView3.setImageDrawable(closeProgressDrawable2);
        this.progressDrawable.setSide(AndroidUtilities.dp(7.0f));
        this.clearSearchImageView.setScaleX(0.1f);
        this.clearSearchImageView.setScaleY(0.1f);
        this.clearSearchImageView.setAlpha(0.0f);
        if (supportRtl) {
            lp3 = LayoutHelper.createFrameRelatively(36.0f, 36.0f, 8388661, 14.0f, 11.0f, 14.0f, 0.0f);
        } else {
            lp3 = LayoutHelper.createFrame(36, 36.0f, 53, 14.0f, 11.0f, 14.0f, 0.0f);
        }
        addView(this.clearSearchImageView, lp3);
        this.clearSearchImageView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.SearchField$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                SearchField.this.m2981lambda$new$0$orgtelegramuiComponentsSearchField(view2);
            }
        });
        EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context) { // from class: org.telegram.ui.Components.SearchField.2
            @Override // org.telegram.ui.Components.EditTextEffects, android.view.View
            public boolean dispatchTouchEvent(MotionEvent event) {
                SearchField.this.processTouchEvent(event);
                return super.dispatchTouchEvent(event);
            }

            @Override // org.telegram.ui.Components.EditTextBoldCursor, android.widget.TextView, android.view.View
            public boolean onTouchEvent(MotionEvent event) {
                if (!isEnabled()) {
                    return false;
                }
                if (event.getAction() == 1) {
                    SearchField.this.onFieldTouchUp(this);
                }
                return super.onTouchEvent(event);
            }
        };
        this.searchEditText = editTextBoldCursor;
        editTextBoldCursor.setTextSize(1, 16.0f);
        this.searchEditText.setHintTextColor(getThemedColor(Theme.key_dialogSearchHint));
        this.searchEditText.setTextColor(getThemedColor(Theme.key_dialogSearchText));
        this.searchEditText.setBackgroundDrawable(null);
        this.searchEditText.setPadding(0, 0, 0, 0);
        this.searchEditText.setMaxLines(1);
        this.searchEditText.setLines(1);
        this.searchEditText.setSingleLine(true);
        this.searchEditText.setGravity((supportRtl ? LayoutHelper.getAbsoluteGravityStart() : 3) | 16);
        this.searchEditText.setImeOptions(268435459);
        this.searchEditText.setCursorColor(getThemedColor(Theme.key_featuredStickers_addedIcon));
        this.searchEditText.setCursorSize(AndroidUtilities.dp(20.0f));
        this.searchEditText.setCursorWidth(1.5f);
        if (supportRtl) {
            lp4 = LayoutHelper.createFrameRelatively(-1.0f, 40.0f, 8388659, 54.0f, 9.0f, 46.0f, 0.0f);
        } else {
            lp4 = LayoutHelper.createFrame(-1, 40.0f, 51, 54.0f, 9.0f, 46.0f, 0.0f);
        }
        addView(this.searchEditText, lp4);
        this.searchEditText.addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.Components.SearchField.3
            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable s) {
                boolean showed = true;
                boolean show = SearchField.this.searchEditText.length() > 0;
                float f = 0.0f;
                if (SearchField.this.clearSearchImageView.getAlpha() == 0.0f) {
                    showed = false;
                }
                if (show != showed) {
                    ViewPropertyAnimator animate = SearchField.this.clearSearchImageView.animate();
                    float f2 = 1.0f;
                    if (show) {
                        f = 1.0f;
                    }
                    ViewPropertyAnimator scaleX = animate.alpha(f).setDuration(150L).scaleX(show ? 1.0f : 0.1f);
                    if (!show) {
                        f2 = 0.1f;
                    }
                    scaleX.scaleY(f2).start();
                }
                SearchField searchField = SearchField.this;
                searchField.onTextChange(searchField.searchEditText.getText().toString());
            }
        });
        this.searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: org.telegram.ui.Components.SearchField$$ExternalSyntheticLambda1
            @Override // android.widget.TextView.OnEditorActionListener
            public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return SearchField.this.m2982lambda$new$1$orgtelegramuiComponentsSearchField(textView, i, keyEvent);
            }
        });
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-SearchField */
    public /* synthetic */ void m2981lambda$new$0$orgtelegramuiComponentsSearchField(View v) {
        this.searchEditText.setText("");
        AndroidUtilities.showKeyboard(this.searchEditText);
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-SearchField */
    public /* synthetic */ boolean m2982lambda$new$1$orgtelegramuiComponentsSearchField(TextView v, int actionId, KeyEvent event) {
        if (event != null) {
            if ((event.getAction() == 1 && event.getKeyCode() == 84) || (event.getAction() == 0 && event.getKeyCode() == 66)) {
                this.searchEditText.hideActionMode();
                AndroidUtilities.hideKeyboard(this.searchEditText);
                return false;
            }
            return false;
        }
        return false;
    }

    public void hideKeyboard() {
        AndroidUtilities.hideKeyboard(this.searchEditText);
    }

    public void setHint(String text) {
        this.searchEditText.setHint(text);
    }

    protected void onFieldTouchUp(EditTextBoldCursor editText) {
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    public void processTouchEvent(MotionEvent event) {
    }

    public void onTextChange(String text) {
    }

    public View getSearchBackground() {
        return this.searchBackground;
    }

    public EditTextBoldCursor getSearchEditText() {
        return this.searchEditText;
    }

    public CloseProgressDrawable2 getProgressDrawable() {
        return this.progressDrawable;
    }

    public void getThemeDescriptions(List<ThemeDescription> descriptions) {
        descriptions.add(new ThemeDescription(this.searchBackground, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_dialogSearchBackground));
        descriptions.add(new ThemeDescription(this.searchIconImageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_dialogSearchIcon));
        descriptions.add(new ThemeDescription(this.clearSearchImageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_dialogSearchIcon));
        descriptions.add(new ThemeDescription(this.searchEditText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_dialogSearchText));
        descriptions.add(new ThemeDescription(this.searchEditText, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_dialogSearchHint));
        descriptions.add(new ThemeDescription(this.searchEditText, ThemeDescription.FLAG_CURSORCOLOR, null, null, null, null, Theme.key_featuredStickers_addedIcon));
    }

    public int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
