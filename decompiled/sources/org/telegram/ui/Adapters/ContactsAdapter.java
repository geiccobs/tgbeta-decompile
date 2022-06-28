package org.telegram.ui.Adapters;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.collection.LongSparseArray;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.C;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.DividerCell;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.LetterSectionCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.ContactsEmptyView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
/* loaded from: classes4.dex */
public class ContactsAdapter extends RecyclerListView.SectionsAdapter {
    private LongSparseArray<?> checkedMap;
    private int currentAccount = UserConfig.selectedAccount;
    private boolean disableSections;
    private boolean hasGps;
    private LongSparseArray<TLRPC.User> ignoreUsers;
    private boolean isAdmin;
    private boolean isChannel;
    private boolean isEmpty;
    private Context mContext;
    private boolean needPhonebook;
    private ArrayList<TLRPC.TL_contact> onlineContacts;
    private int onlyUsers;
    private boolean scrolling;
    private int sortType;

    public ContactsAdapter(Context context, int onlyUsersType, boolean showPhoneBook, LongSparseArray<TLRPC.User> usersToIgnore, int flags, boolean gps) {
        this.mContext = context;
        this.onlyUsers = onlyUsersType;
        this.needPhonebook = showPhoneBook;
        this.ignoreUsers = usersToIgnore;
        boolean z = true;
        this.isAdmin = flags != 0;
        this.isChannel = flags != 2 ? false : z;
        this.hasGps = gps;
    }

    public void setDisableSections(boolean value) {
        this.disableSections = value;
    }

    public void setSortType(int value, boolean force) {
        this.sortType = value;
        if (value == 2) {
            if (this.onlineContacts == null || force) {
                this.onlineContacts = new ArrayList<>(ContactsController.getInstance(this.currentAccount).contacts);
                long selfId = UserConfig.getInstance(this.currentAccount).clientUserId;
                int a = 0;
                int N = this.onlineContacts.size();
                while (true) {
                    if (a >= N) {
                        break;
                    } else if (this.onlineContacts.get(a).user_id != selfId) {
                        a++;
                    } else {
                        this.onlineContacts.remove(a);
                        break;
                    }
                }
            }
            sortOnlineContacts();
            return;
        }
        notifyDataSetChanged();
    }

