package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.SystemClock;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.C;
import com.microsoft.appcenter.Constants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ChatActionCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.ChatAttachAlert;
import org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.PhotoViewer;
/* loaded from: classes5.dex */
public class ChatAttachAlertPhotoLayoutPreview extends ChatAttachAlert.AttachAlertLayout {
    private static HashMap<MediaController.PhotoEntry, Boolean> photoRotate = new HashMap<>();
    private ValueAnimator draggingAnimator;
    private PreviewGroupsView groupsView;
    public TextView header;
    private ViewPropertyAnimator headerAnimator;
    private boolean isPortrait;
    private LinearLayoutManager layoutManager;
    public RecyclerListView listView;
    private int paddingTop;
    private ChatAttachAlertPhotoLayout photoLayout;
    private ChatActivity.ThemeDelegate themeDelegate;
    private UndoView undoView;
    private Drawable videoPlayImage;
    private final long durationMultiplier = 1;
    private float draggingCellTouchX = 0.0f;
    private float draggingCellTouchY = 0.0f;
    private float draggingCellTop = 0.0f;
    private float draggingCellLeft = 0.0f;
    private float draggingCellFromWidth = 0.0f;
    private float draggingCellFromHeight = 0.0f;
    private PreviewGroupsView.PreviewGroupCell.MediaCell draggingCell = null;
    private boolean draggingCellHiding = false;
    private float draggingCellGroupY = 0.0f;
    private boolean shown = false;
    private boolean ignoreLayout = false;

    static /* synthetic */ float access$1416(ChatAttachAlertPhotoLayoutPreview x0, float x1) {
        float f = x0.draggingCellTouchY + x1;
        x0.draggingCellTouchY = f;
        return f;
    }

    public float getPreviewScale() {
        return AndroidUtilities.displaySize.y > AndroidUtilities.displaySize.x ? 0.8f : 0.45f;
    }

