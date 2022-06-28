package org.telegram.ui.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.DrawerLayoutContainer;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.DividerCell;
import org.telegram.ui.Cells.DrawerActionCell;
import org.telegram.ui.Cells.DrawerAddCell;
import org.telegram.ui.Cells.DrawerProfileCell;
import org.telegram.ui.Cells.DrawerUserCell;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SideMenultItemAnimator;
/* loaded from: classes4.dex */
public class DrawerLayoutAdapter extends RecyclerListView.SelectionAdapter {
    private boolean accountsShown;
    private boolean hasGps;
    private SideMenultItemAnimator itemAnimator;
    private Context mContext;
    private DrawerLayoutContainer mDrawerLayoutContainer;
    private DrawerProfileCell profileCell;
    private ArrayList<Item> items = new ArrayList<>(11);
    private ArrayList<Integer> accountNumbers = new ArrayList<>();

    public DrawerLayoutAdapter(Context context, SideMenultItemAnimator animator, DrawerLayoutContainer drawerLayoutContainer) {
        this.mContext = context;
        this.mDrawerLayoutContainer = drawerLayoutContainer;
        this.itemAnimator = animator;
        boolean z = true;
        this.accountsShown = (UserConfig.getActivatedAccountsCount() <= 1 || !MessagesController.getGlobalMainSettings().getBoolean("accountsShown", true)) ? false : z;
        Theme.createCommonDialogResources(context);
        resetItems();
        try {
            this.hasGps = ApplicationLoader.applicationContext.getPackageManager().hasSystemFeature("android.hardware.location.gps");
        } catch (Throwable th) {
            this.hasGps = false;
        }
    }

    private int getAccountRowsCount() {
        int count = this.accountNumbers.size() + 1;
        if (this.accountNumbers.size() < 4) {
            return count + 1;
        }
        return count;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        int count = this.items.size() + 2;
        if (this.accountsShown) {
            return count + getAccountRowsCount();
        }
        return count;
    }

    public void setAccountsShown(boolean value, boolean animated) {
        if (this.accountsShown == value || this.itemAnimator.isRunning()) {
            return;
        }
        this.accountsShown = value;
        DrawerProfileCell drawerProfileCell = this.profileCell;
        if (drawerProfileCell != null) {
            drawerProfileCell.setAccountsShown(value, animated);
        }
        MessagesController.getGlobalMainSettings().edit().putBoolean("accountsShown", this.accountsShown).commit();
        if (animated) {
            this.itemAnimator.setShouldClipChildren(false);
            if (this.accountsShown) {
                notifyItemRangeInserted(2, getAccountRowsCount());
                return;
            } else {
                notifyItemRangeRemoved(2, getAccountRowsCount());
                return;
            }
        }
        notifyDataSetChanged();
    }

