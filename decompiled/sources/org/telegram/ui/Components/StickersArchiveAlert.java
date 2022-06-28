package org.telegram.ui.Components;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ArchivedStickerSetCell;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.StickersActivity;
/* loaded from: classes5.dex */
public class StickersArchiveAlert extends AlertDialog.Builder {
    private int currentType;
    private boolean ignoreLayout;
    private BaseFragment parentFragment;
    private int reqId;
    private int scrollOffsetY;
    private ArrayList<TLRPC.StickerSetCovered> stickerSets;

    public StickersArchiveAlert(Context context, BaseFragment baseFragment, ArrayList<TLRPC.StickerSetCovered> sets) {
        super(context);
        TLRPC.StickerSetCovered set = sets.get(0);
        if (set.set.masks) {
            this.currentType = 1;
            setTitle(LocaleController.getString("ArchivedMasksAlertTitle", R.string.ArchivedMasksAlertTitle));
        } else {
            this.currentType = 0;
            setTitle(LocaleController.getString("ArchivedStickersAlertTitle", R.string.ArchivedStickersAlertTitle));
        }
        this.stickerSets = new ArrayList<>(sets);
        this.parentFragment = baseFragment;
        LinearLayout container = new LinearLayout(context);
        container.setOrientation(1);
        setView(container);
        TextView textView = new TextView(context);
        textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        textView.setGravity(LayoutHelper.getAbsoluteGravityStart());
        textView.setTextSize(1, 16.0f);
        textView.setPadding(AndroidUtilities.dp(23.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(23.0f), 0);
        if (set.set.masks) {
            textView.setText(LocaleController.getString("ArchivedMasksAlertInfo", R.string.ArchivedMasksAlertInfo));
        } else {
            textView.setText(LocaleController.getString("ArchivedStickersAlertInfo", R.string.ArchivedStickersAlertInfo));
        }
        container.addView(textView, LayoutHelper.createLinear(-2, -2));
        RecyclerListView listView = new RecyclerListView(context);
        listView.setLayoutManager(new LinearLayoutManager(getContext(), 1, false));
        listView.setAdapter(new ListAdapter(context));
        listView.setVerticalScrollBarEnabled(false);
        listView.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
        listView.setGlowColor(-657673);
        container.addView(listView, LayoutHelper.createLinear(-1, -2, 0.0f, 10.0f, 0.0f, 0.0f));
        setNegativeButton(LocaleController.getString("Close", R.string.Close), StickersArchiveAlert$$ExternalSyntheticLambda1.INSTANCE);
        if (this.parentFragment != null) {
            setPositiveButton(LocaleController.getString("Settings", R.string.Settings), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.StickersArchiveAlert$$ExternalSyntheticLambda0
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    StickersArchiveAlert.this.m3113lambda$new$1$orgtelegramuiComponentsStickersArchiveAlert(dialogInterface, i);
                }
            });
        }
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-StickersArchiveAlert */
    public /* synthetic */ void m3113lambda$new$1$orgtelegramuiComponentsStickersArchiveAlert(DialogInterface dialog, int which) {
        this.parentFragment.presentFragment(new StickersActivity(this.currentType));
        dialog.dismiss();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        Context context;

        public ListAdapter(Context context) {
            StickersArchiveAlert.this = r1;
            this.context = context;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return StickersArchiveAlert.this.stickerSets.size();
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return false;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = new ArchivedStickerSetCell(this.context, false);
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(82.0f)));
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ArchivedStickerSetCell archivedStickerSetCell = (ArchivedStickerSetCell) holder.itemView;
            TLRPC.StickerSetCovered stickerSetCovered = (TLRPC.StickerSetCovered) StickersArchiveAlert.this.stickerSets.get(position);
            boolean z = true;
            if (position == StickersArchiveAlert.this.stickerSets.size() - 1) {
                z = false;
            }
            archivedStickerSetCell.setStickersSet(stickerSetCovered, z);
        }
    }
}
