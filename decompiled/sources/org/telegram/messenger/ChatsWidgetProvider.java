package org.telegram.messenger;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RemoteViews;
import com.microsoft.appcenter.ingestion.models.CommonProperties;
import java.util.ArrayList;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.ui.LaunchActivity;
/* loaded from: classes4.dex */
public class ChatsWidgetProvider extends AppWidgetProvider {
    @Override // android.appwidget.AppWidgetProvider, android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

    @Override // android.appwidget.AppWidgetProvider
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        for (int appWidgetId : appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override // android.appwidget.AppWidgetProvider
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        updateWidget(context, appWidgetManager, appWidgetId);
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    @Override // android.appwidget.AppWidgetProvider
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        ApplicationLoader.postInitApplication();
        SharedPreferences preferences = context.getSharedPreferences("shortcut_widget", 0);
        SharedPreferences.Editor editor = preferences.edit();
        for (int a = 0; a < appWidgetIds.length; a++) {
            int accountId = preferences.getInt("account" + appWidgetIds[a], -1);
            if (accountId >= 0) {
                AccountInstance accountInstance = AccountInstance.getInstance(accountId);
                accountInstance.getMessagesStorage().clearWidgetDialogs(appWidgetIds[a]);
            }
            editor.remove("account" + appWidgetIds[a]);
            editor.remove(CommonProperties.TYPE + appWidgetIds[a]);
            editor.remove("deleted" + appWidgetIds[a]);
        }
        editor.commit();
    }

    private static int getCellsForSize(int size) {
        int n = 2;
        while (n * 72 < size) {
            n++;
        }
        return n - 1;
    }

    public static void updateWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        int id;
        ApplicationLoader.postInitApplication();
        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
        int minHeight = options.getInt("appWidgetMaxHeight");
        int rows = getCellsForSize(minHeight);
        Intent intent2 = new Intent(context, ChatsWidgetService.class);
        intent2.putExtra("appWidgetId", appWidgetId);
        intent2.setData(Uri.parse(intent2.toUri(1)));
        SharedPreferences preferences = context.getSharedPreferences("shortcut_widget", 0);
        boolean deleted = preferences.getBoolean("deleted" + appWidgetId, false);
        if (!deleted) {
            int accountId = preferences.getInt("account" + appWidgetId, -1);
            if (accountId == -1) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("account" + appWidgetId, UserConfig.selectedAccount);
                editor.putInt(CommonProperties.TYPE + appWidgetId, 0).commit();
            }
            ArrayList<Long> selectedDialogs = new ArrayList<>();
            if (accountId >= 0) {
                AccountInstance.getInstance(accountId).getMessagesStorage().getWidgetDialogIds(appWidgetId, 0, selectedDialogs, null, null, false);
            }
            if (rows == 1 || selectedDialogs.size() <= 1) {
                id = org.telegram.messenger.beta.R.layout.shortcut_widget_layout_1;
            } else if (rows == 2 || selectedDialogs.size() <= 2) {
                id = org.telegram.messenger.beta.R.layout.shortcut_widget_layout_2;
            } else if (rows == 3 || selectedDialogs.size() <= 3) {
                id = org.telegram.messenger.beta.R.layout.shortcut_widget_layout_3;
            } else {
                id = org.telegram.messenger.beta.R.layout.shortcut_widget_layout_4;
            }
        } else {
            id = org.telegram.messenger.beta.R.layout.shortcut_widget_layout_1;
        }
        RemoteViews rv = new RemoteViews(context.getPackageName(), id);
        rv.setRemoteAdapter(appWidgetId, org.telegram.messenger.beta.R.id.list_view, intent2);
        rv.setEmptyView(org.telegram.messenger.beta.R.id.list_view, org.telegram.messenger.beta.R.id.empty_view);
        Intent intent = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
        intent.setAction("com.tmessages.openchat" + Math.random() + Integer.MAX_VALUE);
        intent.addFlags(ConnectionsManager.FileTypeFile);
        intent.addCategory("android.intent.category.LAUNCHER");
        PendingIntent contentIntent = PendingIntent.getActivity(ApplicationLoader.applicationContext, 0, intent, 134217728);
        rv.setPendingIntentTemplate(org.telegram.messenger.beta.R.id.list_view, contentIntent);
        appWidgetManager.updateAppWidget(appWidgetId, rv);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, org.telegram.messenger.beta.R.id.list_view);
    }
}
