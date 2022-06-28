package com.google.android.exoplayer2.source.dash.manifest;

import com.google.android.exoplayer2.util.Util;
/* loaded from: classes3.dex */
public final class Descriptor {
    public final String id;
    public final String schemeIdUri;
    public final String value;

    public Descriptor(String schemeIdUri, String value, String id) {
        this.schemeIdUri = schemeIdUri;
        this.value = value;
        this.id = id;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Descriptor other = (Descriptor) obj;
        return Util.areEqual(this.schemeIdUri, other.schemeIdUri) && Util.areEqual(this.value, other.value) && Util.areEqual(this.id, other.id);
    }

    public int hashCode() {
        int result = this.schemeIdUri.hashCode();
        int i = result * 31;
        String str = this.value;
        int i2 = 0;
        int result2 = i + (str != null ? str.hashCode() : 0);
        int result3 = result2 * 31;
        String str2 = this.id;
        if (str2 != null) {
            i2 = str2.hashCode();
        }
        return result3 + i2;
    }
}
