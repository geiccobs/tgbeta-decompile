package com.google.android.gms.common.internal;

import androidx.annotation.RecentlyNonNull;
import com.huawei.hms.framework.common.ContainerUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
/* loaded from: classes.dex */
public final class Objects {
    public static boolean equal(Object obj, Object obj2) {
        if (obj != obj2) {
            return obj != null && obj.equals(obj2);
        }
        return true;
    }

    public static int hashCode(@RecentlyNonNull Object... objArr) {
        return Arrays.hashCode(objArr);
    }

    @RecentlyNonNull
    public static ToStringHelper toStringHelper(@RecentlyNonNull Object obj) {
        return new ToStringHelper(obj);
    }

    /* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
    /* loaded from: classes.dex */
    public static final class ToStringHelper {
        private final List<String> zza;
        private final Object zzb;

        private ToStringHelper(Object obj) {
            this.zzb = Preconditions.checkNotNull(obj);
            this.zza = new ArrayList();
        }

        @RecentlyNonNull
        public final ToStringHelper add(@RecentlyNonNull String str, Object obj) {
            List<String> list = this.zza;
            String str2 = (String) Preconditions.checkNotNull(str);
            String valueOf = String.valueOf(obj);
            StringBuilder sb = new StringBuilder(String.valueOf(str2).length() + 1 + valueOf.length());
            sb.append(str2);
            sb.append(ContainerUtils.KEY_VALUE_DELIMITER);
            sb.append(valueOf);
            list.add(sb.toString());
            return this;
        }

        @RecentlyNonNull
        public final String toString() {
            StringBuilder sb = new StringBuilder(100);
            sb.append(this.zzb.getClass().getSimpleName());
            sb.append('{');
            int size = this.zza.size();
            for (int i = 0; i < size; i++) {
                sb.append(this.zza.get(i));
                if (i < size - 1) {
                    sb.append(", ");
                }
            }
            sb.append('}');
            return sb.toString();
        }
    }
}
