package org.telegram.messenger;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.RemoteViews;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.ui.LaunchActivity;
/* loaded from: classes4.dex */
public class FeedWidgetProvider extends AppWidgetProvider {
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
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        for (int a = 0; a < appWidgetIds.length; a++) {
            SharedPreferences preferences = context.getSharedPreferences("shortcut_widget", 0);
            SharedPreferences.Editor edit = preferences.edit();
            SharedPreferences.Editor remove = edit.remove("account" + appWidgetIds[a]);
            remove.remove("dialogId" + appWidgetIds[a]).commit();
        }
    }

    public static void updateWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        Intent intent2 = new Intent(context, FeedWidgetService.class);
        intent2.putExtra("appWidgetId", appWidgetId);
        intent2.setData(Uri.parse(intent2.toUri(1)));
        RemoteViews rv = new RemoteViews(context.getPackageName(), (int) org.telegram.messenger.beta.R.layout.feed_widget_layout);
        rv.setRemoteAdapter(appWidgetId, org.telegram.messenger.beta.R.id.list_view, intent2);
        rv.setEmptyView(org.telegram.messenger.beta.R.id.list_view, org.telegram.messenger.beta.R.id.empty_view);
        Intent intent = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
        intent.setAction("com.tmessages.openchat" + Math.random() + Integer.MAX_VALUE);
        intent.addFlags(ConnectionsManager.FileTypeFile);
        intent.addCategory("android.intent.category.LAUNCHER");
        PendingIntent contentIntent = PendingIntent.getActivity(ApplicationLoader.applicationContext, 0, intent, 134217728);
        rv.setPendingIntentTemplate(org.telegram.messenger.beta.R.id.list_view, contentIntent);
        appWidgetManager.updateAppWidget(appWidgetId, rv);
    }
}
