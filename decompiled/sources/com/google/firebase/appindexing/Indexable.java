package com.google.firebase.appindexing;

import android.net.Uri;
import android.os.Bundle;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.internal.icing.zzfw;
import com.google.firebase.appindexing.builders.IndexableBuilder;
import com.google.firebase.appindexing.internal.zzac;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
public interface Indexable {
    public static final int MAX_BYTE_SIZE = 30000;
    public static final int MAX_INDEXABLES_TO_BE_UPDATED_IN_ONE_CALL = 1000;
    public static final int MAX_NESTING_DEPTH = 5;
    public static final int MAX_NUMBER_OF_FIELDS = 20;
    public static final int MAX_REPEATED_SIZE = 100;
    public static final int MAX_STRING_LENGTH = 20000;
    public static final int MAX_URL_LENGTH = 256;

    /* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
    /* loaded from: classes3.dex */
    public static class Builder extends IndexableBuilder<Builder> {
        public Builder() {
            this("Thing");
        }

        public Builder(String type) {
            super(type);
        }
    }

    /* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
    /* loaded from: classes3.dex */
    public interface Metadata {

        /* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
        /* loaded from: classes3.dex */
        public static final class Builder {
            private boolean zza = zzfw.zzd().zza();
            private int zzb = zzfw.zzd().zzb();
            private String zzc = zzfw.zzd().zzc();
            private final Bundle zzd = new Bundle();

            public Builder setScore(int score) {
                boolean z = score >= 0;
                StringBuilder sb = new StringBuilder(53);
                sb.append("Negative score values are invalid. Value: ");
                sb.append(score);
                Preconditions.checkArgument(z, sb.toString());
                this.zzb = score;
                return this;
            }

            public Builder setSliceUri(Uri sliceUri) {
                Preconditions.checkNotNull(sliceUri);
                IndexableBuilder.zza(this.zzd, "sliceUri", sliceUri.toString());
                return this;
            }

            public Builder setWorksOffline(boolean z) {
                this.zza = z;
                return this;
            }

            public final zzac zza() {
                return new zzac(this.zza, this.zzb, this.zzc, this.zzd, null);
            }

            public Builder setScope(int scope) {
                boolean z = scope > 0 && scope <= 3;
                StringBuilder sb = new StringBuilder(69);
                sb.append("The scope of this indexable is not valid, scope value is ");
                sb.append(scope);
                sb.append(".");
                Preconditions.checkArgument(z, sb.toString());
                IndexableBuilder.zzd(this.zzd, "scope", scope);
                return this;
            }
        }
    }
}
