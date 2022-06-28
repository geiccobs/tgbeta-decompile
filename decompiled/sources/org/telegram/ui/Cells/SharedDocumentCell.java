package org.telegram.ui.Cells;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.extractor.ts.TsExtractor;
import com.microsoft.appcenter.Constants;
import java.io.File;
import java.util.Date;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.DotDividerSpan;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LineProgressView;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.FilteredSearchView;
/* loaded from: classes4.dex */
public class SharedDocumentCell extends FrameLayout implements DownloadController.FileDownloadProgressListener {
    public static final int VIEW_TYPE_DEFAULT = 0;
    public static final int VIEW_TYPE_GLOBAL_SEARCH = 2;
    public static final int VIEW_TYPE_PICKER = 1;
    private int TAG;
    private CharSequence caption;
    private TextView captionTextView;
    private CheckBox2 checkBox;
    private int currentAccount;
    private TextView dateTextView;
    private SpannableStringBuilder dotSpan;
    private long downloadedSize;
    private boolean drawDownloadIcon;
    float enterAlpha;
    private TextView extTextView;
    FlickerLoadingView globalGradientView;
    boolean ignoreRequestLayout;
    private boolean loaded;
    private boolean loading;
    private MessageObject message;
    private TextView nameTextView;
    private boolean needDivider;
    private ImageView placeholderImageView;
    private LineProgressView progressView;
    private final Theme.ResourcesProvider resourcesProvider;
    public TextView rightDateTextView;
    private RLottieDrawable statusDrawable;
    private RLottieImageView statusImageView;
    private BackupImageView thumbImageView;
    private int viewType;

    public SharedDocumentCell(Context context) {
        this(context, 0);
    }

    public SharedDocumentCell(Context context, int viewType) {
        this(context, viewType, null);
    }

