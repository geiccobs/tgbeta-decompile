package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import j$.util.Collection;
import j$.util.function.Predicate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Timer;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.LanguageCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckbox2Cell;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
/* loaded from: classes4.dex */
public class RestrictedLanguagesSelectActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private EmptyTextProgressView emptyView;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    private SharedPreferences preferences;
    private ListAdapter searchListViewAdapter;
    private ArrayList<LocaleController.LocaleInfo> searchResult;
    private Timer searchTimer;
    private boolean searchWas;
    private boolean searching;
    private HashSet<String> selectedLanguages = null;
    private ArrayList<LocaleController.LocaleInfo> sortedLanguages;

    public static HashSet<String> getRestrictedLanguages() {
        return new HashSet<>(MessagesController.getGlobalMainSettings().getStringSet("translate_button_restricted_languages", new HashSet()));
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        this.preferences = MessagesController.getGlobalMainSettings();
        this.selectedLanguages = getRestrictedLanguages();
        SharedPreferences sharedPreferences = this.preferences;
        SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() { // from class: org.telegram.ui.RestrictedLanguagesSelectActivity.1
            public int langPos(String lng) {
                if (lng != null) {
                    ArrayList<LocaleController.LocaleInfo> arr = RestrictedLanguagesSelectActivity.this.searching ? RestrictedLanguagesSelectActivity.this.searchResult : RestrictedLanguagesSelectActivity.this.sortedLanguages;
                    if (arr == null) {
                        return -1;
                    }
                    for (int i = 0; i < arr.size(); i++) {
                        if (lng.equals(arr.get(i).pluralLangCode)) {
                            return i;
                        }
                    }
                    return -1;
                }
                return -1;
            }

            @Override // android.content.SharedPreferences.OnSharedPreferenceChangeListener
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences2, String s) {
                RestrictedLanguagesSelectActivity.this.preferences = sharedPreferences2;
                HashSet<String> newSelectedLanguages = RestrictedLanguagesSelectActivity.getRestrictedLanguages();
                if (RestrictedLanguagesSelectActivity.this.listView != null && RestrictedLanguagesSelectActivity.this.listView.getAdapter() != null) {
                    RecyclerView.Adapter adapter = RestrictedLanguagesSelectActivity.this.listView.getAdapter();
                    int offset = !RestrictedLanguagesSelectActivity.this.searching ? 1 : 0;
                    Iterator it = RestrictedLanguagesSelectActivity.this.selectedLanguages.iterator();
                    while (it.hasNext()) {
                        String lng = (String) it.next();
                        if (!newSelectedLanguages.contains(lng)) {
                            adapter.notifyItemChanged(langPos(lng) + offset);
                        }
                    }
                    Iterator<String> it2 = newSelectedLanguages.iterator();
                    while (it2.hasNext()) {
                        String lng2 = it2.next();
                        if (!RestrictedLanguagesSelectActivity.this.selectedLanguages.contains(lng2)) {
                            adapter.notifyItemChanged(langPos(lng2) + offset);
                        }
                    }
                }
                RestrictedLanguagesSelectActivity.this.selectedLanguages = newSelectedLanguages;
            }
        };
        this.listener = onSharedPreferenceChangeListener;
        sharedPreferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
        fillLanguages();
        LocaleController.getInstance().loadRemoteLanguages(this.currentAccount);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.suggestedLangpack);
        return super.onFragmentCreate();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        this.preferences.unregisterOnSharedPreferenceChangeListener(this.listener);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.suggestedLangpack);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        this.searching = false;
        this.searchWas = false;
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("DoNotTranslate", R.string.DoNotTranslate));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.RestrictedLanguagesSelectActivity.2
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int id) {
                if (id == -1) {
                    RestrictedLanguagesSelectActivity.this.finishFragment();
                }
            }
        });
        ActionBarMenu menu = this.actionBar.createMenu();
        ActionBarMenuItem item = menu.addItem(0, R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() { // from class: org.telegram.ui.RestrictedLanguagesSelectActivity.3
            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public void onSearchExpand() {
                RestrictedLanguagesSelectActivity.this.searching = true;
            }

            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public void onSearchCollapse() {
                RestrictedLanguagesSelectActivity.this.search(null);
                RestrictedLanguagesSelectActivity.this.searching = false;
                RestrictedLanguagesSelectActivity.this.searchWas = false;
                if (RestrictedLanguagesSelectActivity.this.listView != null) {
                    RestrictedLanguagesSelectActivity.this.emptyView.setVisibility(8);
                    RestrictedLanguagesSelectActivity.this.listView.setAdapter(RestrictedLanguagesSelectActivity.this.listAdapter);
                }
            }

            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public void onTextChanged(EditText editText) {
                String text = editText.getText().toString();
                RestrictedLanguagesSelectActivity.this.search(text);
                if (text.length() != 0) {
                    RestrictedLanguagesSelectActivity.this.searchWas = true;
                    if (RestrictedLanguagesSelectActivity.this.listView != null) {
                        RestrictedLanguagesSelectActivity.this.listView.setAdapter(RestrictedLanguagesSelectActivity.this.searchListViewAdapter);
                        return;
                    }
                    return;
                }
                RestrictedLanguagesSelectActivity.this.searching = false;
                RestrictedLanguagesSelectActivity.this.searchWas = false;
                if (RestrictedLanguagesSelectActivity.this.listView != null) {
                    RestrictedLanguagesSelectActivity.this.emptyView.setVisibility(8);
                    RestrictedLanguagesSelectActivity.this.listView.setAdapter(RestrictedLanguagesSelectActivity.this.listAdapter);
                }
            }
        });
        item.setSearchFieldHint(LocaleController.getString("Search", R.string.Search));
        this.listAdapter = new ListAdapter(context, false);
        this.searchListViewAdapter = new ListAdapter(context, true);
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context);
        this.emptyView = emptyTextProgressView;
        emptyTextProgressView.setText(LocaleController.getString("NoResult", R.string.NoResult));
        this.emptyView.showTextView();
        this.emptyView.setShowAtCenter(true);
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setEmptyView(this.emptyView);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setAdapter(this.listAdapter);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.RestrictedLanguagesSelectActivity$$ExternalSyntheticLambda4
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i) {
                RestrictedLanguagesSelectActivity.this.m4534xb6e1babc(view, i);
            }
        });
        this.listView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() { // from class: org.telegram.ui.RestrictedLanguagesSelectActivity$$ExternalSyntheticLambda5
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener
            public final boolean onItemClick(View view, int i) {
                return RestrictedLanguagesSelectActivity.this.m4536x3df7f63e(view, i);
            }
        });
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.RestrictedLanguagesSelectActivity.4
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 1) {
                    AndroidUtilities.hideKeyboard(RestrictedLanguagesSelectActivity.this.getParentActivity().getCurrentFocus());
                }
            }
        });
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-RestrictedLanguagesSelectActivity */
    public /* synthetic */ void m4534xb6e1babc(View view, int position) {
        LocaleController.LocaleInfo localeInfo;
        if (getParentActivity() == null || this.parentLayout == null || !(view instanceof TextCheckbox2Cell)) {
            return;
        }
        boolean search = this.listView.getAdapter() == this.searchListViewAdapter;
        if (!search) {
            position--;
        }
        if (search) {
            localeInfo = this.searchResult.get(position);
        } else {
            localeInfo = this.sortedLanguages.get(position);
        }
        if (localeInfo != null) {
            LocaleController.LocaleInfo currentLocaleInfo = LocaleController.getInstance().getCurrentLocaleInfo();
            final String langCode = localeInfo.pluralLangCode;
            if (langCode != null && langCode.equals(currentLocaleInfo.pluralLangCode)) {
                AndroidUtilities.shakeView(((TextCheckbox2Cell) view).checkbox, 2.0f, 0);
                return;
            }
            boolean value = this.selectedLanguages.contains(langCode);
            HashSet<String> newSelectedLanguages = new HashSet<>(this.selectedLanguages);
            if (value) {
                Collection.EL.removeIf(newSelectedLanguages, new Predicate() { // from class: org.telegram.ui.RestrictedLanguagesSelectActivity$$ExternalSyntheticLambda3
                    @Override // j$.util.function.Predicate
                    public /* synthetic */ Predicate and(Predicate predicate) {
                        return predicate.getClass();
                    }

                    @Override // j$.util.function.Predicate
                    public /* synthetic */ Predicate negate() {
                        return Predicate.CC.$default$negate(this);
                    }

                    @Override // j$.util.function.Predicate
                    public /* synthetic */ Predicate or(Predicate predicate) {
                        return predicate.getClass();
                    }

                    @Override // j$.util.function.Predicate
                    public final boolean test(Object obj) {
                        return RestrictedLanguagesSelectActivity.lambda$createView$0(langCode, (String) obj);
                    }
                });
            } else {
                newSelectedLanguages.add(langCode);
            }
            if (newSelectedLanguages.size() == 1 && newSelectedLanguages.contains(currentLocaleInfo.pluralLangCode)) {
                this.preferences.edit().remove("translate_button_restricted_languages").apply();
            } else {
                this.preferences.edit().putStringSet("translate_button_restricted_languages", newSelectedLanguages).apply();
            }
        }
    }

    public static /* synthetic */ boolean lambda$createView$0(String langCode, String s) {
        return s != null && s.equals(langCode);
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-RestrictedLanguagesSelectActivity */
    public /* synthetic */ boolean m4536x3df7f63e(View view, int position) {
        LocaleController.LocaleInfo localeInfo;
        if (getParentActivity() == null || this.parentLayout == null || !(view instanceof TextCheckbox2Cell)) {
            return false;
        }
        boolean search = this.listView.getAdapter() == this.searchListViewAdapter;
        if (!search) {
            position--;
        }
        if (search) {
            localeInfo = this.searchResult.get(position);
        } else {
            localeInfo = this.sortedLanguages.get(position);
        }
        if (localeInfo == null || localeInfo.pathToFile == null || (localeInfo.isRemote() && localeInfo.serverIndex != Integer.MAX_VALUE)) {
            return false;
        }
        final LocaleController.LocaleInfo finalLocaleInfo = localeInfo;
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("DeleteLocalizationTitle", R.string.DeleteLocalizationTitle));
        builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("DeleteLocalizationText", R.string.DeleteLocalizationText, localeInfo.name)));
        builder.setPositiveButton(LocaleController.getString("Delete", R.string.Delete), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.RestrictedLanguagesSelectActivity$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                RestrictedLanguagesSelectActivity.this.m4535xfa6cd87d(finalLocaleInfo, dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        AlertDialog alertDialog = builder.create();
        showDialog(alertDialog);
        TextView button = (TextView) alertDialog.getButton(-1);
        if (button != null) {
            button.setTextColor(Theme.getColor(Theme.key_dialogTextRed2));
        }
        return true;
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-RestrictedLanguagesSelectActivity */
    public /* synthetic */ void m4535xfa6cd87d(LocaleController.LocaleInfo finalLocaleInfo, DialogInterface dialogInterface, int i) {
        if (LocaleController.getInstance().deleteLanguage(finalLocaleInfo, this.currentAccount)) {
            fillLanguages();
            ArrayList<LocaleController.LocaleInfo> arrayList = this.searchResult;
            if (arrayList != null) {
                arrayList.remove(finalLocaleInfo);
            }
            ListAdapter listAdapter = this.listAdapter;
            if (listAdapter != null) {
                listAdapter.notifyDataSetChanged();
            }
            ListAdapter listAdapter2 = this.searchListViewAdapter;
            if (listAdapter2 != null) {
                listAdapter2.notifyDataSetChanged();
            }
        }
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.suggestedLangpack && this.listAdapter != null) {
            fillLanguages();
            this.listAdapter.notifyDataSetChanged();
        }
    }

    private void fillLanguages() {
        final LocaleController.LocaleInfo currentLocale = LocaleController.getInstance().getCurrentLocaleInfo();
        Comparator<LocaleController.LocaleInfo> comparator = new Comparator() { // from class: org.telegram.ui.RestrictedLanguagesSelectActivity$$ExternalSyntheticLambda2
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                return RestrictedLanguagesSelectActivity.lambda$fillLanguages$4(LocaleController.LocaleInfo.this, (LocaleController.LocaleInfo) obj, (LocaleController.LocaleInfo) obj2);
            }
        };
        this.sortedLanguages = new ArrayList<>();
        ArrayList<LocaleController.LocaleInfo> arrayList = LocaleController.getInstance().languages;
        int size = arrayList.size();
        for (int a = 0; a < size; a++) {
            LocaleController.LocaleInfo info = arrayList.get(a);
            if (info != null && info.serverIndex != Integer.MAX_VALUE) {
                this.sortedLanguages.add(info);
            }
        }
        Collections.sort(this.sortedLanguages, comparator);
    }

    public static /* synthetic */ int lambda$fillLanguages$4(LocaleController.LocaleInfo currentLocale, LocaleController.LocaleInfo o, LocaleController.LocaleInfo o2) {
        if (o == currentLocale) {
            return -1;
        }
        if (o2 == currentLocale) {
            return 1;
        }
        if (o.serverIndex == o2.serverIndex) {
            return o.name.compareTo(o2.name);
        }
        if (o.serverIndex > o2.serverIndex) {
            return 1;
        }
        if (o.serverIndex < o2.serverIndex) {
            return -1;
        }
        return 0;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    public void search(String query) {
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
        processSearch(query);
    }

    private void processSearch(String query) {
        String q = query.trim().toLowerCase();
        if (q.length() == 0) {
            updateSearchResults(new ArrayList<>());
            return;
        }
        System.currentTimeMillis();
        ArrayList<LocaleController.LocaleInfo> resultArray = new ArrayList<>();
        int N = this.sortedLanguages.size();
        for (int a = 0; a < N; a++) {
            LocaleController.LocaleInfo c = this.sortedLanguages.get(a);
            if (c.name.toLowerCase().startsWith(query) || c.nameEnglish.toLowerCase().startsWith(query)) {
                resultArray.add(c);
            }
        }
        updateSearchResults(resultArray);
    }

    private void updateSearchResults(final ArrayList<LocaleController.LocaleInfo> arrCounties) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.RestrictedLanguagesSelectActivity$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                RestrictedLanguagesSelectActivity.this.m4537x7d99288a(arrCounties);
            }
        });
    }

    /* renamed from: lambda$updateSearchResults$5$org-telegram-ui-RestrictedLanguagesSelectActivity */
    public /* synthetic */ void m4537x7d99288a(ArrayList arrCounties) {
        this.searchResult = arrCounties;
        this.searchListViewAdapter.notifyDataSetChanged();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;
        private boolean search;

        public ListAdapter(Context context, boolean isSearch) {
            RestrictedLanguagesSelectActivity.this = r1;
            this.mContext = context;
            this.search = isSearch;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() == 0;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            if (this.search) {
                if (RestrictedLanguagesSelectActivity.this.searchResult != null) {
                    return RestrictedLanguagesSelectActivity.this.searchResult.size();
                }
                return 0;
            }
            int count = RestrictedLanguagesSelectActivity.this.sortedLanguages.size();
            return count + 1;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    View view2 = new TextCheckbox2Cell(this.mContext);
                    view2.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view = view2;
                    break;
                case 1:
                default:
                    view = new ShadowSectionCell(this.mContext);
                    break;
                case 2:
                    HeaderCell header = new HeaderCell(this.mContext);
                    header.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    header.setText(LocaleController.getString("ChooseLanguages", R.string.ChooseLanguages));
                    view = header;
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            boolean last;
            LocaleController.LocaleInfo localeInfo;
            switch (holder.getItemViewType()) {
                case 0:
                    if (!this.search) {
                        position--;
                    }
                    TextCheckbox2Cell textSettingsCell = (TextCheckbox2Cell) holder.itemView;
                    boolean z = false;
                    if (this.search) {
                        localeInfo = (LocaleController.LocaleInfo) RestrictedLanguagesSelectActivity.this.searchResult.get(position);
                        last = position == RestrictedLanguagesSelectActivity.this.searchResult.size() - 1;
                    } else {
                        localeInfo = (LocaleController.LocaleInfo) RestrictedLanguagesSelectActivity.this.sortedLanguages.get(position);
                        last = position == RestrictedLanguagesSelectActivity.this.sortedLanguages.size() - 1;
                    }
                    String langCode = localeInfo.pluralLangCode;
                    boolean value = RestrictedLanguagesSelectActivity.this.selectedLanguages.contains(langCode);
                    if (localeInfo.isLocal()) {
                        textSettingsCell.setTextAndValue(String.format("%1$s (%2$s)", localeInfo.name, LocaleController.getString("LanguageCustom", R.string.LanguageCustom)), localeInfo.nameEnglish, false, !last);
                    } else {
                        textSettingsCell.setTextAndValue(localeInfo.name, localeInfo.nameEnglish, false, !last);
                    }
                    boolean isCurrent = langCode != null && langCode.equals(LocaleController.getInstance().getCurrentLocaleInfo().pluralLangCode);
                    if (value || isCurrent) {
                        z = true;
                    }
                    textSettingsCell.setChecked(z);
                    return;
                case 1:
                    if (!this.search) {
                        int i = position - 1;
                    }
                    ShadowSectionCell sectionCell = (ShadowSectionCell) holder.itemView;
                    sectionCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    return;
                default:
                    return;
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            if (!this.search) {
                i--;
            }
            if (i == -1) {
                return 2;
            }
            return 0;
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{LanguageCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, Theme.key_actionBarDefaultSearch));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, Theme.key_actionBarDefaultSearchPlaceholder));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_emptyListPlaceholder));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{LanguageCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{LanguageCell.class}, new String[]{"textView2"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText3));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{LanguageCell.class}, new String[]{"checkImage"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_featuredStickers_addedIcon));
        return themeDescriptions;
    }
}