    public void sortOnlineContacts() {
        if (this.onlineContacts == null) {
            return;
        }
        try {
            final int currentTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
            final MessagesController messagesController = MessagesController.getInstance(this.currentAccount);
            Collections.sort(this.onlineContacts, new Comparator() { // from class: org.telegram.ui.Adapters.ContactsAdapter$$ExternalSyntheticLambda0
                @Override // java.util.Comparator
                public final int compare(Object obj, Object obj2) {
                    return ContactsAdapter.lambda$sortOnlineContacts$0(MessagesController.this, currentTime, (TLRPC.TL_contact) obj, (TLRPC.TL_contact) obj2);
                }
            });
            notifyDataSetChanged();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static /* synthetic */ int lambda$sortOnlineContacts$0(MessagesController messagesController, int currentTime, TLRPC.TL_contact o1, TLRPC.TL_contact o2) {
        TLRPC.User user1 = messagesController.getUser(Long.valueOf(o2.user_id));
        TLRPC.User user2 = messagesController.getUser(Long.valueOf(o1.user_id));
        int status1 = 0;
        int status2 = 0;
        if (user1 != null) {
            if (user1.self) {
                status1 = currentTime + 50000;
            } else if (user1.status != null) {
                status1 = user1.status.expires;
            }
        }
        if (user2 != null) {
            if (user2.self) {
                status2 = currentTime + 50000;
            } else if (user2.status != null) {
                status2 = user2.status.expires;
            }
        }
        if (status1 > 0 && status2 > 0) {
            if (status1 > status2) {
                return 1;
            }
            return status1 < status2 ? -1 : 0;
        } else if (status1 < 0 && status2 < 0) {
            if (status1 > status2) {
                return 1;
            }
            return status1 < status2 ? -1 : 0;
        } else if ((status1 < 0 && status2 > 0) || (status1 == 0 && status2 != 0)) {
            return -1;
        } else {
            return ((status2 >= 0 || status1 <= 0) && (status2 != 0 || status1 == 0)) ? 0 : 1;
        }
    }

    public void setCheckedMap(LongSparseArray<?> map) {
        this.checkedMap = map;
    }

    public void setIsScrolling(boolean value) {
        this.scrolling = value;
    }

    @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
    public Object getItem(int section, int position) {
        HashMap<String, ArrayList<TLRPC.TL_contact>> usersSectionsDict = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).usersMutualSectionsDict : ContactsController.getInstance(this.currentAccount).usersSectionsDict;
        ArrayList<String> sortedUsersSectionsArray = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray : ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
        if (this.onlyUsers != 0 && !this.isAdmin) {
            if (section < sortedUsersSectionsArray.size()) {
                ArrayList<TLRPC.TL_contact> arr = usersSectionsDict.get(sortedUsersSectionsArray.get(section));
                if (position < arr.size()) {
                    return MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(arr.get(position).user_id));
                }
            }
            return null;
        } else if (section == 0) {
            return null;
        } else {
            if (this.sortType == 2) {
                if (section == 1) {
                    if (position >= this.onlineContacts.size()) {
                        return null;
                    }
                    return MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.onlineContacts.get(position).user_id));
                }
            } else if (section - 1 < sortedUsersSectionsArray.size()) {
                ArrayList<TLRPC.TL_contact> arr2 = usersSectionsDict.get(sortedUsersSectionsArray.get(section - 1));
                if (position >= arr2.size()) {
                    return null;
                }
                return MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(arr2.get(position).user_id));
            }
            if (this.needPhonebook && position >= 0 && position < ContactsController.getInstance(this.currentAccount).phoneBookContacts.size()) {
                return ContactsController.getInstance(this.currentAccount).phoneBookContacts.get(position);
            }
            return null;
        }
    }

    @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
    public boolean isEnabled(RecyclerView.ViewHolder holder, int section, int row) {
        HashMap<String, ArrayList<TLRPC.TL_contact>> usersSectionsDict = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).usersMutualSectionsDict : ContactsController.getInstance(this.currentAccount).usersSectionsDict;
        ArrayList<String> sortedUsersSectionsArray = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray : ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
        if (this.onlyUsers != 0 && !this.isAdmin) {
            if (this.isEmpty) {
                return false;
            }
            ArrayList<TLRPC.TL_contact> arr = usersSectionsDict.get(sortedUsersSectionsArray.get(section));
            return row < arr.size();
        } else if (section == 0) {
            if (this.isAdmin) {
                return row != 1;
            } else if (!this.needPhonebook) {
                return row != 3;
            } else {
                boolean z = this.hasGps;
                return (z && row != 2) || (!z && row != 1);
            }
        } else if (this.isEmpty) {
            return false;
        } else {
            if (this.sortType == 2) {
                return section != 1 || row < this.onlineContacts.size();
            } else if (section - 1 < sortedUsersSectionsArray.size()) {
                ArrayList<TLRPC.TL_contact> arr2 = usersSectionsDict.get(sortedUsersSectionsArray.get(section - 1));
                return row < arr2.size();
            }
            return true;
        }
    }

    @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
    public int getSectionCount() {
        int count;
        this.isEmpty = false;
        if (this.sortType == 2) {
            count = 1;
            this.isEmpty = this.onlineContacts.isEmpty();
        } else {
            ArrayList<String> sortedUsersSectionsArray = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray : ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
            int count2 = sortedUsersSectionsArray.size();
            if (count2 != 0) {
                count = count2;
            } else {
                this.isEmpty = true;
                count = 1;
            }
        }
        if (this.onlyUsers == 0) {
            count++;
        }
        if (this.isAdmin) {
            return count + 1;
        }
        return count;
    }

    @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
    public int getCountForSection(int section) {
        HashMap<String, ArrayList<TLRPC.TL_contact>> usersSectionsDict = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).usersMutualSectionsDict : ContactsController.getInstance(this.currentAccount).usersSectionsDict;
        ArrayList<String> sortedUsersSectionsArray = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray : ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
        if (this.onlyUsers != 0 && !this.isAdmin) {
            if (this.isEmpty) {
                return 1;
            }
            if (section < sortedUsersSectionsArray.size()) {
                ArrayList<TLRPC.TL_contact> arr = usersSectionsDict.get(sortedUsersSectionsArray.get(section));
                int count = arr.size();
                if (section != sortedUsersSectionsArray.size() - 1 || this.needPhonebook) {
                    return count + 1;
                }
                return count;
            }
        } else if (section == 0) {
            if (this.isAdmin) {
                return 2;
            }
            if (!this.needPhonebook) {
                return 4;
            }
            return this.hasGps ? 3 : 2;
        } else if (this.isEmpty) {
            return 1;
        } else {
            if (this.sortType == 2) {
                if (section == 1) {
                    if (!this.onlineContacts.isEmpty()) {
                        return this.onlineContacts.size() + 1;
                    }
                    return 0;
                }
            } else if (section - 1 < sortedUsersSectionsArray.size()) {
                ArrayList<TLRPC.TL_contact> arr2 = usersSectionsDict.get(sortedUsersSectionsArray.get(section - 1));
                int count2 = arr2.size();
                if (section - 1 != sortedUsersSectionsArray.size() - 1 || this.needPhonebook) {
                    return count2 + 1;
                }
                return count2;
            }
        }
        if (this.needPhonebook) {
            return ContactsController.getInstance(this.currentAccount).phoneBookContacts.size();
        }
        return 0;
    }

    @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
    public View getSectionHeaderView(int section, View view) {
        if (this.onlyUsers == 2) {
            HashMap<String, ArrayList<TLRPC.TL_contact>> hashMap = ContactsController.getInstance(this.currentAccount).usersMutualSectionsDict;
        } else {
            HashMap<String, ArrayList<TLRPC.TL_contact>> hashMap2 = ContactsController.getInstance(this.currentAccount).usersSectionsDict;
        }
        ArrayList<String> sortedUsersSectionsArray = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray : ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
        if (view == null) {
            view = new LetterSectionCell(this.mContext);
        }
        LetterSectionCell cell = (LetterSectionCell) view;
        if (this.sortType == 2 || this.disableSections || this.isEmpty) {
            cell.setLetter("");
        } else if (this.onlyUsers != 0 && !this.isAdmin) {
            if (section < sortedUsersSectionsArray.size()) {
                cell.setLetter(sortedUsersSectionsArray.get(section));
            } else {
                cell.setLetter("");
            }
        } else if (section == 0) {
            cell.setLetter("");
        } else if (section - 1 < sortedUsersSectionsArray.size()) {
            cell.setLetter(sortedUsersSectionsArray.get(section - 1));
        } else {
            cell.setLetter("");
        }
        return view;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case 0:
                view = new UserCell(this.mContext, 58, 1, false);
                break;
            case 1:
                view = new TextCell(this.mContext);
                break;
            case 2:
                view = new GraySectionCell(this.mContext);
                break;
            case 3:
                view = new DividerCell(this.mContext);
                float f = 28.0f;
                int dp = AndroidUtilities.dp(LocaleController.isRTL ? 28.0f : 72.0f);
                int dp2 = AndroidUtilities.dp(8.0f);
                if (LocaleController.isRTL) {
                    f = 72.0f;
                }
                view.setPadding(dp, dp2, AndroidUtilities.dp(f), AndroidUtilities.dp(8.0f));
                break;
            case 4:
                FrameLayout frameLayout = new FrameLayout(this.mContext) { // from class: org.telegram.ui.Adapters.ContactsAdapter.1
                    @Override // android.widget.FrameLayout, android.view.View
                    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                        int height;
                        int height2 = View.MeasureSpec.getSize(heightMeasureSpec);
                        if (height2 == 0) {
                            height2 = parent.getMeasuredHeight();
                        }
                        int totalHeight = 0;
                        if (height2 == 0) {
                            height2 = (AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight()) - (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
                        }
                        int cellHeight = AndroidUtilities.dp(50.0f);
                        if (ContactsAdapter.this.onlyUsers == 0) {
                            totalHeight = AndroidUtilities.dp(30.0f) + cellHeight;
                        }
                        if (ContactsAdapter.this.hasGps) {
                            totalHeight += cellHeight;
                        }
                        if (!ContactsAdapter.this.isAdmin && !ContactsAdapter.this.needPhonebook) {
                            totalHeight += cellHeight;
                        }
                        if (totalHeight < height2) {
                            height = height2 - totalHeight;
                        } else {
                            height = 0;
                        }
                        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(height, C.BUFFER_FLAG_ENCRYPTED));
                    }
                };
                ContactsEmptyView emptyView = new ContactsEmptyView(this.mContext);
                frameLayout.addView(emptyView, LayoutHelper.createFrame(-2, -2, 17));
                view = frameLayout;
                break;
            default:
                view = new ShadowSectionCell(this.mContext);
                Drawable drawable = Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow);
                CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(Theme.getColor(Theme.key_windowBackgroundGray)), drawable);
                combinedDrawable.setFullsize(true);
                view.setBackgroundDrawable(combinedDrawable);
                break;
        }
        return new RecyclerListView.Holder(view);
    }

    @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
    public void onBindViewHolder(int section, int position, RecyclerView.ViewHolder holder) {
        ArrayList<TLRPC.TL_contact> arr;
        boolean z = false;
        switch (holder.getItemViewType()) {
            case 0:
                UserCell userCell = (UserCell) holder.itemView;
                userCell.setAvatarPadding((this.sortType == 2 || this.disableSections) ? 6 : 58);
                if (this.sortType == 2) {
                    arr = this.onlineContacts;
                } else {
                    HashMap<String, ArrayList<TLRPC.TL_contact>> usersSectionsDict = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).usersMutualSectionsDict : ContactsController.getInstance(this.currentAccount).usersSectionsDict;
                    ArrayList<String> sortedUsersSectionsArray = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray : ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
                    arr = usersSectionsDict.get(sortedUsersSectionsArray.get(section - ((this.onlyUsers == 0 || this.isAdmin) ? 1 : 0)));
                }
                TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(arr.get(position).user_id));
                userCell.setData(user, null, null, 0);
                LongSparseArray<?> longSparseArray = this.checkedMap;
                if (longSparseArray != null) {
                    if (longSparseArray.indexOfKey(user.id) >= 0) {
                        z = true;
                    }
                    userCell.setChecked(z, true ^ this.scrolling);
                }
                LongSparseArray<TLRPC.User> longSparseArray2 = this.ignoreUsers;
                if (longSparseArray2 != null) {
                    if (longSparseArray2.indexOfKey(user.id) >= 0) {
                        userCell.setAlpha(0.5f);
                        return;
                    } else {
                        userCell.setAlpha(1.0f);
                        return;
                    }
                }
                return;
            case 1:
                TextCell textCell = (TextCell) holder.itemView;
                if (section == 0) {
                    if (this.needPhonebook) {
                        if (position == 0) {
                            textCell.setTextAndIcon(LocaleController.getString("InviteFriends", R.string.InviteFriends), R.drawable.msg_invite, false);
                            return;
                        } else if (position == 1) {
                            textCell.setTextAndIcon(LocaleController.getString("AddPeopleNearby", R.string.AddPeopleNearby), R.drawable.msg_location, false);
                            return;
                        } else {
                            return;
                        }
                    } else if (this.isAdmin) {
                        if (this.isChannel) {
                            textCell.setTextAndIcon(LocaleController.getString("ChannelInviteViaLink", R.string.ChannelInviteViaLink), R.drawable.msg_link2, false);
                            return;
                        } else {
                            textCell.setTextAndIcon(LocaleController.getString("InviteToGroupByLink", R.string.InviteToGroupByLink), R.drawable.msg_link2, false);
                            return;
                        }
                    } else if (position == 0) {
                        textCell.setTextAndIcon(LocaleController.getString("NewGroup", R.string.NewGroup), R.drawable.msg_groups, false);
                        return;
                    } else if (position == 1) {
                        textCell.setTextAndIcon(LocaleController.getString("NewSecretChat", R.string.NewSecretChat), R.drawable.msg_secret, false);
                        return;
                    } else if (position == 2) {
                        textCell.setTextAndIcon(LocaleController.getString("NewChannel", R.string.NewChannel), R.drawable.msg_channel, false);
                        return;
                    } else {
                        return;
                    }
                }
                ContactsController.Contact contact = ContactsController.getInstance(this.currentAccount).phoneBookContacts.get(position);
                if (contact.first_name != null && contact.last_name != null) {
                    textCell.setText(contact.first_name + " " + contact.last_name, false);
                    return;
                } else if (contact.first_name != null && contact.last_name == null) {
                    textCell.setText(contact.first_name, false);
                    return;
                } else {
                    textCell.setText(contact.last_name, false);
                    return;
                }
            case 2:
                GraySectionCell sectionCell = (GraySectionCell) holder.itemView;
                int i = this.sortType;
                if (i == 0) {
                    sectionCell.setText(LocaleController.getString("Contacts", R.string.Contacts));
                    return;
                } else if (i == 1) {
                    sectionCell.setText(LocaleController.getString("SortedByName", R.string.SortedByName));
                    return;
                } else {
                    sectionCell.setText(LocaleController.getString("SortedByLastSeen", R.string.SortedByLastSeen));
                    return;
                }
            default:
                return;
        }
    }

    @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
    public int getItemViewType(int section, int position) {
        HashMap<String, ArrayList<TLRPC.TL_contact>> usersSectionsDict = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).usersMutualSectionsDict : ContactsController.getInstance(this.currentAccount).usersSectionsDict;
        ArrayList<String> sortedUsersSectionsArray = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray : ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
        if (this.onlyUsers != 0 && !this.isAdmin) {
            if (this.isEmpty) {
                return 4;
            }
            ArrayList<TLRPC.TL_contact> arr = usersSectionsDict.get(sortedUsersSectionsArray.get(section));
            return position < arr.size() ? 0 : 3;
        }
        if (section == 0) {
            if (this.isAdmin) {
                if (position == 1) {
                    return 2;
                }
            } else if (this.needPhonebook) {
                boolean z = this.hasGps;
                if ((z && position == 2) || (!z && position == 1)) {
                    return this.isEmpty ? 5 : 2;
                }
            } else if (position == 3) {
                return this.isEmpty ? 5 : 2;
            }
        } else if (this.isEmpty) {
            return 4;
        } else {
            if (this.sortType == 2) {
                if (section == 1) {
                    return position < this.onlineContacts.size() ? 0 : 3;
                }
            } else if (section - 1 < sortedUsersSectionsArray.size()) {
                ArrayList<TLRPC.TL_contact> arr2 = usersSectionsDict.get(sortedUsersSectionsArray.get(section - 1));
                return position < arr2.size() ? 0 : 3;
            }
        }
        return 1;
    }

    @Override // org.telegram.ui.Components.RecyclerListView.FastScrollAdapter
    public String getLetter(int position) {
        if (this.sortType == 2 || this.isEmpty) {
            return null;
        }
        ArrayList<String> sortedUsersSectionsArray = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray : ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
        int section = getSectionForPosition(position);
        if (section == -1) {
            section = sortedUsersSectionsArray.size() - 1;
        }
        if (this.onlyUsers != 0 && !this.isAdmin) {
            if (section >= 0 && section < sortedUsersSectionsArray.size()) {
                return sortedUsersSectionsArray.get(section);
            }
        } else if (section > 0 && section <= sortedUsersSectionsArray.size()) {
            return sortedUsersSectionsArray.get(section - 1);
        }
        return null;
    }

    @Override // org.telegram.ui.Components.RecyclerListView.FastScrollAdapter
    public void getPositionForScrollProgress(RecyclerListView listView, float progress, int[] position) {
        position[0] = (int) (getItemCount() * progress);
        position[1] = 0;
    }
}
