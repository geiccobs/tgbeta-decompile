package com.google.android.gms.wallet;

import android.accounts.Account;
import android.app.Activity;
import android.content.Context;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.wallet.wobs.WalletObjects;
import java.util.Locale;
/* compiled from: com.google.android.gms:play-services-wallet@@18.1.3 */
/* loaded from: classes3.dex */
public final class Wallet {
    public static final Api<WalletOptions> API;
    private static final Api.ClientKey<com.google.android.gms.internal.wallet.zzab> zzd;
    private static final Api.AbstractClientBuilder<com.google.android.gms.internal.wallet.zzab, WalletOptions> zze;
    @Deprecated
    public static final com.google.android.gms.internal.wallet.zzv zzb = new com.google.android.gms.internal.wallet.zzv();
    public static final WalletObjects zza = new com.google.android.gms.internal.wallet.zzae();
    public static final com.google.android.gms.internal.wallet.zzac zzc = new com.google.android.gms.internal.wallet.zzac();

    /* compiled from: com.google.android.gms:play-services-wallet@@18.1.3 */
    /* loaded from: classes3.dex */
    public static final class WalletOptions implements Api.ApiOptions.HasAccountOptions {
        public final int environment;
        public final int theme;
        public final Account zza;
        final boolean zzb;

        /* compiled from: com.google.android.gms:play-services-wallet@@18.1.3 */
        /* loaded from: classes3.dex */
        public static final class Builder {
            private int zza = 3;
            private int zzb = 1;
            private boolean zzc = true;

            public WalletOptions build() {
                return new WalletOptions(this, null);
            }

            public Builder setEnvironment(int environment) {
                if (environment != 0) {
                    if (environment == 0) {
                        environment = 0;
                    } else if (environment != 2 && environment != 1 && environment != 23 && environment != 3) {
                        throw new IllegalArgumentException(String.format(Locale.US, "Invalid environment value %d", Integer.valueOf(environment)));
                    }
                }
                this.zza = environment;
                return this;
            }

            public Builder setTheme(int theme) {
                if (theme == 0 || theme == 1 || theme == 2 || theme == 3) {
                    this.zzb = theme;
                    return this;
                }
                throw new IllegalArgumentException(String.format(Locale.US, "Invalid theme value %d", Integer.valueOf(theme)));
            }

            @Deprecated
            public Builder useGoogleWallet() {
                this.zzc = false;
                return this;
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

        public boolean equals(Object other) {
            if (other instanceof WalletOptions) {
                WalletOptions walletOptions = (WalletOptions) other;
                if (Objects.equal(Integer.valueOf(this.environment), Integer.valueOf(walletOptions.environment)) && Objects.equal(Integer.valueOf(this.theme), Integer.valueOf(walletOptions.theme))) {
                    Account account = walletOptions.zza;
                    if (Objects.equal(null, null) && Objects.equal(Boolean.valueOf(this.zzb), Boolean.valueOf(walletOptions.zzb))) {
                        return true;
                    }
                }
            }
            return false;
        }

        @Override // com.google.android.gms.common.api.Api.ApiOptions.HasAccountOptions
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
    }

    private Wallet() {
    }

    public static PaymentsClient getPaymentsClient(Activity activity, WalletOptions options) {
        return new PaymentsClient(activity, options);
    }

    public static WalletObjectsClient getWalletObjectsClient(Activity activity, WalletOptions options) {
        return new WalletObjectsClient(activity, options);
    }

    public static PaymentsClient getPaymentsClient(Context context, WalletOptions options) {
        return new PaymentsClient(context, options);
    }
}
