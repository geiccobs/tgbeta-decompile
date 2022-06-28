package com.google.android.gms.appindexing;

import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import com.google.android.gms.common.internal.ImagesContract;
import com.google.android.gms.common.internal.Preconditions;
import com.microsoft.appcenter.ingestion.models.CommonProperties;
import java.util.ArrayList;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
@Deprecated
/* loaded from: classes3.dex */
public class Thing {
    final Bundle zza;

    public Thing(Bundle bundle) {
        this.zza = bundle;
    }

    public final Bundle zza() {
        return this.zza;
    }

    /* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
    @Deprecated
    /* loaded from: classes3.dex */
    public static class Builder {
        final Bundle zza = new Bundle();

        public Thing build() {
            return new Thing(this.zza);
        }

        public Builder put(String key, Thing value) {
            Preconditions.checkNotNull(key);
            if (value != null) {
                this.zza.putParcelable(key, value.zza);
            }
            return this;
        }

        public Builder setDescription(String description) {
            put("description", description);
            return this;
        }

        public Builder setId(String id) {
            if (id != null) {
                put("id", id);
            }
            return this;
        }

        public Builder setName(String name) {
            Preconditions.checkNotNull(name);
            put(CommonProperties.NAME, name);
            return this;
        }

        public Builder setType(String type) {
            put(CommonProperties.TYPE, type);
            return this;
        }

        public Builder setUrl(Uri url) {
            Preconditions.checkNotNull(url);
            put(ImagesContract.URL, url.toString());
            return this;
        }

        public Builder put(String key, String value) {
            Preconditions.checkNotNull(key);
            if (value != null) {
                this.zza.putString(key, value);
            }
            return this;
        }

        public Builder put(String key, boolean value) {
            Preconditions.checkNotNull(key);
            this.zza.putBoolean(key, value);
            return this;
        }

        public Builder put(String key, Thing[] values) {
            Preconditions.checkNotNull(key);
            if (values != null) {
                ArrayList arrayList = new ArrayList();
                for (Thing thing : values) {
                    if (thing != null) {
                        arrayList.add(thing.zza);
                    }
                }
                this.zza.putParcelableArray(key, (Parcelable[]) arrayList.toArray(new Bundle[arrayList.size()]));
            }
            return this;
        }

        public Builder put(String key, String[] values) {
            Preconditions.checkNotNull(key);
            if (values != null) {
                this.zza.putStringArray(key, values);
            }
            return this;
        }
    }
}
