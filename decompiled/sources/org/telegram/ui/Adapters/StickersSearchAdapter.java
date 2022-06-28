package org.telegram.ui.Adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.C;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Adapters.StickersSearchAdapter;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.FeaturedStickerSetInfoCell;
import org.telegram.ui.Cells.StickerEmojiCell;
import org.telegram.ui.Cells.StickerSetNameCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
/* loaded from: classes4.dex */
public class StickersSearchAdapter extends RecyclerListView.SelectionAdapter {
    public static final int PAYLOAD_ANIMATED = 0;
    boolean cleared;
    private final Context context;
    private final Delegate delegate;
    private int emojiSearchId;
    private ImageView emptyImageView;
    private TextView emptyTextView;
    private final LongSparseArray<TLRPC.StickerSetCovered> installingStickerSets;
    private final TLRPC.StickerSetCovered[] primaryInstallingStickerSets;
    private final LongSparseArray<TLRPC.StickerSetCovered> removingStickerSets;
    private int reqId;
    private int reqId2;
    private final Theme.ResourcesProvider resourcesProvider;
    private String searchQuery;
    private int totalItems;
    private final int currentAccount = UserConfig.selectedAccount;
    private SparseArray<Object> rowStartPack = new SparseArray<>();
    private SparseArray<Object> cache = new SparseArray<>();
    private SparseArray<Object> cacheParent = new SparseArray<>();
    private SparseIntArray positionToRow = new SparseIntArray();
    private SparseArray<String> positionToEmoji = new SparseArray<>();
    private ArrayList<TLRPC.StickerSetCovered> serverPacks = new ArrayList<>();
    private ArrayList<TLRPC.TL_messages_stickerSet> localPacks = new ArrayList<>();
    private HashMap<TLRPC.TL_messages_stickerSet, Boolean> localPacksByShortName = new HashMap<>();
    private HashMap<TLRPC.TL_messages_stickerSet, Integer> localPacksByName = new HashMap<>();
    private HashMap<ArrayList<TLRPC.Document>, String> emojiStickers = new HashMap<>();
    private ArrayList<ArrayList<TLRPC.Document>> emojiArrays = new ArrayList<>();
    private SparseArray<TLRPC.StickerSetCovered> positionsToSets = new SparseArray<>();
    private Runnable searchRunnable = new AnonymousClass1();

    /* loaded from: classes4.dex */
    public interface Delegate {
        String[] getLastSearchKeyboardLanguage();

        int getStickersPerRow();

        void onSearchStart();

        void onSearchStop();

        void onStickerSetAdd(TLRPC.StickerSetCovered stickerSetCovered, boolean z);

        void onStickerSetRemove(TLRPC.StickerSetCovered stickerSetCovered);

        void setAdapterVisible(boolean z);

        void setLastSearchKeyboardLanguage(String[] strArr);
    }

    static /* synthetic */ int access$804(StickersSearchAdapter x0) {
        int i = x0.emojiSearchId + 1;
        x0.emojiSearchId = i;
        return i;
    }

    /* renamed from: org.telegram.ui.Adapters.StickersSearchAdapter$1 */
    /* loaded from: classes4.dex */
    public class AnonymousClass1 implements Runnable {
        AnonymousClass1() {
            StickersSearchAdapter.this = this$0;
        }

        private void clear() {
            if (StickersSearchAdapter.this.cleared) {
                return;
            }
            StickersSearchAdapter.this.cleared = true;
            StickersSearchAdapter.this.emojiStickers.clear();
            StickersSearchAdapter.this.emojiArrays.clear();
            StickersSearchAdapter.this.localPacks.clear();
            StickersSearchAdapter.this.serverPacks.clear();
            StickersSearchAdapter.this.localPacksByShortName.clear();
            StickersSearchAdapter.this.localPacksByName.clear();
        }

