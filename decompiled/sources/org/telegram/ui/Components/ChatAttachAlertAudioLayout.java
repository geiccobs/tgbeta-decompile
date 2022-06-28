package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.SharedAudioCell;
import org.telegram.ui.Components.ChatAttachAlert;
import org.telegram.ui.Components.ChatAttachAlertAudioLayout;
import org.telegram.ui.Components.RecyclerListView;
/* loaded from: classes5.dex */
public class ChatAttachAlertAudioLayout extends ChatAttachAlert.AttachAlertLayout implements NotificationCenter.NotificationCenterDelegate {
    private View currentEmptyView;
    private float currentPanTranslationProgress;
    private AudioSelectDelegate delegate;
    private ImageView emptyImageView;
    private TextView emptySubtitleTextView;
    private TextView emptyTitleTextView;
    private LinearLayout emptyView;
    private FrameLayout frameLayout;
    private boolean ignoreLayout;
    private LinearLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private boolean loadingAudio;
    private MessageObject playingAudio;
    private EmptyTextProgressView progressView;
    private SearchAdapter searchAdapter;
    private SearchField searchField;
    private boolean sendPressed;
    private View shadow;
    private AnimatorSet shadowAnimation;
    private int maxSelectedFiles = -1;
    private ArrayList<MediaController.AudioEntry> audioEntries = new ArrayList<>();
    private ArrayList<MediaController.AudioEntry> selectedAudiosOrder = new ArrayList<>();
    private LongSparseArray<MediaController.AudioEntry> selectedAudios = new LongSparseArray<>();

    /* loaded from: classes5.dex */
    public interface AudioSelectDelegate {
        void didSelectAudio(ArrayList<MessageObject> arrayList, CharSequence charSequence, boolean z, int i);
    }

