package com.google.android.gms.common.api.internal;

import androidx.annotation.RecentlyNonNull;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.ApiOptions;
import com.google.android.gms.common.internal.Objects;
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
/* loaded from: classes.dex */
public final class ApiKey<O extends Api.ApiOptions> {
    private final int zab;
    private final Api<O> zac;
    private final O zad;
    private final String zae;

    private ApiKey(Api<O> api, O o, String str) {
        this.zac = api;
        this.zad = o;
        this.zae = str;
        this.zab = Objects.hashCode(api, o, str);
    }

    @RecentlyNonNull
    public static <O extends Api.ApiOptions> ApiKey<O> zaa(@RecentlyNonNull Api<O> api, O o, String str) {
        return new ApiKey<>(api, o, str);
    }

    @RecentlyNonNull
    public final String zaa() {
        return this.zac.zad();
    }

    public final int hashCode() {
        return this.zab;
    }

    public final boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ApiKey)) {
            return false;
        }
        ApiKey apiKey = (ApiKey) obj;
        return Objects.equal(this.zac, apiKey.zac) && Objects.equal(this.zad, apiKey.zad) && Objects.equal(this.zae, apiKey.zae);
    }
}
