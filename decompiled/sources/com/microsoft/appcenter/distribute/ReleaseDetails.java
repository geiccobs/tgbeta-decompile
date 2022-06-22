package com.microsoft.appcenter.distribute;

import android.net.Uri;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes.dex */
public class ReleaseDetails {
    private String distributionGroupId;
    private Uri downloadUrl;
    private int id;
    private boolean mandatoryUpdate;
    private int minApiLevel;
    private String releaseHash;
    private String releaseNotes;
    private Uri releaseNotesUrl;
    private String shortVersion;
    private long size;
    private int version;

    public static ReleaseDetails parse(String str) throws JSONException {
        JSONObject jSONObject = new JSONObject(str);
        ReleaseDetails releaseDetails = new ReleaseDetails();
        releaseDetails.id = jSONObject.getInt("id");
        releaseDetails.version = jSONObject.getInt("version");
        releaseDetails.shortVersion = jSONObject.getString("short_version");
        releaseDetails.size = jSONObject.getLong("size");
        String str2 = null;
        releaseDetails.releaseNotes = jSONObject.isNull("release_notes") ? null : jSONObject.getString("release_notes");
        releaseDetails.releaseNotesUrl = jSONObject.isNull("release_notes_url") ? null : Uri.parse(jSONObject.getString("release_notes_url"));
        releaseDetails.minApiLevel = jSONObject.getInt("android_min_api_level");
        Uri parse = Uri.parse(jSONObject.getString("download_url"));
        releaseDetails.downloadUrl = parse;
        String scheme = parse.getScheme();
        if (scheme == null || !scheme.startsWith("http")) {
            throw new JSONException("Invalid download_url scheme.");
        }
        releaseDetails.mandatoryUpdate = jSONObject.getBoolean("mandatory_update");
        releaseDetails.releaseHash = jSONObject.getJSONArray("package_hashes").getString(0);
        if (!jSONObject.isNull("distribution_group_id")) {
            str2 = jSONObject.getString("distribution_group_id");
        }
        releaseDetails.distributionGroupId = str2;
        return releaseDetails;
    }

    public int getId() {
        return this.id;
    }

    public int getVersion() {
        return this.version;
    }

    public long getSize() {
        return this.size;
    }

    public String getShortVersion() {
        return this.shortVersion;
    }

    public String getReleaseNotes() {
        return this.releaseNotes;
    }

    public Uri getReleaseNotesUrl() {
        return this.releaseNotesUrl;
    }

    public int getMinApiLevel() {
        return this.minApiLevel;
    }

    public Uri getDownloadUrl() {
        return this.downloadUrl;
    }

    public boolean isMandatoryUpdate() {
        return this.mandatoryUpdate;
    }

    public String getReleaseHash() {
        return this.releaseHash;
    }

    public String getDistributionGroupId() {
        return this.distributionGroupId;
    }
}
