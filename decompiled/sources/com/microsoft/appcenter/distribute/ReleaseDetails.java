package com.microsoft.appcenter.distribute;

import android.net.Uri;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes3.dex */
public class ReleaseDetails {
    private static final String DISTRIBUTION_GROUP_ID = "distribution_group_id";
    private static final String DOWNLOAD_URL = "download_url";
    private static final String ID = "id";
    private static final String MANDATORY_UPDATE = "mandatory_update";
    private static final String MIN_API_LEVEL = "android_min_api_level";
    private static final String PACKAGE_HASHES = "package_hashes";
    private static final String RELEASE_NOTES = "release_notes";
    private static final String RELEASE_NOTES_URL = "release_notes_url";
    private static final String SHORT_VERSION = "short_version";
    private static final String SIZE = "size";
    private static final String VERSION = "version";
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

    public static ReleaseDetails parse(String json) throws JSONException {
        JSONObject object = new JSONObject(json);
        ReleaseDetails releaseDetails = new ReleaseDetails();
        releaseDetails.id = object.getInt("id");
        releaseDetails.version = object.getInt(VERSION);
        releaseDetails.shortVersion = object.getString(SHORT_VERSION);
        releaseDetails.size = object.getLong(SIZE);
        String str = null;
        releaseDetails.releaseNotes = object.isNull(RELEASE_NOTES) ? null : object.getString(RELEASE_NOTES);
        releaseDetails.releaseNotesUrl = object.isNull(RELEASE_NOTES_URL) ? null : Uri.parse(object.getString(RELEASE_NOTES_URL));
        releaseDetails.minApiLevel = object.getInt(MIN_API_LEVEL);
        Uri parse = Uri.parse(object.getString(DOWNLOAD_URL));
        releaseDetails.downloadUrl = parse;
        String scheme = parse.getScheme();
        if (scheme == null || !scheme.startsWith("http")) {
            throw new JSONException("Invalid download_url scheme.");
        }
        releaseDetails.mandatoryUpdate = object.getBoolean(MANDATORY_UPDATE);
        releaseDetails.releaseHash = object.getJSONArray(PACKAGE_HASHES).getString(0);
        if (!object.isNull(DISTRIBUTION_GROUP_ID)) {
            str = object.getString(DISTRIBUTION_GROUP_ID);
        }
        releaseDetails.distributionGroupId = str;
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
