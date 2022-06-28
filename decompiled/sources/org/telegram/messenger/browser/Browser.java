package org.telegram.messenger.browser;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import java.lang.ref.WeakReference;
import java.util.List;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.support.customtabs.CustomTabsCallback;
import org.telegram.messenger.support.customtabs.CustomTabsClient;
import org.telegram.messenger.support.customtabs.CustomTabsServiceConnection;
import org.telegram.messenger.support.customtabs.CustomTabsSession;
import org.telegram.messenger.support.customtabsclient.shared.CustomTabsHelper;
import org.telegram.messenger.support.customtabsclient.shared.ServiceConnection;
import org.telegram.messenger.support.customtabsclient.shared.ServiceConnectionCallback;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
/* loaded from: classes4.dex */
public class Browser {
    private static WeakReference<Activity> currentCustomTabsActivity;
    private static CustomTabsClient customTabsClient;
    private static WeakReference<CustomTabsSession> customTabsCurrentSession;
    private static String customTabsPackageToBind;
    private static CustomTabsServiceConnection customTabsServiceConnection;
    private static CustomTabsSession customTabsSession;

    private static CustomTabsSession getCurrentSession() {
        WeakReference<CustomTabsSession> weakReference = customTabsCurrentSession;
        if (weakReference == null) {
            return null;
        }
        return weakReference.get();
    }

    private static void setCurrentSession(CustomTabsSession session) {
        customTabsCurrentSession = new WeakReference<>(session);
    }

    private static CustomTabsSession getSession() {
        CustomTabsClient customTabsClient2 = customTabsClient;
        if (customTabsClient2 == null) {
            customTabsSession = null;
        } else if (customTabsSession == null) {
            CustomTabsSession newSession = customTabsClient2.newSession(new NavigationCallback());
            customTabsSession = newSession;
            setCurrentSession(newSession);
        }
        return customTabsSession;
    }

    public static void bindCustomTabsService(Activity activity) {
        WeakReference<Activity> weakReference = currentCustomTabsActivity;
        Activity currentActivity = weakReference == null ? null : weakReference.get();
        if (currentActivity != null && currentActivity != activity) {
            unbindCustomTabsService(currentActivity);
        }
        if (customTabsClient != null) {
            return;
        }
        currentCustomTabsActivity = new WeakReference<>(activity);
        try {
            if (TextUtils.isEmpty(customTabsPackageToBind)) {
                String packageNameToUse = CustomTabsHelper.getPackageNameToUse(activity);
                customTabsPackageToBind = packageNameToUse;
                if (packageNameToUse == null) {
                    return;
                }
            }
            ServiceConnection serviceConnection = new ServiceConnection(new ServiceConnectionCallback() { // from class: org.telegram.messenger.browser.Browser.1
                @Override // org.telegram.messenger.support.customtabsclient.shared.ServiceConnectionCallback
                public void onServiceConnected(CustomTabsClient client) {
                    CustomTabsClient unused = Browser.customTabsClient = client;
                    if (SharedConfig.customTabs && Browser.customTabsClient != null) {
                        try {
                            Browser.customTabsClient.warmup(0L);
                        } catch (Exception e) {
                            FileLog.e(e);
                        }
                    }
                }

                @Override // org.telegram.messenger.support.customtabsclient.shared.ServiceConnectionCallback
                public void onServiceDisconnected() {
                    CustomTabsClient unused = Browser.customTabsClient = null;
                }
            });
            customTabsServiceConnection = serviceConnection;
            if (!CustomTabsClient.bindCustomTabsService(activity, customTabsPackageToBind, serviceConnection)) {
                customTabsServiceConnection = null;
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static void unbindCustomTabsService(Activity activity) {
        if (customTabsServiceConnection == null) {
            return;
        }
        WeakReference<Activity> weakReference = currentCustomTabsActivity;
        Activity currentActivity = weakReference == null ? null : weakReference.get();
        if (currentActivity == activity) {
            currentCustomTabsActivity.clear();
        }
        try {
            activity.unbindService(customTabsServiceConnection);
        } catch (Exception e) {
        }
        customTabsClient = null;
        customTabsSession = null;
    }

    /* loaded from: classes4.dex */
    public static class NavigationCallback extends CustomTabsCallback {
        private NavigationCallback() {
        }

        @Override // org.telegram.messenger.support.customtabs.CustomTabsCallback
        public void onNavigationEvent(int navigationEvent, Bundle extras) {
        }
    }

    public static void openUrl(Context context, String url) {
        if (url == null) {
            return;
        }
        openUrl(context, Uri.parse(url), true);
    }

    public static void openUrl(Context context, Uri uri) {
        openUrl(context, uri, true);
    }

    public static void openUrl(Context context, String url, boolean allowCustom) {
        if (context == null || url == null) {
            return;
        }
        openUrl(context, Uri.parse(url), allowCustom);
    }

    public static void openUrl(Context context, Uri uri, boolean allowCustom) {
        openUrl(context, uri, allowCustom, true);
    }

    public static void openUrl(Context context, String url, boolean allowCustom, boolean tryTelegraph) {
        openUrl(context, Uri.parse(url), allowCustom, tryTelegraph);
    }

    public static boolean isTelegraphUrl(String url, boolean equals) {
        return isTelegraphUrl(url, equals, false);
    }

    public static boolean isTelegraphUrl(String url, boolean equals, boolean forceHttps) {
        if (equals) {
            return url.equals("telegra.ph") || url.equals("te.legra.ph") || url.equals("graph.org");
        }
        StringBuilder sb = new StringBuilder();
        sb.append("^(https");
        sb.append(forceHttps ? "" : "?");
        sb.append("://)?(te\\.?legra\\.ph|graph\\.org).*");
        return url.matches(sb.toString());
    }

    public static boolean urlMustNotHaveConfirmation(String url) {
        return isTelegraphUrl(url, false, true) || url.matches("^(https://)?t\\.me/iv\\??.*") || url.matches("^(https://)?telegram\\.org/(blog|tour)/?.*");
    }

    /* JADX WARN: Removed duplicated region for block: B:118:0x0332 A[Catch: Exception -> 0x0357, TryCatch #3 {Exception -> 0x0357, blocks: (B:116:0x032b, B:118:0x0332, B:119:0x0344), top: B:130:0x032b }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static void openUrl(final android.content.Context r17, final android.net.Uri r18, final boolean r19, boolean r20) {
        /*
            Method dump skipped, instructions count: 861
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.browser.Browser.openUrl(android.content.Context, android.net.Uri, boolean, boolean):void");
    }

    public static /* synthetic */ void lambda$openUrl$0(AlertDialog[] progressDialog, TLObject response, int currentAccount, Uri finalUri, Context context, boolean allowCustom) {
        try {
            progressDialog[0].dismiss();
        } catch (Throwable th) {
        }
        progressDialog[0] = null;
        boolean ok = false;
        if (response instanceof TLRPC.TL_messageMediaWebPage) {
            TLRPC.TL_messageMediaWebPage webPage = (TLRPC.TL_messageMediaWebPage) response;
            if ((webPage.webpage instanceof TLRPC.TL_webPage) && webPage.webpage.cached_page != null) {
                NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.openArticle, webPage.webpage, finalUri.toString());
                ok = true;
            }
        }
        if (!ok) {
            openUrl(context, finalUri, allowCustom, false);
        }
    }

    public static /* synthetic */ void lambda$openUrl$3(AlertDialog[] progressDialog, final int reqId) {
        if (progressDialog[0] == null) {
            return;
        }
        try {
            progressDialog[0].setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: org.telegram.messenger.browser.Browser$$ExternalSyntheticLambda0
                @Override // android.content.DialogInterface.OnCancelListener
                public final void onCancel(DialogInterface dialogInterface) {
                    ConnectionsManager.getInstance(UserConfig.selectedAccount).cancelRequest(reqId, true);
                }
            });
            progressDialog[0].show();
        } catch (Exception e) {
        }
    }

