package com.google.android.gms.wearable;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Wearable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
public abstract class MessageClient extends GoogleApi<Wearable.WearableOptions> {
    public static final String ACTION_MESSAGE_RECEIVED = "com.google.android.gms.wearable.MESSAGE_RECEIVED";
    public static final int FILTER_LITERAL = 0;
    public static final int FILTER_PREFIX = 1;

    /* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface FilterType {
    }

    /* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
    /* loaded from: classes3.dex */
    public interface OnMessageReceivedListener extends MessageApi.MessageListener {
        @Override // com.google.android.gms.wearable.MessageApi.MessageListener
        void onMessageReceived(MessageEvent messageEvent);
    }

    public MessageClient(Activity activity, GoogleApi.Settings settings) {
        super(activity, Wearable.API, Wearable.WearableOptions.zza, settings);
    }

    public abstract Task<Void> addListener(OnMessageReceivedListener onMessageReceivedListener);

    public abstract Task<Void> addListener(OnMessageReceivedListener onMessageReceivedListener, Uri uri, int i);

    public abstract Task<Boolean> removeListener(OnMessageReceivedListener onMessageReceivedListener);

    public abstract Task<Integer> sendMessage(String str, String str2, byte[] bArr);

    public abstract Task<Integer> sendMessage(String str, String str2, byte[] bArr, MessageOptions messageOptions);

    public MessageClient(Context context, GoogleApi.Settings settings) {
        super(context, Wearable.API, Wearable.WearableOptions.zza, settings);
    }
}
