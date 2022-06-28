package org.telegram.ui.Adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.EmojiReplacementCell;
import org.telegram.ui.Components.RecyclerListView;
/* loaded from: classes4.dex */
public class StickersAdapter extends RecyclerListView.SelectionAdapter implements NotificationCenter.NotificationCenterDelegate {
    private int currentAccount;
    private StickersAdapterDelegate delegate;
    private ArrayList<MediaDataController.KeywordResult> keywordResults;
    private String lastSearch;
    private String[] lastSearchKeyboardLanguage;
    private Context mContext;
    private final Theme.ResourcesProvider resourcesProvider;
    private Runnable searchRunnable;
    private boolean visible;

    /* loaded from: classes4.dex */
    public interface StickersAdapterDelegate {
        void needChangePanelVisibility(boolean z);
    }

    public StickersAdapter(Context context, StickersAdapterDelegate delegate, Theme.ResourcesProvider resourcesProvider) {
        int i = UserConfig.selectedAccount;
        this.currentAccount = i;
        this.mContext = context;
        this.delegate = delegate;
        this.resourcesProvider = resourcesProvider;
        MediaDataController.getInstance(i).checkStickers(0);
        MediaDataController.getInstance(this.currentAccount).checkStickers(1);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.newEmojiSuggestionsAvailable);
    }

    public void onDestroy() {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.newEmojiSuggestionsAvailable);
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.newEmojiSuggestionsAvailable) {
            ArrayList<MediaDataController.KeywordResult> arrayList = this.keywordResults;
            if ((arrayList == null || arrayList.isEmpty()) && !TextUtils.isEmpty(this.lastSearch) && getItemCount() == 0) {
                searchEmojiByKeyword();
            }
        }
    }

    public void hide() {
        ArrayList<MediaDataController.KeywordResult> arrayList;
        if (this.visible && (arrayList = this.keywordResults) != null && !arrayList.isEmpty()) {
            this.visible = false;
            this.delegate.needChangePanelVisibility(false);
        }
    }

    private void cancelEmojiSearch() {
        Runnable runnable = this.searchRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.searchRunnable = null;
        }
    }

    private void searchEmojiByKeyword() {
        String[] newLanguage = AndroidUtilities.getCurrentKeyboardLanguage();
        if (!Arrays.equals(newLanguage, this.lastSearchKeyboardLanguage)) {
            MediaDataController.getInstance(this.currentAccount).fetchNewEmojiKeywords(newLanguage);
        }
        this.lastSearchKeyboardLanguage = newLanguage;
        final String query = this.lastSearch;
        cancelEmojiSearch();
        this.searchRunnable = new Runnable() { // from class: org.telegram.ui.Adapters.StickersAdapter$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                StickersAdapter.this.m1504xf9c69f8c(query);
            }
        };
        ArrayList<MediaDataController.KeywordResult> arrayList = this.keywordResults;
        if (arrayList == null || arrayList.isEmpty()) {
            AndroidUtilities.runOnUIThread(this.searchRunnable, 1000L);
        } else {
            this.searchRunnable.run();
        }
    }

    /* renamed from: lambda$searchEmojiByKeyword$1$org-telegram-ui-Adapters-StickersAdapter */
    public /* synthetic */ void m1504xf9c69f8c(final String query) {
        MediaDataController.getInstance(this.currentAccount).getEmojiSuggestions(this.lastSearchKeyboardLanguage, query, true, new MediaDataController.KeywordResultCallback() { // from class: org.telegram.ui.Adapters.StickersAdapter$$ExternalSyntheticLambda1
            @Override // org.telegram.messenger.MediaDataController.KeywordResultCallback
            public final void run(ArrayList arrayList, String str) {
                StickersAdapter.this.m1503xf2616a6d(query, arrayList, str);
            }
        });
    }

    /* renamed from: lambda$searchEmojiByKeyword$0$org-telegram-ui-Adapters-StickersAdapter */
    public /* synthetic */ void m1503xf2616a6d(String query, ArrayList param, String alias) {
        if (query.equals(this.lastSearch)) {
            if (!param.isEmpty()) {
                this.keywordResults = param;
            }
            notifyDataSetChanged();
            StickersAdapterDelegate stickersAdapterDelegate = this.delegate;
            boolean z = !param.isEmpty();
            this.visible = z;
            stickersAdapterDelegate.needChangePanelVisibility(z);
        }
    }

    public void searchEmojiByKeyword(CharSequence emoji) {
        ArrayList<MediaDataController.KeywordResult> arrayList;
        TLRPC.Document animatedSticker;
        boolean searchEmoji = emoji != null && emoji.length() > 0 && emoji.length() <= 14;
        String originalEmoji = "";
        if (searchEmoji) {
            originalEmoji = emoji.toString();
            int length = emoji.length();
            int a = 0;
            while (a < length) {
                char ch = emoji.charAt(a);
                char nch = a < length + (-1) ? emoji.charAt(a + 1) : (char) 0;
                if (a < length - 1 && ch == 55356 && nch >= 57339 && nch <= 57343) {
                    emoji = TextUtils.concat(emoji.subSequence(0, a), emoji.subSequence(a + 2, emoji.length()));
                    length -= 2;
                    a--;
                } else if (ch == 65039) {
                    emoji = TextUtils.concat(emoji.subSequence(0, a), emoji.subSequence(a + 1, emoji.length()));
                    length--;
                    a--;
                }
                a++;
            }
        }
        this.lastSearch = emoji.toString().trim();
        boolean isValidEmoji = searchEmoji && (Emoji.isValidEmoji(originalEmoji) || Emoji.isValidEmoji(this.lastSearch));
        if (isValidEmoji && (animatedSticker = MediaDataController.getInstance(this.currentAccount).getEmojiAnimatedSticker(emoji)) != null) {
            ArrayList<TLRPC.TL_messages_stickerSet> sets = MediaDataController.getInstance(this.currentAccount).getStickerSets(4);
            File f = FileLoader.getInstance(this.currentAccount).getPathToAttach(animatedSticker, true);
            if (!f.exists()) {
                FileLoader.getInstance(this.currentAccount).loadFile(ImageLocation.getForDocument(animatedSticker), sets.get(0), null, 1, 1);
            }
        }
        if (this.visible && ((arrayList = this.keywordResults) == null || arrayList.isEmpty())) {
            this.visible = false;
            this.delegate.needChangePanelVisibility(false);
            notifyDataSetChanged();
        }
        if (!isValidEmoji) {
            searchEmojiByKeyword();
            return;
        }
        clearSearch();
        this.delegate.needChangePanelVisibility(false);
    }

    public void clearSearch() {
        this.lastSearch = null;
        this.keywordResults = null;
        notifyDataSetChanged();
    }

    public String getQuery() {
        return this.lastSearch;
    }

    public boolean isShowingKeywords() {
        ArrayList<MediaDataController.KeywordResult> arrayList = this.keywordResults;
        return arrayList != null && !arrayList.isEmpty();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        ArrayList<MediaDataController.KeywordResult> arrayList = this.keywordResults;
        if (arrayList != null && !arrayList.isEmpty()) {
            return this.keywordResults.size();
        }
        return 0;
    }

    public Object getItem(int i) {
        ArrayList<MediaDataController.KeywordResult> arrayList = this.keywordResults;
        if (arrayList == null || arrayList.isEmpty() || i < 0 || i >= this.keywordResults.size()) {
            return null;
        }
        return this.keywordResults.get(i).emoji;
    }

    @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
    public boolean isEnabled(RecyclerView.ViewHolder holder) {
        return false;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return new RecyclerListView.Holder(new EmojiReplacementCell(this.mContext, this.resourcesProvider));
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemViewType(int position) {
        return 0;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int side = 0;
        if (position == 0) {
            if (this.keywordResults.size() == 1) {
                side = 2;
            } else {
                side = -1;
            }
        } else if (position == this.keywordResults.size() - 1) {
            side = 1;
        }
        EmojiReplacementCell cell = (EmojiReplacementCell) holder.itemView;
        cell.setEmoji(this.keywordResults.get(position).emoji, side);
    }
}
