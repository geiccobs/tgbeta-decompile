package org.telegram.ui.Adapters;

import android.location.Location;
import android.os.Build;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.RecyclerListView;
/* loaded from: classes4.dex */
public abstract class BaseLocationAdapter extends RecyclerListView.SelectionAdapter {
    private int currentRequestNum;
    private BaseLocationAdapterDelegate delegate;
    private long dialogId;
    private String lastFoundQuery;
    private Location lastSearchLocation;
    private String lastSearchQuery;
    private boolean searchInProgress;
    private Runnable searchRunnable;
    protected boolean searching;
    private boolean searchingUser;
    protected boolean searched = false;
    protected ArrayList<TLRPC.TL_messageMediaVenue> places = new ArrayList<>();
    protected ArrayList<String> iconUrls = new ArrayList<>();
    private int currentAccount = UserConfig.selectedAccount;

    /* loaded from: classes4.dex */
    public interface BaseLocationAdapterDelegate {
        void didLoadSearchResult(ArrayList<TLRPC.TL_messageMediaVenue> arrayList);
    }

    public void destroy() {
        if (this.currentRequestNum != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.currentRequestNum, true);
            this.currentRequestNum = 0;
        }
    }

    public void setDelegate(long did, BaseLocationAdapterDelegate delegate) {
        this.dialogId = did;
        this.delegate = delegate;
    }

    public void searchDelayed(final String query, final Location coordinate) {
        if (query == null || query.length() == 0) {
            this.places.clear();
            this.searchInProgress = false;
            notifyDataSetChanged();
            return;
        }
        if (this.searchRunnable != null) {
            Utilities.searchQueue.cancelRunnable(this.searchRunnable);
            this.searchRunnable = null;
        }
        this.searchInProgress = true;
        DispatchQueue dispatchQueue = Utilities.searchQueue;
        Runnable runnable = new Runnable() { // from class: org.telegram.ui.Adapters.BaseLocationAdapter$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                BaseLocationAdapter.this.m1447xdd58fbb0(query, coordinate);
            }
        };
        this.searchRunnable = runnable;
        dispatchQueue.postRunnable(runnable, 400L);
    }

    /* renamed from: lambda$searchDelayed$1$org-telegram-ui-Adapters-BaseLocationAdapter */
    public /* synthetic */ void m1447xdd58fbb0(final String query, final Location coordinate) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Adapters.BaseLocationAdapter$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                BaseLocationAdapter.this.m1446x23e16e11(query, coordinate);
            }
        });
    }

    /* renamed from: lambda$searchDelayed$0$org-telegram-ui-Adapters-BaseLocationAdapter */
    public /* synthetic */ void m1446x23e16e11(String query, Location coordinate) {
        this.searchRunnable = null;
        this.lastSearchLocation = null;
        searchPlacesWithQuery(query, coordinate, true);
    }

    private void searchBotUser() {
        if (this.searchingUser) {
            return;
        }
        this.searchingUser = true;
        TLRPC.TL_contacts_resolveUsername req = new TLRPC.TL_contacts_resolveUsername();
        req.username = MessagesController.getInstance(this.currentAccount).venueSearchBot;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.Adapters.BaseLocationAdapter$$ExternalSyntheticLambda4
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                BaseLocationAdapter.this.m1445x63b66c1e(tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$searchBotUser$3$org-telegram-ui-Adapters-BaseLocationAdapter */
    public /* synthetic */ void m1445x63b66c1e(final TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Adapters.BaseLocationAdapter$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    BaseLocationAdapter.this.m1444xaa3ede7f(response);
                }
            });
        }
    }

    /* renamed from: lambda$searchBotUser$2$org-telegram-ui-Adapters-BaseLocationAdapter */
    public /* synthetic */ void m1444xaa3ede7f(TLObject response) {
        TLRPC.TL_contacts_resolvedPeer res = (TLRPC.TL_contacts_resolvedPeer) response;
        MessagesController.getInstance(this.currentAccount).putUsers(res.users, false);
        MessagesController.getInstance(this.currentAccount).putChats(res.chats, false);
        MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(res.users, res.chats, true, true);
        Location coord = this.lastSearchLocation;
        this.lastSearchLocation = null;
        searchPlacesWithQuery(this.lastSearchQuery, coord, false);
    }

    public boolean isSearching() {
        return this.searchInProgress;
    }

    public String getLastSearchString() {
        return this.lastFoundQuery;
    }

    public void searchPlacesWithQuery(String query, Location coordinate, boolean searchUser) {
        searchPlacesWithQuery(query, coordinate, searchUser, false);
    }

    protected void notifyStartSearch(boolean wasSearching, int oldItemCount, boolean animated) {
        if (animated && Build.VERSION.SDK_INT >= 19) {
            if (this.places.isEmpty() || wasSearching) {
                if (!wasSearching) {
                    int fromIndex = Math.max(0, getItemCount() - 4);
                    notifyItemRangeRemoved(fromIndex, getItemCount() - fromIndex);
                    return;
                }
                return;
            }
            int placesCount = this.places.size() + 3;
            int offset = oldItemCount - placesCount;
            notifyItemInserted(offset);
            notifyItemRangeRemoved(offset, placesCount);
            return;
        }
        notifyDataSetChanged();
    }

    public void searchPlacesWithQuery(final String query, Location coordinate, boolean searchUser, boolean animated) {
        if (coordinate != null) {
            Location location = this.lastSearchLocation;
            if (location != null && coordinate.distanceTo(location) < 200.0f) {
                return;
            }
            this.lastSearchLocation = new Location(coordinate);
            this.lastSearchQuery = query;
            if (this.searching) {
                this.searching = false;
                if (this.currentRequestNum != 0) {
                    ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.currentRequestNum, true);
                    this.currentRequestNum = 0;
                }
            }
            getItemCount();
            boolean z = this.searching;
            this.searching = true;
            boolean z2 = this.searched;
            this.searched = true;
            TLObject object = MessagesController.getInstance(this.currentAccount).getUserOrChat(MessagesController.getInstance(this.currentAccount).venueSearchBot);
            if (!(object instanceof TLRPC.User)) {
                if (searchUser) {
                    searchBotUser();
                    return;
                }
                return;
            }
            TLRPC.User user = (TLRPC.User) object;
            TLRPC.TL_messages_getInlineBotResults req = new TLRPC.TL_messages_getInlineBotResults();
            req.query = query == null ? "" : query;
            req.bot = MessagesController.getInstance(this.currentAccount).getInputUser(user);
            req.offset = "";
            req.geo_point = new TLRPC.TL_inputGeoPoint();
            req.geo_point.lat = AndroidUtilities.fixLocationCoord(coordinate.getLatitude());
            req.geo_point._long = AndroidUtilities.fixLocationCoord(coordinate.getLongitude());
            req.flags = 1 | req.flags;
            if (DialogObject.isEncryptedDialog(this.dialogId)) {
                req.peer = new TLRPC.TL_inputPeerEmpty();
            } else {
                req.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(this.dialogId);
            }
            this.currentRequestNum = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.Adapters.BaseLocationAdapter$$ExternalSyntheticLambda5
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    BaseLocationAdapter.this.m1449xd7bd1b58(query, tLObject, tL_error);
                }
            });
            notifyDataSetChanged();
        }
    }

    /* renamed from: lambda$searchPlacesWithQuery$5$org-telegram-ui-Adapters-BaseLocationAdapter */
    public /* synthetic */ void m1449xd7bd1b58(final String query, final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Adapters.BaseLocationAdapter$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                BaseLocationAdapter.this.m1448x1e458db9(error, query, response);
            }
        });
    }

    /* renamed from: lambda$searchPlacesWithQuery$4$org-telegram-ui-Adapters-BaseLocationAdapter */
    public /* synthetic */ void m1448x1e458db9(TLRPC.TL_error error, String query, TLObject response) {
        if (error == null) {
            this.currentRequestNum = 0;
            this.searching = false;
            this.places.clear();
            this.iconUrls.clear();
            this.searchInProgress = false;
            this.lastFoundQuery = query;
            TLRPC.messages_BotResults res = (TLRPC.messages_BotResults) response;
            int size = res.results.size();
            for (int a = 0; a < size; a++) {
                TLRPC.BotInlineResult result = res.results.get(a);
                if ("venue".equals(result.type) && (result.send_message instanceof TLRPC.TL_botInlineMessageMediaVenue)) {
                    TLRPC.TL_botInlineMessageMediaVenue mediaVenue = (TLRPC.TL_botInlineMessageMediaVenue) result.send_message;
                    ArrayList<String> arrayList = this.iconUrls;
                    arrayList.add("https://ss3.4sqi.net/img/categories_v2/" + mediaVenue.venue_type + "_64.png");
                    TLRPC.TL_messageMediaVenue venue = new TLRPC.TL_messageMediaVenue();
                    venue.geo = mediaVenue.geo;
                    venue.address = mediaVenue.address;
                    venue.title = mediaVenue.title;
                    venue.venue_type = mediaVenue.venue_type;
                    venue.venue_id = mediaVenue.venue_id;
                    venue.provider = mediaVenue.provider;
                    this.places.add(venue);
                }
            }
        }
        BaseLocationAdapterDelegate baseLocationAdapterDelegate = this.delegate;
        if (baseLocationAdapterDelegate != null) {
            baseLocationAdapterDelegate.didLoadSearchResult(this.places);
        }
        notifyDataSetChanged();
    }
}
