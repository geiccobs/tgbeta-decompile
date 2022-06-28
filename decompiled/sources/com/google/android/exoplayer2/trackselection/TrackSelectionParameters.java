package com.google.android.exoplayer2.trackselection;

import android.content.Context;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.accessibility.CaptioningManager;
import com.google.android.exoplayer2.util.Util;
import java.util.Locale;
/* loaded from: classes3.dex */
public class TrackSelectionParameters implements Parcelable {
    public static final Parcelable.Creator<TrackSelectionParameters> CREATOR = new Parcelable.Creator<TrackSelectionParameters>() { // from class: com.google.android.exoplayer2.trackselection.TrackSelectionParameters.1
        @Override // android.os.Parcelable.Creator
        public TrackSelectionParameters createFromParcel(Parcel in) {
            return new TrackSelectionParameters(in);
        }

        @Override // android.os.Parcelable.Creator
        public TrackSelectionParameters[] newArray(int size) {
            return new TrackSelectionParameters[size];
        }
    };
    @Deprecated
    public static final TrackSelectionParameters DEFAULT;
    public static final TrackSelectionParameters DEFAULT_WITHOUT_CONTEXT;
    public final int disabledTextTrackSelectionFlags;
    public final String preferredAudioLanguage;
    public final String preferredTextLanguage;
    public final int preferredTextRoleFlags;
    public final boolean selectUndeterminedTextLanguage;

    /* loaded from: classes3.dex */
    public static class Builder {
        int disabledTextTrackSelectionFlags;
        String preferredAudioLanguage;
        String preferredTextLanguage;
        int preferredTextRoleFlags;
        boolean selectUndeterminedTextLanguage;

        public Builder(Context context) {
            this();
            setPreferredTextLanguageAndRoleFlagsToCaptioningManagerSettings(context);
        }

        @Deprecated
        public Builder() {
            this.preferredAudioLanguage = null;
            this.preferredTextLanguage = null;
            this.preferredTextRoleFlags = 0;
            this.selectUndeterminedTextLanguage = false;
            this.disabledTextTrackSelectionFlags = 0;
        }

        public Builder(TrackSelectionParameters initialValues) {
            this.preferredAudioLanguage = initialValues.preferredAudioLanguage;
            this.preferredTextLanguage = initialValues.preferredTextLanguage;
            this.preferredTextRoleFlags = initialValues.preferredTextRoleFlags;
            this.selectUndeterminedTextLanguage = initialValues.selectUndeterminedTextLanguage;
            this.disabledTextTrackSelectionFlags = initialValues.disabledTextTrackSelectionFlags;
        }

        public Builder setPreferredAudioLanguage(String preferredAudioLanguage) {
            this.preferredAudioLanguage = preferredAudioLanguage;
            return this;
        }

        public Builder setPreferredTextLanguageAndRoleFlagsToCaptioningManagerSettings(Context context) {
            if (Util.SDK_INT >= 19) {
                setPreferredTextLanguageAndRoleFlagsToCaptioningManagerSettingsV19(context);
            }
            return this;
        }

        public Builder setPreferredTextLanguage(String preferredTextLanguage) {
            this.preferredTextLanguage = preferredTextLanguage;
            return this;
        }

        public Builder setPreferredTextRoleFlags(int preferredTextRoleFlags) {
            this.preferredTextRoleFlags = preferredTextRoleFlags;
            return this;
        }

        public Builder setSelectUndeterminedTextLanguage(boolean selectUndeterminedTextLanguage) {
            this.selectUndeterminedTextLanguage = selectUndeterminedTextLanguage;
            return this;
        }

        public Builder setDisabledTextTrackSelectionFlags(int disabledTextTrackSelectionFlags) {
            this.disabledTextTrackSelectionFlags = disabledTextTrackSelectionFlags;
            return this;
        }

        public TrackSelectionParameters build() {
            return new TrackSelectionParameters(this.preferredAudioLanguage, this.preferredTextLanguage, this.preferredTextRoleFlags, this.selectUndeterminedTextLanguage, this.disabledTextTrackSelectionFlags);
        }

        private void setPreferredTextLanguageAndRoleFlagsToCaptioningManagerSettingsV19(Context context) {
            CaptioningManager captioningManager;
            if ((Util.SDK_INT < 23 && Looper.myLooper() == null) || (captioningManager = (CaptioningManager) context.getSystemService("captioning")) == null || !captioningManager.isEnabled()) {
                return;
            }
            this.preferredTextRoleFlags = 1088;
            Locale preferredLocale = captioningManager.getLocale();
            if (preferredLocale != null) {
                this.preferredTextLanguage = Util.getLocaleLanguageTag(preferredLocale);
            }
        }
    }

    static {
        TrackSelectionParameters build = new Builder().build();
        DEFAULT_WITHOUT_CONTEXT = build;
        DEFAULT = build;
    }

    public static TrackSelectionParameters getDefaults(Context context) {
        return new Builder(context).build();
    }

    public TrackSelectionParameters(String preferredAudioLanguage, String preferredTextLanguage, int preferredTextRoleFlags, boolean selectUndeterminedTextLanguage, int disabledTextTrackSelectionFlags) {
        this.preferredAudioLanguage = Util.normalizeLanguageCode(preferredAudioLanguage);
        this.preferredTextLanguage = Util.normalizeLanguageCode(preferredTextLanguage);
        this.preferredTextRoleFlags = preferredTextRoleFlags;
        this.selectUndeterminedTextLanguage = selectUndeterminedTextLanguage;
        this.disabledTextTrackSelectionFlags = disabledTextTrackSelectionFlags;
    }

    public TrackSelectionParameters(Parcel in) {
        this.preferredAudioLanguage = in.readString();
        this.preferredTextLanguage = in.readString();
        this.preferredTextRoleFlags = in.readInt();
        this.selectUndeterminedTextLanguage = Util.readBoolean(in);
        this.disabledTextTrackSelectionFlags = in.readInt();
    }

    public Builder buildUpon() {
        return new Builder(this);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        TrackSelectionParameters other = (TrackSelectionParameters) obj;
        return TextUtils.equals(this.preferredAudioLanguage, other.preferredAudioLanguage) && TextUtils.equals(this.preferredTextLanguage, other.preferredTextLanguage) && this.preferredTextRoleFlags == other.preferredTextRoleFlags && this.selectUndeterminedTextLanguage == other.selectUndeterminedTextLanguage && this.disabledTextTrackSelectionFlags == other.disabledTextTrackSelectionFlags;
    }

    public int hashCode() {
        int i = 1 * 31;
        String str = this.preferredAudioLanguage;
        int i2 = 0;
        int result = i + (str == null ? 0 : str.hashCode());
        int result2 = result * 31;
        String str2 = this.preferredTextLanguage;
        if (str2 != null) {
            i2 = str2.hashCode();
        }
        return ((((((result2 + i2) * 31) + this.preferredTextRoleFlags) * 31) + (this.selectUndeterminedTextLanguage ? 1 : 0)) * 31) + this.disabledTextTrackSelectionFlags;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.preferredAudioLanguage);
        dest.writeString(this.preferredTextLanguage);
        dest.writeInt(this.preferredTextRoleFlags);
        Util.writeBoolean(dest, this.selectUndeterminedTextLanguage);
        dest.writeInt(this.disabledTextTrackSelectionFlags);
    }
}