    public static boolean isInternalUrl(String url, boolean[] forceBrowser) {
        return isInternalUri(Uri.parse(url), false, forceBrowser);
    }

    public static boolean isInternalUrl(String url, boolean all, boolean[] forceBrowser) {
        return isInternalUri(Uri.parse(url), all, forceBrowser);
    }

    public static boolean isPassportUrl(String url) {
        String url2;
        if (url == null) {
            return false;
        }
        try {
            url2 = url.toLowerCase();
        } catch (Throwable th) {
        }
        if (!url2.startsWith("tg:passport") && !url2.startsWith("tg://passport") && !url2.startsWith("tg:secureid")) {
            if (url2.contains("resolve")) {
                if (url2.contains("domain=telegrampassport")) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    public static boolean isInternalUri(Uri uri, boolean[] forceBrowser) {
        return isInternalUri(uri, false, forceBrowser);
    }

    public static boolean isInternalUri(Uri uri, boolean all, boolean[] forceBrowser) {
        String host = uri.getHost();
        String host2 = host != null ? host.toLowerCase() : "";
        if ("ton".equals(uri.getScheme())) {
            try {
                Intent viewIntent = new Intent("android.intent.action.VIEW", uri);
                List<ResolveInfo> allActivities = ApplicationLoader.applicationContext.getPackageManager().queryIntentActivities(viewIntent, 0);
                if (allActivities != null) {
                    if (allActivities.size() > 1) {
                        return false;
                    }
                }
            } catch (Exception e) {
            }
            return true;
        } else if ("tg".equals(uri.getScheme())) {
            return true;
        } else {
            if ("telegram.dog".equals(host2)) {
                String path = uri.getPath();
                if (path != null && path.length() > 1) {
                    if (all) {
                        return true;
                    }
                    String path2 = path.substring(1).toLowerCase();
                    if (!path2.startsWith("blog") && !path2.equals("iv") && !path2.startsWith("faq") && !path2.equals("apps") && !path2.startsWith("s/")) {
                        return true;
                    }
                    if (forceBrowser != null) {
                        forceBrowser[0] = true;
                    }
                    return false;
                }
            } else if ("telegram.me".equals(host2) || "t.me".equals(host2)) {
                String path3 = uri.getPath();
                if (path3 != null && path3.length() > 1) {
                    if (all) {
                        return true;
                    }
                    String path4 = path3.substring(1).toLowerCase();
                    if (!path4.equals("iv") && !path4.startsWith("s/")) {
                        return true;
                    }
                    if (forceBrowser != null) {
                        forceBrowser[0] = true;
                    }
                    return false;
                }
            } else if (all && (host2.endsWith("telegram.org") || host2.endsWith("telegra.ph") || host2.endsWith("telesco.pe"))) {
                return true;
            }
            return false;
        }
    }
}