        @Override // java.lang.Runnable
        public void run() {
            int index;
            int index2;
            if (!TextUtils.isEmpty(StickersSearchAdapter.this.searchQuery)) {
                StickersSearchAdapter.this.delegate.onSearchStart();
                StickersSearchAdapter.this.cleared = false;
                final int lastId = StickersSearchAdapter.access$804(StickersSearchAdapter.this);
                final ArrayList<TLRPC.Document> emojiStickersArray = new ArrayList<>(0);
                final LongSparseArray<TLRPC.Document> emojiStickersMap = new LongSparseArray<>(0);
                final HashMap<String, ArrayList<TLRPC.Document>> allStickers = MediaDataController.getInstance(StickersSearchAdapter.this.currentAccount).getAllStickers();
                if (StickersSearchAdapter.this.searchQuery.length() <= 14) {
                    CharSequence emoji = StickersSearchAdapter.this.searchQuery;
                    int length = emoji.length();
                    int a = 0;
                    while (a < length) {
                        if (a < length - 1 && ((emoji.charAt(a) == 55356 && emoji.charAt(a + 1) >= 57339 && emoji.charAt(a + 1) <= 57343) || (emoji.charAt(a) == 8205 && (emoji.charAt(a + 1) == 9792 || emoji.charAt(a + 1) == 9794)))) {
                            emoji = TextUtils.concat(emoji.subSequence(0, a), emoji.subSequence(a + 2, emoji.length()));
                            length -= 2;
                            a--;
                        } else if (emoji.charAt(a) == 65039) {
                            emoji = TextUtils.concat(emoji.subSequence(0, a), emoji.subSequence(a + 1, emoji.length()));
                            length--;
                            a--;
                        }
                        a++;
                    }
                    ArrayList<TLRPC.Document> newStickers = allStickers != null ? allStickers.get(emoji.toString()) : null;
                    if (newStickers != null && !newStickers.isEmpty()) {
                        clear();
                        emojiStickersArray.addAll(newStickers);
                        int size = newStickers.size();
                        for (int a2 = 0; a2 < size; a2++) {
                            TLRPC.Document document = newStickers.get(a2);
                            emojiStickersMap.put(document.id, document);
                        }
                        StickersSearchAdapter.this.emojiStickers.put(emojiStickersArray, StickersSearchAdapter.this.searchQuery);
                        StickersSearchAdapter.this.emojiArrays.add(emojiStickersArray);
                    }
                }
                if (allStickers != null && !allStickers.isEmpty() && StickersSearchAdapter.this.searchQuery.length() > 1) {
                    String[] newLanguage = AndroidUtilities.getCurrentKeyboardLanguage();
                    if (!Arrays.equals(StickersSearchAdapter.this.delegate.getLastSearchKeyboardLanguage(), newLanguage)) {
                        MediaDataController.getInstance(StickersSearchAdapter.this.currentAccount).fetchNewEmojiKeywords(newLanguage);
                    }
                    StickersSearchAdapter.this.delegate.setLastSearchKeyboardLanguage(newLanguage);
                    MediaDataController.getInstance(StickersSearchAdapter.this.currentAccount).getEmojiSuggestions(StickersSearchAdapter.this.delegate.getLastSearchKeyboardLanguage(), StickersSearchAdapter.this.searchQuery, false, new MediaDataController.KeywordResultCallback() { // from class: org.telegram.ui.Adapters.StickersSearchAdapter$1$$ExternalSyntheticLambda2
                        @Override // org.telegram.messenger.MediaDataController.KeywordResultCallback
                        public final void run(ArrayList arrayList, String str) {
                            StickersSearchAdapter.AnonymousClass1.this.m1506lambda$run$0$orgtelegramuiAdaptersStickersSearchAdapter$1(lastId, allStickers, arrayList, str);
                        }
                    });
                }
                ArrayList<TLRPC.TL_messages_stickerSet> local = MediaDataController.getInstance(StickersSearchAdapter.this.currentAccount).getStickerSets(0);
                int size2 = local.size();
                for (int a3 = 0; a3 < size2; a3++) {
                    TLRPC.TL_messages_stickerSet set = local.get(a3);
                    int index3 = AndroidUtilities.indexOfIgnoreCase(set.set.title, StickersSearchAdapter.this.searchQuery);
                    if (index3 >= 0) {
                        if (index3 == 0 || set.set.title.charAt(index3 - 1) == ' ') {
                            clear();
                            StickersSearchAdapter.this.localPacks.add(set);
                            StickersSearchAdapter.this.localPacksByName.put(set, Integer.valueOf(index3));
                        }
                    } else if (set.set.short_name != null && (index2 = AndroidUtilities.indexOfIgnoreCase(set.set.short_name, StickersSearchAdapter.this.searchQuery)) >= 0 && (index2 == 0 || set.set.short_name.charAt(index2 - 1) == ' ')) {
                        clear();
                        StickersSearchAdapter.this.localPacks.add(set);
                        StickersSearchAdapter.this.localPacksByShortName.put(set, true);
                    }
                }
                ArrayList<TLRPC.TL_messages_stickerSet> local2 = MediaDataController.getInstance(StickersSearchAdapter.this.currentAccount).getStickerSets(3);
                int size3 = local2.size();
                for (int a4 = 0; a4 < size3; a4++) {
                    TLRPC.TL_messages_stickerSet set2 = local2.get(a4);
                    int index4 = AndroidUtilities.indexOfIgnoreCase(set2.set.title, StickersSearchAdapter.this.searchQuery);
                    if (index4 >= 0) {
                        if (index4 == 0 || set2.set.title.charAt(index4 - 1) == ' ') {
                            clear();
                            StickersSearchAdapter.this.localPacks.add(set2);
                            StickersSearchAdapter.this.localPacksByName.put(set2, Integer.valueOf(index4));
                        }
                    } else if (set2.set.short_name != null && (index = AndroidUtilities.indexOfIgnoreCase(set2.set.short_name, StickersSearchAdapter.this.searchQuery)) >= 0 && (index == 0 || set2.set.short_name.charAt(index - 1) == ' ')) {
                        clear();
                        StickersSearchAdapter.this.localPacks.add(set2);
                        StickersSearchAdapter.this.localPacksByShortName.put(set2, true);
                    }
                }
                if (!StickersSearchAdapter.this.localPacks.isEmpty() || !StickersSearchAdapter.this.emojiStickers.isEmpty()) {
                    StickersSearchAdapter.this.delegate.setAdapterVisible(true);
                }
                final TLRPC.TL_messages_searchStickerSets req = new TLRPC.TL_messages_searchStickerSets();
                req.q = StickersSearchAdapter.this.searchQuery;
                StickersSearchAdapter stickersSearchAdapter = StickersSearchAdapter.this;
                stickersSearchAdapter.reqId = ConnectionsManager.getInstance(stickersSearchAdapter.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.Adapters.StickersSearchAdapter$1$$ExternalSyntheticLambda4
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        StickersSearchAdapter.AnonymousClass1.this.m1508lambda$run$2$orgtelegramuiAdaptersStickersSearchAdapter$1(req, tLObject, tL_error);
                    }
                });
                if (Emoji.isValidEmoji(StickersSearchAdapter.this.searchQuery)) {
                    final TLRPC.TL_messages_getStickers req2 = new TLRPC.TL_messages_getStickers();
                    req2.emoticon = StickersSearchAdapter.this.searchQuery;
                    req2.hash = 0L;
                    StickersSearchAdapter stickersSearchAdapter2 = StickersSearchAdapter.this;
                    stickersSearchAdapter2.reqId2 = ConnectionsManager.getInstance(stickersSearchAdapter2.currentAccount).sendRequest(req2, new RequestDelegate() { // from class: org.telegram.ui.Adapters.StickersSearchAdapter$1$$ExternalSyntheticLambda3
                        @Override // org.telegram.tgnet.RequestDelegate
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            StickersSearchAdapter.AnonymousClass1.this.m1510lambda$run$4$orgtelegramuiAdaptersStickersSearchAdapter$1(req2, emojiStickersArray, emojiStickersMap, tLObject, tL_error);
                        }
                    });
                }
                StickersSearchAdapter.this.notifyDataSetChanged();
            }
        }

        /* renamed from: lambda$run$0$org-telegram-ui-Adapters-StickersSearchAdapter$1 */
        public /* synthetic */ void m1506lambda$run$0$orgtelegramuiAdaptersStickersSearchAdapter$1(int lastId, HashMap allStickers, ArrayList param, String alias) {
            if (lastId != StickersSearchAdapter.this.emojiSearchId) {
                return;
            }
            boolean added = false;
            int size = param.size();
            for (int a = 0; a < size; a++) {
                String emoji = ((MediaDataController.KeywordResult) param.get(a)).emoji;
                ArrayList<TLRPC.Document> newStickers = allStickers != null ? (ArrayList) allStickers.get(emoji) : null;
                if (newStickers != null && !newStickers.isEmpty()) {
                    clear();
                    if (!StickersSearchAdapter.this.emojiStickers.containsKey(newStickers)) {
                        StickersSearchAdapter.this.emojiStickers.put(newStickers, emoji);
                        StickersSearchAdapter.this.emojiArrays.add(newStickers);
                        added = true;
                    }
                }
            }
            if (added) {
                StickersSearchAdapter.this.notifyDataSetChanged();
            }
        }

        /* renamed from: lambda$run$2$org-telegram-ui-Adapters-StickersSearchAdapter$1 */
        public /* synthetic */ void m1508lambda$run$2$orgtelegramuiAdaptersStickersSearchAdapter$1(final TLRPC.TL_messages_searchStickerSets req, final TLObject response, TLRPC.TL_error error) {
            if (response instanceof TLRPC.TL_messages_foundStickerSets) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Adapters.StickersSearchAdapter$1$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        StickersSearchAdapter.AnonymousClass1.this.m1507lambda$run$1$orgtelegramuiAdaptersStickersSearchAdapter$1(req, response);
                    }
                });
            }
        }

        /* renamed from: lambda$run$1$org-telegram-ui-Adapters-StickersSearchAdapter$1 */
        public /* synthetic */ void m1507lambda$run$1$orgtelegramuiAdaptersStickersSearchAdapter$1(TLRPC.TL_messages_searchStickerSets req, TLObject response) {
            if (req.q.equals(StickersSearchAdapter.this.searchQuery)) {
                clear();
                StickersSearchAdapter.this.delegate.onSearchStop();
                StickersSearchAdapter.this.reqId = 0;
                StickersSearchAdapter.this.delegate.setAdapterVisible(true);
                TLRPC.TL_messages_foundStickerSets res = (TLRPC.TL_messages_foundStickerSets) response;
                StickersSearchAdapter.this.serverPacks.addAll(res.sets);
                StickersSearchAdapter.this.notifyDataSetChanged();
            }
        }

        /* renamed from: lambda$run$4$org-telegram-ui-Adapters-StickersSearchAdapter$1 */
        public /* synthetic */ void m1510lambda$run$4$orgtelegramuiAdaptersStickersSearchAdapter$1(final TLRPC.TL_messages_getStickers req2, final ArrayList emojiStickersArray, final LongSparseArray emojiStickersMap, final TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Adapters.StickersSearchAdapter$1$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    StickersSearchAdapter.AnonymousClass1.this.m1509lambda$run$3$orgtelegramuiAdaptersStickersSearchAdapter$1(req2, response, emojiStickersArray, emojiStickersMap);
                }
            });
        }

        /* renamed from: lambda$run$3$org-telegram-ui-Adapters-StickersSearchAdapter$1 */
        public /* synthetic */ void m1509lambda$run$3$orgtelegramuiAdaptersStickersSearchAdapter$1(TLRPC.TL_messages_getStickers req2, TLObject response, ArrayList emojiStickersArray, LongSparseArray emojiStickersMap) {
            if (req2.emoticon.equals(StickersSearchAdapter.this.searchQuery)) {
                StickersSearchAdapter.this.reqId2 = 0;
                if (!(response instanceof TLRPC.TL_messages_stickers)) {
                    return;
                }
                TLRPC.TL_messages_stickers res = (TLRPC.TL_messages_stickers) response;
                int oldCount = emojiStickersArray.size();
                int size = res.stickers.size();
                for (int a = 0; a < size; a++) {
                    TLRPC.Document document = res.stickers.get(a);
                    if (emojiStickersMap.indexOfKey(document.id) < 0) {
                        emojiStickersArray.add(document);
                    }
                }
                int newCount = emojiStickersArray.size();
                if (oldCount != newCount) {
                    StickersSearchAdapter.this.emojiStickers.put(emojiStickersArray, StickersSearchAdapter.this.searchQuery);
                    if (oldCount == 0) {
                        StickersSearchAdapter.this.emojiArrays.add(emojiStickersArray);
                    }
                    StickersSearchAdapter.this.notifyDataSetChanged();
                }
            }
        }
    }

    public StickersSearchAdapter(Context context, Delegate delegate, TLRPC.StickerSetCovered[] primaryInstallingStickerSets, LongSparseArray<TLRPC.StickerSetCovered> installingStickerSets, LongSparseArray<TLRPC.StickerSetCovered> removingStickerSets, Theme.ResourcesProvider resourcesProvider) {
        this.context = context;
        this.delegate = delegate;
        this.primaryInstallingStickerSets = primaryInstallingStickerSets;
        this.installingStickerSets = installingStickerSets;
        this.removingStickerSets = removingStickerSets;
        this.resourcesProvider = resourcesProvider;
    }

    @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
    public boolean isEnabled(RecyclerView.ViewHolder holder) {
        return false;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return Math.max(1, this.totalItems + 1);
    }

    public Object getItem(int i) {
        return this.cache.get(i);
    }

    public void search(String text) {
        if (this.reqId != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.reqId, true);
            this.reqId = 0;
        }
        if (this.reqId2 != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.reqId2, true);
            this.reqId2 = 0;
        }
        if (TextUtils.isEmpty(text)) {
            this.searchQuery = null;
            this.localPacks.clear();
            this.emojiStickers.clear();
            this.serverPacks.clear();
            this.delegate.setAdapterVisible(false);
            notifyDataSetChanged();
        } else {
            this.searchQuery = text.toLowerCase();
        }
        AndroidUtilities.cancelRunOnUIThread(this.searchRunnable);
        AndroidUtilities.runOnUIThread(this.searchRunnable, 300L);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemViewType(int position) {
        if (position == 0 && this.totalItems == 0) {
            return 5;
        }
        if (position == getItemCount() - 1) {
            return 4;
        }
        Object object = this.cache.get(position);
        if (object == null) {
            return 1;
        }
        if (object instanceof TLRPC.Document) {
            return 0;
        }
        if (object instanceof TLRPC.StickerSetCovered) {
            return 3;
        }
        return 2;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType) {
            case 0:
                StickerEmojiCell stickerEmojiCell = new StickerEmojiCell(this.context, false) { // from class: org.telegram.ui.Adapters.StickersSearchAdapter.2
                    @Override // android.widget.FrameLayout, android.view.View
                    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(82.0f), C.BUFFER_FLAG_ENCRYPTED));
                    }
                };
                view = stickerEmojiCell;
                stickerEmojiCell.getImageView().setLayerNum(3);
                break;
            case 1:
                view = new EmptyCell(this.context);
                break;
            case 2:
                view = new StickerSetNameCell(this.context, false, true, this.resourcesProvider);
                break;
            case 3:
                view = new FeaturedStickerSetInfoCell(this.context, 17, true, true, this.resourcesProvider);
                ((FeaturedStickerSetInfoCell) view).setAddOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Adapters.StickersSearchAdapter$$ExternalSyntheticLambda0
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view2) {
                        StickersSearchAdapter.this.m1505x31f1c9cd(view2);
                    }
                });
                break;
            case 4:
                view = new View(this.context);
                break;
            case 5:
                LinearLayout layout = new LinearLayout(this.context);
                layout.setOrientation(1);
                layout.setGravity(17);
                ImageView imageView = new ImageView(this.context);
                this.emptyImageView = imageView;
                imageView.setScaleType(ImageView.ScaleType.CENTER);
                this.emptyImageView.setImageResource(R.drawable.stickers_empty);
                this.emptyImageView.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_chat_emojiPanelEmptyText), PorterDuff.Mode.MULTIPLY));
                layout.addView(this.emptyImageView, LayoutHelper.createLinear(-2, -2));
                layout.addView(new Space(this.context), LayoutHelper.createLinear(-1, 15));
                TextView textView = new TextView(this.context);
                this.emptyTextView = textView;
                textView.setText(LocaleController.getString("NoStickersFound", R.string.NoStickersFound));
                this.emptyTextView.setTextSize(1, 16.0f);
                this.emptyTextView.setTextColor(getThemedColor(Theme.key_chat_emojiPanelEmptyText));
                layout.addView(this.emptyTextView, LayoutHelper.createLinear(-2, -2));
                view = layout;
                view.setMinimumHeight(AndroidUtilities.dp(112.0f));
                view.setLayoutParams(LayoutHelper.createFrame(-1, -1.0f));
                break;
        }
        return new RecyclerListView.Holder(view);
    }

    /* renamed from: lambda$onCreateViewHolder$0$org-telegram-ui-Adapters-StickersSearchAdapter */
    public /* synthetic */ void m1505x31f1c9cd(View v) {
        FeaturedStickerSetInfoCell cell = (FeaturedStickerSetInfoCell) v.getParent();
        TLRPC.StickerSetCovered pack = cell.getStickerSet();
        if (pack == null || this.installingStickerSets.indexOfKey(pack.set.id) >= 0 || this.removingStickerSets.indexOfKey(pack.set.id) >= 0) {
            return;
        }
        if (cell.isInstalled()) {
            this.removingStickerSets.put(pack.set.id, pack);
            this.delegate.onStickerSetRemove(cell.getStickerSet());
            return;
        }
        installStickerSet(pack, cell);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case 0:
                TLRPC.Document sticker = (TLRPC.Document) this.cache.get(position);
                StickerEmojiCell cell = (StickerEmojiCell) holder.itemView;
                cell.setSticker(sticker, null, this.cacheParent.get(position), this.positionToEmoji.get(position), false);
                return;
            case 1:
                EmptyCell cell2 = (EmptyCell) holder.itemView;
                cell2.setHeight(0);
                return;
            case 2:
                StickerSetNameCell cell3 = (StickerSetNameCell) holder.itemView;
                Object object = this.cache.get(position);
                if (object instanceof TLRPC.TL_messages_stickerSet) {
                    TLRPC.TL_messages_stickerSet set = (TLRPC.TL_messages_stickerSet) object;
                    if (!TextUtils.isEmpty(this.searchQuery) && this.localPacksByShortName.containsKey(set)) {
                        if (set.set != null) {
                            cell3.setText(set.set.title, 0);
                        }
                        cell3.setUrl(set.set.short_name, this.searchQuery.length());
                        return;
                    }
                    Integer start = this.localPacksByName.get(set);
                    if (set.set != null && start != null) {
                        cell3.setText(set.set.title, 0, start.intValue(), !TextUtils.isEmpty(this.searchQuery) ? this.searchQuery.length() : 0);
                    }
                    cell3.setUrl(null, 0);
                    return;
                }
                return;
            case 3:
                bindFeaturedStickerSetInfoCell((FeaturedStickerSetInfoCell) holder.itemView, position, false);
                return;
            default:
                return;
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List payloads) {
        if (payloads.contains(0) && holder.getItemViewType() == 3) {
            bindFeaturedStickerSetInfoCell((FeaturedStickerSetInfoCell) holder.itemView, position, true);
        } else {
            super.onBindViewHolder(holder, position, payloads);
        }
    }

    public void installStickerSet(TLRPC.InputStickerSet inputSet) {
        for (int i = 0; i < this.serverPacks.size(); i++) {
            TLRPC.StickerSetCovered setCovered = this.serverPacks.get(i);
            if (setCovered.set.id == inputSet.id) {
                installStickerSet(setCovered, null);
                return;
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:16:0x003e, code lost:
        r0 = false;
        r1 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:17:0x0040, code lost:
        r2 = r9.primaryInstallingStickerSets;
     */
    /* JADX WARN: Code restructure failed: missing block: B:18:0x0043, code lost:
        if (r1 >= r2.length) goto L43;
     */
    /* JADX WARN: Code restructure failed: missing block: B:20:0x0047, code lost:
        if (r2[r1] != null) goto L22;
     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x0049, code lost:
        r2[r1] = r10;
        r0 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:22:0x004d, code lost:
        r1 = r1 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x0050, code lost:
        if (r0 != false) goto L26;
     */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x0052, code lost:
        if (r11 == null) goto L26;
     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x0054, code lost:
        r11.setAddDrawProgress(true, true);
     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x0058, code lost:
        r9.installingStickerSets.put(r10.set.id, r10);
     */
    /* JADX WARN: Code restructure failed: missing block: B:27:0x0061, code lost:
        if (r11 == null) goto L29;
     */
    /* JADX WARN: Code restructure failed: missing block: B:28:0x0063, code lost:
        r9.delegate.onStickerSetAdd(r11.getStickerSet(), r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:29:0x006d, code lost:
        r1 = 0;
        r2 = r9.positionsToSets.size();
     */
    /* JADX WARN: Code restructure failed: missing block: B:30:0x0074, code lost:
        if (r1 >= r2) goto L45;
     */
    /* JADX WARN: Code restructure failed: missing block: B:31:0x0076, code lost:
        r3 = r9.positionsToSets.get(r1);
     */
    /* JADX WARN: Code restructure failed: missing block: B:32:0x007e, code lost:
        if (r3 == null) goto L47;
     */
    /* JADX WARN: Code restructure failed: missing block: B:34:0x008a, code lost:
        if (r3.set.id != r10.set.id) goto L48;
     */
    /* JADX WARN: Code restructure failed: missing block: B:35:0x008c, code lost:
        notifyItemChanged(r1, 0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:36:0x0095, code lost:
        r1 = r1 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:37:0x0098, code lost:
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:49:?, code lost:
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:50:?, code lost:
        return;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void installStickerSet(org.telegram.tgnet.TLRPC.StickerSetCovered r10, org.telegram.ui.Cells.FeaturedStickerSetInfoCell r11) {
        /*
            r9 = this;
            r0 = 0
        L1:
            org.telegram.tgnet.TLRPC$StickerSetCovered[] r1 = r9.primaryInstallingStickerSets
            int r2 = r1.length
            if (r0 >= r2) goto L3e
            r1 = r1[r0]
            if (r1 == 0) goto L3b
            int r1 = r9.currentAccount
            org.telegram.messenger.MediaDataController r1 = org.telegram.messenger.MediaDataController.getInstance(r1)
            org.telegram.tgnet.TLRPC$StickerSetCovered[] r2 = r9.primaryInstallingStickerSets
            r2 = r2[r0]
            org.telegram.tgnet.TLRPC$StickerSet r2 = r2.set
            long r2 = r2.id
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r1 = r1.getStickerSetById(r2)
            if (r1 == 0) goto L2a
            org.telegram.tgnet.TLRPC$StickerSet r2 = r1.set
            boolean r2 = r2.archived
            if (r2 != 0) goto L2a
            org.telegram.tgnet.TLRPC$StickerSetCovered[] r2 = r9.primaryInstallingStickerSets
            r3 = 0
            r2[r0] = r3
            goto L3e
        L2a:
            org.telegram.tgnet.TLRPC$StickerSetCovered[] r2 = r9.primaryInstallingStickerSets
            r2 = r2[r0]
            org.telegram.tgnet.TLRPC$StickerSet r2 = r2.set
            long r2 = r2.id
            org.telegram.tgnet.TLRPC$StickerSet r4 = r10.set
            long r4 = r4.id
            int r6 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r6 != 0) goto L3b
            return
        L3b:
            int r0 = r0 + 1
            goto L1
        L3e:
            r0 = 0
            r1 = 0
        L40:
            org.telegram.tgnet.TLRPC$StickerSetCovered[] r2 = r9.primaryInstallingStickerSets
            int r3 = r2.length
            if (r1 >= r3) goto L50
            r3 = r2[r1]
            if (r3 != 0) goto L4d
            r2[r1] = r10
            r0 = 1
            goto L50
        L4d:
            int r1 = r1 + 1
            goto L40
        L50:
            if (r0 != 0) goto L58
            if (r11 == 0) goto L58
            r1 = 1
            r11.setAddDrawProgress(r1, r1)
        L58:
            android.util.LongSparseArray<org.telegram.tgnet.TLRPC$StickerSetCovered> r1 = r9.installingStickerSets
            org.telegram.tgnet.TLRPC$StickerSet r2 = r10.set
            long r2 = r2.id
            r1.put(r2, r10)
            if (r11 == 0) goto L6d
            org.telegram.ui.Adapters.StickersSearchAdapter$Delegate r1 = r9.delegate
            org.telegram.tgnet.TLRPC$StickerSetCovered r2 = r11.getStickerSet()
            r1.onStickerSetAdd(r2, r0)
            goto L98
        L6d:
            r1 = 0
            android.util.SparseArray<org.telegram.tgnet.TLRPC$StickerSetCovered> r2 = r9.positionsToSets
            int r2 = r2.size()
        L74:
            if (r1 >= r2) goto L98
            android.util.SparseArray<org.telegram.tgnet.TLRPC$StickerSetCovered> r3 = r9.positionsToSets
            java.lang.Object r3 = r3.get(r1)
            org.telegram.tgnet.TLRPC$StickerSetCovered r3 = (org.telegram.tgnet.TLRPC.StickerSetCovered) r3
            if (r3 == 0) goto L95
            org.telegram.tgnet.TLRPC$StickerSet r4 = r3.set
            long r4 = r4.id
            org.telegram.tgnet.TLRPC$StickerSet r6 = r10.set
            long r6 = r6.id
            int r8 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r8 != 0) goto L95
            r4 = 0
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            r9.notifyItemChanged(r1, r4)
            goto L98
        L95:
            int r1 = r1 + 1
            goto L74
        L98:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.StickersSearchAdapter.installStickerSet(org.telegram.tgnet.TLRPC$StickerSetCovered, org.telegram.ui.Cells.FeaturedStickerSetInfoCell):void");
    }

    private void bindFeaturedStickerSetInfoCell(FeaturedStickerSetInfoCell cell, int position, boolean animated) {
        boolean forceInstalled;
        MediaDataController mediaDataController = MediaDataController.getInstance(this.currentAccount);
        ArrayList<Long> unreadStickers = mediaDataController.getUnreadStickerSets();
        TLRPC.StickerSetCovered stickerSetCovered = (TLRPC.StickerSetCovered) this.cache.get(position);
        boolean z = false;
        boolean unread = unreadStickers != null && unreadStickers.contains(Long.valueOf(stickerSetCovered.set.id));
        int i = 0;
        while (true) {
            TLRPC.StickerSetCovered[] stickerSetCoveredArr = this.primaryInstallingStickerSets;
            if (i >= stickerSetCoveredArr.length) {
                forceInstalled = false;
                break;
            }
            if (stickerSetCoveredArr[i] != null) {
                TLRPC.TL_messages_stickerSet s = MediaDataController.getInstance(this.currentAccount).getStickerSetById(this.primaryInstallingStickerSets[i].set.id);
                if (s != null && !s.set.archived) {
                    this.primaryInstallingStickerSets[i] = null;
                } else if (this.primaryInstallingStickerSets[i].set.id == stickerSetCovered.set.id) {
                    forceInstalled = true;
                    break;
                }
            }
            i++;
        }
        int idx = TextUtils.isEmpty(this.searchQuery) ? -1 : AndroidUtilities.indexOfIgnoreCase(stickerSetCovered.set.title, this.searchQuery);
        if (idx >= 0) {
            cell.setStickerSet(stickerSetCovered, unread, animated, idx, this.searchQuery.length(), forceInstalled);
        } else {
            cell.setStickerSet(stickerSetCovered, unread, animated, 0, 0, forceInstalled);
            if (!TextUtils.isEmpty(this.searchQuery) && AndroidUtilities.indexOfIgnoreCase(stickerSetCovered.set.short_name, this.searchQuery) == 0) {
                cell.setUrl(stickerSetCovered.set.short_name, this.searchQuery.length());
            }
        }
        if (unread) {
            mediaDataController.markFaturedStickersByIdAsRead(stickerSetCovered.set.id);
        }
        boolean installing = this.installingStickerSets.indexOfKey(stickerSetCovered.set.id) >= 0;
        boolean removing = this.removingStickerSets.indexOfKey(stickerSetCovered.set.id) >= 0;
        if (installing || removing) {
            if (installing && cell.isInstalled()) {
                this.installingStickerSets.remove(stickerSetCovered.set.id);
                installing = false;
            } else if (removing && !cell.isInstalled()) {
                this.removingStickerSets.remove(stickerSetCovered.set.id);
            }
        }
        cell.setAddDrawProgress(!forceInstalled && installing, animated);
        mediaDataController.preloadStickerSetThumb(stickerSetCovered);
        if (position > 0) {
            z = true;
        }
        cell.setNeedDivider(z);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void notifyDataSetChanged() {
        int serverCount;
        ArrayList<TLRPC.Document> documents;
        Object pack;
        this.rowStartPack.clear();
        this.positionToRow.clear();
        this.cache.clear();
        this.positionsToSets.clear();
        this.positionToEmoji.clear();
        this.totalItems = 0;
        int startRow = 0;
        int a = 0;
        int serverCount2 = this.serverPacks.size();
        int localCount = this.localPacks.size();
        int emojiCount = !this.emojiArrays.isEmpty() ? 1 : 0;
        while (a < serverCount2 + localCount + emojiCount) {
            Object pack2 = null;
            int idx = a;
            if (idx < localCount) {
                TLRPC.TL_messages_stickerSet set = this.localPacks.get(idx);
                documents = set.documents;
                pack = set;
                serverCount = serverCount2;
            } else {
                int idx2 = idx - localCount;
                if (idx2 < emojiCount) {
                    int documentsCount = 0;
                    String lastEmoji = "";
                    int N = this.emojiArrays.size();
                    for (int i = 0; i < N; i++) {
                        ArrayList<TLRPC.Document> documents2 = this.emojiArrays.get(i);
                        String emoji = this.emojiStickers.get(documents2);
                        if (emoji != null && !lastEmoji.equals(emoji)) {
                            lastEmoji = emoji;
                            this.positionToEmoji.put(this.totalItems + documentsCount, lastEmoji);
                        }
                        int b = 0;
                        int size = documents2.size();
                        while (b < size) {
                            int serverCount3 = serverCount2;
                            int serverCount4 = this.totalItems;
                            int num = serverCount4 + documentsCount;
                            String lastEmoji2 = lastEmoji;
                            int row = (documentsCount / this.delegate.getStickersPerRow()) + startRow;
                            int N2 = N;
                            TLRPC.Document document = documents2.get(b);
                            ArrayList<TLRPC.Document> documents3 = documents2;
                            this.cache.put(num, document);
                            String emoji2 = emoji;
                            int b2 = b;
                            Object parent = MediaDataController.getInstance(this.currentAccount).getStickerSetById(MediaDataController.getStickerSetId(document));
                            if (parent != null) {
                                this.cacheParent.put(num, parent);
                            }
                            this.positionToRow.put(num, row);
                            if (a >= localCount && (pack2 instanceof TLRPC.StickerSetCovered)) {
                                this.positionsToSets.put(num, null);
                            }
                            documentsCount++;
                            b = b2 + 1;
                            serverCount2 = serverCount3;
                            lastEmoji = lastEmoji2;
                            documents2 = documents3;
                            N = N2;
                            emoji = emoji2;
                        }
                    }
                    serverCount = serverCount2;
                    int count = (int) Math.ceil(documentsCount / this.delegate.getStickersPerRow());
                    for (int b3 = 0; b3 < count; b3++) {
                        this.rowStartPack.put(startRow + b3, Integer.valueOf(documentsCount));
                    }
                    int b4 = this.totalItems;
                    this.totalItems = b4 + (this.delegate.getStickersPerRow() * count);
                    startRow += count;
                    a++;
                    serverCount2 = serverCount;
                } else {
                    serverCount = serverCount2;
                    TLRPC.StickerSetCovered set2 = this.serverPacks.get(idx2 - emojiCount);
                    documents = set2.covers;
                    pack = set2;
                }
            }
            if (!documents.isEmpty()) {
                int count2 = (int) Math.ceil(documents.size() / this.delegate.getStickersPerRow());
                this.cache.put(this.totalItems, pack);
                if (a >= localCount && (pack instanceof TLRPC.StickerSetCovered)) {
                    this.positionsToSets.put(this.totalItems, (TLRPC.StickerSetCovered) pack);
                }
                this.positionToRow.put(this.totalItems, startRow);
                int size2 = documents.size();
                for (int b5 = 0; b5 < size2; b5++) {
                    int num2 = b5 + 1 + this.totalItems;
                    int row2 = startRow + 1 + (b5 / this.delegate.getStickersPerRow());
                    this.cache.put(num2, documents.get(b5));
                    if (pack != null) {
                        this.cacheParent.put(num2, pack);
                    }
                    this.positionToRow.put(num2, row2);
                    if (a >= localCount && (pack instanceof TLRPC.StickerSetCovered)) {
                        this.positionsToSets.put(num2, (TLRPC.StickerSetCovered) pack);
                    }
                }
                int N3 = count2 + 1;
                for (int b6 = 0; b6 < N3; b6++) {
                    this.rowStartPack.put(startRow + b6, pack);
                }
                int b7 = this.totalItems;
                this.totalItems = b7 + (this.delegate.getStickersPerRow() * count2) + 1;
                startRow += count2 + 1;
            }
            a++;
            serverCount2 = serverCount;
        }
        super.notifyDataSetChanged();
    }

    public int getSpanSize(int position) {
        if (position != this.totalItems) {
            Object object = this.cache.get(position);
            if (object == null || (this.cache.get(position) instanceof TLRPC.Document)) {
                return 1;
            }
        }
        return this.delegate.getStickersPerRow();
    }

    public TLRPC.StickerSetCovered getSetForPosition(int position) {
        return this.positionsToSets.get(position);
    }

    public void updateColors(RecyclerListView listView) {
        int size = listView.getChildCount();
        for (int i = 0; i < size; i++) {
            View child = listView.getChildAt(i);
            if (child instanceof FeaturedStickerSetInfoCell) {
                ((FeaturedStickerSetInfoCell) child).updateColors();
            } else if (child instanceof StickerSetNameCell) {
                ((StickerSetNameCell) child).updateColors();
            }
        }
    }

    public void getThemeDescriptions(List<ThemeDescription> descriptions, RecyclerListView listView, ThemeDescription.ThemeDescriptionDelegate delegate) {
        FeaturedStickerSetInfoCell.createThemeDescriptions(descriptions, listView, delegate);
        StickerSetNameCell.createThemeDescriptions(descriptions, listView, delegate);
        descriptions.add(new ThemeDescription(this.emptyImageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_chat_emojiPanelEmptyText));
        descriptions.add(new ThemeDescription(this.emptyTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_emojiPanelEmptyText));
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
