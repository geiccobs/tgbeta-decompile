package com.google.firebase.remoteconfig.internal;

import java.util.Date;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes3.dex */
public class ConfigContainer {
    static final String ABT_EXPERIMENTS_KEY = "abt_experiments_key";
    static final String CONFIGS_KEY = "configs_key";
    private static final Date DEFAULTS_FETCH_TIME = new Date(0);
    static final String FETCH_TIME_KEY = "fetch_time_key";
    static final String PERSONALIZATION_METADATA_KEY = "personalization_metadata_key";
    private JSONArray abtExperiments;
    private JSONObject configsJson;
    private JSONObject containerJson;
    private Date fetchTime;
    private JSONObject personalizationMetadata;

    private ConfigContainer(JSONObject configsJson, Date fetchTime, JSONArray abtExperiments, JSONObject personalizationMetadata) throws JSONException {
        JSONObject containerJson = new JSONObject();
        containerJson.put(CONFIGS_KEY, configsJson);
        containerJson.put(FETCH_TIME_KEY, fetchTime.getTime());
        containerJson.put(ABT_EXPERIMENTS_KEY, abtExperiments);
        containerJson.put(PERSONALIZATION_METADATA_KEY, personalizationMetadata);
        this.configsJson = configsJson;
        this.fetchTime = fetchTime;
        this.abtExperiments = abtExperiments;
        this.personalizationMetadata = personalizationMetadata;
        this.containerJson = containerJson;
    }

    public static ConfigContainer copyOf(JSONObject containerJson) throws JSONException {
        JSONObject personalizationMetadataJSON = containerJson.optJSONObject(PERSONALIZATION_METADATA_KEY);
        if (personalizationMetadataJSON == null) {
            personalizationMetadataJSON = new JSONObject();
        }
        return new ConfigContainer(containerJson.getJSONObject(CONFIGS_KEY), new Date(containerJson.getLong(FETCH_TIME_KEY)), containerJson.getJSONArray(ABT_EXPERIMENTS_KEY), personalizationMetadataJSON);
    }

    public JSONObject getConfigs() {
        return this.configsJson;
    }

    public Date getFetchTime() {
        return this.fetchTime;
    }

    public JSONArray getAbtExperiments() {
        return this.abtExperiments;
    }

    public JSONObject getPersonalizationMetadata() {
        return this.personalizationMetadata;
    }

    public String toString() {
        return this.containerJson.toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ConfigContainer)) {
            return false;
        }
        ConfigContainer that = (ConfigContainer) o;
        return this.containerJson.toString().equals(that.toString());
    }

    public int hashCode() {
        return this.containerJson.hashCode();
    }

    /* loaded from: classes3.dex */
    public static class Builder {
        private JSONArray builderAbtExperiments;
        private JSONObject builderConfigsJson;
        private Date builderFetchTime;
        private JSONObject builderPersonalizationMetadata;

        private Builder() {
            this.builderConfigsJson = new JSONObject();
            this.builderFetchTime = ConfigContainer.DEFAULTS_FETCH_TIME;
            this.builderAbtExperiments = new JSONArray();
            this.builderPersonalizationMetadata = new JSONObject();
        }

        public Builder(ConfigContainer otherContainer) {
            this.builderConfigsJson = otherContainer.getConfigs();
            this.builderFetchTime = otherContainer.getFetchTime();
            this.builderAbtExperiments = otherContainer.getAbtExperiments();
            this.builderPersonalizationMetadata = otherContainer.getPersonalizationMetadata();
        }

        public Builder replaceConfigsWith(Map<String, String> configsMap) {
            this.builderConfigsJson = new JSONObject(configsMap);
            return this;
        }

        public Builder replaceConfigsWith(JSONObject configsJson) {
            try {
                this.builderConfigsJson = new JSONObject(configsJson.toString());
            } catch (JSONException e) {
            }
            return this;
        }

        public Builder withFetchTime(Date fetchTime) {
            this.builderFetchTime = fetchTime;
            return this;
        }

        public Builder withAbtExperiments(JSONArray abtExperiments) {
            try {
                this.builderAbtExperiments = new JSONArray(abtExperiments.toString());
            } catch (JSONException e) {
            }
            return this;
        }

        public Builder withPersonalizationMetadata(JSONObject personalizationMetadata) {
            try {
                this.builderPersonalizationMetadata = new JSONObject(personalizationMetadata.toString());
            } catch (JSONException e) {
            }
            return this;
        }

        public ConfigContainer build() throws JSONException {
            return new ConfigContainer(this.builderConfigsJson, this.builderFetchTime, this.builderAbtExperiments, this.builderPersonalizationMetadata);
        }
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(ConfigContainer otherContainer) {
        return new Builder(otherContainer);
    }
}
