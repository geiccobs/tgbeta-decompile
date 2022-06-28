package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.C;
import com.microsoft.appcenter.ingestion.models.CommonProperties;
import j$.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.CalendarActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.Easings;
import org.telegram.ui.Components.HideViewAfterAnimation;
import org.telegram.ui.Components.HintView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
/* loaded from: classes4.dex */
public class CalendarActivity extends BaseFragment {
    public static final int TYPE_CHAT_ACTIVITY = 0;
    public static final int TYPE_MEDIA_CALENDAR = 1;
    CalendarAdapter adapter;
    BackDrawable backDrawable;
    private View blurredView;
    private FrameLayout bottomBar;
    private int calendarType;
    Callback callback;
    private boolean canClearHistory;
    private boolean checkEnterItems;
    FrameLayout contentView;
    private int dateSelectedEnd;
    private int dateSelectedStart;
    private long dialogId;
    boolean endReached;
    private boolean inSelectionMode;
    private boolean isOpened;
    int lastDaysSelected;
    int lastId;
    boolean lastInSelectionMode;
    LinearLayoutManager layoutManager;
    RecyclerListView listView;
    private boolean loading;
    private int minDate;
    int minMontYear;
    int monthCount;
    private int photosVideosTypeFilter;
    TextView removeDaysButton;
    TextView selectDaysButton;
    HintView selectDaysHint;
    int selectedMonth;
    int selectedYear;
    private ValueAnimator selectionAnimator;
    int startFromMonth;
    int startFromYear;
    TextPaint textPaint = new TextPaint(1);
    TextPaint activeTextPaint = new TextPaint(1);
    TextPaint textPaint2 = new TextPaint(1);
    private Paint selectOutlinePaint = new Paint(1);
    private Paint selectPaint = new Paint(1);
    Paint blackoutPaint = new Paint(1);
    SparseArray<SparseArray<PeriodDay>> messagesByYearMounth = new SparseArray<>();
    int startOffset = 0;

    /* loaded from: classes4.dex */
    public interface Callback {
        void onDateSelected(int i, int i2);
    }

