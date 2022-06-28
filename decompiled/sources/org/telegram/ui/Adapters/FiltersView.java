package org.telegram.ui.Adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListUpdateCallback;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.C;
import j$.time.LocalDate;
import j$.time.LocalDateTime;
import j$.time.LocalTime;
import j$.time.format.DateTimeFormatter;
import j$.time.format.DateTimeParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
/* loaded from: classes4.dex */
public class FiltersView extends RecyclerListView {
    public static final int FILTER_INDEX_FILES = 2;
    public static final int FILTER_INDEX_LINKS = 1;
    public static final int FILTER_INDEX_MEDIA = 0;
    public static final int FILTER_INDEX_MUSIC = 3;
    public static final int FILTER_INDEX_VOICE = 4;
    public static final int FILTER_TYPE_ARCHIVE = 7;
    public static final int FILTER_TYPE_CHAT = 4;
    public static final int FILTER_TYPE_DATE = 6;
    public static final int FILTER_TYPE_FILES = 1;
    public static final int FILTER_TYPE_LINKS = 2;
    public static final int FILTER_TYPE_MEDIA = 0;
    public static final int FILTER_TYPE_MUSIC = 3;
    public static final int FILTER_TYPE_VOICE = 5;
    private static final int minYear = 2013;
    LinearLayoutManager layoutManager;
    public static final MediaFilterData[] filters = {new MediaFilterData(R.drawable.search_media_filled, LocaleController.getString("SharedMediaTab2", R.string.SharedMediaTab2), new TLRPC.TL_inputMessagesFilterPhotoVideo(), 0), new MediaFilterData(R.drawable.search_links_filled, LocaleController.getString("SharedLinksTab2", R.string.SharedLinksTab2), new TLRPC.TL_inputMessagesFilterUrl(), 2), new MediaFilterData(R.drawable.search_files_filled, LocaleController.getString("SharedFilesTab2", R.string.SharedFilesTab2), new TLRPC.TL_inputMessagesFilterDocument(), 1), new MediaFilterData(R.drawable.search_music_filled, LocaleController.getString("SharedMusicTab2", R.string.SharedMusicTab2), new TLRPC.TL_inputMessagesFilterMusic(), 3), new MediaFilterData(R.drawable.search_voice_filled, LocaleController.getString("SharedVoiceTab2", R.string.SharedVoiceTab2), new TLRPC.TL_inputMessagesFilterRoundVoice(), 5)};
    private static final Pattern yearPatter = Pattern.compile("20[0-9]{1,2}");
    private static final Pattern monthYearOrDayPatter = Pattern.compile("(\\w{3,}) ([0-9]{0,4})");
    private static final Pattern yearOrDayAndMonthPatter = Pattern.compile("([0-9]{0,4}) (\\w{2,})");
    private static final Pattern shortDate = Pattern.compile("^([0-9]{1,4})(\\.| |/|\\-)([0-9]{1,4})$");
    private static final Pattern longDate = Pattern.compile("^([0-9]{1,2})(\\.| |/|\\-)([0-9]{1,2})(\\.| |/|\\-)([0-9]{1,4})$");
    private static final int[] numberOfDaysEachMonth = {31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    private ArrayList<MediaFilterData> usersFilters = new ArrayList<>();
    private ArrayList<MediaFilterData> oldItems = new ArrayList<>();
    DiffUtil.Callback diffUtilsCallback = new DiffUtil.Callback() { // from class: org.telegram.ui.Adapters.FiltersView.4
        @Override // androidx.recyclerview.widget.DiffUtil.Callback
        public int getOldListSize() {
            return FiltersView.this.oldItems.size();
        }

        @Override // androidx.recyclerview.widget.DiffUtil.Callback
        public int getNewListSize() {
            return FiltersView.this.usersFilters.size();
        }

        @Override // androidx.recyclerview.widget.DiffUtil.Callback
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            MediaFilterData oldItem = (MediaFilterData) FiltersView.this.oldItems.get(oldItemPosition);
            MediaFilterData newItem = (MediaFilterData) FiltersView.this.usersFilters.get(newItemPosition);
            if (oldItem.isSameType(newItem)) {
                if (oldItem.filterType == 4) {
                    return (!(oldItem.chat instanceof TLRPC.User) || !(newItem.chat instanceof TLRPC.User)) ? (oldItem.chat instanceof TLRPC.Chat) && (newItem.chat instanceof TLRPC.Chat) && ((TLRPC.Chat) oldItem.chat).id == ((TLRPC.Chat) newItem.chat).id : ((TLRPC.User) oldItem.chat).id == ((TLRPC.User) newItem.chat).id;
                } else if (oldItem.filterType == 6) {
                    return oldItem.title.equals(newItem.title);
                } else {
                    if (oldItem.filterType == 7) {
                        return true;
                    }
                }
            }
            return false;
        }

        @Override // androidx.recyclerview.widget.DiffUtil.Callback
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return true;
        }
    };

    public FiltersView(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context, resourcesProvider);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context) { // from class: org.telegram.ui.Adapters.FiltersView.1
            @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }

            @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
            public void onInitializeAccessibilityNodeInfo(RecyclerView.Recycler recycler, RecyclerView.State state, AccessibilityNodeInfoCompat info) {
                super.onInitializeAccessibilityNodeInfo(recycler, state, info);
                if (!FiltersView.this.isEnabled()) {
                    info.setVisibleToUser(false);
                }
            }
        };
        this.layoutManager = linearLayoutManager;
        linearLayoutManager.setOrientation(0);
        setLayoutManager(this.layoutManager);
        setAdapter(new Adapter());
        addItemDecoration(new RecyclerView.ItemDecoration() { // from class: org.telegram.ui.Adapters.FiltersView.2
            @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int position = parent.getChildAdapterPosition(view);
                outRect.left = AndroidUtilities.dp(8.0f);
                if (position == state.getItemCount() - 1) {
                    outRect.right = AndroidUtilities.dp(10.0f);
                }
                if (position == 0) {
                    outRect.left = AndroidUtilities.dp(10.0f);
                }
            }
        });
        setItemAnimator(new DefaultItemAnimator() { // from class: org.telegram.ui.Adapters.FiltersView.3
            private final float scaleFrom = 0.0f;

            @Override // androidx.recyclerview.widget.DefaultItemAnimator
            protected long getMoveAnimationDelay() {
                return 0L;
            }

            @Override // androidx.recyclerview.widget.DefaultItemAnimator
            protected long getAddAnimationDelay(long removeDuration, long moveDuration, long changeDuration) {
                return 0L;
            }

            @Override // androidx.recyclerview.widget.RecyclerView.ItemAnimator
            public long getMoveDuration() {
                return 220L;
            }

            @Override // androidx.recyclerview.widget.RecyclerView.ItemAnimator
            public long getAddDuration() {
                return 220L;
            }

            @Override // androidx.recyclerview.widget.DefaultItemAnimator, androidx.recyclerview.widget.SimpleItemAnimator
            public boolean animateAdd(RecyclerView.ViewHolder holder) {
                boolean r = super.animateAdd(holder);
                if (r) {
                    holder.itemView.setScaleX(0.0f);
                    holder.itemView.setScaleY(0.0f);
                }
                return r;
            }

            @Override // androidx.recyclerview.widget.DefaultItemAnimator
            public void animateAddImpl(final RecyclerView.ViewHolder holder) {
                final View view = holder.itemView;
                final ViewPropertyAnimator animation = view.animate();
                this.mAddAnimations.add(holder);
                animation.alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setDuration(getAddDuration()).setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Adapters.FiltersView.3.1
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationStart(Animator animator) {
                        dispatchAddStarting(holder);
                    }

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationCancel(Animator animator) {
                        view.setAlpha(1.0f);
                    }

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animator) {
                        animation.setListener(null);
                        dispatchAddFinished(holder);
                        AnonymousClass3.this.mAddAnimations.remove(holder);
                        dispatchFinishedWhenDone();
                    }
                }).start();
            }

            @Override // androidx.recyclerview.widget.DefaultItemAnimator
            protected void animateRemoveImpl(final RecyclerView.ViewHolder holder) {
                final View view = holder.itemView;
                final ViewPropertyAnimator animation = view.animate();
                this.mRemoveAnimations.add(holder);
                animation.setDuration(getRemoveDuration()).alpha(0.0f).scaleX(0.0f).scaleY(0.0f).setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Adapters.FiltersView.3.2
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationStart(Animator animator) {
                        dispatchRemoveStarting(holder);
                    }

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animator) {
                        animation.setListener(null);
                        view.setAlpha(1.0f);
                        view.setTranslationX(0.0f);
                        view.setTranslationY(0.0f);
                        view.setScaleX(1.0f);
                        view.setScaleY(1.0f);
                        dispatchRemoveFinished(holder);
                        AnonymousClass3.this.mRemoveAnimations.remove(holder);
                        dispatchFinishedWhenDone();
                    }
                }).start();
            }
        });
        setWillNotDraw(false);
        setHideIfEmpty(false);
        setSelectorRadius(AndroidUtilities.dp(28.0f));
        setSelectorDrawableColor(getThemedColor(Theme.key_listSelector));
    }

    @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(44.0f), C.BUFFER_FLAG_ENCRYPTED));
    }

    public MediaFilterData getFilterAt(int i) {
        if (this.usersFilters.isEmpty()) {
            return filters[i];
        }
        return this.usersFilters.get(i);
    }

    public void setUsersAndDates(ArrayList<Object> localUsers, ArrayList<DateData> dates, boolean archive) {
        String title;
        this.oldItems.clear();
        this.oldItems.addAll(this.usersFilters);
        this.usersFilters.clear();
        if (localUsers != null) {
            for (int i = 0; i < localUsers.size(); i++) {
                Object object = localUsers.get(i);
                if (object instanceof TLRPC.User) {
                    TLRPC.User user = (TLRPC.User) object;
                    if (UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser().id == user.id) {
                        title = LocaleController.getString("SavedMessages", R.string.SavedMessages);
                    } else {
                        title = ContactsController.formatName(user.first_name, user.last_name, 10);
                    }
                    MediaFilterData data = new MediaFilterData(R.drawable.search_users_filled, title, null, 4);
                    data.setUser(user);
                    this.usersFilters.add(data);
                } else if (object instanceof TLRPC.Chat) {
                    TLRPC.Chat chat = (TLRPC.Chat) object;
                    String title2 = chat.title;
                    if (chat.title.length() > 12) {
                        title2 = String.format("%s...", title2.substring(0, 10));
                    }
                    MediaFilterData data2 = new MediaFilterData(R.drawable.search_users_filled, title2, null, 4);
                    data2.setUser(chat);
                    this.usersFilters.add(data2);
                }
            }
        }
        if (dates != null) {
            for (int i2 = 0; i2 < dates.size(); i2++) {
                DateData dateData = dates.get(i2);
                MediaFilterData data3 = new MediaFilterData(R.drawable.search_date_filled, dateData.title, null, 6);
                data3.setDate(dateData);
                this.usersFilters.add(data3);
            }
        }
        if (archive) {
            MediaFilterData filterData = new MediaFilterData(R.drawable.chats_archive, LocaleController.getString("ArchiveSearchFilter", R.string.ArchiveSearchFilter), null, 7);
            this.usersFilters.add(filterData);
        }
        if (getAdapter() != null) {
            UpdateCallback updateCallback = new UpdateCallback(getAdapter());
            DiffUtil.calculateDiff(this.diffUtilsCallback).dispatchUpdatesTo(updateCallback);
            if (!this.usersFilters.isEmpty() && updateCallback.changed) {
                this.layoutManager.scrollToPositionWithOffset(0, 0);
            }
        }
    }

    public static void fillTipDates(String query, ArrayList<DateData> dates) {
        int year;
        dates.clear();
        if (query == null) {
            return;
        }
        String q = query.trim();
        if (q.length() < 3) {
            return;
        }
        int i = 2;
        if (LocaleController.getString("SearchTipToday", R.string.SearchTipToday).toLowerCase().startsWith(q) || "today".startsWith(q)) {
            Calendar calendar = Calendar.getInstance();
            int year2 = calendar.get(1);
            int month = calendar.get(2);
            int day = calendar.get(5);
            calendar.set(year2, month, day, 0, 0, 0);
            long minDate = calendar.getTimeInMillis();
            calendar.set(year2, month, day + 1, 0, 0, 0);
            long maxDate = calendar.getTimeInMillis() - 1;
            dates.add(new DateData(LocaleController.getString("SearchTipToday", R.string.SearchTipToday), minDate, maxDate));
        } else if (LocaleController.getString("SearchTipYesterday", R.string.SearchTipYesterday).toLowerCase().startsWith(q) || "yesterday".startsWith(q)) {
            Calendar calendar2 = Calendar.getInstance();
            int year3 = calendar2.get(1);
            int month2 = calendar2.get(2);
            int day2 = calendar2.get(5);
            calendar2.set(year3, month2, day2, 0, 0, 0);
            long minDate2 = calendar2.getTimeInMillis() - 86400000;
            calendar2.set(year3, month2, day2 + 1, 0, 0, 0);
            long maxDate2 = calendar2.getTimeInMillis() - 86400001;
            dates.add(new DateData(LocaleController.getString("SearchTipYesterday", R.string.SearchTipYesterday), minDate2, maxDate2));
        } else {
            int dayOfWeek = getDayOfWeek(q);
            if (dayOfWeek >= 0) {
                Calendar calendar3 = Calendar.getInstance();
                long now = calendar3.getTimeInMillis();
                calendar3.set(7, dayOfWeek);
                if (calendar3.getTimeInMillis() > now) {
                    calendar3.setTimeInMillis(calendar3.getTimeInMillis() - 604800000);
                }
                int year4 = calendar3.get(1);
                int month3 = calendar3.get(2);
                int day3 = calendar3.get(5);
                calendar3.set(year4, month3, day3, 0, 0, 0);
                long minDate3 = calendar3.getTimeInMillis();
                calendar3.set(year4, month3, day3 + 1, 0, 0, 0);
                long maxDate3 = calendar3.getTimeInMillis() - 1;
                dates.add(new DateData(LocaleController.getInstance().formatterWeekLong.format(minDate3), minDate3, maxDate3));
                return;
            }
            Matcher matcher = shortDate.matcher(q);
            if (matcher.matches()) {
                String g1 = matcher.group(1);
                String g2 = matcher.group(3);
                int k = Integer.parseInt(g1);
                int k1 = Integer.parseInt(g2);
                if (k > 0 && k <= 31) {
                    if (k1 >= minYear && k <= 12) {
                        createForMonthYear(dates, k - 1, k1);
                        return;
                    } else if (k1 <= 12) {
                        createForDayMonth(dates, k - 1, k1 - 1);
                        return;
                    } else {
                        return;
                    }
                } else if (k >= minYear && k1 <= 12) {
                    createForMonthYear(dates, k1 - 1, k);
                    return;
                } else {
                    return;
                }
            }
            Matcher matcher2 = longDate.matcher(q);
            if (matcher2.matches()) {
                String g12 = matcher2.group(1);
                String g22 = matcher2.group(3);
                String g3 = matcher2.group(5);
                if (!matcher2.group(2).equals(matcher2.group(4))) {
                    return;
                }
                int day4 = Integer.parseInt(g12);
                int month4 = Integer.parseInt(g22) - 1;
                int year5 = Integer.parseInt(g3);
                if (year5 >= 10 && year5 <= 99) {
                    year = year5 + 2000;
                } else {
                    year = year5;
                }
                int currentYear = Calendar.getInstance().get(1);
                if (validDateForMont(day4 - 1, month4) && year >= minYear && year <= currentYear) {
                    Calendar calendar4 = Calendar.getInstance();
                    calendar4.set(year, month4, day4, 0, 0, 0);
                    long minDate4 = calendar4.getTimeInMillis();
                    calendar4.set(year, month4, day4 + 1, 0, 0, 0);
                    long maxDate4 = calendar4.getTimeInMillis() - 1;
                    dates.add(new DateData(LocaleController.getInstance().formatterYearMax.format(minDate4), minDate4, maxDate4));
                }
            } else if (yearPatter.matcher(q).matches()) {
                int selectedYear = Integer.valueOf(q).intValue();
                int currentYear2 = Calendar.getInstance().get(1);
                if (selectedYear < minYear) {
                    for (int i2 = currentYear2; i2 >= minYear; i2--) {
                        Calendar calendar5 = Calendar.getInstance();
                        calendar5.set(i2, 0, 1, 0, 0, 0);
                        long minDate5 = calendar5.getTimeInMillis();
                        calendar5.set(i2 + 1, 0, 1, 0, 0, 0);
                        long maxDate5 = calendar5.getTimeInMillis() - 1;
                        dates.add(new DateData(Integer.toString(i2), minDate5, maxDate5));
                    }
                } else if (selectedYear <= currentYear2) {
                    Calendar calendar6 = Calendar.getInstance();
                    calendar6.set(selectedYear, 0, 1, 0, 0, 0);
                    long minDate6 = calendar6.getTimeInMillis();
                    calendar6.set(selectedYear + 1, 0, 1, 0, 0, 0);
                    long maxDate6 = calendar6.getTimeInMillis() - 1;
                    dates.add(new DateData(Integer.toString(selectedYear), minDate6, maxDate6));
                }
            } else {
                Matcher matcher3 = monthYearOrDayPatter.matcher(q);
                if (matcher3.matches()) {
                    String g13 = matcher3.group(1);
                    String g23 = matcher3.group(2);
                    int month5 = getMonth(g13);
                    if (month5 >= 0) {
                        int k2 = Integer.valueOf(g23).intValue();
                        if (k2 > 0 && k2 <= 31) {
                            createForDayMonth(dates, k2 - 1, month5);
                            return;
                        } else if (k2 >= minYear) {
                            createForMonthYear(dates, month5, k2);
                            return;
                        }
                    }
                }
                Matcher matcher4 = yearOrDayAndMonthPatter.matcher(q);
                if (matcher4.matches()) {
                    String g14 = matcher4.group(1);
                    String g24 = matcher4.group(2);
                    int month6 = getMonth(g24);
                    if (month6 >= 0) {
                        int k3 = Integer.valueOf(g14).intValue();
                        if (k3 > 0 && k3 <= 31) {
                            createForDayMonth(dates, k3 - 1, month6);
                            return;
                        } else if (k3 >= minYear) {
                            createForMonthYear(dates, month6, k3);
                        }
                    }
                }
                if (!TextUtils.isEmpty(q) && q.length() > 2) {
                    int month7 = getMonth(q);
                    long today = Calendar.getInstance().getTimeInMillis();
                    if (month7 >= 0) {
                        int j = Calendar.getInstance().get(1);
                        while (j >= minYear) {
                            Calendar calendar7 = Calendar.getInstance();
                            calendar7.set(j, month7, 1, 0, 0, 0);
                            long minDate7 = calendar7.getTimeInMillis();
                            if (minDate7 <= today) {
                                calendar7.add(i, 1);
                                long maxDate7 = calendar7.getTimeInMillis() - 1;
                                dates.add(new DateData(LocaleController.getInstance().formatterMonthYear.format(minDate7), minDate7, maxDate7));
                            }
                            j--;
                            i = 2;
                        }
                    }
                }
            }
        }
    }

    private static void createForMonthYear(ArrayList<DateData> dates, int month, int selectedYear) {
        int currentYear = Calendar.getInstance().get(1);
        long today = Calendar.getInstance().getTimeInMillis();
        if (selectedYear >= minYear && selectedYear <= currentYear) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(selectedYear, month, 1, 0, 0, 0);
            long minDate = calendar.getTimeInMillis();
            if (minDate > today) {
                return;
            }
            calendar.add(2, 1);
            long maxDate = calendar.getTimeInMillis() - 1;
            dates.add(new DateData(LocaleController.getInstance().formatterMonthYear.format(minDate), minDate, maxDate));
        }
    }

    private static void createForDayMonth(ArrayList<DateData> dates, int day, int month) {
        long today;
        if (validDateForMont(day, month)) {
            int i = 1;
            int currentYear = Calendar.getInstance().get(1);
            long today2 = Calendar.getInstance().getTimeInMillis();
            GregorianCalendar georgianCal = (GregorianCalendar) GregorianCalendar.getInstance();
            int i2 = currentYear;
            while (i2 >= minYear) {
                if (month == i && day == 28 && !georgianCal.isLeapYear(i2)) {
                    today = today2;
                } else {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(i2, month, day + 1, 0, 0, 0);
                    long minDate = calendar.getTimeInMillis();
                    if (minDate > today2) {
                        today = today2;
                    } else {
                        today = today2;
                        calendar.set(i2, month, day + 2, 0, 0, 0);
                        long maxDate = calendar.getTimeInMillis() - 1;
                        if (i2 == currentYear) {
                            dates.add(new DateData(LocaleController.getInstance().formatterDayMonth.format(minDate), minDate, maxDate));
                        } else {
                            dates.add(new DateData(LocaleController.getInstance().formatterYearMax.format(minDate), minDate, maxDate));
                        }
                    }
                }
                i2--;
                today2 = today;
                i = 1;
            }
        }
    }

    private static boolean validDateForMont(int day, int month) {
        if (month >= 0 && month < 12 && day >= 0 && day < numberOfDaysEachMonth[month]) {
            return true;
        }
        return false;
    }

    public static int getDayOfWeek(String q) {
        Calendar c = Calendar.getInstance();
        if (q.length() <= 3) {
            return -1;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE", Locale.ENGLISH);
        for (int i = 0; i < 7; i++) {
            c.set(7, i);
            if (LocaleController.getInstance().formatterWeekLong.format(c.getTime()).toLowerCase().startsWith(q)) {
                return i;
            }
            if (dateFormat.format(c.getTime()).toLowerCase().startsWith(q)) {
                return i;
            }
        }
        return -1;
    }

    public static int getMonth(String q) {
        String[] months = {LocaleController.getString("January", R.string.January).toLowerCase(), LocaleController.getString("February", R.string.February).toLowerCase(), LocaleController.getString("March", R.string.March).toLowerCase(), LocaleController.getString("April", R.string.April).toLowerCase(), LocaleController.getString("May", R.string.May).toLowerCase(), LocaleController.getString("June", R.string.June).toLowerCase(), LocaleController.getString("July", R.string.July).toLowerCase(), LocaleController.getString("August", R.string.August).toLowerCase(), LocaleController.getString("September", R.string.September).toLowerCase(), LocaleController.getString("October", R.string.October).toLowerCase(), LocaleController.getString("November", R.string.November).toLowerCase(), LocaleController.getString("December", R.string.December).toLowerCase()};
        String[] monthsEng = new String[12];
        Calendar c = Calendar.getInstance();
        for (int i = 1; i <= 12; i++) {
            c.set(0, 0, 0, 0, 0, 0);
            c.set(2, i);
            monthsEng[i - 1] = c.getDisplayName(2, 2, Locale.ENGLISH).toLowerCase();
        }
        for (int i2 = 0; i2 < 12; i2++) {
            if (monthsEng[i2].startsWith(q) || months[i2].startsWith(q)) {
                return i2;
            }
        }
        return -1;
    }

    public static boolean isValidFormat(String format, String value, Locale locale) {
        DateTimeFormatter fomatter = DateTimeFormatter.ofPattern(format, locale);
        try {
            LocalDateTime ldt = LocalDateTime.parse(value, fomatter);
            String result = ldt.format(fomatter);
            return result.equals(value);
        } catch (DateTimeParseException e) {
            try {
                LocalDate ld = LocalDate.parse(value, fomatter);
                String result2 = ld.format(fomatter);
                return result2.equals(value);
            } catch (DateTimeParseException e2) {
                try {
                    LocalTime lt = LocalTime.parse(value, fomatter);
                    String result3 = lt.format(fomatter);
                    return result3.equals(value);
                } catch (DateTimeParseException e3) {
                    return false;
                }
            }
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView, android.view.View
    public void onDraw(Canvas c) {
        super.onDraw(c);
        c.drawRect(0.0f, getMeasuredHeight() - 1, getMeasuredWidth(), getMeasuredHeight(), Theme.dividerPaint);
    }

    public void updateColors() {
        getRecycledViewPool().clear();
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view instanceof FilterView) {
                ((FilterView) view).updateColors();
            }
        }
        for (int i2 = 0; i2 < getCachedChildCount(); i2++) {
            View view2 = getCachedChildAt(i2);
            if (view2 instanceof FilterView) {
                ((FilterView) view2).updateColors();
            }
        }
        for (int i3 = 0; i3 < getAttachedScrapChildCount(); i3++) {
            View view3 = getAttachedScrapChildAt(i3);
            if (view3 instanceof FilterView) {
                ((FilterView) view3).updateColors();
            }
        }
        setSelectorDrawableColor(getThemedColor(Theme.key_listSelector));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class Adapter extends RecyclerListView.SelectionAdapter {
        private Adapter() {
            FiltersView.this = r1;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ViewHolder holder = new ViewHolder(new FilterView(parent.getContext(), FiltersView.this.resourcesProvider));
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(-2, AndroidUtilities.dp(32.0f));
            lp.topMargin = AndroidUtilities.dp(6.0f);
            holder.itemView.setLayoutParams(lp);
            return holder;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            MediaFilterData data = (MediaFilterData) FiltersView.this.usersFilters.get(position);
            ((ViewHolder) holder).filterView.setData(data);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return FiltersView.this.usersFilters.size();
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return true;
        }
    }

    /* loaded from: classes4.dex */
    public static class FilterView extends FrameLayout {
        BackupImageView avatarImageView;
        MediaFilterData data;
        private final Theme.ResourcesProvider resourcesProvider;
        CombinedDrawable thumbDrawable;
        TextView titleView;

        public FilterView(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            this.resourcesProvider = resourcesProvider;
            BackupImageView backupImageView = new BackupImageView(context);
            this.avatarImageView = backupImageView;
            addView(backupImageView, LayoutHelper.createFrame(32, 32.0f));
            TextView textView = new TextView(context);
            this.titleView = textView;
            textView.setTextSize(1, 14.0f);
            addView(this.titleView, LayoutHelper.createFrame(-2, -2.0f, 16, 38.0f, 0.0f, 16.0f, 0.0f));
            updateColors();
        }

        public void updateColors() {
            setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(28.0f), getThemedColor(Theme.key_groupcreate_spanBackground)));
            this.titleView.setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteBlackText));
            if (this.thumbDrawable != null) {
                if (this.data.filterType == 7) {
                    Theme.setCombinedDrawableColor(this.thumbDrawable, getThemedColor(Theme.key_avatar_backgroundArchived), false);
                    Theme.setCombinedDrawableColor(this.thumbDrawable, getThemedColor(Theme.key_avatar_actionBarIconBlue), true);
                    return;
                }
                Theme.setCombinedDrawableColor(this.thumbDrawable, getThemedColor(Theme.key_avatar_backgroundBlue), false);
                Theme.setCombinedDrawableColor(this.thumbDrawable, getThemedColor(Theme.key_avatar_actionBarIconBlue), true);
            }
        }

        public void setData(MediaFilterData data) {
            this.data = data;
            this.avatarImageView.getImageReceiver().clearImage();
            if (data.filterType == 7) {
                CombinedDrawable createCircleDrawableWithIcon = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(32.0f), R.drawable.chats_archive);
                this.thumbDrawable = createCircleDrawableWithIcon;
                createCircleDrawableWithIcon.setIconSize(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f));
                Theme.setCombinedDrawableColor(this.thumbDrawable, getThemedColor(Theme.key_avatar_backgroundArchived), false);
                Theme.setCombinedDrawableColor(this.thumbDrawable, getThemedColor(Theme.key_avatar_actionBarIconBlue), true);
                this.avatarImageView.setImageDrawable(this.thumbDrawable);
                this.titleView.setText(data.title);
                return;
            }
            CombinedDrawable createCircleDrawableWithIcon2 = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(32.0f), data.iconResFilled);
            this.thumbDrawable = createCircleDrawableWithIcon2;
            Theme.setCombinedDrawableColor(createCircleDrawableWithIcon2, getThemedColor(Theme.key_avatar_backgroundBlue), false);
            Theme.setCombinedDrawableColor(this.thumbDrawable, getThemedColor(Theme.key_avatar_actionBarIconBlue), true);
            if (data.filterType == 4) {
                if (data.chat instanceof TLRPC.User) {
                    TLRPC.User user = (TLRPC.User) data.chat;
                    if (UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser().id == user.id) {
                        CombinedDrawable combinedDrawable = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(32.0f), R.drawable.chats_saved);
                        combinedDrawable.setIconSize(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f));
                        Theme.setCombinedDrawableColor(combinedDrawable, getThemedColor(Theme.key_avatar_backgroundSaved), false);
                        Theme.setCombinedDrawableColor(combinedDrawable, getThemedColor(Theme.key_avatar_actionBarIconBlue), true);
                        this.avatarImageView.setImageDrawable(combinedDrawable);
                    } else {
                        this.avatarImageView.getImageReceiver().setRoundRadius(AndroidUtilities.dp(16.0f));
                        this.avatarImageView.getImageReceiver().setForUserOrChat(user, this.thumbDrawable);
                    }
                } else if (data.chat instanceof TLRPC.Chat) {
                    TLRPC.Chat chat = (TLRPC.Chat) data.chat;
                    this.avatarImageView.getImageReceiver().setRoundRadius(AndroidUtilities.dp(16.0f));
                    this.avatarImageView.getImageReceiver().setForUserOrChat(chat, this.thumbDrawable);
                }
            } else {
                this.avatarImageView.setImageDrawable(this.thumbDrawable);
            }
            this.titleView.setText(data.title);
        }

        private int getThemedColor(String key) {
            Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
            Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
            return color != null ? color.intValue() : Theme.getColor(key);
        }
    }

    /* loaded from: classes4.dex */
    public class ViewHolder extends RecyclerView.ViewHolder {
        FilterView filterView;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public ViewHolder(FilterView itemView) {
            super(itemView);
            FiltersView.this = r1;
            this.filterView = itemView;
        }
    }

    /* loaded from: classes4.dex */
    public static class MediaFilterData {
        public TLObject chat;
        public DateData dateData;
        public final TLRPC.MessagesFilter filter;
        public final int filterType;
        public final int iconResFilled;
        public boolean removable = true;
        public final String title;

        public MediaFilterData(int iconResFilled, String title, TLRPC.MessagesFilter filter, int filterType) {
            this.iconResFilled = iconResFilled;
            this.title = title;
            this.filter = filter;
            this.filterType = filterType;
        }

        public void setUser(TLObject chat) {
            this.chat = chat;
        }

        public boolean isSameType(MediaFilterData filterData) {
            if (this.filterType == filterData.filterType) {
                return true;
            }
            return isMedia() && filterData.isMedia();
        }

        public boolean isMedia() {
            int i = this.filterType;
            return i == 0 || i == 1 || i == 2 || i == 3 || i == 5;
        }

        public void setDate(DateData dateData) {
            this.dateData = dateData;
        }
    }

    /* loaded from: classes4.dex */
    public static class DateData {
        public final long maxDate;
        public final long minDate;
        public final String title;

        private DateData(String title, long minDate, long maxDate) {
            this.title = title;
            this.minDate = minDate;
            this.maxDate = maxDate;
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this, 0, null, null, null, null, Theme.key_graySection));
        arrayList.add(new ThemeDescription(this, 0, null, null, null, null, Theme.key_graySectionText));
        return arrayList;
    }

    @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (!isEnabled()) {
            return false;
        }
        return super.onInterceptTouchEvent(e);
    }

    @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View
    public boolean onTouchEvent(MotionEvent e) {
        if (!isEnabled()) {
            return false;
        }
        return super.onTouchEvent(e);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class UpdateCallback implements ListUpdateCallback {
        final RecyclerView.Adapter adapter;
        boolean changed;

        private UpdateCallback(RecyclerView.Adapter adapter) {
            this.adapter = adapter;
        }

        @Override // androidx.recyclerview.widget.ListUpdateCallback
        public void onInserted(int position, int count) {
            this.changed = true;
            this.adapter.notifyItemRangeInserted(position, count);
        }

        @Override // androidx.recyclerview.widget.ListUpdateCallback
        public void onRemoved(int position, int count) {
            this.changed = true;
            this.adapter.notifyItemRangeRemoved(position, count);
        }

        @Override // androidx.recyclerview.widget.ListUpdateCallback
        public void onMoved(int fromPosition, int toPosition) {
            this.changed = true;
            this.adapter.notifyItemMoved(fromPosition, toPosition);
        }

        @Override // androidx.recyclerview.widget.ListUpdateCallback
        public void onChanged(int position, int count, Object payload) {
            this.adapter.notifyItemRangeChanged(position, count, payload);
        }
    }
}
