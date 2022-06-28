package com.google.firebase.appindexing;

import android.os.Bundle;
import com.google.android.gms.common.internal.ImagesContract;
import com.google.android.gms.common.internal.Preconditions;
import com.google.firebase.appindexing.builders.IndexableBuilder;
import com.google.firebase.appindexing.internal.zzc;
import com.google.firebase.appindexing.internal.zzw;
import com.microsoft.appcenter.ingestion.models.CommonProperties;
import java.util.Arrays;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
public interface Action {

    /* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
    /* loaded from: classes3.dex */
    public interface Metadata {

        /* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
        /* loaded from: classes3.dex */
        public static class Builder {
            private boolean zza = true;

            public Builder setUpload(boolean z) {
                this.zza = z;
                return this;
            }

            public final com.google.firebase.appindexing.internal.zzb zza() {
                return new com.google.firebase.appindexing.internal.zzb(this.zza, null, null, null, false);
            }
        }
    }

    /* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
    /* loaded from: classes3.dex */
    public static class Builder {
        public static final String ACTIVATE_ACTION = "ActivateAction";
        public static final String ADD_ACTION = "AddAction";
        public static final String BOOKMARK_ACTION = "BookmarkAction";
        public static final String COMMENT_ACTION = "CommentAction";
        public static final String LIKE_ACTION = "LikeAction";
        public static final String LISTEN_ACTION = "ListenAction";
        public static final String SEND_ACTION = "SendAction";
        public static final String SHARE_ACTION = "ShareAction";
        public static final String STATUS_TYPE_ACTIVE = "http://schema.org/ActiveActionStatus";
        public static final String STATUS_TYPE_COMPLETED = "http://schema.org/CompletedActionStatus";
        public static final String STATUS_TYPE_FAILED = "http://schema.org/FailedActionStatus";
        public static final String VIEW_ACTION = "ViewAction";
        public static final String WATCH_ACTION = "WatchAction";
        private final Bundle zza = new Bundle();
        private final String zzb;
        private String zzc;
        private String zzd;
        private String zze;
        private com.google.firebase.appindexing.internal.zzb zzf;
        private String zzg;

        public Builder(String type) {
            this.zzb = type;
        }

        public Action build() {
            com.google.firebase.appindexing.internal.zzb zzbVar;
            Preconditions.checkNotNull(this.zzc, "setObject is required before calling build().");
            Preconditions.checkNotNull(this.zzd, "setObject is required before calling build().");
            String str = this.zzb;
            String str2 = this.zzc;
            String str3 = this.zzd;
            String str4 = this.zze;
            com.google.firebase.appindexing.internal.zzb zzbVar2 = this.zzf;
            if (zzbVar2 != null) {
                zzbVar = zzbVar2;
            } else {
                zzbVar = new Metadata.Builder().zza();
            }
            return new zzc(str, str2, str3, str4, zzbVar, this.zzg, this.zza);
        }

        public Builder put(String key, double... values) {
            Bundle bundle = this.zza;
            Preconditions.checkNotNull(key);
            Preconditions.checkNotNull(values);
            int length = values.length;
            if (length > 0) {
                if (length >= 100) {
                    zzw.zza("Input Array of elements is too big, cutting off.");
                    values = Arrays.copyOf(values, 100);
                }
                bundle.putDoubleArray(key, values);
            } else {
                zzw.zza("Double array is empty and is ignored by put method.");
            }
            return this;
        }

        public Builder setActionStatus(String actionStatus) {
            Preconditions.checkNotNull(actionStatus);
            this.zzg = actionStatus;
            return this;
        }

        public Builder setMetadata(Metadata.Builder metadataBuilder) {
            Preconditions.checkNotNull(metadataBuilder);
            this.zzf = metadataBuilder.zza();
            return this;
        }

        public final Builder setName(String name) {
            Preconditions.checkNotNull(name);
            this.zzc = name;
            return put(CommonProperties.NAME, name);
        }

        public Builder setObject(String name, String url) {
            Preconditions.checkNotNull(name);
            Preconditions.checkNotNull(url);
            this.zzc = name;
            this.zzd = url;
            return this;
        }

        public Builder setResult(Indexable... values) throws FirebaseAppIndexingInvalidArgumentException {
            return put("result", values);
        }

        public final Builder setUrl(String url) {
            Preconditions.checkNotNull(url);
            this.zzd = url;
            return put(ImagesContract.URL, url);
        }

        public final String zza() {
            String str = this.zzc;
            if (str == null) {
                return null;
            }
            return new String(str);
        }

        public final String zzb() {
            String str = this.zzd;
            if (str == null) {
                return null;
            }
            return new String(str);
        }

        public final String zzc() {
            return new String(this.zzg);
        }

        public Builder setObject(String name, String url, String webUrl) {
            Preconditions.checkNotNull(name);
            Preconditions.checkNotNull(url);
            Preconditions.checkNotNull(webUrl);
            this.zzc = name;
            this.zzd = url;
            this.zze = webUrl;
            return this;
        }

        public Builder put(String key, long... values) {
            IndexableBuilder.zzd(this.zza, key, values);
            return this;
        }

        public Builder put(String key, Indexable... values) throws FirebaseAppIndexingInvalidArgumentException {
            IndexableBuilder.zzb(this.zza, key, values);
            return this;
        }

        public Builder put(String key, String... values) {
            IndexableBuilder.zza(this.zza, key, values);
            return this;
        }

        public Builder put(String key, boolean... values) {
            IndexableBuilder.zzc(this.zza, key, values);
            return this;
        }
    }
}