    public CalendarActivity(Bundle args, int photosVideosTypeFilter, int selectedDate) {
        super(args);
        this.photosVideosTypeFilter = photosVideosTypeFilter;
        if (selectedDate != 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(selectedDate * 1000);
            this.selectedYear = calendar.get(1);
            this.selectedMonth = calendar.get(2);
        }
        this.selectOutlinePaint.setStyle(Paint.Style.STROKE);
        this.selectOutlinePaint.setStrokeCap(Paint.Cap.ROUND);
        this.selectOutlinePaint.setStrokeWidth(AndroidUtilities.dp(2.0f));
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        this.dialogId = getArguments().getLong("dialog_id");
        this.calendarType = getArguments().getInt(CommonProperties.TYPE);
        if (this.dialogId >= 0) {
            this.canClearHistory = true;
        } else {
            this.canClearHistory = false;
        }
        return super.onFragmentCreate();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        this.textPaint.setTextSize(AndroidUtilities.dp(16.0f));
        this.textPaint.setTextAlign(Paint.Align.CENTER);
        this.textPaint2.setTextSize(AndroidUtilities.dp(11.0f));
        this.textPaint2.setTextAlign(Paint.Align.CENTER);
        this.textPaint2.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.activeTextPaint.setTextSize(AndroidUtilities.dp(16.0f));
        this.activeTextPaint.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.activeTextPaint.setTextAlign(Paint.Align.CENTER);
        this.contentView = new FrameLayout(context) { // from class: org.telegram.ui.CalendarActivity.1
            int lastSize;

            @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                int size = (getMeasuredHeight() + getMeasuredWidth()) << 16;
                if (this.lastSize != size) {
                    this.lastSize = size;
                    CalendarActivity.this.adapter.notifyDataSetChanged();
                }
            }
        };
        createActionBar(context);
        this.contentView.addView(this.actionBar);
        this.actionBar.setTitle(LocaleController.getString("Calendar", R.string.Calendar));
        this.actionBar.setCastShadows(false);
        RecyclerListView recyclerListView = new RecyclerListView(context) { // from class: org.telegram.ui.CalendarActivity.2
            @Override // org.telegram.ui.Components.RecyclerListView, android.view.ViewGroup, android.view.View
            public void dispatchDraw(Canvas canvas) {
                super.dispatchDraw(canvas);
                CalendarActivity.this.checkEnterItems = false;
            }
        };
        this.listView = recyclerListView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        this.layoutManager = linearLayoutManager;
        recyclerListView.setLayoutManager(linearLayoutManager);
        this.layoutManager.setReverseLayout(true);
        RecyclerListView recyclerListView2 = this.listView;
        CalendarAdapter calendarAdapter = new CalendarAdapter();
        this.adapter = calendarAdapter;
        recyclerListView2.setAdapter(calendarAdapter);
        this.listView.addOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.CalendarActivity.3
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                CalendarActivity.this.checkLoadNext();
            }
        });
        boolean showBottomPanel = this.calendarType == 0 && this.canClearHistory;
        this.contentView.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f, 0, 0.0f, 36.0f, 0.0f, showBottomPanel ? 48.0f : 0.0f));
        final String[] daysOfWeek = {LocaleController.getString("CalendarWeekNameShortMonday", R.string.CalendarWeekNameShortMonday), LocaleController.getString("CalendarWeekNameShortTuesday", R.string.CalendarWeekNameShortTuesday), LocaleController.getString("CalendarWeekNameShortWednesday", R.string.CalendarWeekNameShortWednesday), LocaleController.getString("CalendarWeekNameShortThursday", R.string.CalendarWeekNameShortThursday), LocaleController.getString("CalendarWeekNameShortFriday", R.string.CalendarWeekNameShortFriday), LocaleController.getString("CalendarWeekNameShortSaturday", R.string.CalendarWeekNameShortSaturday), LocaleController.getString("CalendarWeekNameShortSunday", R.string.CalendarWeekNameShortSunday)};
        final Drawable headerShadowDrawable = ContextCompat.getDrawable(context, R.drawable.header_shadow).mutate();
        View calendarSignatureView = new View(context) { // from class: org.telegram.ui.CalendarActivity.4
            @Override // android.view.View
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                float xStep = getMeasuredWidth() / 7.0f;
                for (int i = 0; i < 7; i++) {
                    float cx = (i * xStep) + (xStep / 2.0f);
                    float cy = (getMeasuredHeight() - AndroidUtilities.dp(2.0f)) / 2.0f;
                    canvas.drawText(daysOfWeek[i], cx, AndroidUtilities.dp(5.0f) + cy, CalendarActivity.this.textPaint2);
                }
                headerShadowDrawable.setBounds(0, getMeasuredHeight() - AndroidUtilities.dp(3.0f), getMeasuredWidth(), getMeasuredHeight());
                headerShadowDrawable.draw(canvas);
            }
        };
        this.contentView.addView(calendarSignatureView, LayoutHelper.createFrame(-1, 38.0f, 0, 0.0f, 0.0f, 0.0f, 0.0f));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.CalendarActivity.5
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int id) {
                if (id == -1) {
                    if (CalendarActivity.this.dateSelectedStart != 0 || CalendarActivity.this.dateSelectedEnd != 0 || CalendarActivity.this.inSelectionMode) {
                        CalendarActivity.this.inSelectionMode = false;
                        CalendarActivity.this.dateSelectedStart = 0;
                        CalendarActivity.this.dateSelectedEnd = 0;
                        CalendarActivity.this.updateTitle();
                        CalendarActivity.this.animateSelection();
                        return;
                    }
                    CalendarActivity.this.finishFragment();
                }
            }
        });
        this.fragmentView = this.contentView;
        Calendar calendar = Calendar.getInstance();
        this.startFromYear = calendar.get(1);
        int i = calendar.get(2);
        this.startFromMonth = i;
        int i2 = this.selectedYear;
        if (i2 != 0) {
            int i3 = ((((this.startFromYear - i2) * 12) + i) - this.selectedMonth) + 1;
            this.monthCount = i3;
            this.layoutManager.scrollToPositionWithOffset(i3 - 1, AndroidUtilities.dp(120.0f));
        }
        if (this.monthCount < 3) {
            this.monthCount = 3;
        }
        this.backDrawable = new BackDrawable(false);
        this.actionBar.setBackButtonDrawable(this.backDrawable);
        this.backDrawable.setRotation(0.0f, false);
        loadNext();
        updateColors();
        this.activeTextPaint.setColor(-1);
        if (showBottomPanel) {
            FrameLayout frameLayout = new FrameLayout(context) { // from class: org.telegram.ui.CalendarActivity.6
                @Override // android.view.View
                public void onDraw(Canvas canvas) {
                    canvas.drawRect(0.0f, 0.0f, getMeasuredWidth(), AndroidUtilities.getShadowHeight(), Theme.dividerPaint);
                }
            };
            this.bottomBar = frameLayout;
            frameLayout.setWillNotDraw(false);
            this.bottomBar.setPadding(0, AndroidUtilities.getShadowHeight(), 0, 0);
            this.bottomBar.setClipChildren(false);
            TextView textView = new TextView(context);
            this.selectDaysButton = textView;
            textView.setGravity(17);
            this.selectDaysButton.setTextSize(1, 15.0f);
            this.selectDaysButton.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            this.selectDaysButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.CalendarActivity$$ExternalSyntheticLambda1
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    CalendarActivity.this.m1583lambda$createView$0$orgtelegramuiCalendarActivity(view);
                }
            });
            this.selectDaysButton.setText(LocaleController.getString("SelectDays", R.string.SelectDays));
            this.selectDaysButton.setAllCaps(true);
            this.bottomBar.addView(this.selectDaysButton, LayoutHelper.createFrame(-1, -1.0f, 0, 0.0f, 0.0f, 0.0f, 0.0f));
            TextView textView2 = new TextView(context);
            this.removeDaysButton = textView2;
            textView2.setGravity(17);
            this.removeDaysButton.setTextSize(1, 15.0f);
            this.removeDaysButton.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            this.removeDaysButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.CalendarActivity$$ExternalSyntheticLambda2
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    CalendarActivity.this.m1584lambda$createView$1$orgtelegramuiCalendarActivity(view);
                }
            });
            this.removeDaysButton.setAllCaps(true);
            this.removeDaysButton.setVisibility(8);
            this.bottomBar.addView(this.removeDaysButton, LayoutHelper.createFrame(-1, -1.0f, 0, 0.0f, 0.0f, 0.0f, 0.0f));
            this.contentView.addView(this.bottomBar, LayoutHelper.createFrame(-1, 48.0f, 80, 0.0f, 0.0f, 0.0f, 0.0f));
            this.selectDaysButton.setBackground(Theme.createSelectorDrawable(ColorUtils.setAlphaComponent(Theme.getColor(Theme.key_chat_fieldOverlayText), 51), 2));
            this.removeDaysButton.setBackground(Theme.createSelectorDrawable(ColorUtils.setAlphaComponent(Theme.getColor(Theme.key_dialogTextRed), 51), 2));
            this.selectDaysButton.setTextColor(Theme.getColor(Theme.key_chat_fieldOverlayText));
            this.removeDaysButton.setTextColor(Theme.getColor(Theme.key_dialogTextRed));
        }
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-CalendarActivity */
    public /* synthetic */ void m1583lambda$createView$0$orgtelegramuiCalendarActivity(View view) {
        this.inSelectionMode = true;
        updateTitle();
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-CalendarActivity */
    public /* synthetic */ void m1584lambda$createView$1$orgtelegramuiCalendarActivity(View view) {
        int i = this.lastDaysSelected;
        if (i == 0) {
            if (this.selectDaysHint == null) {
                HintView hintView = new HintView(this.contentView.getContext(), 8);
                this.selectDaysHint = hintView;
                hintView.setExtraTranslationY(AndroidUtilities.dp(24.0f));
                this.contentView.addView(this.selectDaysHint, LayoutHelper.createFrame(-2, -2.0f, 51, 19.0f, 0.0f, 19.0f, 0.0f));
                this.selectDaysHint.setText(LocaleController.getString("SelectDaysTooltip", R.string.SelectDaysTooltip));
            }
            this.selectDaysHint.showForView(this.bottomBar, true);
            return;
        }
        AlertsCreator.createClearDaysDialogAlert(this, i, getMessagesController().getUser(Long.valueOf(this.dialogId)), null, false, new MessagesStorage.BooleanCallback() { // from class: org.telegram.ui.CalendarActivity.7
            @Override // org.telegram.messenger.MessagesStorage.BooleanCallback
            public void run(boolean forAll) {
                CalendarActivity.this.finishFragment();
                if (CalendarActivity.this.parentLayout.fragmentsStack.size() >= 2) {
                    BaseFragment fragment = CalendarActivity.this.parentLayout.fragmentsStack.get(CalendarActivity.this.parentLayout.fragmentsStack.size() - 2);
                    if (fragment instanceof ChatActivity) {
                        ((ChatActivity) fragment).deleteHistory(CalendarActivity.this.dateSelectedStart, CalendarActivity.this.dateSelectedEnd + 86400, forAll);
                    }
                }
            }
        }, null);
    }

    public void updateColors() {
        this.actionBar.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.activeTextPaint.setColor(-1);
        this.textPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.textPaint2.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.actionBar.setTitleColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.backDrawable.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.actionBar.setItemsColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_listSelector), false);
    }

    private void loadNext() {
        if (this.loading || this.endReached) {
            return;
        }
        this.loading = true;
        TLRPC.TL_messages_getSearchResultsCalendar req = new TLRPC.TL_messages_getSearchResultsCalendar();
        int i = this.photosVideosTypeFilter;
        if (i == 1) {
            req.filter = new TLRPC.TL_inputMessagesFilterPhotos();
        } else if (i == 2) {
            req.filter = new TLRPC.TL_inputMessagesFilterVideo();
        } else {
            req.filter = new TLRPC.TL_inputMessagesFilterPhotoVideo();
        }
        req.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(this.dialogId);
        req.offset_id = this.lastId;
        final Calendar calendar = Calendar.getInstance();
        this.listView.setItemAnimator(null);
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.CalendarActivity$$ExternalSyntheticLambda4
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                CalendarActivity.this.m1586lambda$loadNext$3$orgtelegramuiCalendarActivity(calendar, tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$loadNext$3$org-telegram-ui-CalendarActivity */
    public /* synthetic */ void m1586lambda$loadNext$3$orgtelegramuiCalendarActivity(final Calendar calendar, final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.CalendarActivity$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                CalendarActivity.this.m1585lambda$loadNext$2$orgtelegramuiCalendarActivity(error, response, calendar);
            }
        });
    }

    /* renamed from: lambda$loadNext$2$org-telegram-ui-CalendarActivity */
    public /* synthetic */ void m1585lambda$loadNext$2$orgtelegramuiCalendarActivity(TLRPC.TL_error error, TLObject response, Calendar calendar) {
        int i;
        int i2;
        if (error == null) {
            TLRPC.TL_messages_searchResultsCalendar res = (TLRPC.TL_messages_searchResultsCalendar) response;
            int i3 = 0;
            while (true) {
                i = 5;
                i2 = 2;
                if (i3 >= res.periods.size()) {
                    break;
                }
                TLRPC.TL_searchResultsCalendarPeriod period = res.periods.get(i3);
                calendar.setTimeInMillis(period.date * 1000);
                int month = (calendar.get(1) * 100) + calendar.get(2);
                SparseArray<PeriodDay> messagesByDays = this.messagesByYearMounth.get(month);
                if (messagesByDays == null) {
                    messagesByDays = new SparseArray<>();
                    this.messagesByYearMounth.put(month, messagesByDays);
                }
                PeriodDay periodDay = new PeriodDay();
                MessageObject messageObject = new MessageObject(this.currentAccount, res.messages.get(i3), false, false);
                periodDay.messageObject = messageObject;
                periodDay.date = (int) (calendar.getTimeInMillis() / 1000);
                int i4 = this.startOffset + res.periods.get(i3).count;
                this.startOffset = i4;
                periodDay.startOffset = i4;
                int index = calendar.get(5) - 1;
                if (messagesByDays.get(index, null) == null || !messagesByDays.get(index, null).hasImage) {
                    messagesByDays.put(index, periodDay);
                }
                int i5 = this.minMontYear;
                if (month < i5 || i5 == 0) {
                    this.minMontYear = month;
                }
                i3++;
            }
            int maxDate = (int) (System.currentTimeMillis() / 1000);
            this.minDate = res.min_date;
            int date = res.min_date;
            while (date < maxDate) {
                calendar.setTimeInMillis(date * 1000);
                calendar.set(11, 0);
                calendar.set(12, 0);
                calendar.set(13, 0);
                calendar.set(14, 0);
                int month2 = (calendar.get(1) * 100) + calendar.get(i2);
                SparseArray<PeriodDay> messagesByDays2 = this.messagesByYearMounth.get(month2);
                if (messagesByDays2 == null) {
                    messagesByDays2 = new SparseArray<>();
                    this.messagesByYearMounth.put(month2, messagesByDays2);
                }
                int index2 = calendar.get(i) - 1;
                if (messagesByDays2.get(index2, null) == null) {
                    PeriodDay periodDay2 = new PeriodDay();
                    periodDay2.hasImage = false;
                    periodDay2.date = (int) (calendar.getTimeInMillis() / 1000);
                    messagesByDays2.put(index2, periodDay2);
                }
                date += 86400;
                i = 5;
                i2 = 2;
            }
            this.loading = false;
            if (!res.messages.isEmpty()) {
                this.lastId = res.messages.get(res.messages.size() - 1).id;
                this.endReached = false;
                checkLoadNext();
            } else {
                this.endReached = true;
            }
            if (this.isOpened) {
                this.checkEnterItems = true;
            }
            this.listView.invalidate();
            int newMonthCount = ((int) (((calendar.getTimeInMillis() / 1000) - res.min_date) / 2629800)) + 1;
            this.adapter.notifyItemRangeChanged(0, this.monthCount);
            int i6 = this.monthCount;
            if (newMonthCount > i6) {
                this.adapter.notifyItemRangeInserted(i6 + 1, newMonthCount);
                this.monthCount = newMonthCount;
            }
            if (this.endReached) {
                resumeDelayedFragmentAnimation();
            }
        }
    }

    public void checkLoadNext() {
        int currentMonth;
        if (this.loading || this.endReached) {
            return;
        }
        int listMinMonth = Integer.MAX_VALUE;
        for (int i = 0; i < this.listView.getChildCount(); i++) {
            View child = this.listView.getChildAt(i);
            if ((child instanceof MonthView) && (currentMonth = (((MonthView) child).currentYear * 100) + ((MonthView) child).currentMonthInYear) < listMinMonth) {
                listMinMonth = currentMonth;
            }
        }
        int i2 = this.minMontYear;
        int min1 = ((i2 / 100) * 12) + (i2 % 100);
        int min2 = ((listMinMonth / 100) * 12) + (listMinMonth % 100);
        if (min1 + 3 >= min2) {
            loadNext();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class CalendarAdapter extends RecyclerView.Adapter {
        private CalendarAdapter() {
            CalendarActivity.this = r1;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerListView.Holder(new MonthView(parent.getContext()));
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            MonthView monthView = (MonthView) holder.itemView;
            int year = CalendarActivity.this.startFromYear - (position / 12);
            int month = CalendarActivity.this.startFromMonth - (position % 12);
            if (month < 0) {
                month += 12;
                year--;
            }
            boolean animated = monthView.currentYear == year && monthView.currentMonthInYear == month;
            monthView.setDate(year, month, CalendarActivity.this.messagesByYearMounth.get((year * 100) + month), animated);
            monthView.startSelectionAnimation(CalendarActivity.this.dateSelectedStart, CalendarActivity.this.dateSelectedEnd);
            monthView.setSelectionValue(1.0f);
            CalendarActivity.this.updateRowSelections(monthView, false);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public long getItemId(int position) {
            int year = CalendarActivity.this.startFromYear - (position / 12);
            int month = CalendarActivity.this.startFromMonth - (position % 12);
            return (year * 100) + month;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return CalendarActivity.this.monthCount;
        }
    }

    /* loaded from: classes4.dex */
    public class MonthView extends FrameLayout {
        boolean attached;
        int cellCount;
        int currentMonthInYear;
        int currentYear;
        int daysInMonth;
        GestureDetectorCompat gestureDetector;
        int startDayOfWeek;
        int startMonthTime;
        SimpleTextView titleView;
        SparseArray<PeriodDay> messagesByDays = new SparseArray<>();
        SparseArray<ImageReceiver> imagesByDays = new SparseArray<>();
        private SparseArray<ValueAnimator> rowAnimators = new SparseArray<>();
        private SparseArray<RowAnimationValue> rowSelectionPos = new SparseArray<>();

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public MonthView(Context context) {
            super(context);
            CalendarActivity.this = r10;
            boolean z = false;
            setWillNotDraw(false);
            this.titleView = new SimpleTextView(context);
            if (r10.calendarType == 0 && r10.canClearHistory) {
                this.titleView.setOnLongClickListener(new View.OnLongClickListener() { // from class: org.telegram.ui.CalendarActivity$MonthView$$ExternalSyntheticLambda1
                    @Override // android.view.View.OnLongClickListener
                    public final boolean onLongClick(View view) {
                        return CalendarActivity.MonthView.this.m1588lambda$new$0$orgtelegramuiCalendarActivity$MonthView(view);
                    }
                });
                this.titleView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.CalendarActivity.MonthView.1
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view) {
                        if (MonthView.this.messagesByDays != null && CalendarActivity.this.inSelectionMode) {
                            int start = -1;
                            int end = -1;
                            for (int i = 0; i < MonthView.this.daysInMonth; i++) {
                                PeriodDay day = MonthView.this.messagesByDays.get(i, null);
                                if (day != null) {
                                    if (start == -1) {
                                        start = day.date;
                                    }
                                    end = day.date;
                                }
                            }
                            if (start >= 0 && end >= 0) {
                                CalendarActivity.this.dateSelectedStart = start;
                                CalendarActivity.this.dateSelectedEnd = end;
                                CalendarActivity.this.updateTitle();
                                CalendarActivity.this.animateSelection();
                            }
                        }
                    }
                });
            }
            this.titleView.setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_listSelector), 2));
            this.titleView.setTextSize(15);
            this.titleView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            this.titleView.setGravity(17);
            this.titleView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            addView(this.titleView, LayoutHelper.createFrame(-1, 28.0f, 0, 0.0f, 12.0f, 0.0f, 4.0f));
            GestureDetectorCompat gestureDetectorCompat = new GestureDetectorCompat(context, new AnonymousClass2(r10, context));
            this.gestureDetector = gestureDetectorCompat;
            gestureDetectorCompat.setIsLongpressEnabled(r10.calendarType == 0 ? true : z);
        }

        /* renamed from: lambda$new$0$org-telegram-ui-CalendarActivity$MonthView */
        public /* synthetic */ boolean m1588lambda$new$0$orgtelegramuiCalendarActivity$MonthView(View view) {
            if (this.messagesByDays == null) {
                return false;
            }
            int start = -1;
            int end = -1;
            for (int i = 0; i < this.daysInMonth; i++) {
                PeriodDay day = this.messagesByDays.get(i, null);
                if (day != null) {
                    if (start == -1) {
                        start = day.date;
                    }
                    end = day.date;
                }
            }
            if (start >= 0 && end >= 0) {
                CalendarActivity.this.inSelectionMode = true;
                CalendarActivity.this.dateSelectedStart = start;
                CalendarActivity.this.dateSelectedEnd = end;
                CalendarActivity.this.updateTitle();
                CalendarActivity.this.animateSelection();
            }
            return false;
        }

        /* renamed from: org.telegram.ui.CalendarActivity$MonthView$2 */
        /* loaded from: classes4.dex */
        public class AnonymousClass2 extends GestureDetector.SimpleOnGestureListener {
            final /* synthetic */ Context val$context;
            final /* synthetic */ CalendarActivity val$this$0;

            AnonymousClass2(CalendarActivity calendarActivity, Context context) {
                MonthView.this = this$1;
                this.val$this$0 = calendarActivity;
                this.val$context = context;
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public boolean onSingleTapUp(MotionEvent e) {
                PeriodDay day;
                if (CalendarActivity.this.calendarType == 1 && MonthView.this.messagesByDays != null && (day = getDayAtCoord(e.getX(), e.getY())) != null && day.messageObject != null && CalendarActivity.this.callback != null) {
                    CalendarActivity.this.callback.onDateSelected(day.messageObject.getId(), day.startOffset);
                    CalendarActivity.this.finishFragment();
                }
                if (MonthView.this.messagesByDays != null) {
                    if (CalendarActivity.this.inSelectionMode) {
                        PeriodDay day2 = getDayAtCoord(e.getX(), e.getY());
                        if (day2 != null) {
                            if (CalendarActivity.this.selectionAnimator != null) {
                                CalendarActivity.this.selectionAnimator.cancel();
                                CalendarActivity.this.selectionAnimator = null;
                            }
                            if (CalendarActivity.this.dateSelectedStart != 0 || CalendarActivity.this.dateSelectedEnd != 0) {
                                if (CalendarActivity.this.dateSelectedStart != day2.date || CalendarActivity.this.dateSelectedEnd != day2.date) {
                                    if (CalendarActivity.this.dateSelectedStart == day2.date) {
                                        CalendarActivity.this.dateSelectedStart = CalendarActivity.this.dateSelectedEnd;
                                    } else if (CalendarActivity.this.dateSelectedEnd == day2.date) {
                                        CalendarActivity.this.dateSelectedEnd = CalendarActivity.this.dateSelectedStart;
                                    } else if (CalendarActivity.this.dateSelectedStart == CalendarActivity.this.dateSelectedEnd) {
                                        if (day2.date > CalendarActivity.this.dateSelectedEnd) {
                                            CalendarActivity.this.dateSelectedEnd = day2.date;
                                        } else {
                                            CalendarActivity.this.dateSelectedStart = day2.date;
                                        }
                                    } else {
                                        CalendarActivity.this.dateSelectedStart = CalendarActivity.this.dateSelectedEnd = day2.date;
                                    }
                                } else {
                                    CalendarActivity.this.dateSelectedStart = CalendarActivity.this.dateSelectedEnd = 0;
                                }
                            } else {
                                CalendarActivity.this.dateSelectedStart = CalendarActivity.this.dateSelectedEnd = day2.date;
                            }
                            CalendarActivity.this.updateTitle();
                            CalendarActivity.this.animateSelection();
                        }
                    } else {
                        PeriodDay day3 = getDayAtCoord(e.getX(), e.getY());
                        if (day3 != null && CalendarActivity.this.parentLayout.fragmentsStack.size() >= 2) {
                            BaseFragment fragment = CalendarActivity.this.parentLayout.fragmentsStack.get(CalendarActivity.this.parentLayout.fragmentsStack.size() - 2);
                            if (fragment instanceof ChatActivity) {
                                CalendarActivity.this.finishFragment();
                                ((ChatActivity) fragment).jumpToDate(day3.date);
                            }
                        }
                    }
                }
                return false;
            }

            private PeriodDay getDayAtCoord(float pressedX, float pressedY) {
                PeriodDay day;
                if (MonthView.this.messagesByDays == null) {
                    return null;
                }
                int currentCell = 0;
                int currentColumn = MonthView.this.startDayOfWeek;
                float xStep = MonthView.this.getMeasuredWidth() / 7.0f;
                float yStep = AndroidUtilities.dp(52.0f);
                int hrad = AndroidUtilities.dp(44.0f) / 2;
                for (int i = 0; i < MonthView.this.daysInMonth; i++) {
                    float cx = (currentColumn * xStep) + (xStep / 2.0f);
                    float cy = (currentCell * yStep) + (yStep / 2.0f) + AndroidUtilities.dp(44.0f);
                    if (pressedX >= cx - hrad && pressedX <= hrad + cx && pressedY >= cy - hrad && pressedY <= hrad + cy && (day = MonthView.this.messagesByDays.get(i, null)) != null) {
                        return day;
                    }
                    currentColumn++;
                    if (currentColumn >= 7) {
                        currentColumn = 0;
                        currentCell++;
                    }
                }
                return null;
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public void onLongPress(MotionEvent e) {
                final PeriodDay periodDay;
                super.onLongPress(e);
                if (CalendarActivity.this.calendarType == 0 && (periodDay = getDayAtCoord(e.getX(), e.getY())) != null) {
                    MonthView.this.performHapticFeedback(0);
                    Bundle bundle = new Bundle();
                    if (CalendarActivity.this.dialogId > 0) {
                        bundle.putLong("user_id", CalendarActivity.this.dialogId);
                    } else {
                        bundle.putLong(ChatReactionsEditActivity.KEY_CHAT_ID, -CalendarActivity.this.dialogId);
                    }
                    bundle.putInt("start_from_date", periodDay.date);
                    bundle.putBoolean("need_remove_previous_same_chat_activity", false);
                    ChatActivity chatActivity = new ChatActivity(bundle);
                    ActionBarPopupWindow.ActionBarPopupWindowLayout previewMenu = new ActionBarPopupWindow.ActionBarPopupWindowLayout(CalendarActivity.this.getParentActivity(), R.drawable.popup_fixed_alert, CalendarActivity.this.getResourceProvider());
                    previewMenu.setBackgroundColor(CalendarActivity.this.getThemedColor(Theme.key_actionBarDefaultSubmenuBackground));
                    ActionBarMenuSubItem cellJump = new ActionBarMenuSubItem(CalendarActivity.this.getParentActivity(), true, false);
                    cellJump.setTextAndIcon(LocaleController.getString("JumpToDate", R.string.JumpToDate), R.drawable.msg_message);
                    cellJump.setMinimumWidth(160);
                    cellJump.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.CalendarActivity$MonthView$2$$ExternalSyntheticLambda2
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view) {
                            CalendarActivity.MonthView.AnonymousClass2.this.m1590x44edf01e(periodDay, view);
                        }
                    });
                    previewMenu.addView(cellJump);
                    if (CalendarActivity.this.canClearHistory) {
                        ActionBarMenuSubItem cellSelect = new ActionBarMenuSubItem(CalendarActivity.this.getParentActivity(), false, false);
                        cellSelect.setTextAndIcon(LocaleController.getString("SelectThisDay", R.string.SelectThisDay), R.drawable.msg_select);
                        cellSelect.setMinimumWidth(160);
                        cellSelect.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.CalendarActivity$MonthView$2$$ExternalSyntheticLambda3
                            @Override // android.view.View.OnClickListener
                            public final void onClick(View view) {
                                CalendarActivity.MonthView.AnonymousClass2.this.m1591xfe657dbd(periodDay, view);
                            }
                        });
                        previewMenu.addView(cellSelect);
                        ActionBarMenuSubItem cellDelete = new ActionBarMenuSubItem(CalendarActivity.this.getParentActivity(), false, true);
                        cellDelete.setTextAndIcon(LocaleController.getString("ClearHistory", R.string.ClearHistory), R.drawable.msg_delete);
                        cellDelete.setMinimumWidth(160);
                        cellDelete.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.CalendarActivity$MonthView$2$$ExternalSyntheticLambda0
                            @Override // android.view.View.OnClickListener
                            public final void onClick(View view) {
                                CalendarActivity.MonthView.AnonymousClass2.this.m1592xb7dd0b5c(view);
                            }
                        });
                        previewMenu.addView(cellDelete);
                    }
                    previewMenu.setFitItems(true);
                    CalendarActivity.this.blurredView = new View(this.val$context) { // from class: org.telegram.ui.CalendarActivity.MonthView.2.2
                        @Override // android.view.View
                        public void setAlpha(float alpha) {
                            super.setAlpha(alpha);
                            if (CalendarActivity.this.fragmentView != null) {
                                CalendarActivity.this.fragmentView.invalidate();
                            }
                        }
                    };
                    CalendarActivity.this.blurredView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.CalendarActivity$MonthView$2$$ExternalSyntheticLambda1
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view) {
                            CalendarActivity.MonthView.AnonymousClass2.this.m1593x715498fb(view);
                        }
                    });
                    CalendarActivity.this.blurredView.setVisibility(8);
                    CalendarActivity.this.blurredView.setFitsSystemWindows(true);
                    CalendarActivity.this.parentLayout.containerView.addView(CalendarActivity.this.blurredView, LayoutHelper.createFrame(-1, -1.0f));
                    CalendarActivity.this.prepareBlurBitmap();
                    CalendarActivity.this.presentFragmentAsPreviewWithMenu(chatActivity, previewMenu);
                }
            }

            /* renamed from: lambda$onLongPress$1$org-telegram-ui-CalendarActivity$MonthView$2 */
            public /* synthetic */ void m1590x44edf01e(final PeriodDay periodDay, View view) {
                if (CalendarActivity.this.parentLayout.fragmentsStack.size() >= 3) {
                    final BaseFragment fragment = CalendarActivity.this.parentLayout.fragmentsStack.get(CalendarActivity.this.parentLayout.fragmentsStack.size() - 3);
                    if (fragment instanceof ChatActivity) {
                        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.CalendarActivity$MonthView$2$$ExternalSyntheticLambda4
                            @Override // java.lang.Runnable
                            public final void run() {
                                CalendarActivity.MonthView.AnonymousClass2.this.m1589x8b76627f(fragment, periodDay);
                            }
                        }, 300L);
                    }
                }
                CalendarActivity.this.finishPreviewFragment();
            }

            /* renamed from: lambda$onLongPress$0$org-telegram-ui-CalendarActivity$MonthView$2 */
            public /* synthetic */ void m1589x8b76627f(BaseFragment fragment, PeriodDay periodDay) {
                CalendarActivity.this.finishFragment();
                ((ChatActivity) fragment).jumpToDate(periodDay.date);
            }

            /* renamed from: lambda$onLongPress$2$org-telegram-ui-CalendarActivity$MonthView$2 */
            public /* synthetic */ void m1591xfe657dbd(PeriodDay periodDay, View view) {
                CalendarActivity.this.dateSelectedStart = CalendarActivity.this.dateSelectedEnd = periodDay.date;
                CalendarActivity.this.inSelectionMode = true;
                CalendarActivity.this.updateTitle();
                CalendarActivity.this.animateSelection();
                CalendarActivity.this.finishPreviewFragment();
            }

            /* renamed from: lambda$onLongPress$3$org-telegram-ui-CalendarActivity$MonthView$2 */
            public /* synthetic */ void m1592xb7dd0b5c(View view) {
                if (CalendarActivity.this.parentLayout.fragmentsStack.size() >= 3) {
                    final BaseFragment fragment = CalendarActivity.this.parentLayout.fragmentsStack.get(CalendarActivity.this.parentLayout.fragmentsStack.size() - 3);
                    if (fragment instanceof ChatActivity) {
                        AlertsCreator.createClearDaysDialogAlert(CalendarActivity.this, 1, CalendarActivity.this.getMessagesController().getUser(Long.valueOf(CalendarActivity.this.dialogId)), null, false, new MessagesStorage.BooleanCallback() { // from class: org.telegram.ui.CalendarActivity.MonthView.2.1
                            @Override // org.telegram.messenger.MessagesStorage.BooleanCallback
                            public void run(boolean forAll) {
                                CalendarActivity.this.finishFragment();
                                ((ChatActivity) fragment).deleteHistory(CalendarActivity.this.dateSelectedStart, CalendarActivity.this.dateSelectedEnd + 86400, forAll);
                            }
                        }, null);
                    }
                }
                CalendarActivity.this.finishPreviewFragment();
            }

            /* renamed from: lambda$onLongPress$4$org-telegram-ui-CalendarActivity$MonthView$2 */
            public /* synthetic */ void m1593x715498fb(View view) {
                CalendarActivity.this.finishPreviewFragment();
            }
        }

        public void startSelectionAnimation(int fromDate, int toDate) {
            if (this.messagesByDays != null) {
                for (int i = 0; i < this.daysInMonth; i++) {
                    PeriodDay day = this.messagesByDays.get(i, null);
                    if (day != null) {
                        day.fromSelProgress = day.selectProgress;
                        day.toSelProgress = (day.date < fromDate || day.date > toDate) ? 0.0f : 1.0f;
                        day.fromSelSEProgress = day.selectStartEndProgress;
                        if (day.date == fromDate || day.date == toDate) {
                            day.toSelSEProgress = 1.0f;
                        } else {
                            day.toSelSEProgress = 0.0f;
                        }
                    }
                }
            }
        }

        public void setSelectionValue(float f) {
            if (this.messagesByDays != null) {
                for (int i = 0; i < this.daysInMonth; i++) {
                    PeriodDay day = this.messagesByDays.get(i, null);
                    if (day != null) {
                        day.selectProgress = day.fromSelProgress + ((day.toSelProgress - day.fromSelProgress) * f);
                        day.selectStartEndProgress = day.fromSelSEProgress + ((day.toSelSEProgress - day.fromSelSEProgress) * f);
                    }
                }
            }
            invalidate();
        }

        public void dismissRowAnimations(boolean animate) {
            for (int i = 0; i < this.rowSelectionPos.size(); i++) {
                animateRow(this.rowSelectionPos.keyAt(i), 0, 0, false, animate);
            }
        }

        public void animateRow(final int row, int startColumn, int endColumn, final boolean appear, boolean animate) {
            float fromAlpha;
            float cxFrom1;
            float cxFrom2;
            ValueAnimator a = this.rowAnimators.get(row);
            if (a != null) {
                a.cancel();
            }
            float xStep = getMeasuredWidth() / 7.0f;
            RowAnimationValue p = this.rowSelectionPos.get(row);
            if (p == null) {
                cxFrom1 = (startColumn * xStep) + (xStep / 2.0f);
                cxFrom2 = (startColumn * xStep) + (xStep / 2.0f);
                fromAlpha = 0.0f;
            } else {
                cxFrom1 = p.startX;
                cxFrom2 = p.endX;
                fromAlpha = p.alpha;
            }
            final float cxTo1 = appear ? (startColumn * xStep) + (xStep / 2.0f) : cxFrom1;
            final float cxTo2 = appear ? (endColumn * xStep) + (xStep / 2.0f) : cxFrom2;
            final float toAlpha = appear ? 1.0f : 0.0f;
            final RowAnimationValue pr = new RowAnimationValue(cxFrom1, cxFrom2);
            this.rowSelectionPos.put(row, pr);
            if (animate) {
                ValueAnimator anim = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(300L);
                anim.setInterpolator(Easings.easeInOutQuad);
                final float f = cxFrom1;
                final float cxTo22 = cxFrom2;
                final float cxTo12 = fromAlpha;
                anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.CalendarActivity$MonthView$$ExternalSyntheticLambda0
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        CalendarActivity.MonthView.this.m1587lambda$animateRow$1$orgtelegramuiCalendarActivity$MonthView(pr, f, cxTo1, cxTo22, cxTo2, cxTo12, toAlpha, valueAnimator);
                    }
                });
                anim.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.CalendarActivity.MonthView.3
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationCancel(Animator animation) {
                        pr.startX = cxTo1;
                        pr.endX = cxTo2;
                        pr.alpha = toAlpha;
                        MonthView.this.invalidate();
                    }

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        MonthView.this.rowAnimators.remove(row);
                        if (!appear) {
                            MonthView.this.rowSelectionPos.remove(row);
                        }
                    }
                });
                anim.start();
                this.rowAnimators.put(row, anim);
                return;
            }
            pr.startX = cxTo1;
            pr.endX = cxTo2;
            pr.alpha = toAlpha;
            invalidate();
        }

        /* renamed from: lambda$animateRow$1$org-telegram-ui-CalendarActivity$MonthView */
        public /* synthetic */ void m1587lambda$animateRow$1$orgtelegramuiCalendarActivity$MonthView(RowAnimationValue pr, float cxFrom1, float cxTo1, float cxFrom2, float cxTo2, float fromAlpha, float toAlpha, ValueAnimator animation) {
            float val = ((Float) animation.getAnimatedValue()).floatValue();
            pr.startX = ((cxTo1 - cxFrom1) * val) + cxFrom1;
            pr.endX = ((cxTo2 - cxFrom2) * val) + cxFrom2;
            pr.alpha = ((toAlpha - fromAlpha) * val) + fromAlpha;
            invalidate();
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent event) {
            return this.gestureDetector.onTouchEvent(event);
        }

        public void setDate(int year, int monthInYear, SparseArray<PeriodDay> messagesByDays, boolean animated) {
            ImageReceiver imageReceiver;
            TLRPC.PhotoSize currentPhotoObject;
            TLRPC.PhotoSize qualityThumb;
            MessageObject messageObject;
            boolean dateChanged = (year == this.currentYear && monthInYear == this.currentMonthInYear) ? false : true;
            this.currentYear = year;
            this.currentMonthInYear = monthInYear;
            this.messagesByDays = messagesByDays;
            ImageReceiver imageReceiver2 = null;
            if (dateChanged && this.imagesByDays != null) {
                for (int i = 0; i < this.imagesByDays.size(); i++) {
                    this.imagesByDays.valueAt(i).onDetachedFromWindow();
                    this.imagesByDays.valueAt(i).setParentView(null);
                }
                this.imagesByDays = null;
            }
            if (messagesByDays != null) {
                if (this.imagesByDays == null) {
                    this.imagesByDays = new SparseArray<>();
                }
                int i2 = 0;
                while (i2 < messagesByDays.size()) {
                    int key = messagesByDays.keyAt(i2);
                    if (this.imagesByDays.get(key, imageReceiver2) != null) {
                        imageReceiver = imageReceiver2;
                    } else if (!messagesByDays.get(key).hasImage) {
                        imageReceiver = imageReceiver2;
                    } else {
                        ImageReceiver receiver = new ImageReceiver();
                        receiver.setParentView(this);
                        PeriodDay periodDay = messagesByDays.get(key);
                        MessageObject messageObject2 = periodDay.messageObject;
                        if (messageObject2 == null) {
                            imageReceiver = imageReceiver2;
                        } else {
                            if (messageObject2.isVideo()) {
                                TLRPC.Document document = messageObject2.getDocument();
                                TLRPC.PhotoSize thumb = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 50);
                                TLRPC.PhotoSize qualityThumb2 = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, GroupCallActivity.TABLET_LIST_SIZE);
                                if (thumb != qualityThumb2) {
                                    qualityThumb = qualityThumb2;
                                } else {
                                    qualityThumb = null;
                                }
                                if (thumb == null) {
                                    messageObject = messageObject2;
                                } else if (messageObject2.strippedThumb != null) {
                                    messageObject = messageObject2;
                                    receiver.setImage(ImageLocation.getForDocument(qualityThumb, document), "44_44", messageObject2.strippedThumb, null, messageObject, 0);
                                } else {
                                    messageObject = messageObject2;
                                    receiver.setImage(ImageLocation.getForDocument(qualityThumb, document), "44_44", ImageLocation.getForDocument(thumb, document), "b", (String) null, messageObject, 0);
                                }
                                imageReceiver = null;
                            } else if (!(messageObject2.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto) || messageObject2.messageOwner.media.photo == null || messageObject2.photoThumbs.isEmpty()) {
                                imageReceiver = null;
                            } else {
                                TLRPC.PhotoSize currentPhotoObjectThumb = FileLoader.getClosestPhotoSizeWithSize(messageObject2.photoThumbs, 50);
                                TLRPC.PhotoSize currentPhotoObject2 = FileLoader.getClosestPhotoSizeWithSize(messageObject2.photoThumbs, GroupCallActivity.TABLET_LIST_SIZE, false, currentPhotoObjectThumb, false);
                                if (!messageObject2.mediaExists) {
                                    if (DownloadController.getInstance(CalendarActivity.this.currentAccount).canDownloadMedia(messageObject2)) {
                                        currentPhotoObject = currentPhotoObject2;
                                        imageReceiver = null;
                                    } else if (messageObject2.strippedThumb != null) {
                                        receiver.setImage(null, null, messageObject2.strippedThumb, null, messageObject2, 0);
                                        imageReceiver = null;
                                    } else {
                                        imageReceiver = null;
                                        receiver.setImage((ImageLocation) null, (String) null, ImageLocation.getForObject(currentPhotoObjectThumb, messageObject2.photoThumbsObject), "b", (String) null, messageObject2, 0);
                                    }
                                } else {
                                    currentPhotoObject = currentPhotoObject2;
                                    imageReceiver = null;
                                }
                                TLRPC.PhotoSize currentPhotoObject3 = currentPhotoObject;
                                if (currentPhotoObject3 == currentPhotoObjectThumb) {
                                    currentPhotoObjectThumb = null;
                                }
                                long j = 0;
                                if (messageObject2.strippedThumb != null) {
                                    ImageLocation forObject = ImageLocation.getForObject(currentPhotoObject3, messageObject2.photoThumbsObject);
                                    BitmapDrawable bitmapDrawable = messageObject2.strippedThumb;
                                    if (currentPhotoObject3 != null) {
                                        j = currentPhotoObject3.size;
                                    }
                                    receiver.setImage(forObject, "44_44", null, null, bitmapDrawable, j, null, messageObject2, messageObject2.shouldEncryptPhotoOrVideo() ? 2 : 1);
                                } else {
                                    ImageLocation forObject2 = ImageLocation.getForObject(currentPhotoObject3, messageObject2.photoThumbsObject);
                                    ImageLocation forObject3 = ImageLocation.getForObject(currentPhotoObjectThumb, messageObject2.photoThumbsObject);
                                    if (currentPhotoObject3 != null) {
                                        j = currentPhotoObject3.size;
                                    }
                                    receiver.setImage(forObject2, "44_44", forObject3, "b", j, null, messageObject2, messageObject2.shouldEncryptPhotoOrVideo() ? 2 : 1);
                                }
                            }
                            receiver.setRoundRadius(AndroidUtilities.dp(22.0f));
                            this.imagesByDays.put(key, receiver);
                        }
                    }
                    i2++;
                    imageReceiver2 = imageReceiver;
                }
            }
            YearMonth yearMonthObject = YearMonth.of(year, monthInYear + 1);
            this.daysInMonth = yearMonthObject.lengthOfMonth();
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, monthInYear, 0);
            this.startDayOfWeek = (calendar.get(7) + 6) % 7;
            this.startMonthTime = (int) (calendar.getTimeInMillis() / 1000);
            int totalColumns = this.daysInMonth + this.startDayOfWeek;
            this.cellCount = ((int) (totalColumns / 7.0f)) + (totalColumns % 7 == 0 ? 0 : 1);
            calendar.set(year, monthInYear + 1, 0);
            this.titleView.setText(LocaleController.formatYearMont(calendar.getTimeInMillis() / 1000, true));
            CalendarActivity.this.updateRowSelections(this, false);
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((this.cellCount * 52) + 44), C.BUFFER_FLAG_ENCRYPTED));
        }

        /* JADX WARN: Incorrect condition in loop: B:11:0x00a2 */
        /* JADX WARN: Removed duplicated region for block: B:76:0x04e9  */
        /* JADX WARN: Removed duplicated region for block: B:84:0x04ed A[SYNTHETIC] */
        @Override // android.view.View
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        protected void onDraw(android.graphics.Canvas r29) {
            /*
                Method dump skipped, instructions count: 1274
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.CalendarActivity.MonthView.onDraw(android.graphics.Canvas):void");
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.attached = true;
            if (this.imagesByDays != null) {
                for (int i = 0; i < this.imagesByDays.size(); i++) {
                    this.imagesByDays.valueAt(i).onAttachedToWindow();
                }
            }
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.attached = false;
            if (this.imagesByDays != null) {
                for (int i = 0; i < this.imagesByDays.size(); i++) {
                    this.imagesByDays.valueAt(i).onDetachedFromWindow();
                }
            }
        }
    }

    public void updateTitle() {
        int daysSelected;
        String title;
        HintView hintView;
        if (!this.canClearHistory) {
            this.actionBar.setTitle(LocaleController.getString("Calendar", R.string.Calendar));
            this.backDrawable.setRotation(0.0f, true);
            return;
        }
        int daysSelected2 = this.dateSelectedStart;
        int i = this.dateSelectedEnd;
        if (daysSelected2 == i && daysSelected2 == 0) {
            daysSelected = 0;
        } else {
            daysSelected = (Math.abs(daysSelected2 - i) / 86400) + 1;
        }
        boolean z = this.lastInSelectionMode;
        int i2 = this.lastDaysSelected;
        if (daysSelected != i2 || this.lastInSelectionMode != this.inSelectionMode) {
            boolean fromBottom = i2 > daysSelected;
            this.lastDaysSelected = daysSelected;
            boolean z2 = this.inSelectionMode;
            this.lastInSelectionMode = z2;
            float f = 1.0f;
            if (daysSelected > 0) {
                title = LocaleController.formatPluralString("Days", daysSelected, new Object[0]);
                this.backDrawable.setRotation(1.0f, true);
            } else if (z2) {
                title = LocaleController.getString("SelectDays", R.string.SelectDays);
                this.backDrawable.setRotation(1.0f, true);
            } else {
                title = LocaleController.getString("Calendar", R.string.Calendar);
                this.backDrawable.setRotation(0.0f, true);
            }
            if (daysSelected > 1) {
                this.removeDaysButton.setText(LocaleController.formatString("ClearHistoryForTheseDays", R.string.ClearHistoryForTheseDays, new Object[0]));
            } else if (daysSelected > 0 || this.inSelectionMode) {
                this.removeDaysButton.setText(LocaleController.formatString("ClearHistoryForThisDay", R.string.ClearHistoryForThisDay, new Object[0]));
            }
            this.actionBar.setTitleAnimated(title, fromBottom, 150L);
            if ((!this.inSelectionMode || daysSelected > 0) && (hintView = this.selectDaysHint) != null) {
                hintView.hide();
            }
            if (daysSelected > 0 || this.inSelectionMode) {
                if (this.removeDaysButton.getVisibility() == 8) {
                    this.removeDaysButton.setAlpha(0.0f);
                    this.removeDaysButton.setTranslationY(-AndroidUtilities.dp(20.0f));
                }
                this.removeDaysButton.setVisibility(0);
                this.selectDaysButton.animate().setListener(null).cancel();
                this.removeDaysButton.animate().setListener(null).cancel();
                this.selectDaysButton.animate().alpha(0.0f).translationY(AndroidUtilities.dp(20.0f)).setDuration(150L).setListener(new HideViewAfterAnimation(this.selectDaysButton)).start();
                ViewPropertyAnimator animate = this.removeDaysButton.animate();
                if (daysSelected == 0) {
                    f = 0.5f;
                }
                animate.alpha(f).translationY(0.0f).start();
                this.selectDaysButton.setEnabled(false);
                this.removeDaysButton.setEnabled(true);
                return;
            }
            if (this.selectDaysButton.getVisibility() == 8) {
                this.selectDaysButton.setAlpha(0.0f);
                this.selectDaysButton.setTranslationY(AndroidUtilities.dp(20.0f));
            }
            this.selectDaysButton.setVisibility(0);
            this.selectDaysButton.animate().setListener(null).cancel();
            this.removeDaysButton.animate().setListener(null).cancel();
            this.selectDaysButton.animate().alpha(1.0f).translationY(0.0f).start();
            this.removeDaysButton.animate().alpha(0.0f).translationY(-AndroidUtilities.dp(20.0f)).setDuration(150L).setListener(new HideViewAfterAnimation(this.removeDaysButton)).start();
            this.selectDaysButton.setEnabled(true);
            this.removeDaysButton.setEnabled(false);
        }
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    /* loaded from: classes4.dex */
    public class PeriodDay {
        int date;
        float enterAlpha;
        float fromSelProgress;
        float fromSelSEProgress;
        boolean hasImage;
        MessageObject messageObject;
        float selectProgress;
        float selectStartEndProgress;
        float startEnterDelay;
        int startOffset;
        float toSelProgress;
        float toSelSEProgress;
        boolean wasDrawn;

        private PeriodDay() {
            CalendarActivity.this = r1;
            this.enterAlpha = 1.0f;
            this.startEnterDelay = 1.0f;
            this.hasImage = true;
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ThemeDescription.ThemeDescriptionDelegate descriptionDelegate = new ThemeDescription.ThemeDescriptionDelegate() { // from class: org.telegram.ui.CalendarActivity.8
            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public /* synthetic */ void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }

            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public void didSetColor() {
                CalendarActivity.this.updateColors();
            }
        };
        new ArrayList();
        new ThemeDescription(null, 0, null, null, null, descriptionDelegate, Theme.key_windowBackgroundWhite);
        new ThemeDescription(null, 0, null, null, null, descriptionDelegate, Theme.key_windowBackgroundWhiteBlackText);
        new ThemeDescription(null, 0, null, null, null, descriptionDelegate, Theme.key_listSelector);
        return super.getThemeDescriptions();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean needDelayOpenAnimation() {
        return true;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onTransitionAnimationStart(boolean isOpen, boolean backward) {
        super.onTransitionAnimationStart(isOpen, backward);
        this.isOpened = true;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onTransitionAnimationProgress(boolean isOpen, float progress) {
        super.onTransitionAnimationProgress(isOpen, progress);
        View view = this.blurredView;
        if (view != null && view.getVisibility() == 0) {
            if (isOpen) {
                this.blurredView.setAlpha(1.0f - progress);
            } else {
                this.blurredView.setAlpha(progress);
            }
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        View view;
        if (isOpen && (view = this.blurredView) != null && view.getVisibility() == 0) {
            this.blurredView.setVisibility(8);
            this.blurredView.setBackground(null);
        }
    }

    public void animateSelection() {
        ValueAnimator a = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(300L);
        a.setInterpolator(CubicBezierInterpolator.DEFAULT);
        a.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.CalendarActivity$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                CalendarActivity.this.m1582lambda$animateSelection$4$orgtelegramuiCalendarActivity(valueAnimator);
            }
        });
        a.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.CalendarActivity.9
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animation) {
                for (int j = 0; j < CalendarActivity.this.listView.getChildCount(); j++) {
                    MonthView m = (MonthView) CalendarActivity.this.listView.getChildAt(j);
                    m.startSelectionAnimation(CalendarActivity.this.dateSelectedStart, CalendarActivity.this.dateSelectedEnd);
                }
            }
        });
        a.start();
        this.selectionAnimator = a;
        for (int j = 0; j < this.listView.getChildCount(); j++) {
            updateRowSelections((MonthView) this.listView.getChildAt(j), true);
        }
        for (int j2 = 0; j2 < this.listView.getCachedChildCount(); j2++) {
            MonthView m = (MonthView) this.listView.getCachedChildAt(j2);
            updateRowSelections(m, false);
            m.startSelectionAnimation(this.dateSelectedStart, this.dateSelectedEnd);
            m.setSelectionValue(1.0f);
        }
        for (int j3 = 0; j3 < this.listView.getHiddenChildCount(); j3++) {
            MonthView m2 = (MonthView) this.listView.getHiddenChildAt(j3);
            updateRowSelections(m2, false);
            m2.startSelectionAnimation(this.dateSelectedStart, this.dateSelectedEnd);
            m2.setSelectionValue(1.0f);
        }
        for (int j4 = 0; j4 < this.listView.getAttachedScrapChildCount(); j4++) {
            MonthView m3 = (MonthView) this.listView.getAttachedScrapChildAt(j4);
            updateRowSelections(m3, false);
            m3.startSelectionAnimation(this.dateSelectedStart, this.dateSelectedEnd);
            m3.setSelectionValue(1.0f);
        }
    }

    /* renamed from: lambda$animateSelection$4$org-telegram-ui-CalendarActivity */
    public /* synthetic */ void m1582lambda$animateSelection$4$orgtelegramuiCalendarActivity(ValueAnimator animation) {
        float selectProgress = ((Float) animation.getAnimatedValue()).floatValue();
        for (int j = 0; j < this.listView.getChildCount(); j++) {
            MonthView m = (MonthView) this.listView.getChildAt(j);
            m.setSelectionValue(selectProgress);
        }
    }

    public void updateRowSelections(MonthView m, boolean animate) {
        if (this.dateSelectedStart == 0 || this.dateSelectedEnd == 0) {
            m.dismissRowAnimations(animate);
        } else if (m.messagesByDays != null) {
            if (!animate) {
                m.dismissRowAnimations(false);
            }
            int row = 0;
            int dayInRow = m.startDayOfWeek;
            int sDay = -1;
            int eDay = -1;
            for (int i = 0; i < m.daysInMonth; i++) {
                PeriodDay day = m.messagesByDays.get(i, null);
                if (day != null && day.date >= this.dateSelectedStart && day.date <= this.dateSelectedEnd) {
                    if (sDay == -1) {
                        sDay = dayInRow;
                    }
                    eDay = dayInRow;
                }
                dayInRow++;
                if (dayInRow >= 7) {
                    dayInRow = 0;
                    if (sDay != -1 && eDay != -1) {
                        m.animateRow(row, sDay, eDay, true, animate);
                    } else {
                        m.animateRow(row, 0, 0, false, animate);
                    }
                    row++;
                    sDay = -1;
                    eDay = -1;
                }
            }
            if (sDay != -1 && eDay != -1) {
                m.animateRow(row, sDay, eDay, true, animate);
            } else {
                m.animateRow(row, 0, 0, false, animate);
            }
        }
    }

    /* loaded from: classes4.dex */
    public static final class RowAnimationValue {
        float alpha;
        float endX;
        float startX;

        RowAnimationValue(float s, float e) {
            this.startX = s;
            this.endX = e;
        }
    }

    public void prepareBlurBitmap() {
        if (this.blurredView == null) {
            return;
        }
        int w = (int) (this.parentLayout.getMeasuredWidth() / 6.0f);
        int h = (int) (this.parentLayout.getMeasuredHeight() / 6.0f);
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.scale(0.16666667f, 0.16666667f);
        this.parentLayout.draw(canvas);
        Utilities.stackBlurBitmap(bitmap, Math.max(7, Math.max(w, h) / 180));
        this.blurredView.setBackground(new BitmapDrawable(bitmap));
        this.blurredView.setAlpha(0.0f);
        this.blurredView.setVisibility(0);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onBackPressed() {
        if (this.inSelectionMode) {
            this.inSelectionMode = false;
            this.dateSelectedEnd = 0;
            this.dateSelectedStart = 0;
            updateTitle();
            animateSelection();
            return false;
        }
        return super.onBackPressed();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean isLightStatusBar() {
        int color = Theme.getColor(Theme.key_windowBackgroundWhite, null, true);
        return ColorUtils.calculateLuminance(color) > 0.699999988079071d;
    }
}