    public boolean isAccountsShown() {
        return this.accountsShown;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void notifyDataSetChanged() {
        resetItems();
        super.notifyDataSetChanged();
    }

    @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
    public boolean isEnabled(RecyclerView.ViewHolder holder) {
        int itemType = holder.getItemViewType();
        return itemType == 3 || itemType == 4 || itemType == 5 || itemType == 6;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case 0:
                DrawerProfileCell drawerProfileCell = new DrawerProfileCell(this.mContext, this.mDrawerLayoutContainer);
                this.profileCell = drawerProfileCell;
                view = drawerProfileCell;
                break;
            case 1:
            default:
                view = new EmptyCell(this.mContext, AndroidUtilities.dp(8.0f));
                break;
            case 2:
                view = new DividerCell(this.mContext);
                break;
            case 3:
                view = new DrawerActionCell(this.mContext);
                break;
            case 4:
                view = new DrawerUserCell(this.mContext);
                break;
            case 5:
                view = new DrawerAddCell(this.mContext);
                break;
        }
        view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
        return new RecyclerListView.Holder(view);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case 0:
                DrawerProfileCell profileCell = (DrawerProfileCell) holder.itemView;
                profileCell.setUser(MessagesController.getInstance(UserConfig.selectedAccount).getUser(Long.valueOf(UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId())), this.accountsShown);
                return;
            case 1:
            case 2:
            default:
                return;
            case 3:
                DrawerActionCell drawerActionCell = (DrawerActionCell) holder.itemView;
                int position2 = position - 2;
                if (this.accountsShown) {
                    position2 -= getAccountRowsCount();
                }
                this.items.get(position2).bind(drawerActionCell);
                drawerActionCell.setPadding(0, 0, 0, 0);
                return;
            case 4:
                DrawerUserCell drawerUserCell = (DrawerUserCell) holder.itemView;
                drawerUserCell.setAccount(this.accountNumbers.get(position - 2).intValue());
                return;
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemViewType(int i) {
        if (i == 0) {
            return 0;
        }
        if (i == 1) {
            return 1;
        }
        int i2 = i - 2;
        if (this.accountsShown) {
            if (i2 < this.accountNumbers.size()) {
                return 4;
            }
            if (this.accountNumbers.size() < 4) {
                if (i2 != this.accountNumbers.size()) {
                    if (i2 == this.accountNumbers.size() + 1) {
                        return 2;
                    }
                } else {
                    return 5;
                }
            } else if (i2 == this.accountNumbers.size()) {
                return 2;
            }
            i2 -= getAccountRowsCount();
        }
        return (i2 < 0 || i2 >= this.items.size() || this.items.get(i2) == null) ? 2 : 3;
    }

    public void swapElements(int fromIndex, int toIndex) {
        int idx1 = fromIndex - 2;
        int idx2 = toIndex - 2;
        if (idx1 < 0 || idx2 < 0 || idx1 >= this.accountNumbers.size() || idx2 >= this.accountNumbers.size()) {
            return;
        }
        UserConfig userConfig1 = UserConfig.getInstance(this.accountNumbers.get(idx1).intValue());
        UserConfig userConfig2 = UserConfig.getInstance(this.accountNumbers.get(idx2).intValue());
        int tempLoginTime = userConfig1.loginTime;
        userConfig1.loginTime = userConfig2.loginTime;
        userConfig2.loginTime = tempLoginTime;
        userConfig1.saveConfig(false);
        userConfig2.saveConfig(false);
        Collections.swap(this.accountNumbers, idx1, idx2);
        notifyItemMoved(fromIndex, toIndex);
    }

    private void resetItems() {
        int peopleNearbyIcon;
        int helpIcon;
        int inviteIcon;
        int settingsIcon;
        int savedIcon;
        int callsIcon;
        int contactsIcon;
        int newGroupIcon;
        this.accountNumbers.clear();
        for (int a = 0; a < 4; a++) {
            if (UserConfig.getInstance(a).isClientActivated()) {
                this.accountNumbers.add(Integer.valueOf(a));
            }
        }
        Collections.sort(this.accountNumbers, DrawerLayoutAdapter$$ExternalSyntheticLambda0.INSTANCE);
        this.items.clear();
        if (!UserConfig.getInstance(UserConfig.selectedAccount).isClientActivated()) {
            return;
        }
        int eventType = Theme.getEventType();
        if (eventType == 0) {
            newGroupIcon = R.drawable.msg_groups_ny;
            contactsIcon = R.drawable.msg_contacts_ny;
            callsIcon = R.drawable.msg_calls_ny;
            savedIcon = R.drawable.msg_saved_ny;
            settingsIcon = R.drawable.msg_settings_ny;
            inviteIcon = R.drawable.msg_invite_ny;
            helpIcon = R.drawable.msg_help_ny;
            peopleNearbyIcon = R.drawable.msg_nearby_ny;
        } else if (eventType == 1) {
            newGroupIcon = R.drawable.msg_groups_14;
            contactsIcon = R.drawable.msg_contacts_14;
            callsIcon = R.drawable.msg_calls_14;
            savedIcon = R.drawable.msg_saved_14;
            settingsIcon = R.drawable.msg_settings_14;
            inviteIcon = R.drawable.msg_secret_ny;
            helpIcon = R.drawable.msg_help;
            peopleNearbyIcon = R.drawable.msg_secret_14;
        } else if (eventType == 2) {
            newGroupIcon = R.drawable.msg_groups_hw;
            contactsIcon = R.drawable.msg_contacts_hw;
            callsIcon = R.drawable.msg_calls_hw;
            savedIcon = R.drawable.msg_saved_hw;
            settingsIcon = R.drawable.msg_settings_hw;
            inviteIcon = R.drawable.msg_invite_hw;
            helpIcon = R.drawable.msg_help_hw;
            peopleNearbyIcon = R.drawable.msg_secret_hw;
        } else {
            newGroupIcon = R.drawable.msg_groups;
            contactsIcon = R.drawable.msg_contacts;
            callsIcon = R.drawable.msg_calls;
            savedIcon = R.drawable.msg_saved;
            settingsIcon = R.drawable.msg_settings_old;
            inviteIcon = R.drawable.msg_invite;
            helpIcon = R.drawable.msg_help;
            peopleNearbyIcon = R.drawable.msg_nearby;
        }
        this.items.add(new Item(2, LocaleController.getString("NewGroup", R.string.NewGroup), newGroupIcon));
        this.items.add(new Item(6, LocaleController.getString("Contacts", R.string.Contacts), contactsIcon));
        this.items.add(new Item(10, LocaleController.getString("Calls", R.string.Calls), callsIcon));
        if (this.hasGps) {
            this.items.add(new Item(12, LocaleController.getString("PeopleNearby", R.string.PeopleNearby), peopleNearbyIcon));
        }
        this.items.add(new Item(11, LocaleController.getString("SavedMessages", R.string.SavedMessages), savedIcon));
        this.items.add(new Item(8, LocaleController.getString("Settings", R.string.Settings), settingsIcon));
        this.items.add(null);
        this.items.add(new Item(7, LocaleController.getString("InviteFriends", R.string.InviteFriends), inviteIcon));
        this.items.add(new Item(13, LocaleController.getString("TelegramFeatures", R.string.TelegramFeatures), helpIcon));
    }

    public static /* synthetic */ int lambda$resetItems$0(Integer o1, Integer o2) {
        long l1 = UserConfig.getInstance(o1.intValue()).loginTime;
        long l2 = UserConfig.getInstance(o2.intValue()).loginTime;
        if (l1 > l2) {
            return 1;
        }
        if (l1 < l2) {
            return -1;
        }
        return 0;
    }

    public int getId(int position) {
        Item item;
        int position2 = position - 2;
        if (this.accountsShown) {
            position2 -= getAccountRowsCount();
        }
        if (position2 < 0 || position2 >= this.items.size() || (item = this.items.get(position2)) == null) {
            return -1;
        }
        return item.id;
    }

    public int getFirstAccountPosition() {
        if (!this.accountsShown) {
            return -1;
        }
        return 2;
    }

    public int getLastAccountPosition() {
        if (!this.accountsShown) {
            return -1;
        }
        return this.accountNumbers.size() + 1;
    }

    /* loaded from: classes4.dex */
    public static class Item {
        public int icon;
        public int id;
        public String text;

        public Item(int id, String text, int icon) {
            this.icon = icon;
            this.id = id;
            this.text = text;
        }

        public void bind(DrawerActionCell actionCell) {
            actionCell.setTextAndIcon(this.id, this.text, this.icon);
        }
    }
}
