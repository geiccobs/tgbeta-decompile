package com.google.android.gms.common.internal;

import android.accounts.Account;
import android.view.View;
import androidx.annotation.RecentlyNonNull;
import androidx.annotation.RecentlyNullable;
import androidx.collection.ArraySet;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.signin.SignInOptions;
import com.huawei.hms.api.HuaweiApiClientImpl;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
/* loaded from: classes.dex */
public final class ClientSettings {
    @Nullable
    private final Account zaa;
    private final Set<Scope> zab;
    private final Set<Scope> zac;
    private final Map<Api<?>, zaa> zad;
    private final String zag;
    private final String zah;
    private final SignInOptions zai;
    private Integer zak;

    /* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
    /* loaded from: classes.dex */
    public static final class zaa {
        public final Set<Scope> zaa;
    }

    /* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
    /* loaded from: classes.dex */
    public static final class Builder {
        @Nullable
        private Account zaa;
        private ArraySet<Scope> zab;
        private String zad;
        private String zae;
        private SignInOptions zaf = SignInOptions.zaa;

        @RecentlyNonNull
        public final Builder zaa(@Nullable Account account) {
            this.zaa = account;
            return this;
        }

        @RecentlyNonNull
        public final Builder zaa(@RecentlyNonNull Collection<Scope> collection) {
            if (this.zab == null) {
                this.zab = new ArraySet<>();
            }
            this.zab.addAll(collection);
            return this;
        }

        @RecentlyNonNull
        public final Builder setRealClientPackageName(@RecentlyNonNull String str) {
            this.zad = str;
            return this;
        }

        @RecentlyNonNull
        public final Builder zaa(@RecentlyNonNull String str) {
            this.zae = str;
            return this;
        }

        @RecentlyNonNull
        public final ClientSettings build() {
            return new ClientSettings(this.zaa, this.zab, null, 0, null, this.zad, this.zae, this.zaf, false);
        }
    }

    public ClientSettings(@Nullable Account account, @RecentlyNonNull Set<Scope> set, @RecentlyNonNull Map<Api<?>, zaa> map, int i, @RecentlyNonNull View view, @RecentlyNonNull String str, @RecentlyNonNull String str2, @RecentlyNonNull SignInOptions signInOptions, boolean z) {
        this.zaa = account;
        Set<Scope> emptySet = set == null ? Collections.emptySet() : Collections.unmodifiableSet(set);
        this.zab = emptySet;
        map = map == null ? Collections.emptyMap() : map;
        this.zad = map;
        this.zag = str;
        this.zah = str2;
        this.zai = signInOptions;
        HashSet hashSet = new HashSet(emptySet);
        for (zaa zaaVar : map.values()) {
            hashSet.addAll(zaaVar.zaa);
        }
        this.zac = Collections.unmodifiableSet(hashSet);
    }

    @RecentlyNullable
    @Deprecated
    public final String getAccountName() {
        Account account = this.zaa;
        if (account != null) {
            return account.name;
        }
        return null;
    }

    @RecentlyNullable
    public final Account getAccount() {
        return this.zaa;
    }

    @RecentlyNonNull
    public final Account getAccountOrDefault() {
        Account account = this.zaa;
        return account != null ? account : new Account(HuaweiApiClientImpl.DEFAULT_ACCOUNT, "com.google");
    }

    @RecentlyNonNull
    public final Set<Scope> getRequiredScopes() {
        return this.zab;
    }

    @RecentlyNonNull
    public final Set<Scope> getAllRequestedScopes() {
        return this.zac;
    }

    @RecentlyNonNull
    public final Map<Api<?>, zaa> zaa() {
        return this.zad;
    }

    @RecentlyNullable
    public final String getRealClientPackageName() {
        return this.zag;
    }

    @RecentlyNullable
    public final String zab() {
        return this.zah;
    }

    @RecentlyNonNull
    public final SignInOptions zac() {
        return this.zai;
    }

    @RecentlyNullable
    public final Integer zad() {
        return this.zak;
    }

    public final void zaa(@RecentlyNonNull Integer num) {
        this.zak = num;
    }

    @RecentlyNonNull
    public final Set<Scope> getApplicableScopes(@RecentlyNonNull Api<?> api) {
        zaa zaaVar = this.zad.get(api);
        if (zaaVar == null || zaaVar.zaa.isEmpty()) {
            return this.zab;
        }
        HashSet hashSet = new HashSet(this.zab);
        hashSet.addAll(zaaVar.zaa);
        return hashSet;
    }
}