    public ChatAttachAlertPhotoLayoutPreview(ChatAttachAlert alert, Context context, ChatActivity.ThemeDelegate themeDelegate) {
        super(alert, context, themeDelegate);
        this.isPortrait = AndroidUtilities.displaySize.y > AndroidUtilities.displaySize.x;
        this.themeDelegate = themeDelegate;
        setWillNotDraw(false);
        ActionBarMenu menu = this.parentAlert.actionBar.createMenu();
        this.header = new TextView(context);
        ActionBarMenuItem dropDownContainer = new ActionBarMenuItem(context, menu, 0, 0, this.resourcesProvider) { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.1
            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem, android.view.View
            public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
                super.onInitializeAccessibilityNodeInfo(info);
                info.setText(ChatAttachAlertPhotoLayoutPreview.this.header.getText());
            }
        };
        this.parentAlert.actionBar.addView(dropDownContainer, 0, LayoutHelper.createFrame(-2, -1.0f, 51, AndroidUtilities.isTablet() ? 64.0f : 56.0f, 0.0f, 40.0f, 0.0f));
        this.header.setImportantForAccessibility(2);
        this.header.setGravity(3);
        this.header.setSingleLine(true);
        this.header.setLines(1);
        this.header.setMaxLines(1);
        this.header.setEllipsize(TextUtils.TruncateAt.END);
        this.header.setTextColor(getThemedColor(Theme.key_dialogTextBlack));
        this.header.setText(LocaleController.getString("AttachMediaPreview", R.string.AttachMediaPreview));
        this.header.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.header.setCompoundDrawablePadding(AndroidUtilities.dp(4.0f));
        this.header.setPadding(0, 0, AndroidUtilities.dp(10.0f), 0);
        this.header.setAlpha(0.0f);
        dropDownContainer.addView(this.header, LayoutHelper.createFrame(-2, -2.0f, 16, 16.0f, 0.0f, 0.0f, 0.0f));
        RecyclerListView recyclerListView = new RecyclerListView(context, this.resourcesProvider) { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.2
            @Override // androidx.recyclerview.widget.RecyclerView
            public void onScrolled(int dx, int dy) {
                ChatAttachAlertPhotoLayoutPreview.this.invalidate();
                ChatAttachAlertPhotoLayoutPreview.this.parentAlert.updateLayout(ChatAttachAlertPhotoLayoutPreview.this, true, dy);
                ChatAttachAlertPhotoLayoutPreview.this.groupsView.onScroll();
                super.onScrolled(dx, dy);
            }

            @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View
            public boolean onTouchEvent(MotionEvent ev) {
                if (ChatAttachAlertPhotoLayoutPreview.this.draggingCell != null) {
                    return false;
                }
                return super.onTouchEvent(ev);
            }

            @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                if (ChatAttachAlertPhotoLayoutPreview.this.draggingCell != null) {
                    return false;
                }
                return super.onInterceptTouchEvent(ev);
            }
        };
        this.listView = recyclerListView;
        recyclerListView.setAdapter(new RecyclerView.Adapter() { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.3
            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new RecyclerListView.Holder(ChatAttachAlertPhotoLayoutPreview.this.groupsView);
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public int getItemCount() {
                return 1;
            }
        });
        RecyclerListView recyclerListView2 = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView2.setLayoutManager(linearLayoutManager);
        this.listView.setClipChildren(false);
        this.listView.setClipToPadding(false);
        this.listView.setOverScrollMode(2);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setPadding(0, 0, 0, AndroidUtilities.dp(46.0f));
        PreviewGroupsView previewGroupsView = new PreviewGroupsView(context);
        this.groupsView = previewGroupsView;
        previewGroupsView.setClipToPadding(true);
        this.groupsView.setClipChildren(true);
        addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.photoLayout = this.parentAlert.getPhotoLayout();
        this.groupsView.deletedPhotos.clear();
        this.groupsView.fromPhotoLayout(this.photoLayout);
        UndoView undoView = new UndoView(context, null, false, this.parentAlert.parentThemeDelegate);
        this.undoView = undoView;
        undoView.setEnterOffsetMargin(AndroidUtilities.dp(32.0f));
        addView(this.undoView, LayoutHelper.createFrame(-1, -2.0f, 83, 8.0f, 0.0f, 8.0f, 52.0f));
        this.videoPlayImage = context.getResources().getDrawable(R.drawable.play_mini_video);
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    void onShow(final ChatAttachAlert.AttachAlertLayout previousLayout) {
        this.shown = true;
        if (previousLayout instanceof ChatAttachAlertPhotoLayout) {
            this.photoLayout = (ChatAttachAlertPhotoLayout) previousLayout;
            this.groupsView.deletedPhotos.clear();
            this.groupsView.fromPhotoLayout(this.photoLayout);
            this.groupsView.requestLayout();
            this.layoutManager.scrollToPositionWithOffset(0, 0);
            Runnable setScrollY = new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    ChatAttachAlertPhotoLayoutPreview.this.m2498x7a1626c9(previousLayout);
                }
            };
            this.listView.post(setScrollY);
            postDelayed(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ChatAttachAlertPhotoLayoutPreview.this.m2499xe549668();
                }
            }, 250L);
            this.groupsView.toPhotoLayout(this.photoLayout, false);
        } else {
            scrollToTop();
        }
        ViewPropertyAnimator viewPropertyAnimator = this.headerAnimator;
        if (viewPropertyAnimator != null) {
            viewPropertyAnimator.cancel();
        }
        ViewPropertyAnimator interpolator = this.header.animate().alpha(1.0f).setDuration(150L).setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.headerAnimator = interpolator;
        interpolator.start();
    }

    /* renamed from: lambda$onShow$0$org-telegram-ui-Components-ChatAttachAlertPhotoLayoutPreview */
    public /* synthetic */ void m2498x7a1626c9(ChatAttachAlert.AttachAlertLayout previousLayout) {
        int currentItemTop = previousLayout.getCurrentItemTop();
        int paddingTop = previousLayout.getListTopPadding();
        this.listView.scrollBy(0, currentItemTop > AndroidUtilities.dp(7.0f) ? paddingTop - currentItemTop : paddingTop);
    }

    /* renamed from: lambda$onShow$1$org-telegram-ui-Components-ChatAttachAlertPhotoLayoutPreview */
    public /* synthetic */ void m2499xe549668() {
        if (this.shown) {
            this.parentAlert.selectedMenuItem.hideSubItem(3);
        }
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public void onHide() {
        this.shown = false;
        ViewPropertyAnimator viewPropertyAnimator = this.headerAnimator;
        if (viewPropertyAnimator != null) {
            viewPropertyAnimator.cancel();
        }
        ViewPropertyAnimator interpolator = this.header.animate().alpha(0.0f).setDuration(150L).setInterpolator(CubicBezierInterpolator.EASE_BOTH);
        this.headerAnimator = interpolator;
        interpolator.start();
        if (getSelectedItemsCount() > 1) {
            this.parentAlert.selectedMenuItem.showSubItem(3);
        }
        this.groupsView.toPhotoLayout(this.photoLayout, true);
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    int getSelectedItemsCount() {
        return this.groupsView.getPhotosCount();
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public void onHidden() {
        this.draggingCell = null;
        UndoView undoView = this.undoView;
        if (undoView != null) {
            undoView.hide(false, 0);
        }
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    int getFirstOffset() {
        return getListTopPadding() + AndroidUtilities.dp(56.0f);
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    boolean shouldHideBottomButtons() {
        return true;
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    void applyCaption(CharSequence text) {
        ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout = this.photoLayout;
        if (chatAttachAlertPhotoLayout != null) {
            chatAttachAlertPhotoLayout.applyCaption(text);
        }
    }

    /* loaded from: classes5.dex */
    public class GroupCalculator {
        float height;
        int maxX;
        int maxY;
        ArrayList<MediaController.PhotoEntry> photos;
        int width;
        public ArrayList<MessageObject.GroupedMessagePosition> posArray = new ArrayList<>();
        public HashMap<MediaController.PhotoEntry, MessageObject.GroupedMessagePosition> positions = new HashMap<>();
        private final int maxSizeWidth = 1000;

        /* loaded from: classes5.dex */
        public class MessageGroupedLayoutAttempt {
            public float[] heights;
            public int[] lineCounts;

            public MessageGroupedLayoutAttempt(int i1, int i2, float f1, float f2) {
                GroupCalculator.this = r4;
                this.lineCounts = new int[]{i1, i2};
                this.heights = new float[]{f1, f2};
            }

            public MessageGroupedLayoutAttempt(int i1, int i2, int i3, float f1, float f2, float f3) {
                GroupCalculator.this = r5;
                this.lineCounts = new int[]{i1, i2, i3};
                this.heights = new float[]{f1, f2, f3};
            }

            public MessageGroupedLayoutAttempt(int i1, int i2, int i3, int i4, float f1, float f2, float f3, float f4) {
                GroupCalculator.this = r6;
                this.lineCounts = new int[]{i1, i2, i3, i4};
                this.heights = new float[]{f1, f2, f3, f4};
            }
        }

        private float multiHeight(float[] array, int start, int end) {
            float sum = 0.0f;
            for (int a = start; a < end; a++) {
                sum += array[a];
            }
            return 1000.0f / sum;
        }

        public GroupCalculator(ArrayList<MediaController.PhotoEntry> photos) {
            ChatAttachAlertPhotoLayoutPreview.this = r1;
            this.photos = photos;
            calculate();
        }

        public void calculate(ArrayList<MediaController.PhotoEntry> photos) {
            this.photos = photos;
            calculate();
        }

        /* JADX WARN: Code restructure failed: missing block: B:187:0x0855, code lost:
            if (r5.lineCounts[2] > r5.lineCounts[3]) goto L189;
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void calculate() {
            /*
                Method dump skipped, instructions count: 2446
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.GroupCalculator.calculate():void");
        }

        public int getWidth() {
            int[] lineWidths = new int[10];
            Arrays.fill(lineWidths, 0);
            int count = this.posArray.size();
            for (int i = 0; i < count; i++) {
                MessageObject.GroupedMessagePosition pos = this.posArray.get(i);
                int width = pos.pw;
                for (int y = pos.minY; y <= pos.maxY; y++) {
                    lineWidths[y] = lineWidths[y] + width;
                }
            }
            int width2 = lineWidths[0];
            for (int y2 = 1; y2 < lineWidths.length; y2++) {
                if (width2 < lineWidths[y2]) {
                    width2 = lineWidths[y2];
                }
            }
            return width2;
        }

        public float getHeight() {
            float[] lineHeights = new float[10];
            Arrays.fill(lineHeights, 0.0f);
            int count = this.posArray.size();
            for (int i = 0; i < count; i++) {
                MessageObject.GroupedMessagePosition pos = this.posArray.get(i);
                float height = pos.ph;
                for (int x = pos.minX; x <= pos.maxX; x++) {
                    lineHeights[x] = lineHeights[x] + height;
                }
            }
            float height2 = lineHeights[0];
            for (int y = 1; y < lineHeights.length; y++) {
                if (height2 < lineHeights[y]) {
                    height2 = lineHeights[y];
                }
            }
            return height2;
        }

        private float getLeft(MessageObject.GroupedMessagePosition except, int minY, int maxY, int minX) {
            float[] sums = new float[(maxY - minY) + 1];
            Arrays.fill(sums, 0.0f);
            int count = this.posArray.size();
            for (int i = 0; i < count; i++) {
                MessageObject.GroupedMessagePosition pos = this.posArray.get(i);
                if (pos != except && pos.maxX < minX) {
                    int end = Math.min((int) pos.maxY, maxY) - minY;
                    for (int y = Math.max(pos.minY - minY, 0); y <= end; y++) {
                        sums[y] = sums[y] + pos.pw;
                    }
                }
            }
            float max = 0.0f;
            for (int i2 = 0; i2 < sums.length; i2++) {
                if (max < sums[i2]) {
                    max = sums[i2];
                }
            }
            return max;
        }

        private float getTop(MessageObject.GroupedMessagePosition except, int minY) {
            float[] sums = new float[this.maxX + 1];
            Arrays.fill(sums, 0.0f);
            int count = this.posArray.size();
            for (int i = 0; i < count; i++) {
                MessageObject.GroupedMessagePosition pos = this.posArray.get(i);
                if (pos != except && pos.maxY < minY) {
                    for (int x = pos.minX; x <= pos.maxX; x++) {
                        sums[x] = sums[x] + pos.ph;
                    }
                }
            }
            float max = 0.0f;
            for (int i2 = 0; i2 < sums.length; i2++) {
                if (max < sums[i2]) {
                    max = sums[i2];
                }
            }
            return max;
        }
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public int getListTopPadding() {
        return this.listView.getPaddingTop();
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public int getCurrentItemTop() {
        if (this.listView.getChildCount() <= 0) {
            RecyclerListView recyclerListView = this.listView;
            recyclerListView.setTopGlowOffset(recyclerListView.getPaddingTop());
            return Integer.MAX_VALUE;
        }
        View child = this.listView.getChildAt(0);
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(child);
        int top = child.getTop();
        int newOffset = AndroidUtilities.dp(8.0f);
        if (top >= AndroidUtilities.dp(8.0f) && holder != null && holder.getAdapterPosition() == 0) {
            newOffset = top;
        }
        this.listView.setTopGlowOffset(newOffset);
        return newOffset;
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public void onPreMeasure(int availableWidth, int availableHeight) {
        this.ignoreLayout = true;
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) getLayoutParams();
        layoutParams.topMargin = ActionBar.getCurrentActionBarHeight();
        if (!AndroidUtilities.isTablet() && AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
            this.paddingTop = (int) (availableHeight / 3.5f);
        } else {
            this.paddingTop = (availableHeight / 5) * 2;
        }
        int dp = this.paddingTop - AndroidUtilities.dp(52.0f);
        this.paddingTop = dp;
        if (dp < 0) {
            this.paddingTop = 0;
        }
        if (this.listView.getPaddingTop() != this.paddingTop) {
            RecyclerListView recyclerListView = this.listView;
            recyclerListView.setPadding(recyclerListView.getPaddingLeft(), this.paddingTop, this.listView.getPaddingRight(), this.listView.getPaddingBottom());
            invalidate();
        }
        this.header.setTextSize((AndroidUtilities.isTablet() || AndroidUtilities.displaySize.x <= AndroidUtilities.displaySize.y) ? 20.0f : 18.0f);
        this.ignoreLayout = false;
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    void scrollToTop() {
        this.listView.smoothScrollToPosition(0);
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    int needsActionBar() {
        return 1;
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public boolean onBackPressed() {
        this.parentAlert.updatePhotoPreview(false);
        return true;
    }

    @Override // android.view.View, android.view.ViewParent
    public void requestLayout() {
        if (this.ignoreLayout) {
            return;
        }
        super.requestLayout();
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    void onMenuItemClick(int id) {
        try {
            this.parentAlert.getPhotoLayout().onMenuItemClick(id);
        } catch (Exception e) {
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        Drawable chatBackgroundDrawable;
        int finalMove;
        boolean restore = false;
        if (this.parentAlert.parentThemeDelegate != null && (chatBackgroundDrawable = this.parentAlert.parentThemeDelegate.getWallpaperDrawable()) != null) {
            int paddingTop = getCurrentItemTop();
            if (AndroidUtilities.isTablet()) {
                finalMove = 16;
            } else if (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
                finalMove = 6;
            } else {
                finalMove = 12;
            }
            if (paddingTop < ActionBar.getCurrentActionBarHeight()) {
                paddingTop -= AndroidUtilities.dp((1.0f - (paddingTop / ActionBar.getCurrentActionBarHeight())) * finalMove);
            }
            int paddingTop2 = Math.max(0, paddingTop);
            canvas.save();
            canvas.clipRect(0, paddingTop2, getWidth(), getHeight());
            chatBackgroundDrawable.setBounds(0, paddingTop2, getWidth(), AndroidUtilities.displaySize.y + paddingTop2);
            chatBackgroundDrawable.draw(canvas);
            restore = true;
        }
        super.dispatchDraw(canvas);
        if (restore) {
            canvas.restore();
        }
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        boolean isPortrait = AndroidUtilities.displaySize.y > AndroidUtilities.displaySize.x;
        if (this.isPortrait != isPortrait) {
            this.isPortrait = isPortrait;
            int groupCellsCount = this.groupsView.groupCells.size();
            for (int i = 0; i < groupCellsCount; i++) {
                PreviewGroupsView.PreviewGroupCell groupCell = (PreviewGroupsView.PreviewGroupCell) this.groupsView.groupCells.get(i);
                if (groupCell.group.photos.size() == 1) {
                    groupCell.setGroup(groupCell.group, true);
                }
            }
        }
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    void onSelectedItemsCountChanged(int count) {
        if (count > 1) {
            this.parentAlert.selectedMenuItem.showSubItem(0);
        } else {
            this.parentAlert.selectedMenuItem.hideSubItem(0);
        }
    }

    /* loaded from: classes5.dex */
    public class PreviewGroupsView extends ViewGroup {
        private ChatActionCell hintView;
        HashMap<Object, Object> photosMap;
        List<Map.Entry<Object, Object>> photosMapKeys;
        ArrayList<Object> photosOrder;
        private float savedDragFromX;
        private float savedDragFromY;
        private float savedDraggingT;
        HashMap<Object, Object> selectedPhotos;
        float viewBottom;
        float viewTop;
        private ArrayList<PreviewGroupCell> groupCells = new ArrayList<>();
        private HashMap<Object, Object> deletedPhotos = new HashMap<>();
        private int paddingTop = AndroidUtilities.dp(16.0f);
        private int paddingBottom = AndroidUtilities.dp(64.0f);
        private int lastMeasuredHeight = 0;
        boolean[] lastGroupSeen = null;
        long tapTime = 0;
        PreviewGroupCell tapGroupCell = null;
        PreviewGroupCell.MediaCell tapMediaCell = null;
        private float draggingT = 0.0f;
        private final Point tmpPoint = new Point();
        private boolean scrollerStarted = false;
        private final Runnable scroller = new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.PreviewGroupsView.2
            @Override // java.lang.Runnable
            public void run() {
                if (ChatAttachAlertPhotoLayoutPreview.this.draggingCell == null || ChatAttachAlertPhotoLayoutPreview.this.draggingCellHiding) {
                    return;
                }
                int scrollY = ChatAttachAlertPhotoLayoutPreview.this.listView.computeVerticalScrollOffset();
                boolean atBottom = ChatAttachAlertPhotoLayoutPreview.this.listView.computeVerticalScrollExtent() + scrollY >= (PreviewGroupsView.this.measurePureHeight() - PreviewGroupsView.this.paddingBottom) + PreviewGroupsView.this.paddingTop;
                float top = Math.max(0.0f, (ChatAttachAlertPhotoLayoutPreview.this.draggingCellTouchY - Math.max(0, scrollY - ChatAttachAlertPhotoLayoutPreview.this.getListTopPadding())) - AndroidUtilities.dp(52.0f));
                float bottom = Math.max(0.0f, ((ChatAttachAlertPhotoLayoutPreview.this.listView.getMeasuredHeight() - (ChatAttachAlertPhotoLayoutPreview.this.draggingCellTouchY - scrollY)) - ChatAttachAlertPhotoLayoutPreview.this.getListTopPadding()) - AndroidUtilities.dp(84.0f));
                float r = AndroidUtilities.dp(32.0f);
                float dy = 0.0f;
                if (top < r && scrollY > ChatAttachAlertPhotoLayoutPreview.this.getListTopPadding()) {
                    dy = (-(1.0f - (top / r))) * AndroidUtilities.dp(6.0f);
                } else if (bottom < r) {
                    dy = (1.0f - (bottom / r)) * AndroidUtilities.dp(6.0f);
                }
                if (Math.abs((int) dy) > 0 && ChatAttachAlertPhotoLayoutPreview.this.listView.canScrollVertically((int) dy) && (dy <= 0.0f || !atBottom)) {
                    ChatAttachAlertPhotoLayoutPreview.access$1416(ChatAttachAlertPhotoLayoutPreview.this, dy);
                    ChatAttachAlertPhotoLayoutPreview.this.listView.scrollBy(0, (int) dy);
                    PreviewGroupsView.this.invalidate();
                }
                PreviewGroupsView.this.scrollerStarted = true;
                PreviewGroupsView.this.postDelayed(this, 15L);
            }
        };
        GroupingPhotoViewerProvider photoViewerProvider = new GroupingPhotoViewerProvider();
        private int undoViewId = 0;
        private HashMap<MediaController.PhotoEntry, ImageReceiver> images = new HashMap<>();

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public PreviewGroupsView(Context context) {
            super(context);
            ChatAttachAlertPhotoLayoutPreview.this = r5;
            setWillNotDraw(false);
            ChatActionCell chatActionCell = new ChatActionCell(context, true, r5.themeDelegate);
            this.hintView = chatActionCell;
            chatActionCell.setCustomText(LocaleController.getString("AttachMediaDragHint", R.string.AttachMediaDragHint));
            addView(this.hintView);
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
            ChatActionCell chatActionCell = this.hintView;
            chatActionCell.layout(0, 0, chatActionCell.getMeasuredWidth(), this.hintView.getMeasuredHeight());
        }

        @Override // android.view.ViewGroup
        protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
            return false;
        }

        public void saveDeletedImageId(MediaController.PhotoEntry photo) {
            if (ChatAttachAlertPhotoLayoutPreview.this.photoLayout != null) {
                HashMap<Object, Object> photosMap = ChatAttachAlertPhotoLayoutPreview.this.photoLayout.getSelectedPhotos();
                List<Map.Entry<Object, Object>> entries = new ArrayList<>(photosMap.entrySet());
                int entriesCount = entries.size();
                for (int i = 0; i < entriesCount; i++) {
                    if (entries.get(i).getValue() == photo) {
                        this.deletedPhotos.put(photo, entries.get(i).getKey());
                        return;
                    }
                }
            }
        }

        public void fromPhotoLayout(ChatAttachAlertPhotoLayout photoLayout) {
            this.photosOrder = photoLayout.getSelectedPhotosOrder();
            this.photosMap = photoLayout.getSelectedPhotos();
            fromPhotoArrays();
        }

        public void fromPhotoArrays() {
            this.groupCells.clear();
            ArrayList<MediaController.PhotoEntry> photos = new ArrayList<>();
            int photosOrderSize = this.photosOrder.size();
            int photosOrderLast = photosOrderSize - 1;
            for (int i = 0; i < photosOrderSize; i++) {
                int imageId = ((Integer) this.photosOrder.get(i)).intValue();
                photos.add((MediaController.PhotoEntry) this.photosMap.get(Integer.valueOf(imageId)));
                if (i % 10 == 9 || i == photosOrderLast) {
                    PreviewGroupCell groupCell = new PreviewGroupCell();
                    groupCell.setGroup(new GroupCalculator(photos), false);
                    this.groupCells.add(groupCell);
                    photos = new ArrayList<>();
                }
            }
        }

        public void calcPhotoArrays() {
            this.photosMap = ChatAttachAlertPhotoLayoutPreview.this.photoLayout.getSelectedPhotos();
            this.photosMapKeys = new ArrayList(this.photosMap.entrySet());
            this.selectedPhotos = new HashMap<>();
            this.photosOrder = new ArrayList<>();
            int groupCellsCount = this.groupCells.size();
            for (int i = 0; i < groupCellsCount; i++) {
                PreviewGroupCell groupCell = this.groupCells.get(i);
                GroupCalculator group = groupCell.group;
                if (group.photos.size() != 0) {
                    int photosCount = group.photos.size();
                    for (int j = 0; j < photosCount; j++) {
                        MediaController.PhotoEntry photoEntry = group.photos.get(j);
                        if (this.deletedPhotos.containsKey(photoEntry)) {
                            Object imageId = this.deletedPhotos.get(photoEntry);
                            this.selectedPhotos.put(imageId, photoEntry);
                            this.photosOrder.add(imageId);
                        } else {
                            boolean found = false;
                            int k = 0;
                            while (true) {
                                if (k >= this.photosMapKeys.size()) {
                                    break;
                                }
                                Map.Entry<Object, Object> entry = this.photosMapKeys.get(k);
                                Object value = entry.getValue();
                                if (value != photoEntry) {
                                    k++;
                                } else {
                                    Object key = entry.getKey();
                                    this.selectedPhotos.put(key, value);
                                    this.photosOrder.add(key);
                                    found = true;
                                    break;
                                }
                            }
                            if (!found) {
                                int k2 = 0;
                                while (true) {
                                    if (k2 < this.photosMapKeys.size()) {
                                        Map.Entry<Object, Object> entry2 = this.photosMapKeys.get(k2);
                                        Object value2 = entry2.getValue();
                                        if (!(value2 instanceof MediaController.PhotoEntry) || ((MediaController.PhotoEntry) value2).path == null || photoEntry == null || !((MediaController.PhotoEntry) value2).path.equals(photoEntry.path)) {
                                            k2++;
                                        } else {
                                            Object key2 = entry2.getKey();
                                            this.selectedPhotos.put(key2, value2);
                                            this.photosOrder.add(key2);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        public void toPhotoLayout(ChatAttachAlertPhotoLayout photoLayout, boolean updateLayout) {
            int previousCount = photoLayout.getSelectedPhotosOrder().size();
            calcPhotoArrays();
            photoLayout.updateSelected(this.selectedPhotos, this.photosOrder, updateLayout);
            if (previousCount != this.photosOrder.size()) {
                ChatAttachAlertPhotoLayoutPreview.this.parentAlert.updateCountButton(1);
            }
        }

        public int getPhotosCount() {
            int count = 0;
            int groupCellsCount = this.groupCells.size();
            for (int i = 0; i < groupCellsCount; i++) {
                PreviewGroupCell groupCell = this.groupCells.get(i);
                if (groupCell != null && groupCell.group != null && groupCell.group.photos != null) {
                    count += groupCell.group.photos.size();
                }
            }
            return count;
        }

        public ArrayList<MediaController.PhotoEntry> getPhotos() {
            ArrayList<MediaController.PhotoEntry> photos = new ArrayList<>();
            int groupCellsCount = this.groupCells.size();
            for (int i = 0; i < groupCellsCount; i++) {
                PreviewGroupCell groupCell = this.groupCells.get(i);
                if (groupCell != null && groupCell.group != null && groupCell.group.photos != null) {
                    photos.addAll(groupCell.group.photos);
                }
            }
            return photos;
        }

        public int measurePureHeight() {
            int height = this.paddingTop + this.paddingBottom;
            int groupCellsCount = this.groupCells.size();
            for (int i = 0; i < groupCellsCount; i++) {
                height = (int) (height + this.groupCells.get(i).measure());
            }
            if (this.hintView.getMeasuredHeight() <= 0) {
                this.hintView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.x, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(9999, Integer.MIN_VALUE));
            }
            return height + this.hintView.getMeasuredHeight();
        }

        private int measureHeight() {
            return Math.max(measurePureHeight(), (AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight()) - AndroidUtilities.dp(45.0f));
        }

        @Override // android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            this.hintView.measure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(9999, Integer.MIN_VALUE));
            if (this.lastMeasuredHeight <= 0) {
                this.lastMeasuredHeight = measureHeight();
            }
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(Math.max(View.MeasureSpec.getSize(heightMeasureSpec), this.lastMeasuredHeight), C.BUFFER_FLAG_ENCRYPTED));
        }

        @Override // android.view.View
        public void invalidate() {
            int measuredHeight = measureHeight();
            if (this.lastMeasuredHeight != measuredHeight) {
                this.lastMeasuredHeight = measuredHeight;
                requestLayout();
            }
            super.invalidate();
        }

        private boolean[] groupSeen() {
            boolean[] seen = new boolean[this.groupCells.size()];
            float y = this.paddingTop;
            int scrollY = ChatAttachAlertPhotoLayoutPreview.this.listView.computeVerticalScrollOffset();
            this.viewTop = Math.max(0, scrollY - ChatAttachAlertPhotoLayoutPreview.this.getListTopPadding());
            this.viewBottom = (ChatAttachAlertPhotoLayoutPreview.this.listView.getMeasuredHeight() - ChatAttachAlertPhotoLayoutPreview.this.getListTopPadding()) + scrollY;
            int groupCellsSize = this.groupCells.size();
            for (int i = 0; i < groupCellsSize; i++) {
                PreviewGroupCell groupCell = this.groupCells.get(i);
                float height = groupCell.measure();
                seen[i] = isSeen(y, y + height);
                y += height;
            }
            return seen;
        }

        public boolean isSeen(float fromY, float toY) {
            float f = this.viewTop;
            return (fromY >= f && fromY <= this.viewBottom) || (toY >= f && toY <= this.viewBottom) || (fromY <= f && toY >= this.viewBottom);
        }

        public void onScroll() {
            boolean newGroupSeen = this.lastGroupSeen == null;
            if (!newGroupSeen) {
                boolean[] seen = groupSeen();
                if (seen.length != this.lastGroupSeen.length) {
                    newGroupSeen = true;
                } else {
                    int i = 0;
                    while (true) {
                        if (i >= seen.length) {
                            break;
                        } else if (seen[i] == this.lastGroupSeen[i]) {
                            i++;
                        } else {
                            newGroupSeen = true;
                            break;
                        }
                    }
                }
            } else {
                this.lastGroupSeen = groupSeen();
            }
            if (newGroupSeen) {
                invalidate();
            }
        }

        public void remeasure() {
            float y = this.paddingTop;
            int i = 0;
            int groupCellsCount = this.groupCells.size();
            for (int j = 0; j < groupCellsCount; j++) {
                PreviewGroupCell groupCell = this.groupCells.get(j);
                float height = groupCell.measure();
                groupCell.y = y;
                groupCell.indexStart = i;
                y += height;
                i += groupCell.group.photos.size();
            }
        }

        @Override // android.view.View
        public void onDraw(Canvas canvas) {
            float y = this.paddingTop;
            int i = 0;
            int scrollY = ChatAttachAlertPhotoLayoutPreview.this.listView.computeVerticalScrollOffset();
            this.viewTop = Math.max(0, scrollY - ChatAttachAlertPhotoLayoutPreview.this.getListTopPadding());
            this.viewBottom = (ChatAttachAlertPhotoLayoutPreview.this.listView.getMeasuredHeight() - ChatAttachAlertPhotoLayoutPreview.this.getListTopPadding()) + scrollY;
            canvas.save();
            canvas.translate(0.0f, this.paddingTop);
            int groupCellsCount = this.groupCells.size();
            int j = 0;
            while (true) {
                boolean groupIsSeen = true;
                if (j >= groupCellsCount) {
                    break;
                }
                PreviewGroupCell groupCell = this.groupCells.get(j);
                float height = groupCell.measure();
                groupCell.y = y;
                groupCell.indexStart = i;
                float f = this.viewTop;
                if ((y < f || y > this.viewBottom) && ((y + height < f || y + height > this.viewBottom) && (y > f || y + height < this.viewBottom))) {
                    groupIsSeen = false;
                }
                if (groupIsSeen && groupCell.draw(canvas)) {
                    invalidate();
                }
                canvas.translate(0.0f, height);
                y += height;
                i += groupCell.group.photos.size();
                j++;
            }
            ChatActionCell chatActionCell = this.hintView;
            chatActionCell.setVisiblePart(y, chatActionCell.getMeasuredHeight());
            if (this.hintView.hasGradientService()) {
                this.hintView.drawBackground(canvas, true);
            }
            this.hintView.draw(canvas);
            canvas.restore();
            if (ChatAttachAlertPhotoLayoutPreview.this.draggingCell != null) {
                canvas.save();
                Point point = dragTranslate();
                canvas.translate(point.x, point.y);
                if (ChatAttachAlertPhotoLayoutPreview.this.draggingCell.draw(canvas, true)) {
                    invalidate();
                }
                canvas.restore();
            }
            super.onDraw(canvas);
        }

        Point dragTranslate() {
            if (ChatAttachAlertPhotoLayoutPreview.this.draggingCell != null) {
                if (!ChatAttachAlertPhotoLayoutPreview.this.draggingCellHiding) {
                    RectF drawingRect = ChatAttachAlertPhotoLayoutPreview.this.draggingCell.rect();
                    RectF finalDrawingRect = ChatAttachAlertPhotoLayoutPreview.this.draggingCell.rect(1.0f);
                    this.tmpPoint.x = AndroidUtilities.lerp(finalDrawingRect.left + (drawingRect.width() / 2.0f), ChatAttachAlertPhotoLayoutPreview.this.draggingCellTouchX - ((ChatAttachAlertPhotoLayoutPreview.this.draggingCellLeft - 0.5f) * ChatAttachAlertPhotoLayoutPreview.this.draggingCellFromWidth), this.draggingT);
                    this.tmpPoint.y = AndroidUtilities.lerp(ChatAttachAlertPhotoLayoutPreview.this.draggingCell.groupCell.y + finalDrawingRect.top + (drawingRect.height() / 2.0f), (ChatAttachAlertPhotoLayoutPreview.this.draggingCellTouchY - ((ChatAttachAlertPhotoLayoutPreview.this.draggingCellTop - 0.5f) * ChatAttachAlertPhotoLayoutPreview.this.draggingCellFromHeight)) + ChatAttachAlertPhotoLayoutPreview.this.draggingCellGroupY, this.draggingT);
                } else {
                    RectF drawingRect2 = ChatAttachAlertPhotoLayoutPreview.this.draggingCell.rect();
                    RectF finalDrawingRect2 = ChatAttachAlertPhotoLayoutPreview.this.draggingCell.rect(1.0f);
                    this.tmpPoint.x = AndroidUtilities.lerp(finalDrawingRect2.left + (drawingRect2.width() / 2.0f), this.savedDragFromX, this.draggingT / this.savedDraggingT);
                    this.tmpPoint.y = AndroidUtilities.lerp(ChatAttachAlertPhotoLayoutPreview.this.draggingCell.groupCell.y + finalDrawingRect2.top + (drawingRect2.height() / 2.0f), this.savedDragFromY, this.draggingT / this.savedDraggingT);
                }
                return this.tmpPoint;
            }
            this.tmpPoint.x = 0.0f;
            this.tmpPoint.y = 0.0f;
            return this.tmpPoint;
        }

        void stopDragging() {
            if (ChatAttachAlertPhotoLayoutPreview.this.draggingAnimator != null) {
                ChatAttachAlertPhotoLayoutPreview.this.draggingAnimator.cancel();
            }
            Point dragTranslate = dragTranslate();
            this.savedDraggingT = this.draggingT;
            this.savedDragFromX = dragTranslate.x;
            this.savedDragFromY = dragTranslate.y;
            ChatAttachAlertPhotoLayoutPreview.this.draggingCellHiding = true;
            ChatAttachAlertPhotoLayoutPreview.this.draggingAnimator = ValueAnimator.ofFloat(this.savedDraggingT, 0.0f);
            ChatAttachAlertPhotoLayoutPreview.this.draggingAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$$ExternalSyntheticLambda1
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    ChatAttachAlertPhotoLayoutPreview.PreviewGroupsView.this.m2504x76aa0fb1(valueAnimator);
                }
            });
            ChatAttachAlertPhotoLayoutPreview.this.draggingAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview.PreviewGroupsView.1
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    ChatAttachAlertPhotoLayoutPreview.this.draggingCell = null;
                    ChatAttachAlertPhotoLayoutPreview.this.draggingCellHiding = false;
                    PreviewGroupsView.this.invalidate();
                }
            });
            ChatAttachAlertPhotoLayoutPreview.this.draggingAnimator.setDuration(200L);
            ChatAttachAlertPhotoLayoutPreview.this.draggingAnimator.start();
            invalidate();
        }

        /* renamed from: lambda$stopDragging$0$org-telegram-ui-Components-ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView */
        public /* synthetic */ void m2504x76aa0fb1(ValueAnimator a) {
            this.draggingT = ((Float) a.getAnimatedValue()).floatValue();
            invalidate();
        }

        void startDragging(PreviewGroupCell.MediaCell cell) {
            ChatAttachAlertPhotoLayoutPreview.this.draggingCell = cell;
            ChatAttachAlertPhotoLayoutPreview chatAttachAlertPhotoLayoutPreview = ChatAttachAlertPhotoLayoutPreview.this;
            chatAttachAlertPhotoLayoutPreview.draggingCellGroupY = chatAttachAlertPhotoLayoutPreview.draggingCell.groupCell.y;
            ChatAttachAlertPhotoLayoutPreview.this.draggingCellHiding = false;
            this.draggingT = 0.0f;
            invalidate();
            if (ChatAttachAlertPhotoLayoutPreview.this.draggingAnimator != null) {
                ChatAttachAlertPhotoLayoutPreview.this.draggingAnimator.cancel();
            }
            ChatAttachAlertPhotoLayoutPreview.this.draggingAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
            ChatAttachAlertPhotoLayoutPreview.this.draggingAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    ChatAttachAlertPhotoLayoutPreview.PreviewGroupsView.this.m2503x4294d1ca(valueAnimator);
                }
            });
            ChatAttachAlertPhotoLayoutPreview.this.draggingAnimator.setDuration(200L);
            ChatAttachAlertPhotoLayoutPreview.this.draggingAnimator.start();
        }

        /* renamed from: lambda$startDragging$1$org-telegram-ui-Components-ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView */
        public /* synthetic */ void m2503x4294d1ca(ValueAnimator a) {
            this.draggingT = ((Float) a.getAnimatedValue()).floatValue();
            invalidate();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes5.dex */
        public class GroupingPhotoViewerProvider extends PhotoViewer.EmptyPhotoViewerProvider {
            private ArrayList<MediaController.PhotoEntry> photos = new ArrayList<>();

            GroupingPhotoViewerProvider() {
                PreviewGroupsView.this = this$1;
            }

            public void init(ArrayList<MediaController.PhotoEntry> photos) {
                this.photos = photos;
            }

            @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
            public void onClose() {
                PreviewGroupsView.this.fromPhotoArrays();
                PreviewGroupsView previewGroupsView = PreviewGroupsView.this;
                previewGroupsView.toPhotoLayout(ChatAttachAlertPhotoLayoutPreview.this.photoLayout, false);
            }

            @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
            public boolean isPhotoChecked(int index) {
                if (index < 0 || index >= this.photos.size()) {
                    return false;
                }
                return PreviewGroupsView.this.photosOrder.contains(Integer.valueOf(this.photos.get(index).imageId));
            }

            @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
            public int setPhotoChecked(int index, VideoEditedInfo videoEditedInfo) {
                if (index < 0 || index >= this.photos.size()) {
                    return -1;
                }
                Object imageId = Integer.valueOf(this.photos.get(index).imageId);
                int orderIndex = PreviewGroupsView.this.photosOrder.indexOf((Integer) imageId);
                if (orderIndex >= 0) {
                    if (PreviewGroupsView.this.photosOrder.size() <= 1) {
                        return -1;
                    }
                    PreviewGroupsView.this.photosOrder.remove(orderIndex);
                    PreviewGroupsView.this.fromPhotoArrays();
                    return orderIndex;
                }
                PreviewGroupsView.this.photosOrder.add(imageId);
                PreviewGroupsView.this.fromPhotoArrays();
                return PreviewGroupsView.this.photosOrder.size() - 1;
            }

            @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
            public int setPhotoUnchecked(Object entry) {
                int index;
                MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) entry;
                Object imageId = Integer.valueOf(photoEntry.imageId);
                if (PreviewGroupsView.this.photosOrder.size() > 1 && (index = PreviewGroupsView.this.photosOrder.indexOf((Integer) imageId)) >= 0) {
                    PreviewGroupsView.this.photosOrder.remove(index);
                    PreviewGroupsView.this.fromPhotoArrays();
                    return index;
                }
                return -1;
            }

            @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
            public int getSelectedCount() {
                return PreviewGroupsView.this.photosOrder.size();
            }

            @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
            public ArrayList<Object> getSelectedPhotosOrder() {
                return PreviewGroupsView.this.photosOrder;
            }

            @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
            public HashMap<Object, Object> getSelectedPhotos() {
                return PreviewGroupsView.this.photosMap;
            }

            @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
            public int getPhotoIndex(int index) {
                MediaController.PhotoEntry photoEntry;
                if (index < 0 || index >= this.photos.size() || (photoEntry = this.photos.get(index)) == null) {
                    return -1;
                }
                return PreviewGroupsView.this.photosOrder.indexOf(Integer.valueOf(photoEntry.imageId));
            }

            @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
            public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index, boolean needPreview) {
                MediaController.PhotoEntry photoEntry;
                if (index >= 0 && index < this.photos.size() && isPhotoChecked(index) && (photoEntry = this.photos.get(index)) != null) {
                    PreviewGroupCell group = null;
                    PreviewGroupCell.MediaCell mediaCell = null;
                    int groupCellsCount = PreviewGroupsView.this.groupCells.size();
                    for (int i = 0; i < groupCellsCount; i++) {
                        group = (PreviewGroupCell) PreviewGroupsView.this.groupCells.get(i);
                        if (group != null && group.media != null) {
                            int count = group.media.size();
                            int j = 0;
                            while (true) {
                                if (j >= count) {
                                    break;
                                }
                                PreviewGroupCell.MediaCell cell = group.media.get(j);
                                if (cell == null || cell.photoEntry != photoEntry || cell.scale <= 0.5d) {
                                    j++;
                                } else {
                                    PreviewGroupCell.MediaCell mediaCell2 = group.media.get(j);
                                    mediaCell = mediaCell2;
                                    break;
                                }
                            }
                            if (mediaCell != null) {
                                break;
                            }
                        }
                    }
                    if (group != null && mediaCell != null) {
                        PhotoViewer.PlaceProviderObject object = new PhotoViewer.PlaceProviderObject();
                        int[] coords = new int[2];
                        PreviewGroupsView.this.getLocationInWindow(coords);
                        if (Build.VERSION.SDK_INT < 26) {
                            coords[0] = coords[0] - ChatAttachAlertPhotoLayoutPreview.this.parentAlert.getLeftInset();
                        }
                        object.viewX = coords[0];
                        object.viewY = coords[1] + ((int) group.y);
                        object.scale = 1.0f;
                        object.parentView = PreviewGroupsView.this;
                        object.imageReceiver = mediaCell.image;
                        object.thumb = object.imageReceiver.getBitmapSafe();
                        object.radius = new int[4];
                        object.radius[0] = (int) mediaCell.roundRadiuses.left;
                        object.radius[1] = (int) mediaCell.roundRadiuses.top;
                        object.radius[2] = (int) mediaCell.roundRadiuses.right;
                        object.radius[3] = (int) mediaCell.roundRadiuses.bottom;
                        object.clipTopAddition = (int) (-PreviewGroupsView.this.getY());
                        object.clipBottomAddition = PreviewGroupsView.this.getHeight() - ((int) (((-PreviewGroupsView.this.getY()) + ChatAttachAlertPhotoLayoutPreview.this.listView.getHeight()) - ChatAttachAlertPhotoLayoutPreview.this.parentAlert.getClipLayoutBottom()));
                        return object;
                    }
                }
                return null;
            }

            @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
            public boolean cancelButtonPressed() {
                return false;
            }

            @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
            public void updatePhotoAtIndex(int index) {
                MediaController.PhotoEntry photoEntry;
                if (index < 0 || index >= this.photos.size() || (photoEntry = this.photos.get(index)) == null) {
                    return;
                }
                int imageId = photoEntry.imageId;
                PreviewGroupsView.this.invalidate();
                for (int i = 0; i < PreviewGroupsView.this.groupCells.size(); i++) {
                    PreviewGroupCell groupCell = (PreviewGroupCell) PreviewGroupsView.this.groupCells.get(i);
                    if (groupCell != null && groupCell.media != null) {
                        for (int j = 0; j < groupCell.media.size(); j++) {
                            PreviewGroupCell.MediaCell mediaCell = groupCell.media.get(j);
                            if (mediaCell != null && mediaCell.photoEntry.imageId == imageId) {
                                mediaCell.setImage(photoEntry);
                            }
                        }
                        boolean hadUpdates = false;
                        if (groupCell.group != null && groupCell.group.photos != null) {
                            for (int j2 = 0; j2 < groupCell.group.photos.size(); j2++) {
                                if (groupCell.group.photos.get(j2).imageId == imageId) {
                                    groupCell.group.photos.set(j2, photoEntry);
                                    hadUpdates = true;
                                }
                            }
                        }
                        if (hadUpdates) {
                            groupCell.setGroup(groupCell.group, true);
                        }
                    }
                }
                PreviewGroupsView.this.remeasure();
                PreviewGroupsView.this.invalidate();
            }
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent event) {
            PreviewGroupCell touchGroupCell;
            boolean result;
            PreviewGroupCell draggingOverGroupCell;
            boolean result2;
            PreviewGroupCell.MediaCell mediaCell;
            int type;
            ChatActivity chatActivity;
            PreviewGroupCell.MediaCell replaceMediaCell;
            PreviewGroupCell replaceGroupCell;
            int mediaCount;
            PreviewGroupCell draggingOverGroupCell2;
            float maxLength;
            float f;
            RectF drawingRect;
            float touchX = event.getX();
            float touchY = event.getY();
            PreviewGroupCell.MediaCell touchMediaCell = null;
            float groupY = 0.0f;
            int groupCellsCount = this.groupCells.size();
            int j = 0;
            while (true) {
                if (j >= groupCellsCount) {
                    touchGroupCell = null;
                    break;
                }
                PreviewGroupCell groupCell = this.groupCells.get(j);
                float height = groupCell.measure();
                if (touchY >= groupY && touchY <= groupY + height) {
                    touchGroupCell = groupCell;
                    break;
                }
                groupY += height;
                j++;
            }
            if (touchGroupCell != null) {
                int mediaCount2 = touchGroupCell.media.size();
                int i = 0;
                while (true) {
                    if (i < mediaCount2) {
                        PreviewGroupCell.MediaCell mediaCell2 = touchGroupCell.media.get(i);
                        if (mediaCell2 == null || !mediaCell2.drawingRect().contains(touchX, touchY - groupY)) {
                            i++;
                        } else {
                            touchMediaCell = mediaCell2;
                            break;
                        }
                    } else {
                        break;
                    }
                }
            }
            PreviewGroupCell.MediaCell draggingOverMediaCell = null;
            if (ChatAttachAlertPhotoLayoutPreview.this.draggingCell != null) {
                RectF drawingRect2 = ChatAttachAlertPhotoLayoutPreview.this.draggingCell.rect();
                Point dragPoint = dragTranslate();
                RectF draggingCellXY = new RectF();
                float cx = dragPoint.x;
                float cy = dragPoint.y;
                float width = cx - (drawingRect2.width() / 2.0f);
                float height2 = cy - (drawingRect2.height() / 2.0f);
                result = false;
                float width2 = cx + (drawingRect2.width() / 2.0f);
                float groupY2 = 0.0f;
                float groupY3 = cy + (drawingRect2.height() / 2.0f);
                draggingCellXY.set(width, height2, width2, groupY3);
                int j2 = 0;
                float maxLength2 = 0.0f;
                PreviewGroupCell draggingOverGroupCell3 = null;
                while (j2 < groupCellsCount) {
                    PreviewGroupCell groupCell2 = this.groupCells.get(j2);
                    float height3 = groupCell2.measure();
                    float top = groupY2;
                    int groupCellsCount2 = groupCellsCount;
                    float bottom = groupY2 + height3;
                    PreviewGroupCell.MediaCell draggingOverMediaCell2 = draggingOverMediaCell;
                    if (bottom >= draggingCellXY.top) {
                        drawingRect = drawingRect2;
                        if (draggingCellXY.bottom >= top) {
                            float length = Math.min(bottom, draggingCellXY.bottom) - Math.max(top, draggingCellXY.top);
                            if (length > maxLength2) {
                                draggingOverGroupCell3 = groupCell2;
                                maxLength2 = length;
                            }
                        }
                    } else {
                        drawingRect = drawingRect2;
                    }
                    groupY2 += height3;
                    j2++;
                    groupCellsCount = groupCellsCount2;
                    draggingOverMediaCell = draggingOverMediaCell2;
                    drawingRect2 = drawingRect;
                }
                PreviewGroupCell.MediaCell draggingOverMediaCell3 = draggingOverMediaCell;
                if (draggingOverGroupCell3 == null) {
                    draggingOverGroupCell = draggingOverGroupCell3;
                    draggingOverMediaCell = draggingOverMediaCell3;
                } else {
                    float maxArea = 0.0f;
                    int mediaCount3 = draggingOverGroupCell3.media.size();
                    int i2 = 0;
                    while (i2 < mediaCount3) {
                        PreviewGroupCell.MediaCell mediaCell3 = draggingOverGroupCell3.media.get(i2);
                        if (mediaCell3 == null || mediaCell3 == ChatAttachAlertPhotoLayoutPreview.this.draggingCell) {
                            draggingOverGroupCell2 = draggingOverGroupCell3;
                            maxLength = maxLength2;
                            mediaCount = mediaCount3;
                        } else {
                            maxLength = maxLength2;
                            if (!draggingOverGroupCell3.group.photos.contains(mediaCell3.photoEntry)) {
                                draggingOverGroupCell2 = draggingOverGroupCell3;
                                mediaCount = mediaCount3;
                            } else {
                                RectF mediaCellRect = mediaCell3.drawingRect();
                                if ((mediaCell3.positionFlags & 4) <= 0) {
                                    f = 0.0f;
                                } else {
                                    f = 0.0f;
                                    mediaCellRect.top = 0.0f;
                                }
                                if ((mediaCell3.positionFlags & 1) > 0) {
                                    mediaCellRect.left = f;
                                }
                                if ((mediaCell3.positionFlags & 2) > 0) {
                                    mediaCellRect.right = getWidth();
                                }
                                if ((mediaCell3.positionFlags & 8) > 0) {
                                    mediaCellRect.bottom = draggingOverGroupCell3.height;
                                }
                                if (!RectF.intersects(draggingCellXY, mediaCellRect)) {
                                    draggingOverGroupCell2 = draggingOverGroupCell3;
                                    mediaCount = mediaCount3;
                                } else {
                                    draggingOverGroupCell2 = draggingOverGroupCell3;
                                    mediaCount = mediaCount3;
                                    float area = ((Math.min(mediaCellRect.right, draggingCellXY.right) - Math.max(mediaCellRect.left, draggingCellXY.left)) * (Math.min(mediaCellRect.bottom, draggingCellXY.bottom) - Math.max(mediaCellRect.top, draggingCellXY.top))) / (draggingCellXY.width() * draggingCellXY.height());
                                    if (area > 0.15f && area > maxArea) {
                                        maxArea = area;
                                        draggingOverMediaCell3 = mediaCell3;
                                    }
                                }
                            }
                        }
                        i2++;
                        maxLength2 = maxLength;
                        draggingOverGroupCell3 = draggingOverGroupCell2;
                        mediaCount3 = mediaCount;
                    }
                    draggingOverGroupCell = draggingOverGroupCell3;
                    draggingOverMediaCell = draggingOverMediaCell3;
                }
            } else {
                draggingOverGroupCell = null;
                result = false;
            }
            int action = event.getAction();
            if (action != 0 || ChatAttachAlertPhotoLayoutPreview.this.draggingCell != null || ChatAttachAlertPhotoLayoutPreview.this.listView.scrollingByUser || ((ChatAttachAlertPhotoLayoutPreview.this.draggingAnimator != null && ChatAttachAlertPhotoLayoutPreview.this.draggingAnimator.isRunning()) || touchGroupCell == null || touchMediaCell == null || touchGroupCell.group == null || !touchGroupCell.group.photos.contains(touchMediaCell.photoEntry))) {
                if (action == 2 && ChatAttachAlertPhotoLayoutPreview.this.draggingCell != null && !ChatAttachAlertPhotoLayoutPreview.this.draggingCellHiding) {
                    ChatAttachAlertPhotoLayoutPreview.this.draggingCellTouchX = touchX;
                    ChatAttachAlertPhotoLayoutPreview.this.draggingCellTouchY = touchY;
                    if (!this.scrollerStarted) {
                        this.scrollerStarted = true;
                        postDelayed(this.scroller, 16L);
                    }
                    invalidate();
                    result2 = true;
                } else if (action == 1 && ChatAttachAlertPhotoLayoutPreview.this.draggingCell != null) {
                    if (touchGroupCell != null && touchMediaCell != null && touchMediaCell != ChatAttachAlertPhotoLayoutPreview.this.draggingCell) {
                        replaceMediaCell = touchMediaCell;
                        replaceGroupCell = touchGroupCell;
                    } else if (draggingOverGroupCell != null && draggingOverMediaCell != null && draggingOverMediaCell != ChatAttachAlertPhotoLayoutPreview.this.draggingCell && draggingOverMediaCell.photoEntry != ChatAttachAlertPhotoLayoutPreview.this.draggingCell.photoEntry) {
                        replaceMediaCell = draggingOverMediaCell;
                        replaceGroupCell = draggingOverGroupCell;
                    } else {
                        replaceMediaCell = null;
                        replaceGroupCell = null;
                    }
                    if (replaceGroupCell != null && replaceMediaCell != null && replaceMediaCell != ChatAttachAlertPhotoLayoutPreview.this.draggingCell) {
                        int draggingIndex = ChatAttachAlertPhotoLayoutPreview.this.draggingCell.groupCell.group.photos.indexOf(ChatAttachAlertPhotoLayoutPreview.this.draggingCell.photoEntry);
                        int tapIndex = replaceGroupCell.group.photos.indexOf(replaceMediaCell.photoEntry);
                        if (draggingIndex >= 0) {
                            ChatAttachAlertPhotoLayoutPreview.this.draggingCell.groupCell.group.photos.remove(draggingIndex);
                            ChatAttachAlertPhotoLayoutPreview.this.draggingCell.groupCell.setGroup(ChatAttachAlertPhotoLayoutPreview.this.draggingCell.groupCell.group, true);
                        }
                        if (tapIndex >= 0) {
                            if (this.groupCells.indexOf(replaceGroupCell) > this.groupCells.indexOf(ChatAttachAlertPhotoLayoutPreview.this.draggingCell.groupCell)) {
                                tapIndex++;
                            }
                            pushToGroup(replaceGroupCell, ChatAttachAlertPhotoLayoutPreview.this.draggingCell.photoEntry, tapIndex);
                            if (ChatAttachAlertPhotoLayoutPreview.this.draggingCell.groupCell != replaceGroupCell) {
                                PreviewGroupCell.MediaCell newDraggingCell = null;
                                int mediaCount4 = replaceGroupCell.media.size();
                                int i3 = 0;
                                while (true) {
                                    if (i3 >= mediaCount4) {
                                        break;
                                    }
                                    PreviewGroupCell.MediaCell mediaCell4 = replaceGroupCell.media.get(i3);
                                    int tapIndex2 = tapIndex;
                                    PreviewGroupCell.MediaCell touchMediaCell2 = touchMediaCell;
                                    if (mediaCell4.photoEntry != ChatAttachAlertPhotoLayoutPreview.this.draggingCell.photoEntry) {
                                        i3++;
                                        tapIndex = tapIndex2;
                                        touchMediaCell = touchMediaCell2;
                                    } else {
                                        newDraggingCell = mediaCell4;
                                        break;
                                    }
                                }
                                if (newDraggingCell != null) {
                                    remeasure();
                                    newDraggingCell.layoutFrom(ChatAttachAlertPhotoLayoutPreview.this.draggingCell);
                                    ChatAttachAlertPhotoLayoutPreview.this.draggingCell = newDraggingCell;
                                    newDraggingCell.groupCell = replaceGroupCell;
                                    ChatAttachAlertPhotoLayoutPreview.this.draggingCell.fromScale = 1.0f;
                                    newDraggingCell.scale = 1.0f;
                                    remeasure();
                                }
                            }
                        }
                        try {
                            ChatAttachAlertPhotoLayoutPreview.this.performHapticFeedback(7, 2);
                        } catch (Exception e) {
                        }
                        updateGroups();
                        toPhotoLayout(ChatAttachAlertPhotoLayoutPreview.this.photoLayout, false);
                    }
                    stopDragging();
                    result2 = true;
                } else if (action == 1 && ChatAttachAlertPhotoLayoutPreview.this.draggingCell == null && (mediaCell = this.tapMediaCell) != null && this.tapGroupCell != null) {
                    RectF cellRect = mediaCell.drawingRect();
                    AndroidUtilities.rectTmp.set(cellRect.right - AndroidUtilities.dp(36.4f), this.tapGroupCell.top + cellRect.top, cellRect.right, this.tapGroupCell.top + cellRect.top + AndroidUtilities.dp(36.4f));
                    boolean tappedAtIndex = AndroidUtilities.rectTmp.contains(touchX, touchY - this.tapMediaCell.groupCell.y);
                    if (tappedAtIndex) {
                        if (ChatAttachAlertPhotoLayoutPreview.this.getSelectedItemsCount() > 1) {
                            final MediaController.PhotoEntry photo = this.tapMediaCell.photoEntry;
                            final int index = this.tapGroupCell.group.photos.indexOf(photo);
                            if (index >= 0) {
                                saveDeletedImageId(photo);
                                final PreviewGroupCell groupCell3 = this.tapGroupCell;
                                groupCell3.group.photos.remove(index);
                                groupCell3.setGroup(groupCell3.group, true);
                                updateGroups();
                                toPhotoLayout(ChatAttachAlertPhotoLayoutPreview.this.photoLayout, false);
                                final int currentUndoViewId = this.undoViewId + 1;
                                this.undoViewId = currentUndoViewId;
                                ChatAttachAlertPhotoLayoutPreview.this.undoView.showWithAction(0L, 82, photo, null, new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$$ExternalSyntheticLambda4
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        ChatAttachAlertPhotoLayoutPreview.PreviewGroupsView.this.m2501x3ce7e05(groupCell3, photo, index);
                                    }
                                });
                                postDelayed(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$$ExternalSyntheticLambda2
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        ChatAttachAlertPhotoLayoutPreview.PreviewGroupsView.this.m2502xe1c1e3e4(currentUndoViewId);
                                    }
                                }, 4000L);
                            }
                            if (ChatAttachAlertPhotoLayoutPreview.this.draggingAnimator != null) {
                                ChatAttachAlertPhotoLayoutPreview.this.draggingAnimator.cancel();
                            }
                        }
                    } else {
                        calcPhotoArrays();
                        ArrayList<MediaController.PhotoEntry> arrayList = getPhotos();
                        int position = arrayList.indexOf(this.tapMediaCell.photoEntry);
                        if (ChatAttachAlertPhotoLayoutPreview.this.parentAlert.avatarPicker != 0) {
                            chatActivity = null;
                            type = 1;
                        } else if (ChatAttachAlertPhotoLayoutPreview.this.parentAlert.baseFragment instanceof ChatActivity) {
                            chatActivity = (ChatActivity) ChatAttachAlertPhotoLayoutPreview.this.parentAlert.baseFragment;
                            type = 0;
                        } else {
                            chatActivity = null;
                            type = 4;
                        }
                        if (!ChatAttachAlertPhotoLayoutPreview.this.parentAlert.delegate.needEnterComment()) {
                            AndroidUtilities.hideKeyboard(ChatAttachAlertPhotoLayoutPreview.this.parentAlert.baseFragment.getFragmentView().findFocus());
                            AndroidUtilities.hideKeyboard(ChatAttachAlertPhotoLayoutPreview.this.parentAlert.getContainer().findFocus());
                        }
                        PhotoViewer.getInstance().setParentActivity(ChatAttachAlertPhotoLayoutPreview.this.parentAlert.baseFragment.getParentActivity(), ChatAttachAlertPhotoLayoutPreview.this.resourcesProvider);
                        PhotoViewer.getInstance().setParentAlert(ChatAttachAlertPhotoLayoutPreview.this.parentAlert);
                        PhotoViewer.getInstance().setMaxSelectedPhotos(ChatAttachAlertPhotoLayoutPreview.this.parentAlert.maxSelectedPhotos, ChatAttachAlertPhotoLayoutPreview.this.parentAlert.allowOrder);
                        this.photoViewerProvider.init(arrayList);
                        ArrayList<Object> objectArrayList = new ArrayList<>(arrayList);
                        PhotoViewer.getInstance().openPhotoForSelect(objectArrayList, position, type, false, this.photoViewerProvider, chatActivity);
                        if (ChatAttachAlertPhotoLayoutPreview.this.photoLayout.captionForAllMedia()) {
                            PhotoViewer.getInstance().setCaption(ChatAttachAlertPhotoLayoutPreview.this.parentAlert.getCommentTextView().getText());
                        }
                    }
                    this.tapMediaCell = null;
                    this.tapTime = 0L;
                    ChatAttachAlertPhotoLayoutPreview.this.draggingCell = null;
                    this.draggingT = 0.0f;
                    result2 = true;
                } else {
                    result2 = result;
                }
            } else {
                this.tapGroupCell = touchGroupCell;
                this.tapMediaCell = touchMediaCell;
                ChatAttachAlertPhotoLayoutPreview.this.draggingCellTouchX = touchX;
                ChatAttachAlertPhotoLayoutPreview.this.draggingCellTouchY = touchY;
                ChatAttachAlertPhotoLayoutPreview.this.draggingCell = null;
                final long wasTapTime = SystemClock.elapsedRealtime();
                this.tapTime = wasTapTime;
                final PreviewGroupCell.MediaCell wasTapMediaCell = this.tapMediaCell;
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$$ExternalSyntheticLambda3
                    @Override // java.lang.Runnable
                    public final void run() {
                        ChatAttachAlertPhotoLayoutPreview.PreviewGroupsView.this.m2500x25db1826(wasTapTime, wasTapMediaCell);
                    }
                }, ViewConfiguration.getLongPressTimeout());
                invalidate();
                result2 = true;
            }
            if (action == 1 || action == 3) {
                this.tapTime = 0L;
                removeCallbacks(this.scroller);
                this.scrollerStarted = false;
                if (!result2) {
                    stopDragging();
                    return true;
                }
                return result2;
            }
            return result2;
        }

        /* renamed from: lambda$onTouchEvent$2$org-telegram-ui-Components-ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView */
        public /* synthetic */ void m2500x25db1826(long wasTapTime, PreviewGroupCell.MediaCell wasTapMediaCell) {
            PreviewGroupCell.MediaCell mediaCell;
            if (ChatAttachAlertPhotoLayoutPreview.this.listView.scrollingByUser || this.tapTime != wasTapTime || (mediaCell = this.tapMediaCell) != wasTapMediaCell) {
                return;
            }
            startDragging(mediaCell);
            RectF draggingCellRect = ChatAttachAlertPhotoLayoutPreview.this.draggingCell.rect();
            RectF draggingCellDrawingRect = ChatAttachAlertPhotoLayoutPreview.this.draggingCell.drawingRect();
            ChatAttachAlertPhotoLayoutPreview chatAttachAlertPhotoLayoutPreview = ChatAttachAlertPhotoLayoutPreview.this;
            chatAttachAlertPhotoLayoutPreview.draggingCellLeft = (((chatAttachAlertPhotoLayoutPreview.draggingCellTouchX - draggingCellRect.left) / draggingCellRect.width()) + 0.5f) / 2.0f;
            ChatAttachAlertPhotoLayoutPreview chatAttachAlertPhotoLayoutPreview2 = ChatAttachAlertPhotoLayoutPreview.this;
            chatAttachAlertPhotoLayoutPreview2.draggingCellTop = (chatAttachAlertPhotoLayoutPreview2.draggingCellTouchY - draggingCellRect.top) / draggingCellRect.height();
            ChatAttachAlertPhotoLayoutPreview.this.draggingCellFromWidth = draggingCellDrawingRect.width();
            ChatAttachAlertPhotoLayoutPreview.this.draggingCellFromHeight = draggingCellDrawingRect.height();
            try {
                ChatAttachAlertPhotoLayoutPreview.this.performHapticFeedback(0, 2);
            } catch (Exception e) {
            }
        }

        /* renamed from: lambda$onTouchEvent$3$org-telegram-ui-Components-ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView */
        public /* synthetic */ void m2501x3ce7e05(PreviewGroupCell groupCell, MediaController.PhotoEntry photo, int index) {
            if (ChatAttachAlertPhotoLayoutPreview.this.draggingAnimator != null) {
                ChatAttachAlertPhotoLayoutPreview.this.draggingAnimator.cancel();
            }
            ChatAttachAlertPhotoLayoutPreview.this.draggingCell = null;
            this.draggingT = 0.0f;
            pushToGroup(groupCell, photo, index);
            updateGroups();
            toPhotoLayout(ChatAttachAlertPhotoLayoutPreview.this.photoLayout, false);
        }

        /* renamed from: lambda$onTouchEvent$4$org-telegram-ui-Components-ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView */
        public /* synthetic */ void m2502xe1c1e3e4(int currentUndoViewId) {
            if (currentUndoViewId == this.undoViewId && ChatAttachAlertPhotoLayoutPreview.this.undoView.isShown()) {
                ChatAttachAlertPhotoLayoutPreview.this.undoView.hide(true, 1);
            }
        }

        private void pushToGroup(PreviewGroupCell groupCell, MediaController.PhotoEntry photoEntry, int index) {
            groupCell.group.photos.add(Math.min(groupCell.group.photos.size(), index), photoEntry);
            if (groupCell.group.photos.size() == 11) {
                MediaController.PhotoEntry jumpPhoto = groupCell.group.photos.get(10);
                groupCell.group.photos.remove(10);
                int groupIndex = this.groupCells.indexOf(groupCell);
                if (groupIndex >= 0) {
                    PreviewGroupCell nextGroupCell = groupIndex + 1 == this.groupCells.size() ? null : this.groupCells.get(groupIndex + 1);
                    if (nextGroupCell == null) {
                        PreviewGroupCell nextGroupCell2 = new PreviewGroupCell();
                        ArrayList<MediaController.PhotoEntry> newPhotos = new ArrayList<>();
                        newPhotos.add(jumpPhoto);
                        nextGroupCell2.setGroup(new GroupCalculator(newPhotos), true);
                        invalidate();
                    } else {
                        pushToGroup(nextGroupCell, jumpPhoto, 0);
                    }
                }
            }
            groupCell.setGroup(groupCell.group, true);
        }

        private void updateGroups() {
            int groupCellsCount = this.groupCells.size();
            for (int i = 0; i < groupCellsCount; i++) {
                PreviewGroupCell groupCell = this.groupCells.get(i);
                if (groupCell.group.photos.size() < 10 && i < this.groupCells.size() - 1) {
                    int photosToTake = 10 - groupCell.group.photos.size();
                    PreviewGroupCell nextGroup = this.groupCells.get(i + 1);
                    ArrayList<MediaController.PhotoEntry> takenPhotos = new ArrayList<>();
                    int photosToTake2 = Math.min(photosToTake, nextGroup.group.photos.size());
                    for (int j = 0; j < photosToTake2; j++) {
                        takenPhotos.add(nextGroup.group.photos.remove(0));
                    }
                    groupCell.group.photos.addAll(takenPhotos);
                    groupCell.setGroup(groupCell.group, true);
                    nextGroup.setGroup(nextGroup.group, true);
                }
            }
        }

        /* loaded from: classes5.dex */
        public class PreviewGroupCell {
            private Theme.MessageDrawable.PathDrawParams backgroundCacheParams;
            private float bottom;
            final int gap;
            private GroupCalculator group;
            private float groupHeight;
            private float groupWidth;
            final int halfGap;
            private float height;
            public int indexStart;
            private Interpolator interpolator;
            private long lastMediaUpdate;
            private float left;
            public ArrayList<MediaCell> media;
            private Theme.MessageDrawable messageBackground;
            public boolean needToUpdate;
            final int padding;
            private float previousGroupHeight;
            private float previousGroupWidth;
            private float right;
            private float top;
            private final long updateDuration;
            private float width;
            public float y;

            private PreviewGroupCell() {
                PreviewGroupsView.this = r5;
                this.y = 0.0f;
                this.indexStart = 0;
                this.updateDuration = 200L;
                this.lastMediaUpdate = 0L;
                this.groupWidth = 0.0f;
                this.groupHeight = 0.0f;
                this.previousGroupWidth = 0.0f;
                this.previousGroupHeight = 0.0f;
                this.media = new ArrayList<>();
                this.interpolator = CubicBezierInterpolator.EASE_BOTH;
                this.padding = AndroidUtilities.dp(4.0f);
                int dp = AndroidUtilities.dp(2.0f);
                this.gap = dp;
                this.halfGap = dp / 2;
                this.needToUpdate = false;
                this.messageBackground = (Theme.MessageDrawable) ChatAttachAlertPhotoLayoutPreview.this.getThemedDrawable(Theme.key_drawable_msgOutMedia);
                this.backgroundCacheParams = new Theme.MessageDrawable.PathDrawParams();
            }

            /* loaded from: classes5.dex */
            public class MediaCell {
                private Paint bitmapPaint;
                private android.graphics.Rect durationIn;
                private android.graphics.Rect durationOut;
                private RectF fromRect;
                public RectF fromRoundRadiuses;
                public float fromScale;
                public PreviewGroupCell groupCell;
                public ImageReceiver image;
                private Bitmap indexBitmap;
                private String indexBitmapText;
                private android.graphics.Rect indexIn;
                private android.graphics.Rect indexOut;
                private long lastUpdate;
                private long lastVisibleTUpdate;
                private Paint paint;
                public MediaController.PhotoEntry photoEntry;
                private int positionFlags;
                public RectF rect;
                public RectF roundRadiuses;
                public float scale;
                private Paint strokePaint;
                private RectF tempRect;
                private TextPaint textPaint;
                private final long updateDuration;
                private Bitmap videoDurationBitmap;
                private String videoDurationBitmapText;
                private String videoDurationText;
                private TextPaint videoDurationTextPaint;
                private float visibleT;

                private MediaCell() {
                    PreviewGroupCell.this = r6;
                    this.groupCell = r6;
                    this.fromRect = null;
                    this.rect = new RectF();
                    this.lastUpdate = 0L;
                    this.updateDuration = 200L;
                    this.positionFlags = 0;
                    this.fromScale = 1.0f;
                    this.scale = 0.0f;
                    this.fromRoundRadiuses = null;
                    this.roundRadiuses = new RectF();
                    this.videoDurationText = null;
                    this.tempRect = new RectF();
                    this.paint = new Paint(1);
                    this.strokePaint = new Paint(1);
                    this.bitmapPaint = new Paint(1);
                    this.indexBitmap = null;
                    this.indexBitmapText = null;
                    this.videoDurationBitmap = null;
                    this.videoDurationBitmapText = null;
                    this.indexIn = new android.graphics.Rect();
                    this.indexOut = new android.graphics.Rect();
                    this.durationIn = new android.graphics.Rect();
                    this.durationOut = new android.graphics.Rect();
                    this.visibleT = 1.0f;
                    this.lastVisibleTUpdate = 0L;
                }

                public void setImage(MediaController.PhotoEntry photoEntry) {
                    this.photoEntry = photoEntry;
                    if (photoEntry == null || !photoEntry.isVideo) {
                        this.videoDurationText = null;
                    } else {
                        this.videoDurationText = AndroidUtilities.formatShortDuration(photoEntry.duration);
                    }
                    if (this.image == null) {
                        this.image = new ImageReceiver(PreviewGroupsView.this);
                    }
                    if (photoEntry != null) {
                        if (photoEntry.thumbPath != null) {
                            this.image.setImage(ImageLocation.getForPath(photoEntry.thumbPath), null, null, null, Theme.chat_attachEmptyDrawable, 0L, null, null, 0);
                        } else if (photoEntry.path != null) {
                            if (photoEntry.isVideo) {
                                ImageReceiver imageReceiver = this.image;
                                imageReceiver.setImage(ImageLocation.getForPath("vthumb://" + photoEntry.imageId + Constants.COMMON_SCHEMA_PREFIX_SEPARATOR + photoEntry.path), null, null, null, Theme.chat_attachEmptyDrawable, 0L, null, null, 0);
                                this.image.setAllowStartAnimation(true);
                                return;
                            }
                            this.image.setOrientation(photoEntry.orientation, true);
                            ImageReceiver imageReceiver2 = this.image;
                            imageReceiver2.setImage(ImageLocation.getForPath("thumb://" + photoEntry.imageId + Constants.COMMON_SCHEMA_PREFIX_SEPARATOR + photoEntry.path), null, null, null, Theme.chat_attachEmptyDrawable, 0L, null, null, 0);
                        } else {
                            this.image.setImageBitmap(Theme.chat_attachEmptyDrawable);
                        }
                    }
                }

                public void layoutFrom(MediaCell fromCell) {
                    this.fromScale = AndroidUtilities.lerp(fromCell.fromScale, fromCell.scale, fromCell.getT());
                    if (this.fromRect == null) {
                        this.fromRect = new RectF();
                    }
                    RectF myRect = new RectF();
                    RectF rectF = this.fromRect;
                    if (rectF == null) {
                        myRect.set(this.rect);
                    } else {
                        AndroidUtilities.lerp(rectF, this.rect, getT(), myRect);
                    }
                    RectF rectF2 = fromCell.fromRect;
                    if (rectF2 != null) {
                        AndroidUtilities.lerp(rectF2, fromCell.rect, fromCell.getT(), this.fromRect);
                        this.fromRect.set(myRect.centerX() - (((this.fromRect.width() / 2.0f) * fromCell.groupCell.width) / PreviewGroupCell.this.width), myRect.centerY() - (((this.fromRect.height() / 2.0f) * fromCell.groupCell.height) / PreviewGroupCell.this.height), myRect.centerX() + (((this.fromRect.width() / 2.0f) * fromCell.groupCell.width) / PreviewGroupCell.this.width), myRect.centerY() + (((this.fromRect.height() / 2.0f) * fromCell.groupCell.height) / PreviewGroupCell.this.height));
                    } else {
                        this.fromRect.set(myRect.centerX() - (((fromCell.rect.width() / 2.0f) * fromCell.groupCell.width) / PreviewGroupCell.this.width), myRect.centerY() - (((fromCell.rect.height() / 2.0f) * fromCell.groupCell.height) / PreviewGroupCell.this.height), myRect.centerX() + (((fromCell.rect.width() / 2.0f) * fromCell.groupCell.width) / PreviewGroupCell.this.width), myRect.centerY() + (((fromCell.rect.height() / 2.0f) * fromCell.groupCell.height) / PreviewGroupCell.this.height));
                    }
                    this.fromScale = AndroidUtilities.lerp(this.fromScale, this.scale, getT());
                    this.lastUpdate = SystemClock.elapsedRealtime();
                }

                public void layout(GroupCalculator group, MessageObject.GroupedMessagePosition pos, boolean animated) {
                    if (group == null || pos == null) {
                        if (animated) {
                            long now = SystemClock.elapsedRealtime();
                            this.fromScale = AndroidUtilities.lerp(this.fromScale, this.scale, getT());
                            RectF rectF = this.fromRect;
                            if (rectF != null) {
                                AndroidUtilities.lerp(rectF, this.rect, getT(), this.fromRect);
                            }
                            this.scale = 0.0f;
                            this.lastUpdate = now;
                            return;
                        }
                        this.fromScale = 0.0f;
                        this.scale = 0.0f;
                        return;
                    }
                    this.positionFlags = pos.flags;
                    if (animated) {
                        float t = getT();
                        RectF rectF2 = this.fromRect;
                        if (rectF2 != null) {
                            AndroidUtilities.lerp(rectF2, this.rect, t, rectF2);
                        }
                        RectF rectF3 = this.fromRoundRadiuses;
                        if (rectF3 != null) {
                            AndroidUtilities.lerp(rectF3, this.roundRadiuses, t, rectF3);
                        }
                        this.fromScale = AndroidUtilities.lerp(this.fromScale, this.scale, t);
                        this.lastUpdate = SystemClock.elapsedRealtime();
                    }
                    float x = pos.left / group.width;
                    float y = pos.top / group.height;
                    float w = pos.pw / group.width;
                    float h = pos.ph / group.height;
                    this.scale = 1.0f;
                    this.rect.set(x, y, x + w, y + h);
                    float r = AndroidUtilities.dp(2.0f);
                    float R = AndroidUtilities.dp(SharedConfig.bubbleRadius - 1);
                    RectF rectF4 = this.roundRadiuses;
                    int i = this.positionFlags;
                    rectF4.set((i & 5) == 5 ? R : r, (i & 6) == 6 ? R : r, (i & 10) == 10 ? R : r, (i & 9) == 9 ? R : r);
                    if (this.fromRect == null) {
                        RectF rectF5 = new RectF();
                        this.fromRect = rectF5;
                        rectF5.set(this.rect);
                    }
                    if (this.fromRoundRadiuses == null) {
                        RectF rectF6 = new RectF();
                        this.fromRoundRadiuses = rectF6;
                        rectF6.set(this.roundRadiuses);
                    }
                }

                public float getT() {
                    return PreviewGroupCell.this.interpolator.getInterpolation(Math.min(1.0f, ((float) (SystemClock.elapsedRealtime() - this.lastUpdate)) / 200.0f));
                }

                public MediaCell clone() {
                    MediaCell newMediaCell = new MediaCell();
                    newMediaCell.rect.set(this.rect);
                    newMediaCell.image = this.image;
                    newMediaCell.photoEntry = this.photoEntry;
                    return newMediaCell;
                }

                public RectF rect() {
                    return rect(getT());
                }

                public RectF rect(float t) {
                    if (this.rect != null && this.image != null) {
                        float x = PreviewGroupCell.this.left + (this.rect.left * PreviewGroupCell.this.width);
                        float y = PreviewGroupCell.this.top + (this.rect.top * PreviewGroupCell.this.height);
                        float w = this.rect.width() * PreviewGroupCell.this.width;
                        float h = this.rect.height() * PreviewGroupCell.this.height;
                        if (t < 1.0f && this.fromRect != null) {
                            x = AndroidUtilities.lerp(PreviewGroupCell.this.left + (this.fromRect.left * PreviewGroupCell.this.width), x, t);
                            y = AndroidUtilities.lerp(PreviewGroupCell.this.top + (this.fromRect.top * PreviewGroupCell.this.height), y, t);
                            w = AndroidUtilities.lerp(this.fromRect.width() * PreviewGroupCell.this.width, w, t);
                            h = AndroidUtilities.lerp(this.fromRect.height() * PreviewGroupCell.this.height, h, t);
                        }
                        if ((this.positionFlags & 4) == 0) {
                            y += PreviewGroupCell.this.halfGap;
                            h -= PreviewGroupCell.this.halfGap;
                        }
                        if ((this.positionFlags & 8) == 0) {
                            h -= PreviewGroupCell.this.halfGap;
                        }
                        if ((this.positionFlags & 1) == 0) {
                            x += PreviewGroupCell.this.halfGap;
                            w -= PreviewGroupCell.this.halfGap;
                        }
                        if ((this.positionFlags & 2) == 0) {
                            w -= PreviewGroupCell.this.halfGap;
                        }
                        this.tempRect.set(x, y, x + w, y + h);
                        return this.tempRect;
                    }
                    this.tempRect.set(0.0f, 0.0f, 0.0f, 0.0f);
                    return this.tempRect;
                }

                public RectF drawingRect() {
                    float f = 0.0f;
                    if (this.rect != null && this.image != null) {
                        if (ChatAttachAlertPhotoLayoutPreview.this.draggingCell != null && ChatAttachAlertPhotoLayoutPreview.this.draggingCell.photoEntry == this.photoEntry) {
                            f = PreviewGroupsView.this.draggingT;
                        }
                        float dragging = f;
                        float scale = AndroidUtilities.lerp(this.fromScale, this.scale, getT()) * (((1.0f - dragging) * 0.2f) + 0.8f);
                        RectF myRect = rect();
                        myRect.set(myRect.left + ((myRect.width() * (1.0f - scale)) / 2.0f), myRect.top + ((myRect.height() * (1.0f - scale)) / 2.0f), myRect.left + ((myRect.width() * (scale + 1.0f)) / 2.0f), myRect.top + ((myRect.height() * (1.0f + scale)) / 2.0f));
                        return myRect;
                    }
                    this.tempRect.set(0.0f, 0.0f, 0.0f, 0.0f);
                    return this.tempRect;
                }

                private void drawPhotoIndex(Canvas canvas, float top, float right, String indexText, float scale, float alpha) {
                    float textSize;
                    String str;
                    int radius = AndroidUtilities.dp(12.0f);
                    int strokeWidth = AndroidUtilities.dp(1.2f);
                    int sz = (radius + strokeWidth) * 2;
                    int pad = strokeWidth * 4;
                    if (indexText != null && (this.indexBitmap == null || (str = this.indexBitmapText) == null || !str.equals(indexText))) {
                        if (this.indexBitmap == null) {
                            this.indexBitmap = Bitmap.createBitmap(sz, sz, Bitmap.Config.ARGB_8888);
                        }
                        Canvas bitmapCanvas = new Canvas(this.indexBitmap);
                        bitmapCanvas.drawColor(0);
                        if (this.textPaint == null) {
                            TextPaint textPaint = new TextPaint(1);
                            this.textPaint = textPaint;
                            textPaint.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
                        }
                        this.textPaint.setColor(ChatAttachAlertPhotoLayoutPreview.this.getThemedColor(Theme.key_chat_attachCheckBoxCheck));
                        switch (indexText.length()) {
                            case 0:
                            case 1:
                            case 2:
                                textSize = 14.0f;
                                break;
                            case 3:
                                textSize = 10.0f;
                                break;
                            default:
                                textSize = 8.0f;
                                break;
                        }
                        this.textPaint.setTextSize(AndroidUtilities.dp(textSize));
                        float cx = sz / 2.0f;
                        float cy = sz / 2.0f;
                        this.paint.setColor(ChatAttachAlertPhotoLayoutPreview.this.getThemedColor(Theme.key_chat_attachCheckBoxBackground));
                        bitmapCanvas.drawCircle((int) cx, (int) cy, radius, this.paint);
                        this.strokePaint.setColor(AndroidUtilities.getOffsetColor(-1, ChatAttachAlertPhotoLayoutPreview.this.getThemedColor(Theme.key_chat_attachCheckBoxCheck), 1.0f, 1.0f));
                        this.strokePaint.setStyle(Paint.Style.STROKE);
                        this.strokePaint.setStrokeWidth(strokeWidth);
                        bitmapCanvas.drawCircle((int) cx, (int) cy, radius, this.strokePaint);
                        bitmapCanvas.drawText(indexText, cx - (this.textPaint.measureText(indexText) / 2.0f), AndroidUtilities.dp(1.0f) + cy + AndroidUtilities.dp(textSize / 4.0f), this.textPaint);
                        this.indexIn.set(0, 0, sz, sz);
                        this.indexBitmapText = indexText;
                    }
                    if (this.indexBitmap != null) {
                        this.indexOut.set((int) ((right - (sz * scale)) + pad), (int) (top - pad), (int) (right + pad), (int) ((top - pad) + (sz * scale)));
                        this.bitmapPaint.setAlpha((int) (255.0f * alpha));
                        canvas.drawBitmap(this.indexBitmap, this.indexIn, this.indexOut, this.bitmapPaint);
                    }
                }

                private void drawDuration(Canvas canvas, float left, float bottom, String durationText, float scale, float alpha) {
                    String str;
                    if (durationText != null) {
                        if (this.videoDurationBitmap == null || (str = this.videoDurationBitmapText) == null || !str.equals(durationText)) {
                            if (this.videoDurationTextPaint == null) {
                                TextPaint textPaint = new TextPaint(1);
                                this.videoDurationTextPaint = textPaint;
                                textPaint.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
                                this.videoDurationTextPaint.setColor(-1);
                            }
                            float textSize = AndroidUtilities.dp(12.0f);
                            this.videoDurationTextPaint.setTextSize(textSize);
                            float textWidth = this.videoDurationTextPaint.measureText(durationText);
                            float width = ChatAttachAlertPhotoLayoutPreview.this.videoPlayImage.getIntrinsicWidth() + textWidth + AndroidUtilities.dp(15.0f);
                            float height = Math.max(textSize, ChatAttachAlertPhotoLayoutPreview.this.videoPlayImage.getIntrinsicHeight() + AndroidUtilities.dp(4.0f));
                            int w = (int) Math.ceil(width);
                            int h = (int) Math.ceil(height);
                            Bitmap bitmap = this.videoDurationBitmap;
                            if (bitmap == null || bitmap.getWidth() != w || this.videoDurationBitmap.getHeight() != h) {
                                Bitmap bitmap2 = this.videoDurationBitmap;
                                if (bitmap2 != null) {
                                    bitmap2.recycle();
                                }
                                this.videoDurationBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                            }
                            Canvas bitmapCanvas = new Canvas(this.videoDurationBitmap);
                            AndroidUtilities.rectTmp.set(0.0f, 0.0f, width, height);
                            bitmapCanvas.drawRoundRect(AndroidUtilities.rectTmp, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), Theme.chat_timeBackgroundPaint);
                            int imageLeft = AndroidUtilities.dp(5.0f);
                            int imageTop = (int) ((height - ChatAttachAlertPhotoLayoutPreview.this.videoPlayImage.getIntrinsicHeight()) / 2.0f);
                            ChatAttachAlertPhotoLayoutPreview.this.videoPlayImage.setBounds(imageLeft, imageTop, ChatAttachAlertPhotoLayoutPreview.this.videoPlayImage.getIntrinsicWidth() + imageLeft, ChatAttachAlertPhotoLayoutPreview.this.videoPlayImage.getIntrinsicHeight() + imageTop);
                            ChatAttachAlertPhotoLayoutPreview.this.videoPlayImage.draw(bitmapCanvas);
                            bitmapCanvas.drawText(durationText, AndroidUtilities.dp(18.0f), AndroidUtilities.dp(-0.7f) + textSize, this.videoDurationTextPaint);
                            this.durationIn.set(0, 0, w, h);
                            this.videoDurationBitmapText = durationText;
                        }
                        this.durationOut.set((int) left, (int) (bottom - (this.videoDurationBitmap.getHeight() * scale)), (int) ((this.videoDurationBitmap.getWidth() * scale) + left), (int) bottom);
                        this.bitmapPaint.setAlpha((int) (255.0f * alpha));
                        canvas.drawBitmap(this.videoDurationBitmap, this.durationIn, this.durationOut, this.bitmapPaint);
                    }
                }

                public boolean draw(Canvas canvas) {
                    return draw(canvas, false);
                }

                public boolean draw(Canvas canvas, boolean ignoreBounds) {
                    return draw(canvas, getT(), ignoreBounds);
                }

                public boolean draw(Canvas canvas, float t, boolean ignoreBounds) {
                    String str;
                    float bl;
                    float br;
                    RectF rectF;
                    if (this.rect != null && this.image != null) {
                        float dragging = ChatAttachAlertPhotoLayoutPreview.this.draggingCell == this ? PreviewGroupsView.this.draggingT : 0.0f;
                        float scale = AndroidUtilities.lerp(this.fromScale, this.scale, t);
                        if (scale <= 0.0f) {
                            return false;
                        }
                        RectF drawingRect = drawingRect();
                        float R = AndroidUtilities.dp(SharedConfig.bubbleRadius - 1);
                        float tl = this.roundRadiuses.left;
                        float tr = this.roundRadiuses.top;
                        float br2 = this.roundRadiuses.right;
                        float bl2 = this.roundRadiuses.bottom;
                        if (t < 1.0f && (rectF = this.fromRoundRadiuses) != null) {
                            tl = AndroidUtilities.lerp(rectF.left, tl, t);
                            tr = AndroidUtilities.lerp(this.fromRoundRadiuses.top, tr, t);
                            br2 = AndroidUtilities.lerp(this.fromRoundRadiuses.right, br2, t);
                            bl2 = AndroidUtilities.lerp(this.fromRoundRadiuses.bottom, bl2, t);
                        }
                        float tl2 = AndroidUtilities.lerp(tl, R, dragging);
                        float tr2 = AndroidUtilities.lerp(tr, R, dragging);
                        float br3 = AndroidUtilities.lerp(br2, R, dragging);
                        float bl3 = AndroidUtilities.lerp(bl2, R, dragging);
                        if (ignoreBounds) {
                            canvas.save();
                            canvas.translate(-drawingRect.centerX(), -drawingRect.centerY());
                        }
                        this.image.setRoundRadius((int) tl2, (int) tr2, (int) br3, (int) bl3);
                        this.image.setImageCoords(drawingRect.left, drawingRect.top, drawingRect.width(), drawingRect.height());
                        this.image.setAlpha(scale);
                        this.image.draw(canvas);
                        int index = PreviewGroupCell.this.indexStart + PreviewGroupCell.this.group.photos.indexOf(this.photoEntry);
                        if (index >= 0) {
                            str = (index + 1) + "";
                        } else {
                            str = null;
                        }
                        String indexText = str;
                        float shouldVisibleT = this.image.getVisible() ? 1.0f : 0.0f;
                        boolean z = Math.abs(this.visibleT - shouldVisibleT) > 0.01f;
                        boolean needVisibleTUpdate = z;
                        if (!z) {
                            bl = bl3;
                            br = br3;
                        } else {
                            bl = bl3;
                            br = br3;
                            long tx = Math.min(17L, SystemClock.elapsedRealtime() - this.lastVisibleTUpdate);
                            this.lastVisibleTUpdate = SystemClock.elapsedRealtime();
                            float upd = ((float) tx) / 100.0f;
                            float f = this.visibleT;
                            if (shouldVisibleT < f) {
                                this.visibleT = Math.max(0.0f, f - upd);
                            } else {
                                this.visibleT = Math.min(1.0f, f + upd);
                            }
                        }
                        drawPhotoIndex(canvas, AndroidUtilities.dp(10.0f) + drawingRect.top, drawingRect.right - AndroidUtilities.dp(10.0f), indexText, scale, scale * this.visibleT);
                        drawDuration(canvas, drawingRect.left + AndroidUtilities.dp(4.0f), drawingRect.bottom - AndroidUtilities.dp(4.0f), this.videoDurationText, scale, this.visibleT * scale);
                        if (ignoreBounds) {
                            canvas.restore();
                        }
                        return t < 1.0f || needVisibleTUpdate;
                    }
                    return false;
                }
            }

            public void setGroup(GroupCalculator group, boolean animated) {
                this.group = group;
                if (group == null) {
                    return;
                }
                group.calculate();
                long now = SystemClock.elapsedRealtime();
                long j = this.lastMediaUpdate;
                if (now - j >= 200) {
                    this.previousGroupHeight = this.groupHeight;
                    this.previousGroupWidth = this.groupWidth;
                } else {
                    float t = ((float) (now - j)) / 200.0f;
                    this.previousGroupHeight = AndroidUtilities.lerp(this.previousGroupHeight, this.groupHeight, t);
                    this.previousGroupWidth = AndroidUtilities.lerp(this.previousGroupWidth, this.groupWidth, t);
                }
                this.groupWidth = group.width / 1000.0f;
                this.groupHeight = group.height;
                this.lastMediaUpdate = animated ? now : 0L;
                List<MediaController.PhotoEntry> photoEntries = new ArrayList<>(group.positions.keySet());
                int photoEntriesCount = photoEntries.size();
                for (int j2 = 0; j2 < photoEntriesCount; j2++) {
                    MediaController.PhotoEntry photoEntry = photoEntries.get(j2);
                    MessageObject.GroupedMessagePosition pos = group.positions.get(photoEntry);
                    MediaCell properCell = null;
                    int mediaCount = this.media.size();
                    int i = 0;
                    while (true) {
                        if (i >= mediaCount) {
                            break;
                        }
                        MediaCell cell = this.media.get(i);
                        if (cell.photoEntry != photoEntry) {
                            i++;
                        } else {
                            properCell = cell;
                            break;
                        }
                    }
                    if (properCell != null) {
                        properCell.layout(group, pos, animated);
                    } else {
                        MediaCell properCell2 = new MediaCell();
                        properCell2.setImage(photoEntry);
                        properCell2.layout(group, pos, animated);
                        this.media.add(properCell2);
                    }
                }
                int mediaCount2 = this.media.size();
                int i2 = 0;
                while (i2 < mediaCount2) {
                    MediaCell cell2 = this.media.get(i2);
                    if (!group.positions.containsKey(cell2.photoEntry)) {
                        if (cell2.scale <= 0.0f && cell2.lastUpdate + 200 <= now) {
                            this.media.remove(i2);
                            i2--;
                            mediaCount2--;
                        }
                        cell2.layout(null, null, animated);
                    }
                    i2++;
                }
                PreviewGroupsView.this.invalidate();
            }

            public float getT() {
                return this.interpolator.getInterpolation(Math.min(1.0f, ((float) (SystemClock.elapsedRealtime() - this.lastMediaUpdate)) / 200.0f));
            }

            public float measure() {
                float maxHeight = Math.max(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) * 0.5f;
                return AndroidUtilities.lerp(this.previousGroupHeight, this.groupHeight, getT()) * maxHeight * ChatAttachAlertPhotoLayoutPreview.this.getPreviewScale();
            }

            public float maxHeight() {
                float maxHeight = Math.max(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) * 0.5f;
                return getT() >= 0.95f ? this.groupHeight * maxHeight * ChatAttachAlertPhotoLayoutPreview.this.getPreviewScale() : measure();
            }

            public void invalidate() {
                this.needToUpdate = true;
            }

            public boolean draw(Canvas canvas) {
                boolean update = false;
                float t = this.interpolator.getInterpolation(Math.min(1.0f, ((float) (SystemClock.elapsedRealtime() - this.lastMediaUpdate)) / 200.0f));
                if (t < 1.0f) {
                    update = true;
                }
                float maxHeight = Math.max(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) * 0.5f;
                float groupWidth = AndroidUtilities.lerp(this.previousGroupWidth, this.groupWidth, t) * PreviewGroupsView.this.getWidth() * ChatAttachAlertPhotoLayoutPreview.this.getPreviewScale();
                float groupHeight = AndroidUtilities.lerp(this.previousGroupHeight, this.groupHeight, t) * maxHeight * ChatAttachAlertPhotoLayoutPreview.this.getPreviewScale();
                if (this.messageBackground != null) {
                    this.top = 0.0f;
                    this.left = (PreviewGroupsView.this.getWidth() - Math.max(this.padding, groupWidth)) / 2.0f;
                    this.right = (PreviewGroupsView.this.getWidth() + Math.max(this.padding, groupWidth)) / 2.0f;
                    this.bottom = Math.max(this.padding * 2, groupHeight);
                    this.messageBackground.setTop(0, (int) groupWidth, (int) groupHeight, 0, 0, 0, false, false);
                    this.messageBackground.setBounds((int) this.left, (int) this.top, (int) this.right, (int) this.bottom);
                    float alpha = 1.0f;
                    if (this.groupWidth > 0.0f) {
                        if (this.previousGroupWidth <= 0.0f) {
                            alpha = t;
                        }
                    } else {
                        alpha = 1.0f - t;
                    }
                    this.messageBackground.setAlpha((int) (255.0f * alpha));
                    this.messageBackground.drawCached(canvas, this.backgroundCacheParams);
                    float f = this.top;
                    int i = this.padding;
                    this.top = f + i;
                    this.left += i;
                    this.bottom -= i;
                    this.right -= i;
                }
                this.width = this.right - this.left;
                this.height = this.bottom - this.top;
                int count = this.media.size();
                for (int i2 = 0; i2 < count; i2++) {
                    MediaCell cell = this.media.get(i2);
                    if (cell != null && ((ChatAttachAlertPhotoLayoutPreview.this.draggingCell == null || ChatAttachAlertPhotoLayoutPreview.this.draggingCell.photoEntry != cell.photoEntry) && cell.draw(canvas))) {
                        update = true;
                    }
                }
                return update;
            }
        }
    }

    public Drawable getThemedDrawable(String drawableKey) {
        Drawable drawable = this.themeDelegate.getDrawable(drawableKey);
        return drawable != null ? drawable : Theme.getThemeDrawable(drawableKey);
    }
}
