package com.google.android.gms.appindexing;

import android.net.Uri;
import android.os.Bundle;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.internal.ImagesContract;
import com.google.android.gms.common.internal.Preconditions;
import com.microsoft.appcenter.ingestion.models.CommonProperties;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
@Deprecated
/* loaded from: classes3.dex */
public final class Action extends Thing {
    public static final String STATUS_TYPE_ACTIVE = "http://schema.org/ActiveActionStatus";
    public static final String STATUS_TYPE_COMPLETED = "http://schema.org/CompletedActionStatus";
    public static final String STATUS_TYPE_FAILED = "http://schema.org/FailedActionStatus";
    public static final String TYPE_ACTIVATE = "http://schema.org/ActivateAction";
    public static final String TYPE_ADD = "http://schema.org/AddAction";
    public static final String TYPE_BOOKMARK = "http://schema.org/BookmarkAction";
    public static final String TYPE_COMMUNICATE = "http://schema.org/CommunicateAction";
    public static final String TYPE_FILM = "http://schema.org/FilmAction";
    public static final String TYPE_LIKE = "http://schema.org/LikeAction";
    public static final String TYPE_LISTEN = "http://schema.org/ListenAction";
    public static final String TYPE_PHOTOGRAPH = "http://schema.org/PhotographAction";
    public static final String TYPE_RESERVE = "http://schema.org/ReserveAction";
    public static final String TYPE_SEARCH = "http://schema.org/SearchAction";
    public static final String TYPE_VIEW = "http://schema.org/ViewAction";
    public static final String TYPE_WANT = "http://schema.org/WantAction";
    public static final String TYPE_WATCH = "http://schema.org/WatchAction";

    /* synthetic */ Action(Bundle bundle, zza zzaVar) {
        super(bundle);
    }

    public static Action newAction(String actionType, String objectName, Uri objectAppUri) {
        return newAction(actionType, objectName, null, objectAppUri);
    }

    /* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
    @Deprecated
    /* loaded from: classes3.dex */
    public static final class Builder extends Thing.Builder {
        public Builder(String actionType) {
            Preconditions.checkNotNull(actionType);
            super.put(CommonProperties.TYPE, actionType);
        }

        @Override // com.google.android.gms.appindexing.Thing.Builder
        public Action build() {
            Preconditions.checkNotNull(this.zza.get("object"), "setObject is required before calling build().");
            Preconditions.checkNotNull(this.zza.get(CommonProperties.TYPE), "setType is required before calling build().");
            Bundle bundle = (Bundle) this.zza.getParcelable("object");
            if (bundle != null) {
                Preconditions.checkNotNull(bundle.get(CommonProperties.NAME), "Must call setObject() with a valid name. Example: setObject(new Thing.Builder().setName(name).setUrl(url))");
                Preconditions.checkNotNull(bundle.get(ImagesContract.URL), "Must call setObject() with a valid app URI. Example: setObject(new Thing.Builder().setName(name).setUrl(url))");
            }
            return new Action(this.zza, null);
        }

        @Override // com.google.android.gms.appindexing.Thing.Builder
        public Builder put(String key, Thing value) {
            super.put(key, value);
            return this;
        }

        public Builder setActionStatus(String actionStatusType) {
            Preconditions.checkNotNull(actionStatusType);
            super.put("actionStatus", actionStatusType);
            return this;
        }

        @Override // com.google.android.gms.appindexing.Thing.Builder
        public Builder setName(String name) {
            super.put(CommonProperties.NAME, name);
            return this;
        }

        public Builder setObject(Thing thing) {
            Preconditions.checkNotNull(thing);
            super.put("object", thing);
            return this;
        }

        @Override // com.google.android.gms.appindexing.Thing.Builder
        public Builder setUrl(Uri url) {
            if (url != null) {
                super.put(ImagesContract.URL, url.toString());
            }
            return this;
        }

        @Override // com.google.android.gms.appindexing.Thing.Builder
        public Builder put(String key, String value) {
            super.put(key, value);
            return this;
        }

        @Override // com.google.android.gms.appindexing.Thing.Builder
        public Builder put(String key, boolean value) {
            super.put(key, value);
            return this;
        }

        @Override // com.google.android.gms.appindexing.Thing.Builder
        public Builder put(String key, Thing[] values) {
            super.put(key, values);
            return this;
        }

        @Override // com.google.android.gms.appindexing.Thing.Builder
        public Builder put(String key, String[] values) {
            super.put(key, values);
            return this;
        }
    }

    public static Action newAction(String actionType, String objectName, Uri objectId, Uri objectAppUri) {
        Builder builder = new Builder(actionType);
        Thing.Builder builder2 = new Thing.Builder();
        builder2.setName(objectName);
        builder2.setId(objectId == null ? null : objectId.toString());
        builder2.setUrl(objectAppUri);
        builder.setObject(builder2.build());
        return builder.build();
    }
}
