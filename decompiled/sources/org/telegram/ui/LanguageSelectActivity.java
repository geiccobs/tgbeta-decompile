package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.C;
import j$.util.Collection;
import j$.util.function.Predicate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Timer;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LanguageDetector;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.Utilities;
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
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextRadioCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.LanguageSelectActivity;
/* loaded from: classes4.dex */
public class LanguageSelectActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private EmptyTextProgressView emptyView;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private ListAdapter searchListViewAdapter;
    private ArrayList<LocaleController.LocaleInfo> searchResult;
    private Timer searchTimer;
    private boolean searchWas;
    private boolean searching;
    private ArrayList<LocaleController.LocaleInfo> sortedLanguages;
    private ArrayList<LocaleController.LocaleInfo> unofficialLanguages;

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        fillLanguages();
        LocaleController.getInstance().loadRemoteLanguages(this.currentAccount, false);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.suggestedLangpack);
        return super.onFragmentCreate();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.suggestedLangpack);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        this.searching = false;
        this.searchWas = false;
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("Language", R.string.Language));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.LanguageSelectActivity.1
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int id) {
                if (id == -1) {
                    LanguageSelectActivity.this.finishFragment();
                }
            }
        });
        ActionBarMenu menu = this.actionBar.createMenu();
        ActionBarMenuItem item = menu.addItem(0, R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() { // from class: org.telegram.ui.LanguageSelectActivity.2
            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public void onSearchExpand() {
                LanguageSelectActivity.this.searching = true;
            }

            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public void onSearchCollapse() {
                LanguageSelectActivity.this.search(null);
                LanguageSelectActivity.this.searching = false;
                LanguageSelectActivity.this.searchWas = false;
                if (LanguageSelectActivity.this.listView != null) {
                    LanguageSelectActivity.this.emptyView.setVisibility(8);
                    LanguageSelectActivity.this.listView.setAdapter(LanguageSelectActivity.this.listAdapter);
                }
            }

            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public void onTextChanged(EditText editText) {
                String text = editText.getText().toString();
                LanguageSelectActivity.this.search(text);
                if (text.length() != 0) {
                    LanguageSelectActivity.this.searchWas = true;
                    if (LanguageSelectActivity.this.listView != null) {
                        LanguageSelectActivity.this.listView.setAdapter(LanguageSelectActivity.this.searchListViewAdapter);
                        return;
                    }
                    return;
                }
                LanguageSelectActivity.this.searching = false;
                LanguageSelectActivity.this.searchWas = false;
                if (LanguageSelectActivity.this.listView != null) {
                    LanguageSelectActivity.this.emptyView.setVisibility(8);
                    LanguageSelectActivity.this.listView.setAdapter(LanguageSelectActivity.this.listAdapter);
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
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.LanguageSelectActivity$$ExternalSyntheticLambda6
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i) {
                LanguageSelectActivity.this.m3581lambda$createView$1$orgtelegramuiLanguageSelectActivity(view, i);
            }
        });
        this.listView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() { // from class: org.telegram.ui.LanguageSelectActivity$$ExternalSyntheticLambda7
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener
            public final boolean onItemClick(View view, int i) {
                return LanguageSelectActivity.this.m3583lambda$createView$3$orgtelegramuiLanguageSelectActivity(view, i);
            }
        });
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.LanguageSelectActivity.3
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 1) {
                    AndroidUtilities.hideKeyboard(LanguageSelectActivity.this.getParentActivity().getCurrentFocus());
                }
            }
        });
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-LanguageSelectActivity */
    public /* synthetic */ void m3581lambda$createView$1$orgtelegramuiLanguageSelectActivity(View view, int position) {
        LocaleController.LocaleInfo localeInfo;
        try {
            if (getParentActivity() != null && this.parentLayout != null && (view instanceof TextRadioCell)) {
                boolean search = this.listView.getAdapter() == this.searchListViewAdapter;
                if (!search) {
                    position -= 2;
                }
                if (search) {
                    localeInfo = this.searchResult.get(position);
                } else if (!this.unofficialLanguages.isEmpty() && position >= 0 && position < this.unofficialLanguages.size()) {
                    localeInfo = this.unofficialLanguages.get(position);
                } else {
                    if (!this.unofficialLanguages.isEmpty()) {
                        position -= this.unofficialLanguages.size() + 1;
                    }
                    localeInfo = this.sortedLanguages.get(position);
                }
                if (localeInfo != null) {
                    LocaleController.LocaleInfo prevLocale = LocaleController.getInstance().getCurrentLocaleInfo();
                    LocaleController.getInstance().applyLanguage(localeInfo, true, false, false, true, this.currentAccount);
                    this.parentLayout.rebuildAllFragmentViews(false, false);
                    final String langCode = localeInfo.pluralLangCode;
                    String prevLangCode = prevLocale.pluralLangCode;
                    SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                    HashSet<String> selectedLanguages = RestrictedLanguagesSelectActivity.getRestrictedLanguages();
                    HashSet<String> newSelectedLanguages = new HashSet<>(selectedLanguages);
                    if (selectedLanguages.contains(langCode)) {
                        Collection.EL.removeIf(newSelectedLanguages, new Predicate() { // from class: org.telegram.ui.LanguageSelectActivity$$ExternalSyntheticLambda5
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
                                return LanguageSelectActivity.lambda$createView$0(langCode, (String) obj);
                            }
                        });
                        if (!selectedLanguages.contains(prevLangCode)) {
                            newSelectedLanguages.add(prevLangCode);
                        }
                    }
                    preferences.edit().putStringSet("translate_button_restricted_languages", newSelectedLanguages).apply();
                    finishFragment();
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static /* synthetic */ boolean lambda$createView$0(String langCode, String s) {
        return s != null && s.equals(langCode);
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-LanguageSelectActivity */
    public /* synthetic */ boolean m3583lambda$createView$3$orgtelegramuiLanguageSelectActivity(View view, int position) {
        LocaleController.LocaleInfo localeInfo;
        try {
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (getParentActivity() != null && this.parentLayout != null && (view instanceof TextRadioCell)) {
            boolean search = this.listView.getAdapter() == this.searchListViewAdapter;
            if (!search) {
                position -= 2;
            }
            if (search) {
                localeInfo = this.searchResult.get(position);
            } else if (!this.unofficialLanguages.isEmpty() && position >= 0 && position < this.unofficialLanguages.size()) {
                localeInfo = this.unofficialLanguages.get(position);
            } else {
                if (!this.unofficialLanguages.isEmpty()) {
                    position -= this.unofficialLanguages.size() + 1;
                }
                localeInfo = this.sortedLanguages.get(position);
            }
            if (localeInfo != null && localeInfo.pathToFile != null && (!localeInfo.isRemote() || localeInfo.serverIndex == Integer.MAX_VALUE)) {
                final LocaleController.LocaleInfo finalLocaleInfo = localeInfo;
                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                builder.setTitle(LocaleController.getString("DeleteLocalizationTitle", R.string.DeleteLocalizationTitle));
                builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("DeleteLocalizationText", R.string.DeleteLocalizationText, localeInfo.name)));
                builder.setPositiveButton(LocaleController.getString("Delete", R.string.Delete), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.LanguageSelectActivity$$ExternalSyntheticLambda0
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        LanguageSelectActivity.this.m3582lambda$createView$2$orgtelegramuiLanguageSelectActivity(finalLocaleInfo, dialogInterface, i);
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
            return false;
        }
        return false;
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-LanguageSelectActivity */
    public /* synthetic */ void m3582lambda$createView$2$orgtelegramuiLanguageSelectActivity(LocaleController.LocaleInfo finalLocaleInfo, DialogInterface dialogInterface, int i) {
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
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LanguageSelectActivity$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    LanguageSelectActivity.this.m3584x8b9d827f();
                }
            });
        }
    }

    /* renamed from: lambda$didReceivedNotification$4$org-telegram-ui-LanguageSelectActivity */
    public /* synthetic */ void m3584x8b9d827f() {
        this.listAdapter.notifyDataSetChanged();
    }

    private void fillLanguages() {
        final LocaleController.LocaleInfo currentLocale = LocaleController.getInstance().getCurrentLocaleInfo();
        Comparator<LocaleController.LocaleInfo> comparator = new Comparator() { // from class: org.telegram.ui.LanguageSelectActivity$$ExternalSyntheticLambda4
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                return LanguageSelectActivity.lambda$fillLanguages$5(LocaleController.LocaleInfo.this, (LocaleController.LocaleInfo) obj, (LocaleController.LocaleInfo) obj2);
            }
        };
        this.sortedLanguages = new ArrayList<>();
        this.unofficialLanguages = new ArrayList<>(LocaleController.getInstance().unofficialLanguages);
        ArrayList<LocaleController.LocaleInfo> arrayList = LocaleController.getInstance().languages;
        int size = arrayList.size();
        for (int a = 0; a < size; a++) {
            LocaleController.LocaleInfo info = arrayList.get(a);
            if (info.serverIndex != Integer.MAX_VALUE) {
                this.sortedLanguages.add(info);
            } else {
                this.unofficialLanguages.add(info);
            }
        }
        Collections.sort(this.sortedLanguages, comparator);
        Collections.sort(this.unofficialLanguages, comparator);
    }

    public static /* synthetic */ int lambda$fillLanguages$5(LocaleController.LocaleInfo currentLocale, LocaleController.LocaleInfo o, LocaleController.LocaleInfo o2) {
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
            this.searching = false;
            this.searchResult = null;
            if (this.listView != null) {
                this.emptyView.setVisibility(8);
                this.listView.setAdapter(this.listAdapter);
                return;
            }
            return;
        }
        processSearch(query);
    }

    private void processSearch(final String query) {
        Utilities.searchQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.LanguageSelectActivity$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                LanguageSelectActivity.this.m3585lambda$processSearch$6$orgtelegramuiLanguageSelectActivity(query);
            }
        });
    }

    /* renamed from: lambda$processSearch$6$org-telegram-ui-LanguageSelectActivity */
    public /* synthetic */ void m3585lambda$processSearch$6$orgtelegramuiLanguageSelectActivity(String query) {
        String q = query.trim().toLowerCase();
        if (q.length() == 0) {
            updateSearchResults(new ArrayList<>());
            return;
        }
        System.currentTimeMillis();
        ArrayList<LocaleController.LocaleInfo> resultArray = new ArrayList<>();
        int N = this.unofficialLanguages.size();
        for (int a = 0; a < N; a++) {
            LocaleController.LocaleInfo c = this.unofficialLanguages.get(a);
            if (c.name.toLowerCase().startsWith(query) || c.nameEnglish.toLowerCase().startsWith(query)) {
                resultArray.add(c);
            }
        }
        int N2 = this.sortedLanguages.size();
        for (int a2 = 0; a2 < N2; a2++) {
            LocaleController.LocaleInfo c2 = this.sortedLanguages.get(a2);
            if (c2.name.toLowerCase().startsWith(query) || c2.nameEnglish.toLowerCase().startsWith(query)) {
                resultArray.add(c2);
            }
        }
        updateSearchResults(resultArray);
    }

    private void updateSearchResults(final ArrayList<LocaleController.LocaleInfo> arrCounties) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LanguageSelectActivity$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                LanguageSelectActivity.this.m3586x9dcfe282(arrCounties);
            }
        });
    }

    /* renamed from: lambda$updateSearchResults$7$org-telegram-ui-LanguageSelectActivity */
    public /* synthetic */ void m3586x9dcfe282(ArrayList arrCounties) {
        this.searchResult = arrCounties;
        this.searchListViewAdapter.notifyDataSetChanged();
    }

    /* loaded from: classes4.dex */
    public class TranslateSettings extends LinearLayout {
        private TextSettingsCell doNotTranslateCell;
        private HeaderCell header;
        private TextInfoPrivacyCell info;
        private TextInfoPrivacyCell info2;
        private SharedPreferences.OnSharedPreferenceChangeListener listener;
        private TextCheckCell showButtonCheck;
        private ValueAnimator doNotTranslateCellAnimation = null;
        private SharedPreferences preferences = MessagesController.getGlobalMainSettings();

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public TranslateSettings(Context context) {
            super(context);
            LanguageSelectActivity.this = r12;
            setFocusable(false);
            setOrientation(1);
            HeaderCell headerCell = new HeaderCell(context);
            this.header = headerCell;
            headerCell.setFocusable(true);
            this.header.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            this.header.setText(LocaleController.getString("TranslateMessages", R.string.TranslateMessages));
            this.header.setContentDescription(LocaleController.getString("TranslateMessages", R.string.TranslateMessages));
            addView(this.header, LayoutHelper.createLinear(-1, -2));
            boolean value = getValue();
            TextCheckCell textCheckCell = new TextCheckCell(context);
            this.showButtonCheck = textCheckCell;
            textCheckCell.setBackground(Theme.AdaptiveRipple.filledRect(Theme.getColor(Theme.key_windowBackgroundWhite)));
            this.showButtonCheck.setTextAndCheck(LocaleController.getString("ShowTranslateButton", R.string.ShowTranslateButton), value, value);
            this.showButtonCheck.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.LanguageSelectActivity$TranslateSettings$$ExternalSyntheticLambda1
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    LanguageSelectActivity.TranslateSettings.this.m3587x92ea1a5b(view);
                }
            });
            addView(this.showButtonCheck, LayoutHelper.createLinear(-1, -2));
            TextSettingsCell textSettingsCell = new TextSettingsCell(context);
            this.doNotTranslateCell = textSettingsCell;
            textSettingsCell.setBackground(Theme.AdaptiveRipple.filledRect(Theme.getColor(Theme.key_windowBackgroundWhite)));
            this.doNotTranslateCell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.LanguageSelectActivity$TranslateSettings$$ExternalSyntheticLambda2
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    LanguageSelectActivity.TranslateSettings.this.m3588x1fd7317a(view);
                }
            });
            this.doNotTranslateCell.setClickable(value && LanguageDetector.hasSupport());
            float f = 1.0f;
            this.doNotTranslateCell.setAlpha((!value || !LanguageDetector.hasSupport()) ? 0.0f : 1.0f);
            addView(this.doNotTranslateCell, LayoutHelper.createLinear(-1, -2));
            TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(context);
            this.info = textInfoPrivacyCell;
            textInfoPrivacyCell.setTopPadding(11);
            this.info.setBottomPadding(16);
            this.info.setFocusable(true);
            this.info.setText(LocaleController.getString("TranslateMessagesInfo1", R.string.TranslateMessagesInfo1));
            this.info.setContentDescription(LocaleController.getString("TranslateMessagesInfo1", R.string.TranslateMessagesInfo1));
            addView(this.info, LayoutHelper.createLinear(-1, -2));
            TextInfoPrivacyCell textInfoPrivacyCell2 = new TextInfoPrivacyCell(context);
            this.info2 = textInfoPrivacyCell2;
            textInfoPrivacyCell2.setTopPadding(0);
            this.info2.setBottomPadding(16);
            this.info2.setFocusable(true);
            this.info2.setText(LocaleController.getString("TranslateMessagesInfo2", R.string.TranslateMessagesInfo2));
            this.info2.setContentDescription(LocaleController.getString("TranslateMessagesInfo2", R.string.TranslateMessagesInfo2));
            this.info2.setAlpha(value ? 0.0f : f);
            addView(this.info2, LayoutHelper.createLinear(-1, -2));
            updateHeight();
            update();
        }

        /* renamed from: lambda$new$0$org-telegram-ui-LanguageSelectActivity$TranslateSettings */
        public /* synthetic */ void m3587x92ea1a5b(View e) {
            this.preferences.edit().putBoolean("translate_button", !getValue()).apply();
        }

        /* renamed from: lambda$new$1$org-telegram-ui-LanguageSelectActivity$TranslateSettings */
        public /* synthetic */ void m3588x1fd7317a(View e) {
            LanguageSelectActivity.this.presentFragment(new RestrictedLanguagesSelectActivity());
            update();
        }

        private boolean getValue() {
            return this.preferences.getBoolean("translate_button", false);
        }

        private ArrayList<String> getRestrictedLanguages() {
            String currentLang = LocaleController.getInstance().getCurrentLocaleInfo().pluralLangCode;
            ArrayList<String> langCodes = new ArrayList<>(RestrictedLanguagesSelectActivity.getRestrictedLanguages());
            if (!langCodes.contains(currentLang)) {
                langCodes.add(currentLang);
            }
            return langCodes;
        }

        public void update() {
            boolean value = getValue() && LanguageDetector.hasSupport();
            this.showButtonCheck.setChecked(getValue());
            ValueAnimator valueAnimator = this.doNotTranslateCellAnimation;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            this.showButtonCheck.setDivider(value);
            ArrayList<String> langCodes = getRestrictedLanguages();
            String doNotTranslateCellValue = null;
            if (langCodes.size() == 1) {
                try {
                    doNotTranslateCellValue = LocaleController.getInstance().getLanguageFromDict(langCodes.get(0)).name;
                } catch (Exception e) {
                }
            }
            if (doNotTranslateCellValue == null) {
                doNotTranslateCellValue = String.format(LocaleController.getPluralString("Languages", getRestrictedLanguages().size()), Integer.valueOf(getRestrictedLanguages().size()));
            }
            this.doNotTranslateCell.setTextAndValue(LocaleController.getString("DoNotTranslate", R.string.DoNotTranslate), doNotTranslateCellValue, false);
            this.doNotTranslateCell.setClickable(value);
            this.info2.setVisibility(0);
            float[] fArr = new float[2];
            fArr[0] = this.doNotTranslateCell.getAlpha();
            float f = 1.0f;
            fArr[1] = value ? 1.0f : 0.0f;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
            this.doNotTranslateCellAnimation = ofFloat;
            ofFloat.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.doNotTranslateCellAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.LanguageSelectActivity$TranslateSettings$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    LanguageSelectActivity.TranslateSettings.this.m3589xc1a1edb6(valueAnimator2);
                }
            });
            this.doNotTranslateCellAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.LanguageSelectActivity.TranslateSettings.1
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (TranslateSettings.this.doNotTranslateCell.getAlpha() > 0.5d) {
                        TranslateSettings.this.info2.setVisibility(8);
                    } else {
                        TranslateSettings.this.info2.setVisibility(0);
                    }
                }
            });
            ValueAnimator valueAnimator2 = this.doNotTranslateCellAnimation;
            float alpha = this.doNotTranslateCell.getAlpha();
            if (!value) {
                f = 0.0f;
            }
            valueAnimator2.setDuration(Math.abs(alpha - f) * 200.0f);
            this.doNotTranslateCellAnimation.start();
        }

        /* renamed from: lambda$update$2$org-telegram-ui-LanguageSelectActivity$TranslateSettings */
        public /* synthetic */ void m3589xc1a1edb6(ValueAnimator a) {
            float t = ((Float) a.getAnimatedValue()).floatValue();
            this.doNotTranslateCell.setAlpha(t);
            this.doNotTranslateCell.setTranslationY((-AndroidUtilities.dp(8.0f)) * (1.0f - t));
            this.info.setTranslationY((-this.doNotTranslateCell.getHeight()) * (1.0f - t));
            this.info2.setAlpha(1.0f - t);
            this.info2.setTranslationY((-this.doNotTranslateCell.getHeight()) * (1.0f - t));
        }

        @Override // android.view.View
        protected void onConfigurationChanged(Configuration newConfig) {
            super.onConfigurationChanged(newConfig);
            updateHeight();
        }

        @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            updateHeight();
            super.onLayout(changed, l, t, r, b);
        }

        void updateHeight() {
            int i = 0;
            this.header.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.x, C.BUFFER_FLAG_ENCRYPTED), 0);
            this.showButtonCheck.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.x, C.BUFFER_FLAG_ENCRYPTED), 0);
            this.doNotTranslateCell.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.x, C.BUFFER_FLAG_ENCRYPTED), 0);
            this.info.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.x, C.BUFFER_FLAG_ENCRYPTED), 0);
            this.info2.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.x, C.BUFFER_FLAG_ENCRYPTED), 0);
            if (!LanguageSelectActivity.this.searching) {
                i = height();
            }
            int newHeight = i;
            if (getLayoutParams() == null) {
                setLayoutParams(new RecyclerView.LayoutParams(-1, newHeight));
            } else if (getLayoutParams().height != newHeight) {
                RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) getLayoutParams();
                lp.height = newHeight;
                setLayoutParams(lp);
            }
        }

        int height() {
            return Math.max(AndroidUtilities.dp(40.0f), this.header.getMeasuredHeight()) + Math.max(AndroidUtilities.dp(50.0f), this.showButtonCheck.getMeasuredHeight()) + Math.max(Math.max(AndroidUtilities.dp(50.0f), this.doNotTranslateCell.getMeasuredHeight()), this.info2.getMeasuredHeight() <= 0 ? AndroidUtilities.dp(51.0f) : this.info2.getMeasuredHeight()) + (this.info.getMeasuredHeight() <= 0 ? AndroidUtilities.dp(62.0f) : this.info.getMeasuredHeight());
        }

        @Override // android.widget.LinearLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            updateHeight();
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            update();
            SharedPreferences sharedPreferences = this.preferences;
            SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() { // from class: org.telegram.ui.LanguageSelectActivity.TranslateSettings.2
                @Override // android.content.SharedPreferences.OnSharedPreferenceChangeListener
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences2, String s) {
                    TranslateSettings.this.preferences = sharedPreferences2;
                    TranslateSettings.this.update();
                }
            };
            this.listener = onSharedPreferenceChangeListener;
            sharedPreferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
            updateHeight();
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.preferences.unregisterOnSharedPreferenceChangeListener(this.listener);
        }
    }

    /* loaded from: classes4.dex */
    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;
        private boolean search;

        public ListAdapter(Context context, boolean isSearch) {
            LanguageSelectActivity.this = r1;
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
                if (LanguageSelectActivity.this.searchResult != null) {
                    return LanguageSelectActivity.this.searchResult.size();
                }
                return 0;
            }
            int count = LanguageSelectActivity.this.sortedLanguages.size();
            if (count != 0) {
                count++;
            }
            if (!LanguageSelectActivity.this.unofficialLanguages.isEmpty()) {
                count += LanguageSelectActivity.this.unofficialLanguages.size() + 1;
            }
            return count + 2;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    View view2 = new TextRadioCell(this.mContext);
                    view2.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view = view2;
                    break;
                case 1:
                default:
                    view = new ShadowSectionCell(this.mContext);
                    break;
                case 2:
                    view = new TranslateSettings(this.mContext);
                    break;
                case 3:
                    HeaderCell header = new HeaderCell(this.mContext);
                    header.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    header.setText(LocaleController.getString("Language", R.string.Language));
                    view = header;
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            boolean last;
            LocaleController.LocaleInfo localeInfo;
            boolean z = false;
            int i = 0;
            switch (holder.getItemViewType()) {
                case 0:
                    if (!this.search) {
                        position -= 2;
                    }
                    TextRadioCell textSettingsCell = (TextRadioCell) holder.itemView;
                    if (this.search) {
                        localeInfo = (LocaleController.LocaleInfo) LanguageSelectActivity.this.searchResult.get(position);
                        last = position == LanguageSelectActivity.this.searchResult.size() - 1;
                    } else if (LanguageSelectActivity.this.unofficialLanguages.isEmpty() || position < 0 || position >= LanguageSelectActivity.this.unofficialLanguages.size()) {
                        if (!LanguageSelectActivity.this.unofficialLanguages.isEmpty()) {
                            position -= LanguageSelectActivity.this.unofficialLanguages.size() + 1;
                        }
                        localeInfo = (LocaleController.LocaleInfo) LanguageSelectActivity.this.sortedLanguages.get(position);
                        last = position == LanguageSelectActivity.this.sortedLanguages.size() - 1;
                    } else {
                        localeInfo = (LocaleController.LocaleInfo) LanguageSelectActivity.this.unofficialLanguages.get(position);
                        last = position == LanguageSelectActivity.this.unofficialLanguages.size() - 1;
                    }
                    if (localeInfo.isLocal()) {
                        textSettingsCell.setTextAndValueAndCheck(String.format("%1$s (%2$s)", localeInfo.name, LocaleController.getString("LanguageCustom", R.string.LanguageCustom)), localeInfo.nameEnglish, false, false, !last);
                    } else {
                        textSettingsCell.setTextAndValueAndCheck(localeInfo.name, localeInfo.nameEnglish, false, false, !last);
                    }
                    if (localeInfo == LocaleController.getInstance().getCurrentLocaleInfo()) {
                        z = true;
                    }
                    textSettingsCell.setChecked(z);
                    return;
                case 1:
                    if (!this.search) {
                        position--;
                    }
                    ShadowSectionCell sectionCell = (ShadowSectionCell) holder.itemView;
                    if (!LanguageSelectActivity.this.unofficialLanguages.isEmpty() && position == LanguageSelectActivity.this.unofficialLanguages.size()) {
                        sectionCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else {
                        sectionCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        return;
                    }
                case 2:
                    TranslateSettings translateSettings = (TranslateSettings) holder.itemView;
                    if (LanguageSelectActivity.this.searching) {
                        i = 8;
                    }
                    translateSettings.setVisibility(i);
                    translateSettings.updateHeight();
                    return;
                default:
                    return;
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            boolean z = this.search;
            if (!z) {
                i -= 2;
            }
            if (i == -2) {
                return 2;
            }
            if (i == -1) {
                return 3;
            }
            if (z) {
                return 0;
            }
            return ((LanguageSelectActivity.this.unofficialLanguages.isEmpty() || !(i == LanguageSelectActivity.this.unofficialLanguages.size() || i == (LanguageSelectActivity.this.unofficialLanguages.size() + LanguageSelectActivity.this.sortedLanguages.size()) + 1)) && (!LanguageSelectActivity.this.unofficialLanguages.isEmpty() || i != LanguageSelectActivity.this.sortedLanguages.size())) ? 0 : 1;
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
