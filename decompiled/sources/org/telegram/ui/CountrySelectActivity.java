package org.telegram.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ReplacementSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import androidx.core.graphics.ColorUtils$$ExternalSyntheticBackport0;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.DividerCell;
import org.telegram.ui.Cells.LetterSectionCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.CountrySelectActivity;
/* loaded from: classes4.dex */
public class CountrySelectActivity extends BaseFragment {
    private CountrySelectActivityDelegate delegate;
    private EmptyTextProgressView emptyView;
    private ArrayList<Country> existingCountries;
    private RecyclerListView listView;
    private CountryAdapter listViewAdapter;
    private boolean needPhoneCode;
    private CountrySearchAdapter searchListViewAdapter;
    private boolean searchWas;
    private boolean searching;

    /* loaded from: classes4.dex */
    public interface CountrySelectActivityDelegate {
        void didSelectCountry(Country country);
    }

    public CountrySelectActivity(boolean phoneCode) {
        this(phoneCode, null);
    }

    public CountrySelectActivity(boolean phoneCode, ArrayList<Country> existingCountries) {
        if (existingCountries != null && !existingCountries.isEmpty()) {
            this.existingCountries = new ArrayList<>(existingCountries);
        }
        this.needPhoneCode = phoneCode;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        return super.onFragmentCreate();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean hasForceLightStatusBar() {
        return true;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(false);
        this.actionBar.setTitle(LocaleController.getString("ChooseCountry", R.string.ChooseCountry));
        this.actionBar.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.actionBar.setItemsColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_actionBarWhiteSelector), false);
        this.actionBar.setTitleColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.CountrySelectActivity.1
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int id) {
                if (id == -1) {
                    CountrySelectActivity.this.finishFragment();
                }
            }
        });
        ActionBarMenu menu = this.actionBar.createMenu();
        int i = 1;
        ActionBarMenuItem item = menu.addItem(0, R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() { // from class: org.telegram.ui.CountrySelectActivity.2
            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public void onSearchExpand() {
                CountrySelectActivity.this.searching = true;
            }

            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public void onSearchCollapse() {
                CountrySelectActivity.this.searchListViewAdapter.search(null);
                CountrySelectActivity.this.searching = false;
                CountrySelectActivity.this.searchWas = false;
                CountrySelectActivity.this.listView.setAdapter(CountrySelectActivity.this.listViewAdapter);
                CountrySelectActivity.this.listView.setFastScrollVisible(true);
            }

            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public void onTextChanged(EditText editText) {
                String text = editText.getText().toString();
                if (TextUtils.isEmpty(text)) {
                    CountrySelectActivity.this.searchListViewAdapter.search(null);
                    CountrySelectActivity.this.searchWas = false;
                    CountrySelectActivity.this.listView.setAdapter(CountrySelectActivity.this.listViewAdapter);
                    CountrySelectActivity.this.listView.setFastScrollVisible(true);
                    return;
                }
                CountrySelectActivity.this.searchListViewAdapter.search(text);
                if (text.length() != 0) {
                    CountrySelectActivity.this.searchWas = true;
                }
            }
        });
        item.setSearchFieldHint(LocaleController.getString("Search", R.string.Search));
        this.actionBar.setSearchTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText), true);
        this.actionBar.setSearchTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText), false);
        this.actionBar.setSearchCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.searching = false;
        this.searchWas = false;
        this.listViewAdapter = new CountryAdapter(context, this.existingCountries);
        this.searchListViewAdapter = new CountrySearchAdapter(context, this.listViewAdapter.getCountries());
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context);
        this.emptyView = emptyTextProgressView;
        emptyTextProgressView.showTextView();
        this.emptyView.setShowAtCenter(true);
        this.emptyView.setText(LocaleController.getString("NoResult", R.string.NoResult));
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setSectionsType(3);
        this.listView.setEmptyView(this.emptyView);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setFastScrollEnabled(0);
        this.listView.setFastScrollVisible(true);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        this.listView.setAdapter(this.listViewAdapter);
        RecyclerListView recyclerListView2 = this.listView;
        if (!LocaleController.isRTL) {
            i = 2;
        }
        recyclerListView2.setVerticalScrollbarPosition(i);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.CountrySelectActivity$$ExternalSyntheticLambda0
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i2) {
                CountrySelectActivity.this.m3297lambda$createView$0$orgtelegramuiCountrySelectActivity(view, i2);
            }
        });
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.CountrySelectActivity.3
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 1) {
                    AndroidUtilities.hideKeyboard(CountrySelectActivity.this.getParentActivity().getCurrentFocus());
                }
            }
        });
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-CountrySelectActivity */
    public /* synthetic */ void m3297lambda$createView$0$orgtelegramuiCountrySelectActivity(View view, int position) {
        Country country;
        CountrySelectActivityDelegate countrySelectActivityDelegate;
        if (this.searching && this.searchWas) {
            country = this.searchListViewAdapter.getItem(position);
        } else {
            int section = this.listViewAdapter.getSectionForPosition(position);
            int row = this.listViewAdapter.getPositionInSectionForPosition(position);
            if (row < 0 || section < 0) {
                return;
            }
            country = this.listViewAdapter.getItem(section, row);
        }
        if (position < 0) {
            return;
        }
        finishFragment();
        if (country != null && (countrySelectActivityDelegate = this.delegate) != null) {
            countrySelectActivityDelegate.didSelectCountry(country);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        super.onResume();
        CountryAdapter countryAdapter = this.listViewAdapter;
        if (countryAdapter != null) {
            countryAdapter.notifyDataSetChanged();
        }
    }

    public void setCountrySelectActivityDelegate(CountrySelectActivityDelegate delegate) {
        this.delegate = delegate;
    }

    /* loaded from: classes4.dex */
    public static class Country {
        public String code;
        public String name;
        public String shortname;

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Country that = (Country) o;
            return ColorUtils$$ExternalSyntheticBackport0.m(this.name, that.name) && ColorUtils$$ExternalSyntheticBackport0.m(this.code, that.code);
        }

        public int hashCode() {
            return Arrays.hashCode(new Object[]{this.name, this.code});
        }
    }

    /* loaded from: classes4.dex */
    public class CountryAdapter extends RecyclerListView.SectionsAdapter {
        private static final int TYPE_COUNTRY = 0;
        private static final int TYPE_DIVIDER = 1;
        private Context mContext;
        private HashMap<String, ArrayList<Country>> countries = new HashMap<>();
        private ArrayList<String> sortedCountries = new ArrayList<>();

        public CountryAdapter(Context context, ArrayList<Country> exisitingCountries) {
            CountrySelectActivity.this = this$0;
            this.mContext = context;
            if (exisitingCountries != null) {
                for (int i = 0; i < exisitingCountries.size(); i++) {
                    Country c = exisitingCountries.get(i);
                    String n = c.name.substring(0, 1).toUpperCase();
                    ArrayList<Country> arr = this.countries.get(n);
                    if (arr == null) {
                        arr = new ArrayList<>();
                        this.countries.put(n, arr);
                        this.sortedCountries.add(n);
                    }
                    arr.add(c);
                }
            } else {
                try {
                    InputStream stream = ApplicationLoader.applicationContext.getResources().getAssets().open("countries.txt");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                    while (true) {
                        String line = reader.readLine();
                        if (line == null) {
                            break;
                        }
                        String[] args = line.split(";");
                        Country c2 = new Country();
                        c2.name = args[2];
                        c2.code = args[0];
                        c2.shortname = args[1];
                        String n2 = c2.name.substring(0, 1).toUpperCase();
                        ArrayList<Country> arr2 = this.countries.get(n2);
                        if (arr2 == null) {
                            arr2 = new ArrayList<>();
                            this.countries.put(n2, arr2);
                            this.sortedCountries.add(n2);
                        }
                        arr2.add(c2);
                    }
                    reader.close();
                    stream.close();
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            Collections.sort(this.sortedCountries, CountrySelectActivity$CountryAdapter$$ExternalSyntheticLambda0.INSTANCE);
            for (ArrayList<Country> arr3 : this.countries.values()) {
                Collections.sort(arr3, CountrySelectActivity$CountryAdapter$$ExternalSyntheticLambda1.INSTANCE);
            }
        }

        public HashMap<String, ArrayList<Country>> getCountries() {
            return this.countries;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
        public Country getItem(int section, int position) {
            if (section < 0 || section >= this.sortedCountries.size()) {
                return null;
            }
            ArrayList<Country> arr = this.countries.get(this.sortedCountries.get(section));
            if (position < 0 || position >= arr.size()) {
                return null;
            }
            return arr.get(position);
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder, int section, int row) {
            ArrayList<Country> arr = this.countries.get(this.sortedCountries.get(section));
            return row < arr.size();
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
        public int getSectionCount() {
            return this.sortedCountries.size();
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
        public int getCountForSection(int section) {
            int count = this.countries.get(this.sortedCountries.get(section)).size();
            if (section != this.sortedCountries.size() - 1) {
                return count + 1;
            }
            return count;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
        public View getSectionHeaderView(int section, View view) {
            return null;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = CountrySelectActivity.createSettingsCell(this.mContext);
                    break;
                default:
                    view = new DividerCell(this.mContext);
                    view.setPadding(AndroidUtilities.dp(24.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(24.0f), AndroidUtilities.dp(8.0f));
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
        public void onBindViewHolder(int section, int position, RecyclerView.ViewHolder holder) {
            String str;
            if (holder.getItemViewType() == 0) {
                ArrayList<Country> arr = this.countries.get(this.sortedCountries.get(section));
                Country c = arr.get(position);
                TextSettingsCell settingsCell = (TextSettingsCell) holder.itemView;
                CharSequence replaceEmoji = Emoji.replaceEmoji(CountrySelectActivity.getCountryNameWithFlag(c), settingsCell.getTextView().getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
                if (CountrySelectActivity.this.needPhoneCode) {
                    str = "+" + c.code;
                } else {
                    str = null;
                }
                settingsCell.setTextAndValue(replaceEmoji, str, false);
            }
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
        public int getItemViewType(int section, int position) {
            ArrayList<Country> arr = this.countries.get(this.sortedCountries.get(section));
            return position < arr.size() ? 0 : 1;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.FastScrollAdapter
        public String getLetter(int position) {
            int section = getSectionForPosition(position);
            if (section == -1) {
                section = this.sortedCountries.size() - 1;
            }
            return this.sortedCountries.get(section);
        }

        @Override // org.telegram.ui.Components.RecyclerListView.FastScrollAdapter
        public void getPositionForScrollProgress(RecyclerListView listView, float progress, int[] position) {
            position[0] = (int) (getItemCount() * progress);
            position[1] = 0;
        }
    }

    /* loaded from: classes4.dex */
    public class CountrySearchAdapter extends RecyclerListView.SelectionAdapter {
        private List<Country> countryList = new ArrayList();
        private Map<Country, List<String>> countrySearchMap = new HashMap();
        private Context mContext;
        private ArrayList<Country> searchResult;
        private Timer searchTimer;

        public CountrySearchAdapter(Context context, HashMap<String, ArrayList<Country>> countries) {
            CountrySelectActivity.this = this$0;
            this.mContext = context;
            for (List<Country> list : countries.values()) {
                for (Country country : list) {
                    this.countryList.add(country);
                    this.countrySearchMap.put(country, Arrays.asList(country.name.split(" ")));
                }
            }
        }

        public void search(final String query) {
            if (query == null) {
                this.searchResult = null;
                return;
            }
            try {
                Timer timer = this.searchTimer;
                if (timer != null) {
                    timer.cancel();
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
            Timer timer2 = new Timer();
            this.searchTimer = timer2;
            timer2.schedule(new TimerTask() { // from class: org.telegram.ui.CountrySelectActivity.CountrySearchAdapter.1
                @Override // java.util.TimerTask, java.lang.Runnable
                public void run() {
                    try {
                        CountrySearchAdapter.this.searchTimer.cancel();
                        CountrySearchAdapter.this.searchTimer = null;
                    } catch (Exception e2) {
                        FileLog.e(e2);
                    }
                    CountrySearchAdapter.this.processSearch(query);
                }
            }, 100L, 300L);
        }

        public void processSearch(final String query) {
            Utilities.searchQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.CountrySelectActivity$CountrySearchAdapter$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    CountrySelectActivity.CountrySearchAdapter.this.m3298xf9a8c8c8(query);
                }
            });
        }

        /* renamed from: lambda$processSearch$0$org-telegram-ui-CountrySelectActivity$CountrySearchAdapter */
        public /* synthetic */ void m3298xf9a8c8c8(String query) {
            String q = query.trim().toLowerCase();
            if (q.length() == 0) {
                updateSearchResults(new ArrayList<>());
                return;
            }
            ArrayList<Country> resultArray = new ArrayList<>();
            for (Country country : this.countryList) {
                Iterator<String> it = this.countrySearchMap.get(country).iterator();
                while (true) {
                    if (it.hasNext()) {
                        String key = it.next();
                        if (key.toLowerCase().startsWith(q)) {
                            resultArray.add(country);
                            break;
                        }
                    }
                }
            }
            updateSearchResults(resultArray);
        }

        private void updateSearchResults(final ArrayList<Country> arrCounties) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.CountrySelectActivity$CountrySearchAdapter$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    CountrySelectActivity.CountrySearchAdapter.this.m3299x856d2339(arrCounties);
                }
            });
        }

        /* renamed from: lambda$updateSearchResults$1$org-telegram-ui-CountrySelectActivity$CountrySearchAdapter */
        public /* synthetic */ void m3299x856d2339(ArrayList arrCounties) {
            if (!CountrySelectActivity.this.searching) {
                return;
            }
            this.searchResult = arrCounties;
            if (CountrySelectActivity.this.searchWas && CountrySelectActivity.this.listView != null && CountrySelectActivity.this.listView.getAdapter() != CountrySelectActivity.this.searchListViewAdapter) {
                CountrySelectActivity.this.listView.setAdapter(CountrySelectActivity.this.searchListViewAdapter);
                CountrySelectActivity.this.listView.setFastScrollVisible(false);
            }
            notifyDataSetChanged();
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return true;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            ArrayList<Country> arrayList = this.searchResult;
            if (arrayList == null) {
                return 0;
            }
            return arrayList.size();
        }

        public Country getItem(int i) {
            if (i < 0 || i >= this.searchResult.size()) {
                return null;
            }
            return this.searchResult.get(i);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerListView.Holder(CountrySelectActivity.createSettingsCell(this.mContext));
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            String str;
            Country c = this.searchResult.get(position);
            TextSettingsCell settingsCell = (TextSettingsCell) holder.itemView;
            CharSequence replaceEmoji = Emoji.replaceEmoji(CountrySelectActivity.getCountryNameWithFlag(c), settingsCell.getTextView().getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
            if (CountrySelectActivity.this.needPhoneCode) {
                str = "+" + c.code;
            } else {
                str = null;
            }
            settingsCell.setTextAndValue(replaceEmoji, str, false);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            return 0;
        }
    }

    public static TextSettingsCell createSettingsCell(Context context) {
        TextSettingsCell view = new TextSettingsCell(context);
        float f = 16.0f;
        int dp = AndroidUtilities.dp(LocaleController.isRTL ? 16.0f : 12.0f);
        if (LocaleController.isRTL) {
            f = 12.0f;
        }
        view.setPadding(dp, 0, AndroidUtilities.dp(f), 0);
        view.addOnAttachStateChangeListener(new AnonymousClass4(view));
        return view;
    }

    /* renamed from: org.telegram.ui.CountrySelectActivity$4 */
    /* loaded from: classes4.dex */
    public class AnonymousClass4 implements View.OnAttachStateChangeListener {
        private NotificationCenter.NotificationCenterDelegate listener;
        final /* synthetic */ TextSettingsCell val$view;

        AnonymousClass4(final TextSettingsCell textSettingsCell) {
            this.val$view = textSettingsCell;
            this.listener = new NotificationCenter.NotificationCenterDelegate() { // from class: org.telegram.ui.CountrySelectActivity$4$$ExternalSyntheticLambda0
                @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
                public final void didReceivedNotification(int i, int i2, Object[] objArr) {
                    CountrySelectActivity.AnonymousClass4.lambda$$0(TextSettingsCell.this, i, i2, objArr);
                }
            };
        }

        public static /* synthetic */ void lambda$$0(TextSettingsCell view, int id, int account, Object[] args) {
            if (id == NotificationCenter.emojiLoaded) {
                view.getTextView().invalidate();
            }
        }

        @Override // android.view.View.OnAttachStateChangeListener
        public void onViewAttachedToWindow(View v) {
            NotificationCenter.getGlobalInstance().addObserver(this.listener, NotificationCenter.emojiLoaded);
        }

        @Override // android.view.View.OnAttachStateChangeListener
        public void onViewDetachedFromWindow(View v) {
            NotificationCenter.getGlobalInstance().removeObserver(this.listener, NotificationCenter.emojiLoaded);
        }
    }

    public static CharSequence getCountryNameWithFlag(Country c) {
        SpannableStringBuilder sb = new SpannableStringBuilder();
        String flag = LocaleController.getLanguageFlag(c.shortname);
        if (flag != null) {
            sb.append((CharSequence) flag).append((CharSequence) " ");
            sb.setSpan(new ReplacementSpan() { // from class: org.telegram.ui.CountrySelectActivity.5
                @Override // android.text.style.ReplacementSpan
                public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
                    return AndroidUtilities.dp(16.0f);
                }

                @Override // android.text.style.ReplacementSpan
                public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
                }
            }, flag.length(), flag.length() + 1, 0);
        }
        sb.append((CharSequence) c.name);
        return sb;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, Theme.key_actionBarDefaultSearch));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, Theme.key_actionBarDefaultSearchPlaceholder));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, Theme.key_fastScrollActive));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, Theme.key_fastScrollInactive));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, Theme.key_fastScrollText));
        themeDescriptions.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_emptyListPlaceholder));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteValueText));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SECTIONS, new Class[]{LetterSectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4));
        return themeDescriptions;
    }
}
