package org.telegram.messenger;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.RectF;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import androidx.collection.LongSparseArray;
import java.util.ArrayList;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
/* compiled from: ContactsWidgetService.java */
/* loaded from: classes4.dex */
class ContactsRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private AccountInstance accountInstance;
    private int appWidgetId;
    private RectF bitmapRect;
    private boolean deleted;
    private Context mContext;
    private Paint roundPaint;
    private ArrayList<Long> dids = new ArrayList<>();
    private LongSparseArray<TLRPC.Dialog> dialogs = new LongSparseArray<>();

    public ContactsRemoteViewsFactory(Context context, Intent intent) {
        this.mContext = context;
        Theme.createDialogsResources(context);
        boolean z = false;
        this.appWidgetId = intent.getIntExtra("appWidgetId", 0);
        SharedPreferences preferences = context.getSharedPreferences("shortcut_widget", 0);
        int accountId = preferences.getInt("account" + this.appWidgetId, -1);
        if (accountId >= 0) {
            this.accountInstance = AccountInstance.getInstance(accountId);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("deleted");
        sb.append(this.appWidgetId);
        this.deleted = (preferences.getBoolean(sb.toString(), false) || this.accountInstance == null) ? true : z;
    }

    @Override // android.widget.RemoteViewsService.RemoteViewsFactory
    public void onCreate() {
        ApplicationLoader.postInitApplication();
    }

    @Override // android.widget.RemoteViewsService.RemoteViewsFactory
    public void onDestroy() {
    }

    @Override // android.widget.RemoteViewsService.RemoteViewsFactory
    public int getCount() {
        if (this.deleted) {
            return 1;
        }
        int count = (int) Math.ceil(this.dids.size() / 2.0f);
        return count + 1;
    }

    /* JADX WARN: Can't wrap try/catch for region: R(26:24|(1:26)(2:27|(1:29)(2:30|(1:32)(1:33)))|34|(19:47|(1:62)(1:63)|64|(3:137|66|67)|70|139|71|(3:(2:74|(1:76)(2:77|(1:79)))(1:80)|81|82)(10:83|84|135|85|(2:141|87)|90|91|92|143|93)|94|(1:96)(1:97)|98|105|(3:120|(1:122)|123)(6:109|(1:111)(1:112)|(1:114)(1:115)|116|(1:118)|119)|124|(1:126)(1:127)|128|(1:130)(1:131)|132|146)|59|(0)(0)|64|(0)|70|139|71|(0)(0)|94|(0)(0)|98|105|(1:107)|120|(0)|123|124|(0)(0)|128|(0)(0)|132|146) */
    /* JADX WARN: Code restructure failed: missing block: B:102:0x0265, code lost:
        r0 = th;
     */
    /* JADX WARN: Removed duplicated region for block: B:107:0x0281  */
    /* JADX WARN: Removed duplicated region for block: B:122:0x02c9  */
    /* JADX WARN: Removed duplicated region for block: B:126:0x02e0  */
    /* JADX WARN: Removed duplicated region for block: B:127:0x02ea  */
    /* JADX WARN: Removed duplicated region for block: B:130:0x0307  */
    /* JADX WARN: Removed duplicated region for block: B:131:0x030b  */
    /* JADX WARN: Removed duplicated region for block: B:137:0x018d A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:62:0x0180  */
    /* JADX WARN: Removed duplicated region for block: B:63:0x0184  */
    /* JADX WARN: Removed duplicated region for block: B:73:0x01bf  */
    /* JADX WARN: Removed duplicated region for block: B:83:0x01ef A[Catch: all -> 0x0265, TRY_ENTER, TRY_LEAVE, TryCatch #2 {all -> 0x0265, blocks: (B:71:0x01aa, B:83:0x01ef), top: B:139:0x01aa }] */
    /* JADX WARN: Removed duplicated region for block: B:96:0x0254  */
    /* JADX WARN: Removed duplicated region for block: B:97:0x0258  */
    @Override // android.widget.RemoteViewsService.RemoteViewsFactory
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public android.widget.RemoteViews getViewAt(int r22) {
        /*
            Method dump skipped, instructions count: 793
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ContactsRemoteViewsFactory.getViewAt(int):android.widget.RemoteViews");
    }

    @Override // android.widget.RemoteViewsService.RemoteViewsFactory
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override // android.widget.RemoteViewsService.RemoteViewsFactory
    public int getViewTypeCount() {
        return 2;
    }

    @Override // android.widget.RemoteViewsService.RemoteViewsFactory
    public long getItemId(int position) {
        return position;
    }

    @Override // android.widget.RemoteViewsService.RemoteViewsFactory
    public boolean hasStableIds() {
        return true;
    }

    @Override // android.widget.RemoteViewsService.RemoteViewsFactory
    public void onDataSetChanged() {
        this.dids.clear();
        AccountInstance accountInstance = this.accountInstance;
        if (accountInstance == null || !accountInstance.getUserConfig().isClientActivated()) {
            return;
        }
        ArrayList<TLRPC.User> users = new ArrayList<>();
        ArrayList<TLRPC.Chat> chats = new ArrayList<>();
        LongSparseArray<TLRPC.Message> messages = new LongSparseArray<>();
        this.accountInstance.getMessagesStorage().getWidgetDialogs(this.appWidgetId, 1, this.dids, this.dialogs, messages, users, chats);
        this.accountInstance.getMessagesController().putUsers(users, true);
        this.accountInstance.getMessagesController().putChats(chats, true);
    }
}