    public ChatAttachAlertAudioLayout(ChatAttachAlert alert, Context context, Theme.ResourcesProvider resourcesProvider) {
        super(alert, context, resourcesProvider);
        NotificationCenter.getInstance(this.parentAlert.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidReset);
        NotificationCenter.getInstance(this.parentAlert.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidStart);
        NotificationCenter.getInstance(this.parentAlert.currentAccount).addObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        loadAudio();
        FrameLayout frameLayout = new FrameLayout(context);
        this.frameLayout = frameLayout;
        frameLayout.setBackgroundColor(getThemedColor(Theme.key_dialogBackground));
        SearchField searchField = new SearchField(context, false, resourcesProvider) { // from class: org.telegram.ui.Components.ChatAttachAlertAudioLayout.1
            @Override // org.telegram.ui.Components.SearchField
            public void onTextChange(String text) {
                if (text.length() == 0 && ChatAttachAlertAudioLayout.this.listView.getAdapter() != ChatAttachAlertAudioLayout.this.listAdapter) {
                    ChatAttachAlertAudioLayout.this.listView.setAdapter(ChatAttachAlertAudioLayout.this.listAdapter);
                    ChatAttachAlertAudioLayout.this.listAdapter.notifyDataSetChanged();
                }
                if (ChatAttachAlertAudioLayout.this.searchAdapter != null) {
                    ChatAttachAlertAudioLayout.this.searchAdapter.search(text);
                }
            }

            @Override // android.view.ViewGroup
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                ChatAttachAlertAudioLayout.this.parentAlert.makeFocusable(getSearchEditText(), true);
                return super.onInterceptTouchEvent(ev);
            }

            @Override // org.telegram.ui.Components.SearchField
            public void processTouchEvent(MotionEvent event) {
                MotionEvent e = MotionEvent.obtain(event);
                e.setLocation(e.getRawX(), (e.getRawY() - ChatAttachAlertAudioLayout.this.parentAlert.getSheetContainer().getTranslationY()) - AndroidUtilities.dp(58.0f));
                ChatAttachAlertAudioLayout.this.listView.dispatchTouchEvent(e);
                e.recycle();
            }

            @Override // org.telegram.ui.Components.SearchField
            protected void onFieldTouchUp(EditTextBoldCursor editText) {
                ChatAttachAlertAudioLayout.this.parentAlert.makeFocusable(editText, true);
            }
        };
        this.searchField = searchField;
        searchField.setHint(LocaleController.getString("SearchMusic", R.string.SearchMusic));
        this.frameLayout.addView(this.searchField, LayoutHelper.createFrame(-1, -1, 51));
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context, null, resourcesProvider);
        this.progressView = emptyTextProgressView;
        emptyTextProgressView.showProgress();
        addView(this.progressView, LayoutHelper.createFrame(-1, -1.0f));
        LinearLayout linearLayout = new LinearLayout(context);
        this.emptyView = linearLayout;
        linearLayout.setOrientation(1);
        this.emptyView.setGravity(17);
        this.emptyView.setVisibility(8);
        addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        this.emptyView.setOnTouchListener(ChatAttachAlertAudioLayout$$ExternalSyntheticLambda0.INSTANCE);
        ImageView imageView = new ImageView(context);
        this.emptyImageView = imageView;
        imageView.setImageResource(R.drawable.music_empty);
        this.emptyImageView.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_dialogEmptyImage), PorterDuff.Mode.MULTIPLY));
        this.emptyView.addView(this.emptyImageView, LayoutHelper.createLinear(-2, -2));
        TextView textView = new TextView(context);
        this.emptyTitleTextView = textView;
        textView.setTextColor(getThemedColor(Theme.key_dialogEmptyText));
        this.emptyTitleTextView.setGravity(17);
        this.emptyTitleTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.emptyTitleTextView.setTextSize(1, 17.0f);
        this.emptyTitleTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), 0);
        this.emptyView.addView(this.emptyTitleTextView, LayoutHelper.createLinear(-2, -2, 17, 0, 11, 0, 0));
        TextView textView2 = new TextView(context);
        this.emptySubtitleTextView = textView2;
        textView2.setTextColor(getThemedColor(Theme.key_dialogEmptyText));
        this.emptySubtitleTextView.setGravity(17);
        this.emptySubtitleTextView.setTextSize(1, 15.0f);
        this.emptySubtitleTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), 0);
        this.emptyView.addView(this.emptySubtitleTextView, LayoutHelper.createLinear(-2, -2, 17, 0, 6, 0, 0));
        RecyclerListView recyclerListView = new RecyclerListView(context, resourcesProvider) { // from class: org.telegram.ui.Components.ChatAttachAlertAudioLayout.2
            @Override // org.telegram.ui.Components.RecyclerListView
            protected boolean allowSelectChildAtPosition(float x, float y) {
                return y >= ((float) ((ChatAttachAlertAudioLayout.this.parentAlert.scrollOffsetY[0] + AndroidUtilities.dp(30.0f)) + ((Build.VERSION.SDK_INT < 21 || ChatAttachAlertAudioLayout.this.parentAlert.inBubbleMode) ? 0 : AndroidUtilities.statusBarHeight)));
            }
        };
        this.listView = recyclerListView;
        recyclerListView.setClipToPadding(false);
        RecyclerListView recyclerListView2 = this.listView;
        FillLastLinearLayoutManager fillLastLinearLayoutManager = new FillLastLinearLayoutManager(getContext(), 1, false, AndroidUtilities.dp(9.0f), this.listView) { // from class: org.telegram.ui.Components.ChatAttachAlertAudioLayout.3
            @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(recyclerView.getContext()) { // from class: org.telegram.ui.Components.ChatAttachAlertAudioLayout.3.1
                    @Override // androidx.recyclerview.widget.LinearSmoothScroller
                    public int calculateDyToMakeVisible(View view, int snapPreference) {
                        int dy = super.calculateDyToMakeVisible(view, snapPreference);
                        return dy - (ChatAttachAlertAudioLayout.this.listView.getPaddingTop() - AndroidUtilities.dp(7.0f));
                    }

                    @Override // androidx.recyclerview.widget.LinearSmoothScroller
                    public int calculateTimeForDeceleration(int dx) {
                        return super.calculateTimeForDeceleration(dx) * 2;
                    }
                };
                linearSmoothScroller.setTargetPosition(position);
                startSmoothScroll(linearSmoothScroller);
            }
        };
        this.layoutManager = fillLastLinearLayoutManager;
        recyclerListView2.setLayoutManager(fillLastLinearLayoutManager);
        this.listView.setHorizontalScrollBarEnabled(false);
        this.listView.setVerticalScrollBarEnabled(false);
        addView(this.listView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
        RecyclerListView recyclerListView3 = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.listAdapter = listAdapter;
        recyclerListView3.setAdapter(listAdapter);
        this.listView.setGlowColor(getThemedColor(Theme.key_dialogScrollGlow));
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlertAudioLayout$$ExternalSyntheticLambda3
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i) {
                ChatAttachAlertAudioLayout.this.m2404x64d1f02e(view, i);
            }
        });
        this.listView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlertAudioLayout$$ExternalSyntheticLambda4
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener
            public final boolean onItemClick(View view, int i) {
                return ChatAttachAlertAudioLayout.this.m2405x8e26456f(view, i);
            }
        });
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.Components.ChatAttachAlertAudioLayout.4
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                ChatAttachAlertAudioLayout.this.parentAlert.updateLayout(ChatAttachAlertAudioLayout.this, true, dy);
                ChatAttachAlertAudioLayout.this.updateEmptyViewPosition();
            }
        });
        this.searchAdapter = new SearchAdapter(context);
        FrameLayout.LayoutParams frameLayoutParams = new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight(), 51);
        frameLayoutParams.topMargin = AndroidUtilities.dp(58.0f);
        View view = new View(context);
        this.shadow = view;
        view.setBackgroundColor(getThemedColor(Theme.key_dialogShadowLine));
        this.shadow.setAlpha(0.0f);
        this.shadow.setTag(1);
        addView(this.shadow, frameLayoutParams);
        addView(this.frameLayout, LayoutHelper.createFrame(-1, 58, 51));
        updateEmptyView();
    }

    public static /* synthetic */ boolean lambda$new$0(View v, MotionEvent event) {
        return true;
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-ChatAttachAlertAudioLayout */
    public /* synthetic */ void m2404x64d1f02e(View view, int position) {
        onItemClick(view);
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-ChatAttachAlertAudioLayout */
    public /* synthetic */ boolean m2405x8e26456f(View view, int position) {
        onItemClick(view);
        return true;
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    void onDestroy() {
        onHide();
        NotificationCenter.getInstance(this.parentAlert.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidReset);
        NotificationCenter.getInstance(this.parentAlert.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidStart);
        NotificationCenter.getInstance(this.parentAlert.currentAccount).removeObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public void onHide() {
        if (this.playingAudio != null && MediaController.getInstance().isPlayingMessage(this.playingAudio)) {
            MediaController.getInstance().cleanupPlayer(true, true);
        }
        this.playingAudio = null;
    }

    public void updateEmptyViewPosition() {
        View child;
        if (this.currentEmptyView.getVisibility() != 0 || (child = this.listView.getChildAt(0)) == null) {
            return;
        }
        View view = this.currentEmptyView;
        view.setTranslationY((((view.getMeasuredHeight() - getMeasuredHeight()) + child.getTop()) / 2) - (this.currentPanTranslationProgress / 2.0f));
    }

    public void updateEmptyView() {
        boolean visible;
        int i = 8;
        if (this.loadingAudio) {
            this.currentEmptyView = this.progressView;
            this.emptyView.setVisibility(8);
        } else {
            if (this.listView.getAdapter() == this.searchAdapter) {
                this.emptyTitleTextView.setText(LocaleController.getString("NoAudioFound", R.string.NoAudioFound));
            } else {
                this.emptyTitleTextView.setText(LocaleController.getString("NoAudioFiles", R.string.NoAudioFiles));
                this.emptySubtitleTextView.setText(LocaleController.getString("NoAudioFilesInfo", R.string.NoAudioFilesInfo));
            }
            this.currentEmptyView = this.emptyView;
            this.progressView.setVisibility(8);
        }
        RecyclerView.Adapter adapter = this.listView.getAdapter();
        SearchAdapter searchAdapter = this.searchAdapter;
        if (adapter != searchAdapter) {
            visible = this.audioEntries.isEmpty();
        } else {
            visible = searchAdapter.searchResult.isEmpty();
        }
        View view = this.currentEmptyView;
        if (visible) {
            i = 0;
        }
        view.setVisibility(i);
        updateEmptyViewPosition();
    }

    public void setMaxSelectedFiles(int value) {
        this.maxSelectedFiles = value;
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    void scrollToTop() {
        this.listView.smoothScrollToPosition(0);
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public int getCurrentItemTop() {
        if (this.listView.getChildCount() <= 0) {
            return Integer.MAX_VALUE;
        }
        View child = this.listView.getChildAt(0);
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(child);
        int top = child.getTop() - AndroidUtilities.dp(8.0f);
        int newOffset = (top <= 0 || holder == null || holder.getAdapterPosition() != 0) ? 0 : top;
        if (top >= 0 && holder != null && holder.getAdapterPosition() == 0) {
            newOffset = top;
            runShadowAnimation(false);
        } else {
            runShadowAnimation(true);
        }
        this.frameLayout.setTranslationY(newOffset);
        return AndroidUtilities.dp(12.0f) + newOffset;
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    int getFirstOffset() {
        return getListTopPadding() + AndroidUtilities.dp(4.0f);
    }

    @Override // android.view.View
    public void setTranslationY(float translationY) {
        super.setTranslationY(translationY);
        this.parentAlert.getSheetContainer().invalidate();
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public boolean onDismiss() {
        if (this.playingAudio != null && MediaController.getInstance().isPlayingMessage(this.playingAudio)) {
            MediaController.getInstance().cleanupPlayer(true, true);
        }
        return super.onDismiss();
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public int getListTopPadding() {
        return this.listView.getPaddingTop();
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        updateEmptyViewPosition();
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    void onPreMeasure(int availableWidth, int availableHeight) {
        int padding;
        if (this.parentAlert.sizeNotifierFrameLayout.measureKeyboardHeight() > AndroidUtilities.dp(20.0f)) {
            padding = AndroidUtilities.dp(8.0f);
            this.parentAlert.setAllowNestedScroll(false);
        } else {
            if (!AndroidUtilities.isTablet() && AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
                padding = (int) (availableHeight / 3.5f);
            } else {
                padding = (availableHeight / 5) * 2;
            }
            this.parentAlert.setAllowNestedScroll(true);
        }
        if (this.listView.getPaddingTop() != padding) {
            this.ignoreLayout = true;
            this.listView.setPadding(0, padding, 0, AndroidUtilities.dp(48.0f));
            this.ignoreLayout = false;
        }
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    void onShow(ChatAttachAlert.AttachAlertLayout previousLayout) {
        this.layoutManager.scrollToPositionWithOffset(0, 0);
        this.listAdapter.notifyDataSetChanged();
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public void onHidden() {
        this.selectedAudios.clear();
        this.selectedAudiosOrder.clear();
    }

    @Override // android.view.View, android.view.ViewParent
    public void requestLayout() {
        if (this.ignoreLayout) {
            return;
        }
        super.requestLayout();
    }

    private void runShadowAnimation(final boolean show) {
        if ((show && this.shadow.getTag() != null) || (!show && this.shadow.getTag() == null)) {
            this.shadow.setTag(show ? null : 1);
            if (show) {
                this.shadow.setVisibility(0);
            }
            AnimatorSet animatorSet = this.shadowAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.shadowAnimation = animatorSet2;
            Animator[] animatorArr = new Animator[1];
            View view = this.shadow;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = show ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(view, property, fArr);
            animatorSet2.playTogether(animatorArr);
            this.shadowAnimation.setDuration(150L);
            this.shadowAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatAttachAlertAudioLayout.5
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    if (ChatAttachAlertAudioLayout.this.shadowAnimation != null && ChatAttachAlertAudioLayout.this.shadowAnimation.equals(animation)) {
                        if (!show) {
                            ChatAttachAlertAudioLayout.this.shadow.setVisibility(4);
                        }
                        ChatAttachAlertAudioLayout.this.shadowAnimation = null;
                    }
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animation) {
                    if (ChatAttachAlertAudioLayout.this.shadowAnimation != null && ChatAttachAlertAudioLayout.this.shadowAnimation.equals(animation)) {
                        ChatAttachAlertAudioLayout.this.shadowAnimation = null;
                    }
                }
            });
            this.shadowAnimation.start();
        }
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.messagePlayingDidReset || id == NotificationCenter.messagePlayingDidStart || id == NotificationCenter.messagePlayingPlayStateChanged) {
            if (id == NotificationCenter.messagePlayingDidReset || id == NotificationCenter.messagePlayingPlayStateChanged) {
                int count = this.listView.getChildCount();
                for (int a = 0; a < count; a++) {
                    View view = this.listView.getChildAt(a);
                    if (view instanceof SharedAudioCell) {
                        SharedAudioCell cell = (SharedAudioCell) view;
                        MessageObject messageObject = cell.getMessage();
                        if (messageObject != null) {
                            cell.updateButtonState(false, true);
                        }
                    }
                }
            } else if (id == NotificationCenter.messagePlayingDidStart) {
                MessageObject messageObject2 = (MessageObject) args[0];
                if (messageObject2.eventId != 0) {
                    return;
                }
                int count2 = this.listView.getChildCount();
                for (int a2 = 0; a2 < count2; a2++) {
                    View view2 = this.listView.getChildAt(a2);
                    if (view2 instanceof SharedAudioCell) {
                        SharedAudioCell cell2 = (SharedAudioCell) view2;
                        MessageObject messageObject1 = cell2.getMessage();
                        if (messageObject1 != null) {
                            cell2.updateButtonState(false, true);
                        }
                    }
                }
            }
        }
    }

    private void showErrorBox(String error) {
        new AlertDialog.Builder(getContext(), this.resourcesProvider).setTitle(LocaleController.getString("AppName", R.string.AppName)).setMessage(error).setPositiveButton(LocaleController.getString("OK", R.string.OK), null).show();
    }

    private void onItemClick(View view) {
        boolean add;
        if (!(view instanceof SharedAudioCell)) {
            return;
        }
        SharedAudioCell audioCell = (SharedAudioCell) view;
        MediaController.AudioEntry audioEntry = (MediaController.AudioEntry) audioCell.getTag();
        int i = 1;
        if (this.selectedAudios.indexOfKey(audioEntry.id) >= 0) {
            this.selectedAudios.remove(audioEntry.id);
            this.selectedAudiosOrder.remove(audioEntry);
            audioCell.setChecked(false, true);
            add = false;
        } else {
            if (this.maxSelectedFiles >= 0) {
                int size = this.selectedAudios.size();
                int i2 = this.maxSelectedFiles;
                if (size >= i2) {
                    showErrorBox(LocaleController.formatString("PassportUploadMaxReached", R.string.PassportUploadMaxReached, LocaleController.formatPluralString("Files", i2, new Object[0])));
                    return;
                }
            }
            this.selectedAudios.put(audioEntry.id, audioEntry);
            this.selectedAudiosOrder.add(audioEntry);
            audioCell.setChecked(true, true);
            add = true;
        }
        ChatAttachAlert chatAttachAlert = this.parentAlert;
        if (!add) {
            i = 2;
        }
        chatAttachAlert.updateCountButton(i);
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public int getSelectedItemsCount() {
        return this.selectedAudios.size();
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    void sendSelectedItems(boolean notify, int scheduleDate) {
        if (this.selectedAudios.size() == 0 || this.delegate == null || this.sendPressed) {
            return;
        }
        this.sendPressed = true;
        ArrayList<MessageObject> audios = new ArrayList<>();
        for (int a = 0; a < this.selectedAudiosOrder.size(); a++) {
            audios.add(this.selectedAudiosOrder.get(a).messageObject);
        }
        this.delegate.didSelectAudio(audios, this.parentAlert.commentTextView.getText().toString(), notify, scheduleDate);
    }

    public void setDelegate(AudioSelectDelegate audioSelectDelegate) {
        this.delegate = audioSelectDelegate;
    }

    private void loadAudio() {
        this.loadingAudio = true;
        Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertAudioLayout$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                ChatAttachAlertAudioLayout.this.m2403x5f7276a1();
            }
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r13v0 */
    /* JADX WARN: Type inference failed for: r13v19 */
    /* JADX WARN: Type inference failed for: r13v2 */
    /* JADX WARN: Type inference failed for: r13v3 */
    /* JADX WARN: Type inference failed for: r13v4, types: [int] */
    /* renamed from: lambda$loadAudio$4$org-telegram-ui-Components-ChatAttachAlertAudioLayout */
    public /* synthetic */ void m2403x5f7276a1() {
        final ArrayList<MediaController.AudioEntry> newAudioEntries;
        Exception e;
        Throwable th;
        int i = 2;
        int i2 = 4;
        ?? r13 = 5;
        String[] projection = {"_id", "artist", "title", "_data", "duration", "album"};
        ArrayList<MediaController.AudioEntry> newAudioEntries2 = new ArrayList<>();
        try {
            Cursor cursor = ApplicationLoader.applicationContext.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, "is_music != 0", null, "title");
            int id = -2000000000;
            while (cursor.moveToNext()) {
                try {
                    try {
                        MediaController.AudioEntry audioEntry = new MediaController.AudioEntry();
                        audioEntry.id = cursor.getInt(0);
                        audioEntry.author = cursor.getString(1);
                        audioEntry.title = cursor.getString(i);
                        audioEntry.path = cursor.getString(3);
                        audioEntry.duration = (int) (cursor.getLong(i2) / 1000);
                        audioEntry.genre = cursor.getString(r13);
                        File file = new File(audioEntry.path);
                        TLRPC.TL_message message = new TLRPC.TL_message();
                        message.out = true;
                        message.id = id;
                        message.peer_id = new TLRPC.TL_peerUser();
                        message.from_id = new TLRPC.TL_peerUser();
                        TLRPC.Peer peer = message.peer_id;
                        TLRPC.Peer peer2 = message.from_id;
                        ArrayList<MediaController.AudioEntry> newAudioEntries3 = newAudioEntries2;
                        try {
                            long clientUserId = UserConfig.getInstance(this.parentAlert.currentAccount).getClientUserId();
                            peer2.user_id = clientUserId;
                            peer.user_id = clientUserId;
                            message.date = (int) (System.currentTimeMillis() / 1000);
                            message.message = "";
                            message.attachPath = audioEntry.path;
                            message.media = new TLRPC.TL_messageMediaDocument();
                            message.media.flags |= 3;
                            message.media.document = new TLRPC.TL_document();
                            message.flags |= 768;
                            String ext = FileLoader.getFileExtension(file);
                            message.media.document.id = 0L;
                            message.media.document.access_hash = 0L;
                            message.media.document.file_reference = new byte[0];
                            message.media.document.date = message.date;
                            TLRPC.Document document = message.media.document;
                            StringBuilder sb = new StringBuilder();
                            sb.append("audio/");
                            sb.append(ext.length() > 0 ? ext : "mp3");
                            document.mime_type = sb.toString();
                            message.media.document.size = (int) file.length();
                            message.media.document.dc_id = 0;
                            TLRPC.TL_documentAttributeAudio attributeAudio = new TLRPC.TL_documentAttributeAudio();
                            attributeAudio.duration = audioEntry.duration;
                            attributeAudio.title = audioEntry.title;
                            attributeAudio.performer = audioEntry.author;
                            attributeAudio.flags |= 3;
                            message.media.document.attributes.add(attributeAudio);
                            TLRPC.TL_documentAttributeFilename fileName = new TLRPC.TL_documentAttributeFilename();
                            fileName.file_name = file.getName();
                            message.media.document.attributes.add(fileName);
                            audioEntry.messageObject = new MessageObject(this.parentAlert.currentAccount, message, false, true);
                            try {
                                newAudioEntries3.add(audioEntry);
                                id--;
                                newAudioEntries2 = newAudioEntries3;
                                i = 2;
                                i2 = 4;
                                r13 = 5;
                            } catch (Throwable th2) {
                                th = th2;
                                if (cursor != null) {
                                    try {
                                        cursor.close();
                                    } catch (Throwable th3) {
                                    }
                                }
                                throw th;
                            }
                        } catch (Throwable th4) {
                            th = th4;
                        }
                    } catch (Exception e2) {
                        e = e2;
                        FileLog.e(e);
                        newAudioEntries = r13;
                        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertAudioLayout$$ExternalSyntheticLambda2
                            @Override // java.lang.Runnable
                            public final void run() {
                                ChatAttachAlertAudioLayout.this.m2402x361e2160(newAudioEntries);
                            }
                        });
                    }
                } catch (Throwable th5) {
                    th = th5;
                }
            }
            ArrayList<MediaController.AudioEntry> newAudioEntries4 = newAudioEntries2;
            newAudioEntries = newAudioEntries4;
            if (cursor != null) {
                cursor.close();
                newAudioEntries = newAudioEntries4;
            }
        } catch (Exception e3) {
            e = e3;
            r13 = newAudioEntries2;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertAudioLayout$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                ChatAttachAlertAudioLayout.this.m2402x361e2160(newAudioEntries);
            }
        });
    }

    /* renamed from: lambda$loadAudio$3$org-telegram-ui-Components-ChatAttachAlertAudioLayout */
    public /* synthetic */ void m2402x361e2160(ArrayList newAudioEntries) {
        this.loadingAudio = false;
        this.audioEntries = newAudioEntries;
        this.listAdapter.notifyDataSetChanged();
    }

    /* loaded from: classes5.dex */
    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            ChatAttachAlertAudioLayout.this = r1;
            this.mContext = context;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return ChatAttachAlertAudioLayout.this.audioEntries.size() + 1 + (!ChatAttachAlertAudioLayout.this.audioEntries.isEmpty());
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public long getItemId(int i) {
            return i;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() == 0;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            SharedAudioCell sharedAudioCell;
            switch (viewType) {
                case 0:
                    SharedAudioCell sharedAudioCell2 = new SharedAudioCell(this.mContext, ChatAttachAlertAudioLayout.this.resourcesProvider) { // from class: org.telegram.ui.Components.ChatAttachAlertAudioLayout.ListAdapter.1
                        @Override // org.telegram.ui.Cells.SharedAudioCell
                        public boolean needPlayMessage(MessageObject messageObject) {
                            ChatAttachAlertAudioLayout.this.playingAudio = messageObject;
                            ArrayList<MessageObject> arrayList = new ArrayList<>();
                            arrayList.add(messageObject);
                            return MediaController.getInstance().setPlaylist(arrayList, messageObject, 0L);
                        }
                    };
                    sharedAudioCell2.setCheckForButtonPress(true);
                    sharedAudioCell = sharedAudioCell2;
                    break;
                case 1:
                    View view = new View(this.mContext);
                    view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(56.0f)));
                    sharedAudioCell = view;
                    break;
                default:
                    sharedAudioCell = new View(this.mContext);
                    break;
            }
            return new RecyclerListView.Holder(sharedAudioCell);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder.getItemViewType() == 0) {
                int position2 = position - 1;
                MediaController.AudioEntry audioEntry = (MediaController.AudioEntry) ChatAttachAlertAudioLayout.this.audioEntries.get(position2);
                SharedAudioCell audioCell = (SharedAudioCell) holder.itemView;
                audioCell.setTag(audioEntry);
                boolean z = true;
                audioCell.setMessageObject(audioEntry.messageObject, position2 != ChatAttachAlertAudioLayout.this.audioEntries.size() - 1);
                if (ChatAttachAlertAudioLayout.this.selectedAudios.indexOfKey(audioEntry.id) < 0) {
                    z = false;
                }
                audioCell.setChecked(z, false);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            if (i == getItemCount() - 1) {
                return 2;
            }
            return i == 0 ? 1 : 0;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            ChatAttachAlertAudioLayout.this.updateEmptyView();
        }
    }

    /* loaded from: classes5.dex */
    public class SearchAdapter extends RecyclerListView.SelectionAdapter {
        private int lastReqId;
        private int lastSearchId;
        private Context mContext;
        private Runnable searchRunnable;
        private ArrayList<MediaController.AudioEntry> searchResult = new ArrayList<>();
        private int reqId = 0;

        public SearchAdapter(Context context) {
            ChatAttachAlertAudioLayout.this = this$0;
            this.mContext = context;
        }

        public void search(final String query) {
            Runnable runnable = this.searchRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.searchRunnable = null;
            }
            if (TextUtils.isEmpty(query)) {
                if (!this.searchResult.isEmpty()) {
                    this.searchResult.clear();
                }
                if (ChatAttachAlertAudioLayout.this.listView.getAdapter() != ChatAttachAlertAudioLayout.this.listAdapter) {
                    ChatAttachAlertAudioLayout.this.listView.setAdapter(ChatAttachAlertAudioLayout.this.listAdapter);
                }
                notifyDataSetChanged();
                return;
            }
            final int searchId = this.lastSearchId + 1;
            this.lastSearchId = searchId;
            Runnable runnable2 = new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertAudioLayout$SearchAdapter$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    ChatAttachAlertAudioLayout.SearchAdapter.this.m2407xb4e282d3(query, searchId);
                }
            };
            this.searchRunnable = runnable2;
            AndroidUtilities.runOnUIThread(runnable2, 300L);
        }

        /* renamed from: lambda$search$1$org-telegram-ui-Components-ChatAttachAlertAudioLayout$SearchAdapter */
        public /* synthetic */ void m2407xb4e282d3(final String query, final int searchId) {
            final ArrayList<MediaController.AudioEntry> copy = new ArrayList<>(ChatAttachAlertAudioLayout.this.audioEntries);
            Utilities.searchQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertAudioLayout$SearchAdapter$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    ChatAttachAlertAudioLayout.SearchAdapter.this.m2406xc390f352(query, copy, searchId);
                }
            });
        }

        /* renamed from: lambda$search$0$org-telegram-ui-Components-ChatAttachAlertAudioLayout$SearchAdapter */
        public /* synthetic */ void m2406xc390f352(String query, ArrayList copy, int searchId) {
            String search1 = query.trim().toLowerCase();
            if (search1.length() == 0) {
                updateSearchResults(new ArrayList<>(), query, this.lastSearchId);
                return;
            }
            String search2 = LocaleController.getInstance().getTranslitString(search1);
            if (search1.equals(search2) || search2.length() == 0) {
                search2 = null;
            }
            String[] search = new String[(search2 != null ? 1 : 0) + 1];
            search[0] = search1;
            if (search2 != null) {
                search[1] = search2;
            }
            ArrayList<MediaController.AudioEntry> resultArray = new ArrayList<>();
            for (int a = 0; a < copy.size(); a++) {
                MediaController.AudioEntry entry = (MediaController.AudioEntry) copy.get(a);
                int b = 0;
                while (true) {
                    if (b < search.length) {
                        String q = search[b];
                        boolean ok = false;
                        if (entry.author != null) {
                            ok = entry.author.toLowerCase().contains(q);
                        }
                        if (!ok && entry.title != null) {
                            ok = entry.title.toLowerCase().contains(q);
                        }
                        if (!ok) {
                            b++;
                        } else {
                            resultArray.add(entry);
                            break;
                        }
                    }
                }
            }
            updateSearchResults(resultArray, query, searchId);
        }

        private void updateSearchResults(final ArrayList<MediaController.AudioEntry> result, final String query, final int searchId) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertAudioLayout$SearchAdapter$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ChatAttachAlertAudioLayout.SearchAdapter.this.m2408x2081177(searchId, query, result);
                }
            });
        }

        /* renamed from: lambda$updateSearchResults$2$org-telegram-ui-Components-ChatAttachAlertAudioLayout$SearchAdapter */
        public /* synthetic */ void m2408x2081177(int searchId, String query, ArrayList result) {
            if (searchId != this.lastSearchId) {
                return;
            }
            if (searchId != -1 && ChatAttachAlertAudioLayout.this.listView.getAdapter() != ChatAttachAlertAudioLayout.this.searchAdapter) {
                ChatAttachAlertAudioLayout.this.listView.setAdapter(ChatAttachAlertAudioLayout.this.searchAdapter);
            }
            if (ChatAttachAlertAudioLayout.this.listView.getAdapter() == ChatAttachAlertAudioLayout.this.searchAdapter) {
                ChatAttachAlertAudioLayout.this.emptySubtitleTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("NoAudioFoundInfo", R.string.NoAudioFoundInfo, query)));
            }
            this.searchResult = result;
            notifyDataSetChanged();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            ChatAttachAlertAudioLayout.this.updateEmptyView();
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() == 0;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return this.searchResult.size() + 1 + (!this.searchResult.isEmpty() ? 1 : 0);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            SharedAudioCell sharedAudioCell;
            switch (viewType) {
                case 0:
                    SharedAudioCell sharedAudioCell2 = new SharedAudioCell(this.mContext, ChatAttachAlertAudioLayout.this.resourcesProvider) { // from class: org.telegram.ui.Components.ChatAttachAlertAudioLayout.SearchAdapter.1
                        @Override // org.telegram.ui.Cells.SharedAudioCell
                        public boolean needPlayMessage(MessageObject messageObject) {
                            ChatAttachAlertAudioLayout.this.playingAudio = messageObject;
                            ArrayList<MessageObject> arrayList = new ArrayList<>();
                            arrayList.add(messageObject);
                            return MediaController.getInstance().setPlaylist(arrayList, messageObject, 0L);
                        }
                    };
                    sharedAudioCell2.setCheckForButtonPress(true);
                    sharedAudioCell = sharedAudioCell2;
                    break;
                case 1:
                    View view = new View(this.mContext);
                    view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(56.0f)));
                    sharedAudioCell = view;
                    break;
                default:
                    sharedAudioCell = new View(this.mContext);
                    break;
            }
            return new RecyclerListView.Holder(sharedAudioCell);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder.getItemViewType() == 0) {
                int position2 = position - 1;
                MediaController.AudioEntry audioEntry = this.searchResult.get(position2);
                SharedAudioCell audioCell = (SharedAudioCell) holder.itemView;
                audioCell.setTag(audioEntry);
                boolean z = true;
                audioCell.setMessageObject(audioEntry.messageObject, position2 != this.searchResult.size() - 1);
                if (ChatAttachAlertAudioLayout.this.selectedAudios.indexOfKey(audioEntry.id) < 0) {
                    z = false;
                }
                audioCell.setChecked(z, false);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            if (i == getItemCount() - 1) {
                return 2;
            }
            return i == 0 ? 1 : 0;
        }
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public void onContainerTranslationUpdated(float currentPanTranslationY) {
        this.currentPanTranslationProgress = currentPanTranslationY;
        super.onContainerTranslationUpdated(currentPanTranslationY);
        updateEmptyViewPosition();
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(this.frameLayout, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_dialogBackground));
        themeDescriptions.add(new ThemeDescription(this.searchField.getSearchBackground(), ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_dialogSearchBackground));
        themeDescriptions.add(new ThemeDescription(this.searchField, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{SearchField.class}, new String[]{"searchIconImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogSearchIcon));
        themeDescriptions.add(new ThemeDescription(this.searchField, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{SearchField.class}, new String[]{"clearSearchImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogSearchIcon));
        themeDescriptions.add(new ThemeDescription(this.searchField.getSearchEditText(), ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_dialogSearchText));
        themeDescriptions.add(new ThemeDescription(this.searchField.getSearchEditText(), ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_dialogSearchHint));
        themeDescriptions.add(new ThemeDescription(this.searchField.getSearchEditText(), ThemeDescription.FLAG_CURSORCOLOR, null, null, null, null, Theme.key_featuredStickers_addedIcon));
        themeDescriptions.add(new ThemeDescription(this.emptyImageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_dialogEmptyImage));
        themeDescriptions.add(new ThemeDescription(this.emptyTitleTextView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_dialogEmptyText));
        themeDescriptions.add(new ThemeDescription(this.emptySubtitleTextView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_dialogEmptyText));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_dialogScrollGlow));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        themeDescriptions.add(new ThemeDescription(this.progressView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_emptyListPlaceholder));
        themeDescriptions.add(new ThemeDescription(this.progressView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_progressCircle));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{SharedAudioCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_checkbox));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedAudioCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_checkboxCheck));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedAudioCell.class}, Theme.chat_contextResult_titleTextPaint, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedAudioCell.class}, Theme.chat_contextResult_descriptionTextPaint, null, null, Theme.key_windowBackgroundWhiteGrayText2));
        return themeDescriptions;
    }
}