    public SharedDocumentCell(Context context, int viewType, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.drawDownloadIcon = true;
        int i = UserConfig.selectedAccount;
        this.currentAccount = i;
        this.enterAlpha = 1.0f;
        this.resourcesProvider = resourcesProvider;
        this.viewType = viewType;
        this.TAG = DownloadController.getInstance(i).generateObserverTag();
        ImageView imageView = new ImageView(context);
        this.placeholderImageView = imageView;
        if (viewType == 1) {
            addView(imageView, LayoutHelper.createFrame(42, 42.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 15.0f, 12.0f, LocaleController.isRTL ? 15.0f : 0.0f, 0.0f));
        } else {
            addView(imageView, LayoutHelper.createFrame(40, 40.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 12.0f, 8.0f, LocaleController.isRTL ? 12.0f : 0.0f, 0.0f));
        }
        TextView textView = new TextView(context);
        this.extTextView = textView;
        textView.setTextColor(getThemedColor(Theme.key_files_iconText));
        this.extTextView.setTextSize(1, 14.0f);
        this.extTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.extTextView.setLines(1);
        this.extTextView.setMaxLines(1);
        this.extTextView.setSingleLine(true);
        this.extTextView.setGravity(17);
        this.extTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.extTextView.setImportantForAccessibility(2);
        if (viewType == 1) {
            addView(this.extTextView, LayoutHelper.createFrame(32, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 20.0f, 28.0f, LocaleController.isRTL ? 20.0f : 0.0f, 0.0f));
        } else {
            addView(this.extTextView, LayoutHelper.createFrame(32, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 22.0f, LocaleController.isRTL ? 16.0f : 0.0f, 0.0f));
        }
        BackupImageView backupImageView = new BackupImageView(context) { // from class: org.telegram.ui.Cells.SharedDocumentCell.1
            @Override // org.telegram.ui.Components.BackupImageView, android.view.View
            public void onDraw(Canvas canvas) {
                float alpha;
                if (SharedDocumentCell.this.thumbImageView.getImageReceiver().hasBitmapImage()) {
                    alpha = 1.0f - SharedDocumentCell.this.thumbImageView.getImageReceiver().getCurrentAlpha();
                } else {
                    alpha = 1.0f;
                }
                SharedDocumentCell.this.extTextView.setAlpha(alpha);
                SharedDocumentCell.this.placeholderImageView.setAlpha(alpha);
                super.onDraw(canvas);
            }
        };
        this.thumbImageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(4.0f));
        if (viewType == 1) {
            addView(this.thumbImageView, LayoutHelper.createFrame(42, 42.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 12.0f, LocaleController.isRTL ? 16.0f : 0.0f, 0.0f));
        } else {
            addView(this.thumbImageView, LayoutHelper.createFrame(40, 40.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 12.0f, 8.0f, LocaleController.isRTL ? 12.0f : 0.0f, 0.0f));
        }
        TextView textView2 = new TextView(context);
        this.nameTextView = textView2;
        textView2.setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteBlackText));
        this.nameTextView.setTextSize(1, 16.0f);
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        if (viewType == 1) {
            this.nameTextView.setLines(1);
            this.nameTextView.setMaxLines(1);
            this.nameTextView.setSingleLine(true);
            addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 8.0f : 72.0f, 9.0f, LocaleController.isRTL ? 72.0f : 8.0f, 0.0f));
        } else if (viewType != 2) {
            this.nameTextView.setMaxLines(1);
            addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 8.0f : 72.0f, 5.0f, LocaleController.isRTL ? 72.0f : 8.0f, 0.0f));
        } else {
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(0);
            addView(linearLayout, LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 16.0f : 72.0f, 5.0f, LocaleController.isRTL ? 72.0f : 16.0f, 0.0f));
            TextView textView3 = new TextView(context);
            this.rightDateTextView = textView3;
            textView3.setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteGrayText3));
            this.rightDateTextView.setTextSize(1, 14.0f);
            if (LocaleController.isRTL) {
                linearLayout.addView(this.rightDateTextView, LayoutHelper.createLinear(-2, -2, 0.0f));
                linearLayout.addView(this.nameTextView, LayoutHelper.createLinear(-2, -2, 1.0f, 0, 0, 4, 0));
            } else {
                linearLayout.addView(this.nameTextView, LayoutHelper.createLinear(-2, -2, 1.0f));
                linearLayout.addView(this.rightDateTextView, LayoutHelper.createLinear(-2, -2, 0.0f, 4, 0, 0, 0));
            }
            this.nameTextView.setMaxLines(2);
            TextView textView4 = new TextView(context);
            this.captionTextView = textView4;
            textView4.setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteBlackText));
            this.captionTextView.setLines(1);
            this.captionTextView.setMaxLines(1);
            this.captionTextView.setSingleLine(true);
            this.captionTextView.setEllipsize(TextUtils.TruncateAt.END);
            this.captionTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            this.captionTextView.setTextSize(1, 13.0f);
            addView(this.captionTextView, LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 8.0f : 72.0f, 30.0f, LocaleController.isRTL ? 72.0f : 8.0f, 0.0f));
            this.captionTextView.setVisibility(8);
        }
        this.statusDrawable = new RLottieDrawable(R.raw.download_arrow, "download_arrow", AndroidUtilities.dp(14.0f), AndroidUtilities.dp(14.0f), true, null);
        RLottieImageView rLottieImageView = new RLottieImageView(context);
        this.statusImageView = rLottieImageView;
        rLottieImageView.setAnimation(this.statusDrawable);
        this.statusImageView.setVisibility(4);
        this.statusImageView.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_sharedMedia_startStopLoadIcon), PorterDuff.Mode.MULTIPLY));
        if (viewType == 1) {
            addView(this.statusImageView, LayoutHelper.createFrame(14, 14.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 8.0f : 70.0f, 37.0f, LocaleController.isRTL ? 72.0f : 8.0f, 0.0f));
        } else {
            addView(this.statusImageView, LayoutHelper.createFrame(14, 14.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 8.0f : 70.0f, 33.0f, LocaleController.isRTL ? 72.0f : 8.0f, 0.0f));
        }
        TextView textView5 = new TextView(context);
        this.dateTextView = textView5;
        textView5.setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteGrayText3));
        this.dateTextView.setLines(1);
        this.dateTextView.setMaxLines(1);
        this.dateTextView.setSingleLine(true);
        this.dateTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.dateTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        if (viewType == 1) {
            this.dateTextView.setTextSize(1, 13.0f);
            addView(this.dateTextView, LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 8.0f : 72.0f, 34.0f, LocaleController.isRTL ? 72.0f : 8.0f, 0.0f));
        } else {
            this.dateTextView.setTextSize(1, 13.0f);
            addView(this.dateTextView, LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 8.0f : 72.0f, 30.0f, LocaleController.isRTL ? 72.0f : 8.0f, 0.0f));
        }
        LineProgressView lineProgressView = new LineProgressView(context);
        this.progressView = lineProgressView;
        lineProgressView.setProgressColor(getThemedColor(Theme.key_sharedMedia_startStopLoadIcon));
        addView(this.progressView, LayoutHelper.createFrame(-1, 2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 72.0f, 54.0f, LocaleController.isRTL ? 72.0f : 0.0f, 0.0f));
        CheckBox2 checkBox2 = new CheckBox2(context, 21);
        this.checkBox = checkBox2;
        checkBox2.setVisibility(4);
        this.checkBox.setColor(null, Theme.key_windowBackgroundWhite, Theme.key_checkboxCheck);
        this.checkBox.setDrawUnchecked(false);
        this.checkBox.setDrawBackgroundAsArc(2);
        if (viewType == 1) {
            addView(this.checkBox, LayoutHelper.createFrame(24, 24.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 38.0f, 36.0f, LocaleController.isRTL ? 38.0f : 0.0f, 0.0f));
        } else {
            addView(this.checkBox, LayoutHelper.createFrame(24, 24.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 33.0f, 28.0f, LocaleController.isRTL ? 33.0f : 0.0f, 0.0f));
        }
        if (viewType == 2) {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(".");
            this.dotSpan = spannableStringBuilder;
            spannableStringBuilder.setSpan(new DotDividerSpan(), 0, 1, 0);
        }
    }

    public void setDrawDownloadIcon(boolean value) {
        this.drawDownloadIcon = value;
    }

    public void setTextAndValueAndTypeAndThumb(String text, String value, String type, String thumb, int resId, boolean divider) {
        String iconKey;
        String backKey;
        this.nameTextView.setText(text);
        this.dateTextView.setText(value);
        if (type == null) {
            this.extTextView.setVisibility(4);
        } else {
            this.extTextView.setVisibility(0);
            this.extTextView.setText(type.toLowerCase());
        }
        this.needDivider = divider;
        if (resId != 0) {
            this.placeholderImageView.setVisibility(4);
        } else {
            this.placeholderImageView.setImageResource(AndroidUtilities.getThumbForNameOrMime(text, type, false));
            this.placeholderImageView.setVisibility(0);
        }
        if (thumb != null || resId != 0) {
            if (thumb != null) {
                this.thumbImageView.setImage(thumb, "42_42", null);
            } else {
                Drawable drawable = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(42.0f), resId);
                if (resId == R.drawable.files_storage) {
                    backKey = Theme.key_chat_attachLocationBackground;
                    iconKey = Theme.key_chat_attachLocationIcon;
                } else if (resId == R.drawable.files_gallery) {
                    backKey = Theme.key_chat_attachContactBackground;
                    iconKey = Theme.key_chat_attachContactIcon;
                } else if (resId == R.drawable.files_music) {
                    backKey = Theme.key_chat_attachAudioBackground;
                    iconKey = Theme.key_chat_attachAudioIcon;
                } else if (resId == R.drawable.files_internal) {
                    backKey = Theme.key_chat_attachGalleryBackground;
                    iconKey = Theme.key_chat_attachGalleryIcon;
                } else {
                    backKey = Theme.key_files_folderIconBackground;
                    iconKey = Theme.key_files_folderIcon;
                }
                Theme.setCombinedDrawableColor(drawable, getThemedColor(backKey), false);
                Theme.setCombinedDrawableColor(drawable, getThemedColor(iconKey), true);
                this.thumbImageView.setImageDrawable(drawable);
            }
            this.thumbImageView.setVisibility(0);
        } else {
            this.extTextView.setAlpha(1.0f);
            this.placeholderImageView.setAlpha(1.0f);
            this.thumbImageView.setImageBitmap(null);
            this.thumbImageView.setVisibility(4);
        }
        setWillNotDraw(!this.needDivider);
    }

    public void setPhotoEntry(MediaController.PhotoEntry entry) {
        String path;
        if (entry.thumbPath != null) {
            this.thumbImageView.setImage(entry.thumbPath, null, Theme.chat_attachEmptyDrawable);
            path = entry.thumbPath;
        } else {
            String path2 = entry.path;
            if (path2 != null) {
                if (entry.isVideo) {
                    this.thumbImageView.setOrientation(0, true);
                    BackupImageView backupImageView = this.thumbImageView;
                    backupImageView.setImage("vthumb://" + entry.imageId + Constants.COMMON_SCHEMA_PREFIX_SEPARATOR + entry.path, null, Theme.chat_attachEmptyDrawable);
                } else {
                    this.thumbImageView.setOrientation(entry.orientation, true);
                    BackupImageView backupImageView2 = this.thumbImageView;
                    backupImageView2.setImage("thumb://" + entry.imageId + Constants.COMMON_SCHEMA_PREFIX_SEPARATOR + entry.path, null, Theme.chat_attachEmptyDrawable);
                }
                path = entry.path;
            } else {
                this.thumbImageView.setImageDrawable(Theme.chat_attachEmptyDrawable);
                path = "";
            }
        }
        File file = new File(path);
        this.nameTextView.setText(file.getName());
        FileLoader.getFileExtension(file);
        this.extTextView.setVisibility(8);
        StringBuilder builder = new StringBuilder();
        if (entry.width != 0 && entry.height != 0) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(String.format(Locale.US, "%dx%d", Integer.valueOf(entry.width), Integer.valueOf(entry.height)));
        }
        if (entry.isVideo) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(AndroidUtilities.formatShortDuration(entry.duration));
        }
        if (entry.size != 0) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(AndroidUtilities.formatFileSize(entry.size));
        }
        if (builder.length() > 0) {
            builder.append(", ");
        }
        builder.append(LocaleController.getInstance().formatterStats.format(entry.dateTaken));
        this.dateTextView.setText(builder);
        this.placeholderImageView.setVisibility(8);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.progressView.getVisibility() == 0) {
            updateFileExistIcon(false);
        }
    }

    public void setChecked(boolean checked, boolean animated) {
        if (this.checkBox.getVisibility() != 0) {
            this.checkBox.setVisibility(0);
        }
        this.checkBox.setChecked(checked, animated);
    }

    /* JADX WARN: Removed duplicated region for block: B:103:0x0262  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void setDocument(org.telegram.messenger.MessageObject r25, boolean r26) {
        /*
            Method dump skipped, instructions count: 692
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.SharedDocumentCell.setDocument(org.telegram.messenger.MessageObject, boolean):void");
    }

    private void updateDateView() {
        String fileSize;
        MessageObject messageObject = this.message;
        if (messageObject == null || messageObject.getDocument() == null) {
            return;
        }
        long date = this.message.messageOwner.date * 1000;
        if (this.downloadedSize == 0) {
            fileSize = AndroidUtilities.formatFileSize(this.message.getDocument().size);
        } else {
            fileSize = String.format(Locale.ENGLISH, "%s / %s", AndroidUtilities.formatFileSize(this.downloadedSize), AndroidUtilities.formatFileSize(this.message.getDocument().size));
        }
        if (this.viewType == 2) {
            CharSequence fromName = FilteredSearchView.createFromInfoString(this.message);
            this.dateTextView.setText(new SpannableStringBuilder().append((CharSequence) fileSize).append(' ').append((CharSequence) this.dotSpan).append(' ').append(fromName));
            this.rightDateTextView.setText(LocaleController.stringForMessageListDate(this.message.messageOwner.date));
            return;
        }
        this.dateTextView.setText(String.format("%s, %s", fileSize, LocaleController.formatString("formatDateAtTime", R.string.formatDateAtTime, LocaleController.getInstance().formatterYear.format(new Date(date)), LocaleController.getInstance().formatterDay.format(new Date(date)))));
    }

    public void updateFileExistIcon(boolean animated) {
        if (animated && Build.VERSION.SDK_INT >= 19) {
            TransitionSet transition = new TransitionSet();
            ChangeBounds changeBounds = new ChangeBounds();
            changeBounds.setDuration(150L);
            transition.addTransition(new Fade().setDuration(150L)).addTransition(changeBounds);
            transition.setOrdering(0);
            transition.setInterpolator((TimeInterpolator) CubicBezierInterpolator.DEFAULT);
            TransitionManager.beginDelayedTransition(this, transition);
        }
        MessageObject messageObject = this.message;
        float f = 72.0f;
        float f2 = 8.0f;
        if (messageObject != null && messageObject.messageOwner.media != null) {
            this.loaded = false;
            if (this.message.attachPathExists || this.message.mediaExists || !this.drawDownloadIcon) {
                this.statusImageView.setVisibility(4);
                this.progressView.setVisibility(4);
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.dateTextView.getLayoutParams();
                if (layoutParams != null) {
                    layoutParams.leftMargin = AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : 72.0f);
                    if (!LocaleController.isRTL) {
                        f = 8.0f;
                    }
                    layoutParams.rightMargin = AndroidUtilities.dp(f);
                    this.dateTextView.requestLayout();
                }
                this.loading = false;
                this.loaded = true;
                DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
                return;
            }
            String fileName = FileLoader.getAttachFileName(this.message.getDocument());
            DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(fileName, this.message, this);
            this.loading = FileLoader.getInstance(this.currentAccount).isLoadingFile(fileName);
            this.statusImageView.setVisibility(0);
            int i = 15;
            this.statusDrawable.setCustomEndFrame(this.loading ? 15 : 0);
            this.statusDrawable.setPlayInDirectionOfCustomEndFrame(true);
            if (animated) {
                this.statusImageView.playAnimation();
            } else {
                RLottieDrawable rLottieDrawable = this.statusDrawable;
                if (!this.loading) {
                    i = 0;
                }
                rLottieDrawable.setCurrentFrame(i);
                this.statusImageView.invalidate();
            }
            FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.dateTextView.getLayoutParams();
            if (layoutParams2 != null) {
                layoutParams2.leftMargin = AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : 86.0f);
                if (LocaleController.isRTL) {
                    f2 = 86.0f;
                }
                layoutParams2.rightMargin = AndroidUtilities.dp(f2);
                this.dateTextView.requestLayout();
            }
            if (this.loading) {
                this.progressView.setVisibility(0);
                Float progress = ImageLoader.getInstance().getFileProgress(fileName);
                if (progress == null) {
                    progress = Float.valueOf(0.0f);
                }
                this.progressView.setProgress(progress.floatValue(), false);
                return;
            }
            this.progressView.setVisibility(4);
            return;
        }
        this.loading = false;
        this.loaded = true;
        this.progressView.setVisibility(4);
        this.progressView.setProgress(0.0f, false);
        this.statusImageView.setVisibility(4);
        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) this.dateTextView.getLayoutParams();
        if (layoutParams3 != null) {
            layoutParams3.leftMargin = AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : 72.0f);
            if (!LocaleController.isRTL) {
                f = 8.0f;
            }
            layoutParams3.rightMargin = AndroidUtilities.dp(f);
            this.dateTextView.requestLayout();
        }
        DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
    }

    public MessageObject getMessage() {
        return this.message;
    }

    public boolean isLoaded() {
        return this.loaded;
    }

    public boolean isLoading() {
        return this.loading;
    }

    public BackupImageView getImageView() {
        return this.thumbImageView;
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int i = this.viewType;
        if (i == 1) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + (this.needDivider ? 1 : 0), C.BUFFER_FLAG_ENCRYPTED));
        } else if (i == 0) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(56.0f), C.BUFFER_FLAG_ENCRYPTED));
        } else {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(56.0f), C.BUFFER_FLAG_ENCRYPTED));
            int h = AndroidUtilities.dp(34.0f) + this.nameTextView.getMeasuredHeight() + (this.needDivider ? 1 : 0);
            if (this.caption != null && this.captionTextView != null && this.message.hasHighlightedWords()) {
                this.ignoreRequestLayout = true;
                this.captionTextView.setText(AndroidUtilities.ellipsizeCenterEnd(this.caption, this.message.highlightedWords.get(0), this.captionTextView.getMeasuredWidth(), this.captionTextView.getPaint(), TsExtractor.TS_STREAM_TYPE_HDMV_DTS));
                this.ignoreRequestLayout = false;
                h += this.captionTextView.getMeasuredHeight() + AndroidUtilities.dp(3.0f);
            }
            setMeasuredDimension(getMeasuredWidth(), h);
        }
    }

    @Override // android.view.View, android.view.ViewParent
    public void requestLayout() {
        if (this.ignoreRequestLayout) {
            return;
        }
        super.requestLayout();
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        TextView textView;
        super.onLayout(changed, left, top, right, bottom);
        if (this.viewType != 1 && this.nameTextView.getLineCount() <= 1 && (textView = this.captionTextView) != null) {
            textView.getVisibility();
        }
        int y = this.nameTextView.getMeasuredHeight() - AndroidUtilities.dp(22.0f);
        TextView textView2 = this.captionTextView;
        if (textView2 != null && textView2.getVisibility() == 0) {
            TextView textView3 = this.captionTextView;
            textView3.layout(textView3.getLeft(), this.captionTextView.getTop() + y, this.captionTextView.getRight(), this.captionTextView.getBottom() + y);
            y += this.captionTextView.getMeasuredHeight() + AndroidUtilities.dp(3.0f);
        }
        TextView textView4 = this.dateTextView;
        textView4.layout(textView4.getLeft(), this.dateTextView.getTop() + y, this.dateTextView.getRight(), this.dateTextView.getBottom() + y);
        RLottieImageView rLottieImageView = this.statusImageView;
        rLottieImageView.layout(rLottieImageView.getLeft(), this.statusImageView.getTop() + y, this.statusImageView.getRight(), this.statusImageView.getBottom() + y);
        LineProgressView lineProgressView = this.progressView;
        lineProgressView.layout(lineProgressView.getLeft(), (getMeasuredHeight() - this.progressView.getMeasuredHeight()) - (this.needDivider ? 1 : 0), this.progressView.getRight(), getMeasuredHeight() - (this.needDivider ? 1 : 0));
    }

    @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
    public void onFailedDownload(String name, boolean canceled) {
        updateFileExistIcon(true);
        this.downloadedSize = 0L;
        updateDateView();
    }

    @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
    public void onSuccessDownload(String name) {
        this.progressView.setProgress(1.0f, true);
        updateFileExistIcon(true);
        this.downloadedSize = 0L;
        updateDateView();
    }

    @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
    public void onProgressDownload(String fileName, long downloadedSize, long totalSize) {
        if (this.progressView.getVisibility() != 0) {
            updateFileExistIcon(true);
        }
        this.downloadedSize = downloadedSize;
        updateDateView();
        this.progressView.setProgress(Math.min(1.0f, ((float) downloadedSize) / ((float) totalSize)), true);
    }

    @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
    public void onProgressUpload(String fileName, long uploadedSize, long totalSize, boolean isEncrypted) {
    }

    @Override // org.telegram.messenger.DownloadController.FileDownloadProgressListener
    public int getObserverTag() {
        return this.TAG;
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        if (this.checkBox.isChecked()) {
            info.setCheckable(true);
            info.setChecked(this.checkBox.isChecked());
        }
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }

    public void setGlobalGradientView(FlickerLoadingView globalGradientView) {
        this.globalGradientView = globalGradientView;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        if (this.enterAlpha != 1.0f && this.globalGradientView != null) {
            canvas.saveLayerAlpha(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight(), (int) ((1.0f - this.enterAlpha) * 255.0f), 31);
            this.globalGradientView.setViewType(3);
            this.globalGradientView.updateColors();
            this.globalGradientView.updateGradient();
            this.globalGradientView.draw(canvas);
            canvas.restore();
            canvas.saveLayerAlpha(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight(), (int) (this.enterAlpha * 255.0f), 31);
            super.dispatchDraw(canvas);
            drawDivider(canvas);
            canvas.restore();
            return;
        }
        super.dispatchDraw(canvas);
        drawDivider(canvas);
    }

    private void drawDivider(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(AndroidUtilities.dp(72.0f), getHeight() - 1, getWidth() - getPaddingRight(), getHeight() - 1, Theme.dividerPaint);
        }
    }

    public void setEnterAnimationAlpha(float alpha) {
        if (this.enterAlpha != alpha) {
            this.enterAlpha = alpha;
            invalidate();
        }
    }
}
