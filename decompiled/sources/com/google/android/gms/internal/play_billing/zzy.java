package com.google.android.gms.internal.play_billing;

import java.util.Set;
import javax.annotation.CheckForNull;
/* compiled from: com.android.billingclient:billing@@5.0.0 */
/* loaded from: classes.dex */
public abstract class zzy extends zzr implements Set {
    @CheckForNull
    private transient zzu zza;

    @Override // java.util.Collection, java.util.Set
    public final boolean equals(@CheckForNull Object obj) {
        if (obj == this || obj == this) {
            return true;
        }
        if (obj instanceof Set) {
            Set set = (Set) obj;
            try {
                if (size() == set.size()) {
                    if (containsAll(set)) {
                        return true;
                    }
                }
            } catch (ClassCastException | NullPointerException unused) {
            }
        }
        return false;
    }

    @Override // java.util.Collection, java.util.Set
    public final int hashCode() {
        return zzag.zza(this);
    }

    @Override // com.google.android.gms.internal.play_billing.zzr
    public zzu zzd() {
        zzu zzuVar = this.zza;
        if (zzuVar == null) {
            zzu zzh = zzh();
            this.zza = zzh;
            return zzh;
        }
        return zzuVar;
    }

    zzu zzh() {
        return zzu.zzi(toArray());
    }
}
