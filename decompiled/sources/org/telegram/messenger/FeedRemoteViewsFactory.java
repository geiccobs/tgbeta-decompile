package org.telegram.messenger;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import androidx.core.content.FileProvider;
import com.google.firebase.messaging.Constants;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.NotificationCenter;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC;
/* compiled from: FeedWidgetService.java */
/* loaded from: classes4.dex */
public class FeedRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory, NotificationCenter.NotificationCenterDelegate {
    private AccountInstance accountInstance;
    private int classGuid;
    private long dialogId;
    private Context mContext;
    private ArrayList<MessageObject> messages = new ArrayList<>();
    private CountDownLatch countDownLatch = new CountDownLatch(1);

    public FeedRemoteViewsFactory(Context context, Intent intent) {
        this.mContext = context;
        int appWidgetId = intent.getIntExtra("appWidgetId", 0);
        SharedPreferences preferences = context.getSharedPreferences("shortcut_widget", 0);
        int accountId = preferences.getInt("account" + appWidgetId, -1);
        if (accountId >= 0) {
            this.dialogId = preferences.getLong("dialogId" + appWidgetId, 0L);
            this.accountInstance = AccountInstance.getInstance(accountId);
        }
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
        return this.messages.size();
    }

    protected void grantUriAccessToWidget(Context context, Uri uri) {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(intent, 65536);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            context.grantUriPermission(packageName, uri, 1);
        }
    }

    @Override // android.widget.RemoteViewsService.RemoteViewsFactory
    public RemoteViews getViewAt(int position) {
        MessageObject messageObject = this.messages.get(position);
        RemoteViews rv = new RemoteViews(this.mContext.getPackageName(), (int) org.telegram.messenger.beta.R.layout.feed_widget_item);
        if (messageObject.type == 0) {
            rv.setTextViewText(org.telegram.messenger.beta.R.id.feed_widget_item_text, messageObject.messageText);
            rv.setViewVisibility(org.telegram.messenger.beta.R.id.feed_widget_item_text, 0);
        } else if (TextUtils.isEmpty(messageObject.caption)) {
            rv.setViewVisibility(org.telegram.messenger.beta.R.id.feed_widget_item_text, 8);
        } else {
            rv.setTextViewText(org.telegram.messenger.beta.R.id.feed_widget_item_text, messageObject.caption);
            rv.setViewVisibility(org.telegram.messenger.beta.R.id.feed_widget_item_text, 0);
        }
        if (messageObject.photoThumbs == null || messageObject.photoThumbs.isEmpty()) {
            rv.setViewVisibility(org.telegram.messenger.beta.R.id.feed_widget_item_image, 8);
        } else {
            TLRPC.PhotoSize size = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, AndroidUtilities.getPhotoSize());
            File f = FileLoader.getInstance(UserConfig.selectedAccount).getPathToAttach(size);
            if (f.exists()) {
                rv.setViewVisibility(org.telegram.messenger.beta.R.id.feed_widget_item_image, 0);
                Uri uri = FileProvider.getUriForFile(this.mContext, "org.telegram.messenger.beta.provider", f);
                grantUriAccessToWidget(this.mContext, uri);
                rv.setImageViewUri(org.telegram.messenger.beta.R.id.feed_widget_item_image, uri);
            } else {
                rv.setViewVisibility(org.telegram.messenger.beta.R.id.feed_widget_item_image, 8);
            }
        }
        Bundle extras = new Bundle();
        extras.putLong("chatId", -messageObject.getDialogId());
        extras.putInt(Constants.MessagePayloadKeys.MSGID_SERVER, messageObject.getId());
        extras.putInt("currentAccount", this.accountInstance.getCurrentAccount());
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        rv.setOnClickFillInIntent(org.telegram.messenger.beta.R.id.shortcut_widget_item, fillInIntent);
        return rv;
    }

    @Override // android.widget.RemoteViewsService.RemoteViewsFactory
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override // android.widget.RemoteViewsService.RemoteViewsFactory
    public int getViewTypeCount() {
        return 1;
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
        AccountInstance accountInstance = this.accountInstance;
        if (accountInstance == null || !accountInstance.getUserConfig().isClientActivated()) {
            this.messages.clear();
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.FeedRemoteViewsFactory$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                FeedRemoteViewsFactory.this.m204xab71b43d();
            }
        });
        try {
            this.countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* renamed from: lambda$onDataSetChanged$0$org-telegram-messenger-FeedRemoteViewsFactory */
    public /* synthetic */ void m204xab71b43d() {
        this.accountInstance.getNotificationCenter().addObserver(this, NotificationCenter.messagesDidLoad);
        if (this.classGuid == 0) {
            this.classGuid = ConnectionsManager.generateClassGuid();
        }
        this.accountInstance.getMessagesController().loadMessages(this.dialogId, 0L, false, 20, 0, 0, true, 0, this.classGuid, 0, 0, 0, 0, 0, 1);
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.messagesDidLoad) {
            int guid = ((Integer) args[10]).intValue();
            if (guid == this.classGuid) {
                this.messages.clear();
                ArrayList<MessageObject> messArr = (ArrayList) args[2];
                this.messages.addAll(messArr);
                this.countDownLatch.countDown();
            }
        }
    }
}
