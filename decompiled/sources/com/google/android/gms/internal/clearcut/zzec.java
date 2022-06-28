package com.google.android.gms.internal.clearcut;

import com.google.android.gms.internal.clearcut.zzcg;
/* loaded from: classes3.dex */
public final class zzec implements zzdm {
    private final String info;
    private final zzdo zzmn;
    private final zzed zzng;

    public zzec(zzdo zzdoVar, String str, Object[] objArr) {
        this.zzmn = zzdoVar;
        this.info = str;
        this.zzng = new zzed(zzdoVar.getClass(), str, objArr);
    }

    public final int getFieldCount() {
        int i;
        i = this.zzng.zznj;
        return i;
    }

    @Override // com.google.android.gms.internal.clearcut.zzdm
    public final int zzcf() {
        int i;
        i = this.zzng.flags;
        return (i & 1) == 1 ? zzcg.zzg.zzkl : zzcg.zzg.zzkm;
    }

    @Override // com.google.android.gms.internal.clearcut.zzdm
    public final boolean zzcg() {
        int i;
        i = this.zzng.flags;
        return (i & 2) == 2;
    }

    @Override // com.google.android.gms.internal.clearcut.zzdm
    public final zzdo zzch() {
        return this.zzmn;
    }

    public final zzed zzco() {
        return this.zzng;
    }

    public final int zzcp() {
        int i;
        i = this.zzng.zzmk;
        return i;
    }

    public final int zzcq() {
        int i;
        i = this.zzng.zzml;
        return i;
    }

    public final int zzcr() {
        int i;
        i = this.zzng.zznm;
        return i;
    }

    public final int zzcs() {
        int i;
        i = this.zzng.zzno;
        return i;
    }

    public final int[] zzct() {
        int[] iArr;
        iArr = this.zzng.zzms;
        return iArr;
    }

    public final int zzcu() {
        int i;
        i = this.zzng.zznn;
        return i;
    }

    public final int zzcv() {
        int i;
        i = this.zzng.zzmm;
        return i;
    }
}
