package com.google.android.gms.wearable;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
@Deprecated
/* loaded from: classes3.dex */
public interface ChannelApi {
    public static final String ACTION_CHANNEL_EVENT = "com.google.android.gms.wearable.CHANNEL_EVENT";

    /* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
    @Deprecated
    /* loaded from: classes3.dex */
    public interface ChannelListener {
        public static final int CLOSE_REASON_CONNECTION_TIMEOUT = 4;
        public static final int CLOSE_REASON_DISCONNECTED = 1;
        public static final int CLOSE_REASON_LOCAL_CLOSE = 3;
        public static final int CLOSE_REASON_NORMAL = 0;
        public static final int CLOSE_REASON_REMOTE_CLOSE = 2;

        void onChannelClosed(Channel channel, int i, int i2);

        void onChannelOpened(Channel channel);

        void onInputClosed(Channel channel, int i, int i2);

        void onOutputClosed(Channel channel, int i, int i2);
    }

    /* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface CloseReason {
    }

    /* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
    @Deprecated
    /* loaded from: classes3.dex */
    public interface OpenChannelResult extends Result {
        Channel getChannel();
    }

    PendingResult<Status> addListener(GoogleApiClient googleApiClient, ChannelListener channelListener);

    PendingResult<OpenChannelResult> openChannel(GoogleApiClient googleApiClient, String str, String str2);

    PendingResult<Status> removeListener(GoogleApiClient googleApiClient, ChannelListener channelListener);
}
