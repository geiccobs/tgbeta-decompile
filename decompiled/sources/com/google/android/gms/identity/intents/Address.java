package com.google.android.gms.identity.intents;

import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.internal.BaseImplementation;
import com.google.android.gms.internal.identity.zze;
/* loaded from: classes3.dex */
public final class Address {
    public static final Api<AddressOptions> API;
    private static final Api.AbstractClientBuilder<zze, AddressOptions> CLIENT_BUILDER;
    private static final Api.ClientKey<zze> CLIENT_KEY;

    /* loaded from: classes3.dex */
    public static final class AddressOptions implements Api.ApiOptions.HasOptions {
        public final int theme;

        public AddressOptions() {
            this.theme = 0;
        }

        public AddressOptions(int i) {
            this.theme = i;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class zza extends BaseImplementation.ApiMethodImpl<Status, zze> {
        public zza(GoogleApiClient googleApiClient) {
            super(Address.API, googleApiClient);
        }

        @Override // com.google.android.gms.common.api.internal.BasePendingResult
        public /* synthetic */ Result createFailedResult(Status status) {
            return status;
        }
    }

    static {
        Api.ClientKey<zze> clientKey = new Api.ClientKey<>();
        CLIENT_KEY = clientKey;
        com.google.android.gms.identity.intents.zza zzaVar = new com.google.android.gms.identity.intents.zza();
        CLIENT_BUILDER = zzaVar;
        API = new Api<>("Address.API", zzaVar, clientKey);
    }

    public static void requestUserAddress(GoogleApiClient googleApiClient, UserAddressRequest userAddressRequest, int i) {
        googleApiClient.enqueue(new zzb(googleApiClient, userAddressRequest, i));
    }
}
