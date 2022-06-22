package com.google.android.gms.wallet;

import android.accounts.Account;
import android.content.Context;
import androidx.annotation.RecentlyNonNull;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.internal.Objects;
import java.util.Locale;
/* compiled from: com.google.android.gms:play-services-wallet@@18.1.3 */
/* loaded from: classes.dex */
public final class Wallet {
    @RecentlyNonNull
    public static final Api<WalletOptions> API;
    private static final Api.ClientKey<com.google.android.gms.internal.wallet.zzab> zzd;
    private static final Api.AbstractClientBuilder<com.google.android.gms.internal.wallet.zzab, WalletOptions> zze;

    /* compiled from: com.google.android.gms:play-services-wallet@@18.1.3 */
    /* loaded from: classes.dex */
    public static final class WalletOptions implements Api.ApiOptions.HasAccountOptions {
        public final int environment;
        public final int theme;
        @RecentlyNonNull
        public final Account zza;
        final boolean zzb;

        /* compiled from: com.google.android.gms:play-services-wallet@@18.1.3 */
        /* loaded from: classes.dex */
        public static final class Builder {
            private int zza = 3;
            private int zzb = 1;
            private boolean zzc = true;

            @RecentlyNonNull
            public WalletOptions build() {
                return new WalletOptions(this, null);
            }

            @RecentlyNonNull
            public Builder setEnvironment(int i) {
                if (i != 0) {
                    if (i == 0) {
                        i = 0;
                    } else if (i != 2 && i != 1 && i != 23 && i != 3) {
                        throw new IllegalArgumentException(String.format(Locale.US, "Invalid environment value %d", Integer.valueOf(i)));
                    }
                }
                this.zza = i;
                return this;
            }

            @RecentlyNonNull
            public Builder setTheme(int i) {
                if (i == 0 || i == 1 || i == 2 || i == 3) {
                    this.zzb = i;
                    return this;
                }
                throw new IllegalArgumentException(String.format(Locale.US, "Invalid theme value %d", Integer.valueOf(i)));
            }
        }

        private WalletOptions() {
            this(new Builder());
        }

        private WalletOptions(Builder builder) {
            this.environment = builder.zza;
            this.theme = builder.zzb;
            this.zzb = builder.zzc;
            this.zza = null;
        }

        public boolean equals(Object obj) {
            if (obj instanceof WalletOptions) {
                WalletOptions walletOptions = (WalletOptions) obj;
                if (Objects.equal(Integer.valueOf(this.environment), Integer.valueOf(walletOptions.environment)) && Objects.equal(Integer.valueOf(this.theme), Integer.valueOf(walletOptions.theme)) && Objects.equal(null, null) && Objects.equal(Boolean.valueOf(this.zzb), Boolean.valueOf(walletOptions.zzb))) {
                    return true;
                }
            }
            return false;
        }

        @Override // com.google.android.gms.common.api.Api.ApiOptions.HasAccountOptions
        @RecentlyNonNull
        public Account getAccount() {
            return null;
        }

        public int hashCode() {
            return Objects.hashCode(Integer.valueOf(this.environment), Integer.valueOf(this.theme), null, Boolean.valueOf(this.zzb));
        }

        public /* synthetic */ WalletOptions(zzaj zzajVar) {
            this(new Builder());
        }

        /* synthetic */ WalletOptions(Builder builder, zzaj zzajVar) {
            this(builder);
        }
    }

    static {
        Api.ClientKey<com.google.android.gms.internal.wallet.zzab> clientKey = new Api.ClientKey<>();
        zzd = clientKey;
        zzaj zzajVar = new zzaj();
        zze = zzajVar;
        API = new Api<>("Wallet.API", zzajVar, clientKey);
        new com.google.android.gms.internal.wallet.zzv();
        new com.google.android.gms.internal.wallet.zzae();
        new com.google.android.gms.internal.wallet.zzac();
    }

    @RecentlyNonNull
    public static PaymentsClient getPaymentsClient(@RecentlyNonNull Context context, @RecentlyNonNull WalletOptions walletOptions) {
        return new PaymentsClient(context, walletOptions);
    }
}
