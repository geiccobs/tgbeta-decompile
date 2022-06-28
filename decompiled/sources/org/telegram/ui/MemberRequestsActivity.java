package org.telegram.ui;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Delegates.MemberRequestsDelegate;
/* loaded from: classes4.dex */
public class MemberRequestsActivity extends BaseFragment {
    public static final int searchMenuItem = 0;
    private final MemberRequestsDelegate delegate;

    public MemberRequestsActivity(long chatId) {
        this.delegate = new MemberRequestsDelegate(this, getLayoutContainer(), chatId, true) { // from class: org.telegram.ui.MemberRequestsActivity.1
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // org.telegram.ui.Delegates.MemberRequestsDelegate
            public void onImportersChanged(String query, boolean fromCache, boolean fromHide) {
                if (fromHide) {
                    MemberRequestsActivity.this.actionBar.setSearchFieldText("");
                } else {
                    super.onImportersChanged(query, fromCache, fromHide);
                }
            }
        };
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        String str;
        int i;
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.MemberRequestsActivity.2
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int id) {
                if (id == -1) {
                    MemberRequestsActivity.this.finishFragment();
                }
            }
        });
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        ActionBar actionBar = this.actionBar;
        if (this.delegate.isChannel) {
            i = R.string.SubscribeRequests;
            str = "SubscribeRequests";
        } else {
            i = R.string.MemberRequests;
            str = "MemberRequests";
        }
        actionBar.setTitle(LocaleController.getString(str, i));
        ActionBarMenu menu = this.actionBar.createMenu();
        ActionBarMenuItem searchItem = menu.addItem(0, R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() { // from class: org.telegram.ui.MemberRequestsActivity.3
            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public void onSearchExpand() {
                super.onSearchExpand();
                MemberRequestsActivity.this.delegate.setSearchExpanded(true);
            }

            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public void onSearchCollapse() {
                super.onSearchCollapse();
                MemberRequestsActivity.this.delegate.setSearchExpanded(false);
                MemberRequestsActivity.this.delegate.setQuery(null);
            }

            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public void onTextChanged(EditText editText) {
                super.onTextChanged(editText);
                MemberRequestsActivity.this.delegate.setQuery(editText.getText().toString());
            }
        });
        searchItem.setSearchFieldHint(LocaleController.getString("Search", R.string.Search));
        searchItem.setVisibility(8);
        FrameLayout rootLayout = this.delegate.getRootLayout();
        this.delegate.loadMembers();
        this.fragmentView = rootLayout;
        return rootLayout;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onBackPressed() {
        return this.delegate.onBackPressed();
    }
}
