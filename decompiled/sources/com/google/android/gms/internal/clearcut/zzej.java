package com.google.android.gms.internal.clearcut;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
/* loaded from: classes.dex */
public final class zzej extends zzei<FieldDescriptorType, Object> {
    public zzej(int i) {
        super(i, null);
    }

    @Override // com.google.android.gms.internal.clearcut.zzei
    public final void zzv() {
        if (!isImmutable()) {
            for (int i = 0; i < zzdr(); i++) {
                Map.Entry<FieldDescriptorType, Object> zzak = zzak(i);
                if (((zzca) zzak.getKey()).zzaw()) {
                    zzak.setValue(Collections.unmodifiableList((List) zzak.getValue()));
                }
            }
            Iterator it = zzds().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                if (((zzca) entry.getKey()).zzaw()) {
                    entry.setValue(Collections.unmodifiableList((List) entry.getValue()));
                }
            }
        }
        super.zzv();
    }
}
