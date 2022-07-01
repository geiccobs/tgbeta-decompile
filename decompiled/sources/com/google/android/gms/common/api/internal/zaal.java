package com.google.android.gms.common.api.internal;

import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.IAccountAccessor;
import java.util.ArrayList;
import java.util.Set;
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
/* loaded from: classes.dex */
public final class zaal extends zaap {
    private final ArrayList<Api.Client> zaa;
    private final /* synthetic */ zaaf zab;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public zaal(zaaf zaafVar, ArrayList<Api.Client> arrayList) {
        super(zaafVar, null);
        this.zab = zaafVar;
        this.zaa = arrayList;
    }

    @Override // com.google.android.gms.common.api.internal.zaap
    public final void zaa() {
        zaaz zaazVar;
        Set<Scope> zai;
        IAccountAccessor iAccountAccessor;
        zaaz zaazVar2;
        zaazVar = this.zab.zaa;
        zaar zaarVar = zaazVar.zad;
        zai = this.zab.zai();
        zaarVar.zac = zai;
        ArrayList<Api.Client> arrayList = this.zaa;
        int size = arrayList.size();
        int i = 0;
        while (i < size) {
            Api.Client client = arrayList.get(i);
            i++;
            iAccountAccessor = this.zab.zao;
            zaazVar2 = this.zab.zaa;
            client.getRemoteService(iAccountAccessor, zaazVar2.zad.zac);
        }
    }
}
