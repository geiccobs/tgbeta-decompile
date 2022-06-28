package com.google.firebase.remoteconfig;

import com.google.firebase.remoteconfig.internal.ConfigFetchHandler;
/* loaded from: classes3.dex */
public class FirebaseRemoteConfigSettings {
    private final long fetchTimeoutInSeconds;
    private final long minimumFetchInterval;

    private FirebaseRemoteConfigSettings(Builder builder) {
        this.fetchTimeoutInSeconds = builder.fetchTimeoutInSeconds;
        this.minimumFetchInterval = builder.minimumFetchInterval;
    }

    public long getFetchTimeoutInSeconds() {
        return this.fetchTimeoutInSeconds;
    }

    public long getMinimumFetchIntervalInSeconds() {
        return this.minimumFetchInterval;
    }

    public Builder toBuilder() {
        Builder frcBuilder = new Builder();
        frcBuilder.setFetchTimeoutInSeconds(getFetchTimeoutInSeconds());
        frcBuilder.setMinimumFetchIntervalInSeconds(getMinimumFetchIntervalInSeconds());
        return frcBuilder;
    }

    /* loaded from: classes3.dex */
    public static class Builder {
        private long fetchTimeoutInSeconds = 60;
        private long minimumFetchInterval = ConfigFetchHandler.DEFAULT_MINIMUM_FETCH_INTERVAL_IN_SECONDS;

        public Builder setFetchTimeoutInSeconds(long duration) throws IllegalArgumentException {
            if (duration < 0) {
                throw new IllegalArgumentException(String.format("Fetch connection timeout has to be a non-negative number. %d is an invalid argument", Long.valueOf(duration)));
            }
            this.fetchTimeoutInSeconds = duration;
            return this;
        }

        public Builder setMinimumFetchIntervalInSeconds(long duration) {
            if (duration < 0) {
                throw new IllegalArgumentException("Minimum interval between fetches has to be a non-negative number. " + duration + " is an invalid argument");
            }
            this.minimumFetchInterval = duration;
            return this;
        }

        public long getFetchTimeoutInSeconds() {
            return this.fetchTimeoutInSeconds;
        }

        public long getMinimumFetchIntervalInSeconds() {
            return this.minimumFetchInterval;
        }

        public FirebaseRemoteConfigSettings build() {
            return new FirebaseRemoteConfigSettings(this);
        }
    }
}
