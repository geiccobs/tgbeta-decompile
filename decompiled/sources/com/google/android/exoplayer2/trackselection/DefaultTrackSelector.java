package com.google.android.exoplayer2.trackselection;

import android.content.Context;
import android.graphics.Point;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Pair;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.RendererCapabilities;
import com.google.android.exoplayer2.RendererConfiguration;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionParameters;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
/* loaded from: classes3.dex */
public class DefaultTrackSelector extends MappingTrackSelector {
    private static final float FRACTION_TO_CONSIDER_FULLSCREEN = 0.98f;
    private static final int[] NO_TRACKS = new int[0];
    private static final int WITHIN_RENDERER_CAPABILITIES_BONUS = 1000;
    private boolean allowMultipleAdaptiveSelections;
    private final AtomicReference<Parameters> parametersReference;
    private final TrackSelection.Factory trackSelectionFactory;

    /* loaded from: classes3.dex */
    public static final class ParametersBuilder extends TrackSelectionParameters.Builder {
        private boolean allowAudioMixedChannelCountAdaptiveness;
        private boolean allowAudioMixedMimeTypeAdaptiveness;
        private boolean allowAudioMixedSampleRateAdaptiveness;
        private boolean allowVideoMixedMimeTypeAdaptiveness;
        private boolean allowVideoNonSeamlessAdaptiveness;
        private boolean exceedAudioConstraintsIfNecessary;
        private boolean exceedRendererCapabilitiesIfNecessary;
        private boolean exceedVideoConstraintsIfNecessary;
        private boolean forceHighestSupportedBitrate;
        private boolean forceLowestBitrate;
        private int maxAudioBitrate;
        private int maxAudioChannelCount;
        private int maxVideoBitrate;
        private int maxVideoFrameRate;
        private int maxVideoHeight;
        private int maxVideoWidth;
        private final SparseBooleanArray rendererDisabledFlags;
        private final SparseArray<Map<TrackGroupArray, SelectionOverride>> selectionOverrides;
        private int tunnelingAudioSessionId;
        private int viewportHeight;
        private boolean viewportOrientationMayChange;
        private int viewportWidth;

        @Deprecated
        public ParametersBuilder() {
            setInitialValuesWithoutContext();
            this.selectionOverrides = new SparseArray<>();
            this.rendererDisabledFlags = new SparseBooleanArray();
        }

        public ParametersBuilder(Context context) {
            super(context);
            setInitialValuesWithoutContext();
            this.selectionOverrides = new SparseArray<>();
            this.rendererDisabledFlags = new SparseBooleanArray();
            setViewportSizeToPhysicalDisplaySize(context, true);
        }

        private ParametersBuilder(Parameters initialValues) {
            super(initialValues);
            this.maxVideoWidth = initialValues.maxVideoWidth;
            this.maxVideoHeight = initialValues.maxVideoHeight;
            this.maxVideoFrameRate = initialValues.maxVideoFrameRate;
            this.maxVideoBitrate = initialValues.maxVideoBitrate;
            this.exceedVideoConstraintsIfNecessary = initialValues.exceedVideoConstraintsIfNecessary;
            this.allowVideoMixedMimeTypeAdaptiveness = initialValues.allowVideoMixedMimeTypeAdaptiveness;
            this.allowVideoNonSeamlessAdaptiveness = initialValues.allowVideoNonSeamlessAdaptiveness;
            this.viewportWidth = initialValues.viewportWidth;
            this.viewportHeight = initialValues.viewportHeight;
            this.viewportOrientationMayChange = initialValues.viewportOrientationMayChange;
            this.maxAudioChannelCount = initialValues.maxAudioChannelCount;
            this.maxAudioBitrate = initialValues.maxAudioBitrate;
            this.exceedAudioConstraintsIfNecessary = initialValues.exceedAudioConstraintsIfNecessary;
            this.allowAudioMixedMimeTypeAdaptiveness = initialValues.allowAudioMixedMimeTypeAdaptiveness;
            this.allowAudioMixedSampleRateAdaptiveness = initialValues.allowAudioMixedSampleRateAdaptiveness;
            this.allowAudioMixedChannelCountAdaptiveness = initialValues.allowAudioMixedChannelCountAdaptiveness;
            this.forceLowestBitrate = initialValues.forceLowestBitrate;
            this.forceHighestSupportedBitrate = initialValues.forceHighestSupportedBitrate;
            this.exceedRendererCapabilitiesIfNecessary = initialValues.exceedRendererCapabilitiesIfNecessary;
            this.tunnelingAudioSessionId = initialValues.tunnelingAudioSessionId;
            this.selectionOverrides = cloneSelectionOverrides(initialValues.selectionOverrides);
            this.rendererDisabledFlags = initialValues.rendererDisabledFlags.clone();
        }

        public ParametersBuilder setMaxVideoSizeSd() {
            return setMaxVideoSize(1279, 719);
        }

        public ParametersBuilder clearVideoSizeConstraints() {
            return setMaxVideoSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
        }

        public ParametersBuilder setMaxVideoSize(int maxVideoWidth, int maxVideoHeight) {
            this.maxVideoWidth = maxVideoWidth;
            this.maxVideoHeight = maxVideoHeight;
            return this;
        }

        public ParametersBuilder setMaxVideoFrameRate(int maxVideoFrameRate) {
            this.maxVideoFrameRate = maxVideoFrameRate;
            return this;
        }

        public ParametersBuilder setMaxVideoBitrate(int maxVideoBitrate) {
            this.maxVideoBitrate = maxVideoBitrate;
            return this;
        }

        public ParametersBuilder setExceedVideoConstraintsIfNecessary(boolean exceedVideoConstraintsIfNecessary) {
            this.exceedVideoConstraintsIfNecessary = exceedVideoConstraintsIfNecessary;
            return this;
        }

        public ParametersBuilder setAllowVideoMixedMimeTypeAdaptiveness(boolean allowVideoMixedMimeTypeAdaptiveness) {
            this.allowVideoMixedMimeTypeAdaptiveness = allowVideoMixedMimeTypeAdaptiveness;
            return this;
        }

        public ParametersBuilder setAllowVideoNonSeamlessAdaptiveness(boolean allowVideoNonSeamlessAdaptiveness) {
            this.allowVideoNonSeamlessAdaptiveness = allowVideoNonSeamlessAdaptiveness;
            return this;
        }

        public ParametersBuilder setViewportSizeToPhysicalDisplaySize(Context context, boolean viewportOrientationMayChange) {
            Point viewportSize = Util.getCurrentDisplayModeSize(context);
            return setViewportSize(viewportSize.x, viewportSize.y, viewportOrientationMayChange);
        }

        public ParametersBuilder clearViewportSizeConstraints() {
            return setViewportSize(Integer.MAX_VALUE, Integer.MAX_VALUE, true);
        }

        public ParametersBuilder setViewportSize(int viewportWidth, int viewportHeight, boolean viewportOrientationMayChange) {
            this.viewportWidth = viewportWidth;
            this.viewportHeight = viewportHeight;
            this.viewportOrientationMayChange = viewportOrientationMayChange;
            return this;
        }

        @Override // com.google.android.exoplayer2.trackselection.TrackSelectionParameters.Builder
        public ParametersBuilder setPreferredAudioLanguage(String preferredAudioLanguage) {
            super.setPreferredAudioLanguage(preferredAudioLanguage);
            return this;
        }

        public ParametersBuilder setMaxAudioChannelCount(int maxAudioChannelCount) {
            this.maxAudioChannelCount = maxAudioChannelCount;
            return this;
        }

        public ParametersBuilder setMaxAudioBitrate(int maxAudioBitrate) {
            this.maxAudioBitrate = maxAudioBitrate;
            return this;
        }

        public ParametersBuilder setExceedAudioConstraintsIfNecessary(boolean exceedAudioConstraintsIfNecessary) {
            this.exceedAudioConstraintsIfNecessary = exceedAudioConstraintsIfNecessary;
            return this;
        }

        public ParametersBuilder setAllowAudioMixedMimeTypeAdaptiveness(boolean allowAudioMixedMimeTypeAdaptiveness) {
            this.allowAudioMixedMimeTypeAdaptiveness = allowAudioMixedMimeTypeAdaptiveness;
            return this;
        }

        public ParametersBuilder setAllowAudioMixedSampleRateAdaptiveness(boolean allowAudioMixedSampleRateAdaptiveness) {
            this.allowAudioMixedSampleRateAdaptiveness = allowAudioMixedSampleRateAdaptiveness;
            return this;
        }

        public ParametersBuilder setAllowAudioMixedChannelCountAdaptiveness(boolean allowAudioMixedChannelCountAdaptiveness) {
            this.allowAudioMixedChannelCountAdaptiveness = allowAudioMixedChannelCountAdaptiveness;
            return this;
        }

        @Override // com.google.android.exoplayer2.trackselection.TrackSelectionParameters.Builder
        public ParametersBuilder setPreferredTextLanguageAndRoleFlagsToCaptioningManagerSettings(Context context) {
            super.setPreferredTextLanguageAndRoleFlagsToCaptioningManagerSettings(context);
            return this;
        }

        @Override // com.google.android.exoplayer2.trackselection.TrackSelectionParameters.Builder
        public ParametersBuilder setPreferredTextLanguage(String preferredTextLanguage) {
            super.setPreferredTextLanguage(preferredTextLanguage);
            return this;
        }

        @Override // com.google.android.exoplayer2.trackselection.TrackSelectionParameters.Builder
        public ParametersBuilder setPreferredTextRoleFlags(int preferredTextRoleFlags) {
            super.setPreferredTextRoleFlags(preferredTextRoleFlags);
            return this;
        }

        @Override // com.google.android.exoplayer2.trackselection.TrackSelectionParameters.Builder
        public ParametersBuilder setSelectUndeterminedTextLanguage(boolean selectUndeterminedTextLanguage) {
            super.setSelectUndeterminedTextLanguage(selectUndeterminedTextLanguage);
            return this;
        }

        @Override // com.google.android.exoplayer2.trackselection.TrackSelectionParameters.Builder
        public ParametersBuilder setDisabledTextTrackSelectionFlags(int disabledTextTrackSelectionFlags) {
            super.setDisabledTextTrackSelectionFlags(disabledTextTrackSelectionFlags);
            return this;
        }

        public ParametersBuilder setForceLowestBitrate(boolean forceLowestBitrate) {
            this.forceLowestBitrate = forceLowestBitrate;
            return this;
        }

        public ParametersBuilder setForceHighestSupportedBitrate(boolean forceHighestSupportedBitrate) {
            this.forceHighestSupportedBitrate = forceHighestSupportedBitrate;
            return this;
        }

        @Deprecated
        public ParametersBuilder setAllowMixedMimeAdaptiveness(boolean allowMixedMimeAdaptiveness) {
            setAllowAudioMixedMimeTypeAdaptiveness(allowMixedMimeAdaptiveness);
            setAllowVideoMixedMimeTypeAdaptiveness(allowMixedMimeAdaptiveness);
            return this;
        }

        @Deprecated
        public ParametersBuilder setAllowNonSeamlessAdaptiveness(boolean allowNonSeamlessAdaptiveness) {
            return setAllowVideoNonSeamlessAdaptiveness(allowNonSeamlessAdaptiveness);
        }

        public ParametersBuilder setExceedRendererCapabilitiesIfNecessary(boolean exceedRendererCapabilitiesIfNecessary) {
            this.exceedRendererCapabilitiesIfNecessary = exceedRendererCapabilitiesIfNecessary;
            return this;
        }

        public ParametersBuilder setTunnelingAudioSessionId(int tunnelingAudioSessionId) {
            this.tunnelingAudioSessionId = tunnelingAudioSessionId;
            return this;
        }

        public final ParametersBuilder setRendererDisabled(int rendererIndex, boolean disabled) {
            if (this.rendererDisabledFlags.get(rendererIndex) == disabled) {
                return this;
            }
            if (disabled) {
                this.rendererDisabledFlags.put(rendererIndex, true);
            } else {
                this.rendererDisabledFlags.delete(rendererIndex);
            }
            return this;
        }

        public final ParametersBuilder setSelectionOverride(int rendererIndex, TrackGroupArray groups, SelectionOverride override) {
            Map<TrackGroupArray, SelectionOverride> overrides = this.selectionOverrides.get(rendererIndex);
            if (overrides == null) {
                overrides = new HashMap();
                this.selectionOverrides.put(rendererIndex, overrides);
            }
            if (overrides.containsKey(groups) && Util.areEqual(overrides.get(groups), override)) {
                return this;
            }
            overrides.put(groups, override);
            return this;
        }

        public final ParametersBuilder clearSelectionOverride(int rendererIndex, TrackGroupArray groups) {
            Map<TrackGroupArray, SelectionOverride> overrides = this.selectionOverrides.get(rendererIndex);
            if (overrides == null || !overrides.containsKey(groups)) {
                return this;
            }
            overrides.remove(groups);
            if (overrides.isEmpty()) {
                this.selectionOverrides.remove(rendererIndex);
            }
            return this;
        }

        public final ParametersBuilder clearSelectionOverrides(int rendererIndex) {
            Map<TrackGroupArray, SelectionOverride> overrides = this.selectionOverrides.get(rendererIndex);
            if (overrides == null || overrides.isEmpty()) {
                return this;
            }
            this.selectionOverrides.remove(rendererIndex);
            return this;
        }

        public final ParametersBuilder clearSelectionOverrides() {
            if (this.selectionOverrides.size() == 0) {
                return this;
            }
            this.selectionOverrides.clear();
            return this;
        }

        @Override // com.google.android.exoplayer2.trackselection.TrackSelectionParameters.Builder
        public Parameters build() {
            return new Parameters(this.maxVideoWidth, this.maxVideoHeight, this.maxVideoFrameRate, this.maxVideoBitrate, this.exceedVideoConstraintsIfNecessary, this.allowVideoMixedMimeTypeAdaptiveness, this.allowVideoNonSeamlessAdaptiveness, this.viewportWidth, this.viewportHeight, this.viewportOrientationMayChange, this.preferredAudioLanguage, this.maxAudioChannelCount, this.maxAudioBitrate, this.exceedAudioConstraintsIfNecessary, this.allowAudioMixedMimeTypeAdaptiveness, this.allowAudioMixedSampleRateAdaptiveness, this.allowAudioMixedChannelCountAdaptiveness, this.preferredTextLanguage, this.preferredTextRoleFlags, this.selectUndeterminedTextLanguage, this.disabledTextTrackSelectionFlags, this.forceLowestBitrate, this.forceHighestSupportedBitrate, this.exceedRendererCapabilitiesIfNecessary, this.tunnelingAudioSessionId, this.selectionOverrides, this.rendererDisabledFlags);
        }

        private void setInitialValuesWithoutContext() {
            this.maxVideoWidth = Integer.MAX_VALUE;
            this.maxVideoHeight = Integer.MAX_VALUE;
            this.maxVideoFrameRate = Integer.MAX_VALUE;
            this.maxVideoBitrate = Integer.MAX_VALUE;
            this.exceedVideoConstraintsIfNecessary = true;
            this.allowVideoMixedMimeTypeAdaptiveness = false;
            this.allowVideoNonSeamlessAdaptiveness = true;
            this.viewportWidth = Integer.MAX_VALUE;
            this.viewportHeight = Integer.MAX_VALUE;
            this.viewportOrientationMayChange = true;
            this.maxAudioChannelCount = Integer.MAX_VALUE;
            this.maxAudioBitrate = Integer.MAX_VALUE;
            this.exceedAudioConstraintsIfNecessary = true;
            this.allowAudioMixedMimeTypeAdaptiveness = false;
            this.allowAudioMixedSampleRateAdaptiveness = false;
            this.allowAudioMixedChannelCountAdaptiveness = false;
            this.forceLowestBitrate = false;
            this.forceHighestSupportedBitrate = false;
            this.exceedRendererCapabilitiesIfNecessary = true;
            this.tunnelingAudioSessionId = 0;
        }

        private static SparseArray<Map<TrackGroupArray, SelectionOverride>> cloneSelectionOverrides(SparseArray<Map<TrackGroupArray, SelectionOverride>> selectionOverrides) {
            SparseArray<Map<TrackGroupArray, SelectionOverride>> clone = new SparseArray<>();
            for (int i = 0; i < selectionOverrides.size(); i++) {
                clone.put(selectionOverrides.keyAt(i), new HashMap(selectionOverrides.valueAt(i)));
            }
            return clone;
        }
    }

    /* loaded from: classes3.dex */
    public static final class Parameters extends TrackSelectionParameters {
        public static final Parcelable.Creator<Parameters> CREATOR = new Parcelable.Creator<Parameters>() { // from class: com.google.android.exoplayer2.trackselection.DefaultTrackSelector.Parameters.1
            @Override // android.os.Parcelable.Creator
            public Parameters createFromParcel(Parcel in) {
                return new Parameters(in);
            }

            @Override // android.os.Parcelable.Creator
            public Parameters[] newArray(int size) {
                return new Parameters[size];
            }
        };
        @Deprecated
        public static final Parameters DEFAULT;
        public static final Parameters DEFAULT_WITHOUT_CONTEXT;
        @Deprecated
        public static final Parameters DEFAULT_WITHOUT_VIEWPORT;
        public final boolean allowAudioMixedChannelCountAdaptiveness;
        public final boolean allowAudioMixedMimeTypeAdaptiveness;
        public final boolean allowAudioMixedSampleRateAdaptiveness;
        @Deprecated
        public final boolean allowMixedMimeAdaptiveness;
        @Deprecated
        public final boolean allowNonSeamlessAdaptiveness;
        public final boolean allowVideoMixedMimeTypeAdaptiveness;
        public final boolean allowVideoNonSeamlessAdaptiveness;
        public final boolean exceedAudioConstraintsIfNecessary;
        public final boolean exceedRendererCapabilitiesIfNecessary;
        public final boolean exceedVideoConstraintsIfNecessary;
        public final boolean forceHighestSupportedBitrate;
        public final boolean forceLowestBitrate;
        public final int maxAudioBitrate;
        public final int maxAudioChannelCount;
        public final int maxVideoBitrate;
        public final int maxVideoFrameRate;
        public final int maxVideoHeight;
        public final int maxVideoWidth;
        private final SparseBooleanArray rendererDisabledFlags;
        private final SparseArray<Map<TrackGroupArray, SelectionOverride>> selectionOverrides;
        public final int tunnelingAudioSessionId;
        public final int viewportHeight;
        public final boolean viewportOrientationMayChange;
        public final int viewportWidth;

        static {
            Parameters build = new ParametersBuilder().build();
            DEFAULT_WITHOUT_CONTEXT = build;
            DEFAULT_WITHOUT_VIEWPORT = build;
            DEFAULT = build;
        }

        public static Parameters getDefaults(Context context) {
            return new ParametersBuilder(context).build();
        }

        Parameters(int maxVideoWidth, int maxVideoHeight, int maxVideoFrameRate, int maxVideoBitrate, boolean exceedVideoConstraintsIfNecessary, boolean allowVideoMixedMimeTypeAdaptiveness, boolean allowVideoNonSeamlessAdaptiveness, int viewportWidth, int viewportHeight, boolean viewportOrientationMayChange, String preferredAudioLanguage, int maxAudioChannelCount, int maxAudioBitrate, boolean exceedAudioConstraintsIfNecessary, boolean allowAudioMixedMimeTypeAdaptiveness, boolean allowAudioMixedSampleRateAdaptiveness, boolean allowAudioMixedChannelCountAdaptiveness, String preferredTextLanguage, int preferredTextRoleFlags, boolean selectUndeterminedTextLanguage, int disabledTextTrackSelectionFlags, boolean forceLowestBitrate, boolean forceHighestSupportedBitrate, boolean exceedRendererCapabilitiesIfNecessary, int tunnelingAudioSessionId, SparseArray<Map<TrackGroupArray, SelectionOverride>> selectionOverrides, SparseBooleanArray rendererDisabledFlags) {
            super(preferredAudioLanguage, preferredTextLanguage, preferredTextRoleFlags, selectUndeterminedTextLanguage, disabledTextTrackSelectionFlags);
            this.maxVideoWidth = maxVideoWidth;
            this.maxVideoHeight = maxVideoHeight;
            this.maxVideoFrameRate = maxVideoFrameRate;
            this.maxVideoBitrate = maxVideoBitrate;
            this.exceedVideoConstraintsIfNecessary = exceedVideoConstraintsIfNecessary;
            this.allowVideoMixedMimeTypeAdaptiveness = allowVideoMixedMimeTypeAdaptiveness;
            this.allowVideoNonSeamlessAdaptiveness = allowVideoNonSeamlessAdaptiveness;
            this.viewportWidth = viewportWidth;
            this.viewportHeight = viewportHeight;
            this.viewportOrientationMayChange = viewportOrientationMayChange;
            this.maxAudioChannelCount = maxAudioChannelCount;
            this.maxAudioBitrate = maxAudioBitrate;
            this.exceedAudioConstraintsIfNecessary = exceedAudioConstraintsIfNecessary;
            this.allowAudioMixedMimeTypeAdaptiveness = allowAudioMixedMimeTypeAdaptiveness;
            this.allowAudioMixedSampleRateAdaptiveness = allowAudioMixedSampleRateAdaptiveness;
            this.allowAudioMixedChannelCountAdaptiveness = allowAudioMixedChannelCountAdaptiveness;
            this.forceLowestBitrate = forceLowestBitrate;
            this.forceHighestSupportedBitrate = forceHighestSupportedBitrate;
            this.exceedRendererCapabilitiesIfNecessary = exceedRendererCapabilitiesIfNecessary;
            this.tunnelingAudioSessionId = tunnelingAudioSessionId;
            this.allowMixedMimeAdaptiveness = allowVideoMixedMimeTypeAdaptiveness;
            this.allowNonSeamlessAdaptiveness = allowVideoNonSeamlessAdaptiveness;
            this.selectionOverrides = selectionOverrides;
            this.rendererDisabledFlags = rendererDisabledFlags;
        }

        Parameters(Parcel in) {
            super(in);
            this.maxVideoWidth = in.readInt();
            this.maxVideoHeight = in.readInt();
            this.maxVideoFrameRate = in.readInt();
            this.maxVideoBitrate = in.readInt();
            this.exceedVideoConstraintsIfNecessary = Util.readBoolean(in);
            boolean readBoolean = Util.readBoolean(in);
            this.allowVideoMixedMimeTypeAdaptiveness = readBoolean;
            boolean readBoolean2 = Util.readBoolean(in);
            this.allowVideoNonSeamlessAdaptiveness = readBoolean2;
            this.viewportWidth = in.readInt();
            this.viewportHeight = in.readInt();
            this.viewportOrientationMayChange = Util.readBoolean(in);
            this.maxAudioChannelCount = in.readInt();
            this.maxAudioBitrate = in.readInt();
            this.exceedAudioConstraintsIfNecessary = Util.readBoolean(in);
            this.allowAudioMixedMimeTypeAdaptiveness = Util.readBoolean(in);
            this.allowAudioMixedSampleRateAdaptiveness = Util.readBoolean(in);
            this.allowAudioMixedChannelCountAdaptiveness = Util.readBoolean(in);
            this.forceLowestBitrate = Util.readBoolean(in);
            this.forceHighestSupportedBitrate = Util.readBoolean(in);
            this.exceedRendererCapabilitiesIfNecessary = Util.readBoolean(in);
            this.tunnelingAudioSessionId = in.readInt();
            this.selectionOverrides = readSelectionOverrides(in);
            this.rendererDisabledFlags = (SparseBooleanArray) Util.castNonNull(in.readSparseBooleanArray());
            this.allowMixedMimeAdaptiveness = readBoolean;
            this.allowNonSeamlessAdaptiveness = readBoolean2;
        }

        public final boolean getRendererDisabled(int rendererIndex) {
            return this.rendererDisabledFlags.get(rendererIndex);
        }

        public final boolean hasSelectionOverride(int rendererIndex, TrackGroupArray groups) {
            Map<TrackGroupArray, SelectionOverride> overrides = this.selectionOverrides.get(rendererIndex);
            return overrides != null && overrides.containsKey(groups);
        }

        public final SelectionOverride getSelectionOverride(int rendererIndex, TrackGroupArray groups) {
            Map<TrackGroupArray, SelectionOverride> overrides = this.selectionOverrides.get(rendererIndex);
            if (overrides != null) {
                return overrides.get(groups);
            }
            return null;
        }

        @Override // com.google.android.exoplayer2.trackselection.TrackSelectionParameters
        public ParametersBuilder buildUpon() {
            return new ParametersBuilder(this);
        }

        @Override // com.google.android.exoplayer2.trackselection.TrackSelectionParameters
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            Parameters other = (Parameters) obj;
            return super.equals(obj) && this.maxVideoWidth == other.maxVideoWidth && this.maxVideoHeight == other.maxVideoHeight && this.maxVideoFrameRate == other.maxVideoFrameRate && this.maxVideoBitrate == other.maxVideoBitrate && this.exceedVideoConstraintsIfNecessary == other.exceedVideoConstraintsIfNecessary && this.allowVideoMixedMimeTypeAdaptiveness == other.allowVideoMixedMimeTypeAdaptiveness && this.allowVideoNonSeamlessAdaptiveness == other.allowVideoNonSeamlessAdaptiveness && this.viewportOrientationMayChange == other.viewportOrientationMayChange && this.viewportWidth == other.viewportWidth && this.viewportHeight == other.viewportHeight && this.maxAudioChannelCount == other.maxAudioChannelCount && this.maxAudioBitrate == other.maxAudioBitrate && this.exceedAudioConstraintsIfNecessary == other.exceedAudioConstraintsIfNecessary && this.allowAudioMixedMimeTypeAdaptiveness == other.allowAudioMixedMimeTypeAdaptiveness && this.allowAudioMixedSampleRateAdaptiveness == other.allowAudioMixedSampleRateAdaptiveness && this.allowAudioMixedChannelCountAdaptiveness == other.allowAudioMixedChannelCountAdaptiveness && this.forceLowestBitrate == other.forceLowestBitrate && this.forceHighestSupportedBitrate == other.forceHighestSupportedBitrate && this.exceedRendererCapabilitiesIfNecessary == other.exceedRendererCapabilitiesIfNecessary && this.tunnelingAudioSessionId == other.tunnelingAudioSessionId && areRendererDisabledFlagsEqual(this.rendererDisabledFlags, other.rendererDisabledFlags) && areSelectionOverridesEqual(this.selectionOverrides, other.selectionOverrides);
        }

        @Override // com.google.android.exoplayer2.trackselection.TrackSelectionParameters
        public int hashCode() {
            int result = super.hashCode();
            return (((((((((((((((((((((((((((((((((((((((result * 31) + this.maxVideoWidth) * 31) + this.maxVideoHeight) * 31) + this.maxVideoFrameRate) * 31) + this.maxVideoBitrate) * 31) + (this.exceedVideoConstraintsIfNecessary ? 1 : 0)) * 31) + (this.allowVideoMixedMimeTypeAdaptiveness ? 1 : 0)) * 31) + (this.allowVideoNonSeamlessAdaptiveness ? 1 : 0)) * 31) + (this.viewportOrientationMayChange ? 1 : 0)) * 31) + this.viewportWidth) * 31) + this.viewportHeight) * 31) + this.maxAudioChannelCount) * 31) + this.maxAudioBitrate) * 31) + (this.exceedAudioConstraintsIfNecessary ? 1 : 0)) * 31) + (this.allowAudioMixedMimeTypeAdaptiveness ? 1 : 0)) * 31) + (this.allowAudioMixedSampleRateAdaptiveness ? 1 : 0)) * 31) + (this.allowAudioMixedChannelCountAdaptiveness ? 1 : 0)) * 31) + (this.forceLowestBitrate ? 1 : 0)) * 31) + (this.forceHighestSupportedBitrate ? 1 : 0)) * 31) + (this.exceedRendererCapabilitiesIfNecessary ? 1 : 0)) * 31) + this.tunnelingAudioSessionId;
        }

        @Override // com.google.android.exoplayer2.trackselection.TrackSelectionParameters, android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // com.google.android.exoplayer2.trackselection.TrackSelectionParameters, android.os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(this.maxVideoWidth);
            dest.writeInt(this.maxVideoHeight);
            dest.writeInt(this.maxVideoFrameRate);
            dest.writeInt(this.maxVideoBitrate);
            Util.writeBoolean(dest, this.exceedVideoConstraintsIfNecessary);
            Util.writeBoolean(dest, this.allowVideoMixedMimeTypeAdaptiveness);
            Util.writeBoolean(dest, this.allowVideoNonSeamlessAdaptiveness);
            dest.writeInt(this.viewportWidth);
            dest.writeInt(this.viewportHeight);
            Util.writeBoolean(dest, this.viewportOrientationMayChange);
            dest.writeInt(this.maxAudioChannelCount);
            dest.writeInt(this.maxAudioBitrate);
            Util.writeBoolean(dest, this.exceedAudioConstraintsIfNecessary);
            Util.writeBoolean(dest, this.allowAudioMixedMimeTypeAdaptiveness);
            Util.writeBoolean(dest, this.allowAudioMixedSampleRateAdaptiveness);
            Util.writeBoolean(dest, this.allowAudioMixedChannelCountAdaptiveness);
            Util.writeBoolean(dest, this.forceLowestBitrate);
            Util.writeBoolean(dest, this.forceHighestSupportedBitrate);
            Util.writeBoolean(dest, this.exceedRendererCapabilitiesIfNecessary);
            dest.writeInt(this.tunnelingAudioSessionId);
            writeSelectionOverridesToParcel(dest, this.selectionOverrides);
            dest.writeSparseBooleanArray(this.rendererDisabledFlags);
        }

        private static SparseArray<Map<TrackGroupArray, SelectionOverride>> readSelectionOverrides(Parcel in) {
            int renderersWithOverridesCount = in.readInt();
            SparseArray<Map<TrackGroupArray, SelectionOverride>> selectionOverrides = new SparseArray<>(renderersWithOverridesCount);
            for (int i = 0; i < renderersWithOverridesCount; i++) {
                int rendererIndex = in.readInt();
                int overrideCount = in.readInt();
                Map<TrackGroupArray, SelectionOverride> overrides = new HashMap<>(overrideCount);
                for (int j = 0; j < overrideCount; j++) {
                    TrackGroupArray trackGroups = (TrackGroupArray) Assertions.checkNotNull((TrackGroupArray) in.readParcelable(TrackGroupArray.class.getClassLoader()));
                    SelectionOverride override = (SelectionOverride) in.readParcelable(SelectionOverride.class.getClassLoader());
                    overrides.put(trackGroups, override);
                }
                selectionOverrides.put(rendererIndex, overrides);
            }
            return selectionOverrides;
        }

        private static void writeSelectionOverridesToParcel(Parcel dest, SparseArray<Map<TrackGroupArray, SelectionOverride>> selectionOverrides) {
            int renderersWithOverridesCount = selectionOverrides.size();
            dest.writeInt(renderersWithOverridesCount);
            for (int i = 0; i < renderersWithOverridesCount; i++) {
                int rendererIndex = selectionOverrides.keyAt(i);
                Map<TrackGroupArray, SelectionOverride> overrides = selectionOverrides.valueAt(i);
                int overrideCount = overrides.size();
                dest.writeInt(rendererIndex);
                dest.writeInt(overrideCount);
                for (Map.Entry<TrackGroupArray, SelectionOverride> override : overrides.entrySet()) {
                    dest.writeParcelable(override.getKey(), 0);
                    dest.writeParcelable(override.getValue(), 0);
                }
            }
        }

        private static boolean areRendererDisabledFlagsEqual(SparseBooleanArray first, SparseBooleanArray second) {
            int firstSize = first.size();
            if (second.size() != firstSize) {
                return false;
            }
            for (int indexInFirst = 0; indexInFirst < firstSize; indexInFirst++) {
                if (second.indexOfKey(first.keyAt(indexInFirst)) < 0) {
                    return false;
                }
            }
            return true;
        }

        private static boolean areSelectionOverridesEqual(SparseArray<Map<TrackGroupArray, SelectionOverride>> first, SparseArray<Map<TrackGroupArray, SelectionOverride>> second) {
            int firstSize = first.size();
            if (second.size() != firstSize) {
                return false;
            }
            for (int indexInFirst = 0; indexInFirst < firstSize; indexInFirst++) {
                int indexInSecond = second.indexOfKey(first.keyAt(indexInFirst));
                if (indexInSecond < 0 || !areSelectionOverridesEqual(first.valueAt(indexInFirst), second.valueAt(indexInSecond))) {
                    return false;
                }
            }
            return true;
        }

        /* JADX WARN: Removed duplicated region for block: B:8:0x001a  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        private static boolean areSelectionOverridesEqual(java.util.Map<com.google.android.exoplayer2.source.TrackGroupArray, com.google.android.exoplayer2.trackselection.DefaultTrackSelector.SelectionOverride> r7, java.util.Map<com.google.android.exoplayer2.source.TrackGroupArray, com.google.android.exoplayer2.trackselection.DefaultTrackSelector.SelectionOverride> r8) {
            /*
                int r0 = r7.size()
                int r1 = r8.size()
                r2 = 0
                if (r1 == r0) goto Lc
                return r2
            Lc:
                java.util.Set r1 = r7.entrySet()
                java.util.Iterator r1 = r1.iterator()
            L14:
                boolean r3 = r1.hasNext()
                if (r3 == 0) goto L3d
                java.lang.Object r3 = r1.next()
                java.util.Map$Entry r3 = (java.util.Map.Entry) r3
                java.lang.Object r4 = r3.getKey()
                com.google.android.exoplayer2.source.TrackGroupArray r4 = (com.google.android.exoplayer2.source.TrackGroupArray) r4
                boolean r5 = r8.containsKey(r4)
                if (r5 == 0) goto L3c
                java.lang.Object r5 = r3.getValue()
                java.lang.Object r6 = r8.get(r4)
                boolean r5 = com.google.android.exoplayer2.util.Util.areEqual(r5, r6)
                if (r5 != 0) goto L3b
                goto L3c
            L3b:
                goto L14
            L3c:
                return r2
            L3d:
                r1 = 1
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.trackselection.DefaultTrackSelector.Parameters.areSelectionOverridesEqual(java.util.Map, java.util.Map):boolean");
        }
    }

    /* loaded from: classes3.dex */
    public static final class SelectionOverride implements Parcelable {
        public static final Parcelable.Creator<SelectionOverride> CREATOR = new Parcelable.Creator<SelectionOverride>() { // from class: com.google.android.exoplayer2.trackselection.DefaultTrackSelector.SelectionOverride.1
            @Override // android.os.Parcelable.Creator
            public SelectionOverride createFromParcel(Parcel in) {
                return new SelectionOverride(in);
            }

            @Override // android.os.Parcelable.Creator
            public SelectionOverride[] newArray(int size) {
                return new SelectionOverride[size];
            }
        };
        public final int data;
        public final int groupIndex;
        public final int length;
        public final int reason;
        public final int[] tracks;

        public SelectionOverride(int groupIndex, int... tracks) {
            this(groupIndex, tracks, 2, 0);
        }

        public SelectionOverride(int groupIndex, int[] tracks, int reason, int data) {
            this.groupIndex = groupIndex;
            int[] copyOf = Arrays.copyOf(tracks, tracks.length);
            this.tracks = copyOf;
            this.length = tracks.length;
            this.reason = reason;
            this.data = data;
            Arrays.sort(copyOf);
        }

        SelectionOverride(Parcel in) {
            this.groupIndex = in.readInt();
            int readByte = in.readByte();
            this.length = readByte;
            int[] iArr = new int[readByte];
            this.tracks = iArr;
            in.readIntArray(iArr);
            this.reason = in.readInt();
            this.data = in.readInt();
        }

        public boolean containsTrack(int track) {
            int[] iArr;
            for (int overrideTrack : this.tracks) {
                if (overrideTrack == track) {
                    return true;
                }
            }
            return false;
        }

        public int hashCode() {
            int hash = (this.groupIndex * 31) + Arrays.hashCode(this.tracks);
            return (((hash * 31) + this.reason) * 31) + this.data;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            SelectionOverride other = (SelectionOverride) obj;
            return this.groupIndex == other.groupIndex && Arrays.equals(this.tracks, other.tracks) && this.reason == other.reason && this.data == other.data;
        }

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.groupIndex);
            dest.writeInt(this.tracks.length);
            dest.writeIntArray(this.tracks);
            dest.writeInt(this.reason);
            dest.writeInt(this.data);
        }
    }

    @Deprecated
    public DefaultTrackSelector() {
        this(new AdaptiveTrackSelection.Factory());
    }

    @Deprecated
    public DefaultTrackSelector(BandwidthMeter bandwidthMeter) {
        this(new AdaptiveTrackSelection.Factory(bandwidthMeter));
    }

    @Deprecated
    public DefaultTrackSelector(TrackSelection.Factory trackSelectionFactory) {
        this(Parameters.DEFAULT_WITHOUT_CONTEXT, trackSelectionFactory);
    }

    public DefaultTrackSelector(Context context) {
        this(context, new AdaptiveTrackSelection.Factory());
    }

    public DefaultTrackSelector(Context context, TrackSelection.Factory trackSelectionFactory) {
        this(Parameters.getDefaults(context), trackSelectionFactory);
    }

    public DefaultTrackSelector(Parameters parameters, TrackSelection.Factory trackSelectionFactory) {
        this.trackSelectionFactory = trackSelectionFactory;
        this.parametersReference = new AtomicReference<>(parameters);
    }

    public void setParameters(Parameters parameters) {
        Assertions.checkNotNull(parameters);
        if (!this.parametersReference.getAndSet(parameters).equals(parameters)) {
            invalidate();
        }
    }

    public void setParameters(ParametersBuilder parametersBuilder) {
        setParameters(parametersBuilder.build());
    }

    public Parameters getParameters() {
        return this.parametersReference.get();
    }

    public ParametersBuilder buildUponParameters() {
        return getParameters().buildUpon();
    }

    @Deprecated
    public final void setRendererDisabled(int rendererIndex, boolean disabled) {
        setParameters(buildUponParameters().setRendererDisabled(rendererIndex, disabled));
    }

    @Deprecated
    public final boolean getRendererDisabled(int rendererIndex) {
        return getParameters().getRendererDisabled(rendererIndex);
    }

    @Deprecated
    public final void setSelectionOverride(int rendererIndex, TrackGroupArray groups, SelectionOverride override) {
        setParameters(buildUponParameters().setSelectionOverride(rendererIndex, groups, override));
    }

    @Deprecated
    public final boolean hasSelectionOverride(int rendererIndex, TrackGroupArray groups) {
        return getParameters().hasSelectionOverride(rendererIndex, groups);
    }

    @Deprecated
    public final SelectionOverride getSelectionOverride(int rendererIndex, TrackGroupArray groups) {
        return getParameters().getSelectionOverride(rendererIndex, groups);
    }

    @Deprecated
    public final void clearSelectionOverride(int rendererIndex, TrackGroupArray groups) {
        setParameters(buildUponParameters().clearSelectionOverride(rendererIndex, groups));
    }

    @Deprecated
    public final void clearSelectionOverrides(int rendererIndex) {
        setParameters(buildUponParameters().clearSelectionOverrides(rendererIndex));
    }

    @Deprecated
    public final void clearSelectionOverrides() {
        setParameters(buildUponParameters().clearSelectionOverrides());
    }

    @Deprecated
    public void setTunnelingAudioSessionId(int tunnelingAudioSessionId) {
        setParameters(buildUponParameters().setTunnelingAudioSessionId(tunnelingAudioSessionId));
    }

    public void experimental_allowMultipleAdaptiveSelections() {
        this.allowMultipleAdaptiveSelections = true;
    }

    @Override // com.google.android.exoplayer2.trackselection.MappingTrackSelector
    protected final Pair<RendererConfiguration[], TrackSelection[]> selectTracks(MappingTrackSelector.MappedTrackInfo mappedTrackInfo, int[][][] rendererFormatSupports, int[] rendererMixedMimeTypeAdaptationSupports) throws ExoPlaybackException {
        Parameters params = this.parametersReference.get();
        int rendererCount = mappedTrackInfo.getRendererCount();
        TrackSelection.Definition[] definitions = selectAllTracks(mappedTrackInfo, rendererFormatSupports, rendererMixedMimeTypeAdaptationSupports, params);
        int i = 0;
        while (true) {
            TrackSelection.Definition definition = null;
            if (i >= rendererCount) {
                break;
            }
            if (params.getRendererDisabled(i)) {
                definitions[i] = null;
            } else {
                TrackGroupArray rendererTrackGroups = mappedTrackInfo.getTrackGroups(i);
                if (params.hasSelectionOverride(i, rendererTrackGroups)) {
                    SelectionOverride override = params.getSelectionOverride(i, rendererTrackGroups);
                    if (override != null) {
                        definition = new TrackSelection.Definition(rendererTrackGroups.get(override.groupIndex), override.tracks, override.reason, Integer.valueOf(override.data));
                    }
                    definitions[i] = definition;
                }
            }
            i++;
        }
        TrackSelection[] rendererTrackSelections = this.trackSelectionFactory.createTrackSelections(definitions, getBandwidthMeter());
        RendererConfiguration[] rendererConfigurations = new RendererConfiguration[rendererCount];
        for (int i2 = 0; i2 < rendererCount; i2++) {
            boolean forceRendererDisabled = params.getRendererDisabled(i2);
            boolean rendererEnabled = !forceRendererDisabled && (mappedTrackInfo.getRendererType(i2) == 6 || rendererTrackSelections[i2] != null);
            rendererConfigurations[i2] = rendererEnabled ? RendererConfiguration.DEFAULT : null;
        }
        maybeConfigureRenderersForTunneling(mappedTrackInfo, rendererFormatSupports, rendererConfigurations, rendererTrackSelections, params.tunnelingAudioSessionId);
        return Pair.create(rendererConfigurations, rendererTrackSelections);
    }

    protected TrackSelection.Definition[] selectAllTracks(MappingTrackSelector.MappedTrackInfo mappedTrackInfo, int[][][] rendererFormatSupports, int[] rendererMixedMimeTypeAdaptationSupports, Parameters params) throws ExoPlaybackException {
        int i;
        String selectedAudioLanguage;
        int i2;
        String selectedAudioLanguage2;
        String selectedAudioLanguage3;
        int selectedAudioRendererIndex;
        int rendererCount = mappedTrackInfo.getRendererCount();
        TrackSelection.Definition[] definitions = new TrackSelection.Definition[rendererCount];
        boolean seenVideoRendererWithMappedTracks = false;
        boolean selectedVideoTracks = false;
        int i3 = 0;
        while (true) {
            i = 1;
            if (i3 >= rendererCount) {
                break;
            }
            if (2 == mappedTrackInfo.getRendererType(i3)) {
                if (!selectedVideoTracks) {
                    definitions[i3] = selectVideoTrack(mappedTrackInfo.getTrackGroups(i3), rendererFormatSupports[i3], rendererMixedMimeTypeAdaptationSupports[i3], params, true);
                    selectedVideoTracks = definitions[i3] != null;
                }
                seenVideoRendererWithMappedTracks |= mappedTrackInfo.getTrackGroups(i3).length > 0;
            }
            i3++;
        }
        AudioTrackScore selectedAudioTrackScore = null;
        String selectedAudioLanguage4 = null;
        int selectedAudioRendererIndex2 = -1;
        int selectedAudioRendererIndex3 = 0;
        while (selectedAudioRendererIndex3 < rendererCount) {
            if (i != mappedTrackInfo.getRendererType(selectedAudioRendererIndex3)) {
                i2 = selectedAudioRendererIndex3;
                selectedAudioRendererIndex = selectedAudioRendererIndex2;
                selectedAudioLanguage3 = selectedAudioLanguage4;
            } else {
                boolean enableAdaptiveTrackSelection = this.allowMultipleAdaptiveSelections || !seenVideoRendererWithMappedTracks;
                i2 = selectedAudioRendererIndex3;
                selectedAudioRendererIndex = selectedAudioRendererIndex2;
                selectedAudioLanguage3 = selectedAudioLanguage4;
                Pair<TrackSelection.Definition, AudioTrackScore> audioSelection = selectAudioTrack(mappedTrackInfo.getTrackGroups(selectedAudioRendererIndex3), rendererFormatSupports[selectedAudioRendererIndex3], rendererMixedMimeTypeAdaptationSupports[selectedAudioRendererIndex3], params, enableAdaptiveTrackSelection);
                if (audioSelection != null && (selectedAudioTrackScore == null || ((AudioTrackScore) audioSelection.second).compareTo(selectedAudioTrackScore) > 0)) {
                    if (selectedAudioRendererIndex != -1) {
                        definitions[selectedAudioRendererIndex] = null;
                    }
                    TrackSelection.Definition definition = (TrackSelection.Definition) audioSelection.first;
                    definitions[i2] = definition;
                    selectedAudioLanguage2 = definition.group.getFormat(definition.tracks[0]).language;
                    AudioTrackScore selectedAudioTrackScore2 = (AudioTrackScore) audioSelection.second;
                    selectedAudioTrackScore = selectedAudioTrackScore2;
                    selectedAudioRendererIndex2 = i2;
                    selectedAudioLanguage4 = selectedAudioLanguage2;
                    i = 1;
                    selectedAudioRendererIndex3 = i2 + 1;
                }
            }
            selectedAudioRendererIndex2 = selectedAudioRendererIndex;
            selectedAudioLanguage2 = selectedAudioLanguage3;
            selectedAudioLanguage4 = selectedAudioLanguage2;
            i = 1;
            selectedAudioRendererIndex3 = i2 + 1;
        }
        String selectedAudioLanguage5 = selectedAudioLanguage4;
        TextTrackScore selectedTextTrackScore = null;
        int selectedTextRendererIndex = -1;
        int i4 = 0;
        while (i4 < rendererCount) {
            int trackType = mappedTrackInfo.getRendererType(i4);
            switch (trackType) {
                case 1:
                case 2:
                    selectedAudioLanguage = selectedAudioLanguage5;
                    break;
                case 3:
                    selectedAudioLanguage = selectedAudioLanguage5;
                    Pair<TrackSelection.Definition, TextTrackScore> textSelection = selectTextTrack(mappedTrackInfo.getTrackGroups(i4), rendererFormatSupports[i4], params, selectedAudioLanguage);
                    if (textSelection != null && (selectedTextTrackScore == null || ((TextTrackScore) textSelection.second).compareTo(selectedTextTrackScore) > 0)) {
                        if (selectedTextRendererIndex != -1) {
                            definitions[selectedTextRendererIndex] = null;
                        }
                        definitions[i4] = (TrackSelection.Definition) textSelection.first;
                        selectedTextTrackScore = (TextTrackScore) textSelection.second;
                        selectedTextRendererIndex = i4;
                        break;
                    }
                    break;
                default:
                    selectedAudioLanguage = selectedAudioLanguage5;
                    definitions[i4] = selectOtherTrack(trackType, mappedTrackInfo.getTrackGroups(i4), rendererFormatSupports[i4], params);
                    break;
            }
            i4++;
            selectedAudioLanguage5 = selectedAudioLanguage;
        }
        return definitions;
    }

    protected TrackSelection.Definition selectVideoTrack(TrackGroupArray groups, int[][] formatSupports, int mixedMimeTypeAdaptationSupports, Parameters params, boolean enableAdaptiveTrackSelection) throws ExoPlaybackException {
        TrackSelection.Definition definition = null;
        if (!params.forceHighestSupportedBitrate && !params.forceLowestBitrate && enableAdaptiveTrackSelection) {
            definition = selectAdaptiveVideoTrack(groups, formatSupports, mixedMimeTypeAdaptationSupports, params);
        }
        if (definition == null) {
            return selectFixedVideoTrack(groups, formatSupports, params);
        }
        return definition;
    }

    private static TrackSelection.Definition selectAdaptiveVideoTrack(TrackGroupArray groups, int[][] formatSupport, int mixedMimeTypeAdaptationSupports, Parameters params) {
        int requiredAdaptiveSupport;
        if (params.allowVideoNonSeamlessAdaptiveness) {
            requiredAdaptiveSupport = 24;
        } else {
            requiredAdaptiveSupport = 16;
        }
        boolean allowMixedMimeTypes = params.allowVideoMixedMimeTypeAdaptiveness && (mixedMimeTypeAdaptationSupports & requiredAdaptiveSupport) != 0;
        for (int i = 0; i < groups.length; i++) {
            TrackGroup group = groups.get(i);
            int[] adaptiveTracks = getAdaptiveVideoTracksForGroup(group, formatSupport[i], allowMixedMimeTypes, requiredAdaptiveSupport, params.maxVideoWidth, params.maxVideoHeight, params.maxVideoFrameRate, params.maxVideoBitrate, params.viewportWidth, params.viewportHeight, params.viewportOrientationMayChange);
            if (adaptiveTracks.length > 0) {
                return new TrackSelection.Definition(group, adaptiveTracks);
            }
        }
        return null;
    }

    private static int[] getAdaptiveVideoTracksForGroup(TrackGroup group, int[] formatSupport, boolean allowMixedMimeTypes, int requiredAdaptiveSupport, int maxVideoWidth, int maxVideoHeight, int maxVideoFrameRate, int maxVideoBitrate, int viewportWidth, int viewportHeight, boolean viewportOrientationMayChange) {
        String selectedMimeType;
        int i;
        int selectedMimeTypeTrackCount;
        if (group.length >= 2) {
            List<Integer> selectedTrackIndices = getViewportFilteredTrackIndices(group, viewportWidth, viewportHeight, viewportOrientationMayChange);
            if (selectedTrackIndices.size() < 2) {
                return NO_TRACKS;
            }
            if (allowMixedMimeTypes) {
                selectedMimeType = null;
            } else {
                HashSet<String> seenMimeTypes = new HashSet<>();
                selectedMimeType = null;
                int selectedMimeTypeTrackCount2 = 0;
                int i2 = 0;
                while (i2 < selectedTrackIndices.size()) {
                    int trackIndex = selectedTrackIndices.get(i2).intValue();
                    String sampleMimeType = group.getFormat(trackIndex).sampleMimeType;
                    if (seenMimeTypes.add(sampleMimeType)) {
                        i = i2;
                        selectedMimeTypeTrackCount = selectedMimeTypeTrackCount2;
                        int countForMimeType = getAdaptiveVideoTrackCountForMimeType(group, formatSupport, requiredAdaptiveSupport, sampleMimeType, maxVideoWidth, maxVideoHeight, maxVideoFrameRate, maxVideoBitrate, selectedTrackIndices);
                        if (countForMimeType > selectedMimeTypeTrackCount) {
                            selectedMimeType = sampleMimeType;
                            selectedMimeTypeTrackCount2 = countForMimeType;
                            i2 = i + 1;
                        }
                    } else {
                        i = i2;
                        selectedMimeTypeTrackCount = selectedMimeTypeTrackCount2;
                    }
                    selectedMimeTypeTrackCount2 = selectedMimeTypeTrackCount;
                    i2 = i + 1;
                }
            }
            filterAdaptiveVideoTrackCountForMimeType(group, formatSupport, requiredAdaptiveSupport, selectedMimeType, maxVideoWidth, maxVideoHeight, maxVideoFrameRate, maxVideoBitrate, selectedTrackIndices);
            return selectedTrackIndices.size() < 2 ? NO_TRACKS : Util.toArray(selectedTrackIndices);
        }
        return NO_TRACKS;
    }

    private static int getAdaptiveVideoTrackCountForMimeType(TrackGroup group, int[] formatSupport, int requiredAdaptiveSupport, String mimeType, int maxVideoWidth, int maxVideoHeight, int maxVideoFrameRate, int maxVideoBitrate, List<Integer> selectedTrackIndices) {
        int adaptiveTrackCount = 0;
        for (int i = 0; i < selectedTrackIndices.size(); i++) {
            int trackIndex = selectedTrackIndices.get(i).intValue();
            if (isSupportedAdaptiveVideoTrack(group.getFormat(trackIndex), mimeType, formatSupport[trackIndex], requiredAdaptiveSupport, maxVideoWidth, maxVideoHeight, maxVideoFrameRate, maxVideoBitrate)) {
                adaptiveTrackCount++;
            }
        }
        return adaptiveTrackCount;
    }

    private static void filterAdaptiveVideoTrackCountForMimeType(TrackGroup group, int[] formatSupport, int requiredAdaptiveSupport, String mimeType, int maxVideoWidth, int maxVideoHeight, int maxVideoFrameRate, int maxVideoBitrate, List<Integer> selectedTrackIndices) {
        for (int i = selectedTrackIndices.size() - 1; i >= 0; i--) {
            int trackIndex = selectedTrackIndices.get(i).intValue();
            if (!isSupportedAdaptiveVideoTrack(group.getFormat(trackIndex), mimeType, formatSupport[trackIndex], requiredAdaptiveSupport, maxVideoWidth, maxVideoHeight, maxVideoFrameRate, maxVideoBitrate)) {
                selectedTrackIndices.remove(i);
            }
        }
    }

    private static boolean isSupportedAdaptiveVideoTrack(Format format, String mimeType, int formatSupport, int requiredAdaptiveSupport, int maxVideoWidth, int maxVideoHeight, int maxVideoFrameRate, int maxVideoBitrate) {
        if ((format.roleFlags & 16384) == 0 && isSupported(formatSupport, false) && (formatSupport & requiredAdaptiveSupport) != 0) {
            if (mimeType != null && !Util.areEqual(format.sampleMimeType, mimeType)) {
                return false;
            }
            if (format.width != -1 && format.width > maxVideoWidth) {
                return false;
            }
            if (format.height != -1 && format.height > maxVideoHeight) {
                return false;
            }
            if (format.frameRate != -1.0f && format.frameRate > maxVideoFrameRate) {
                return false;
            }
            return format.bitrate == -1 || format.bitrate <= maxVideoBitrate;
        }
        return false;
    }

    private static TrackSelection.Definition selectFixedVideoTrack(TrackGroupArray groups, int[][] formatSupports, Parameters params) {
        TrackGroup selectedGroup;
        TrackGroupArray trackGroupArray = groups;
        TrackGroup selectedGroup2 = null;
        int selectedTrackIndex = 0;
        int selectedTrackScore = 0;
        int selectedBitrate = -1;
        int selectedPixelCount = -1;
        int groupIndex = 0;
        while (groupIndex < trackGroupArray.length) {
            TrackGroup trackGroup = trackGroupArray.get(groupIndex);
            List<Integer> selectedTrackIndices = getViewportFilteredTrackIndices(trackGroup, params.viewportWidth, params.viewportHeight, params.viewportOrientationMayChange);
            int[] trackFormatSupport = formatSupports[groupIndex];
            for (int trackIndex = 0; trackIndex < trackGroup.length; trackIndex++) {
                Format format = trackGroup.getFormat(trackIndex);
                if ((format.roleFlags & 16384) != 0) {
                    selectedGroup = selectedGroup2;
                } else if (!isSupported(trackFormatSupport[trackIndex], params.exceedRendererCapabilitiesIfNecessary)) {
                    selectedGroup = selectedGroup2;
                } else {
                    boolean isWithinConstraints = selectedTrackIndices.contains(Integer.valueOf(trackIndex)) && (format.width == -1 || format.width <= params.maxVideoWidth) && ((format.height == -1 || format.height <= params.maxVideoHeight) && ((format.frameRate == -1.0f || format.frameRate <= ((float) params.maxVideoFrameRate)) && (format.bitrate == -1 || format.bitrate <= params.maxVideoBitrate)));
                    if (!isWithinConstraints && !params.exceedVideoConstraintsIfNecessary) {
                        selectedGroup = selectedGroup2;
                    } else {
                        int trackScore = isWithinConstraints ? 2 : 1;
                        boolean isWithinCapabilities = isSupported(trackFormatSupport[trackIndex], false);
                        if (isWithinCapabilities) {
                            trackScore += 1000;
                        }
                        boolean selectTrack = trackScore > selectedTrackScore;
                        if (trackScore == selectedTrackScore) {
                            int bitrateComparison = compareFormatValues(format.bitrate, selectedBitrate);
                            selectedGroup = selectedGroup2;
                            if (params.forceLowestBitrate && bitrateComparison != 0) {
                                selectTrack = bitrateComparison < 0;
                            } else {
                                int formatPixelCount = format.getPixelCount();
                                int bitrateComparison2 = formatPixelCount != selectedPixelCount ? compareFormatValues(formatPixelCount, selectedPixelCount) : compareFormatValues(format.bitrate, selectedBitrate);
                                boolean selectTrack2 = !isWithinCapabilities || !isWithinConstraints ? bitrateComparison2 < 0 : bitrateComparison2 > 0;
                                selectTrack = selectTrack2;
                            }
                        } else {
                            selectedGroup = selectedGroup2;
                        }
                        if (selectTrack) {
                            selectedGroup2 = trackGroup;
                            selectedTrackIndex = trackIndex;
                            selectedTrackScore = trackScore;
                            selectedBitrate = format.bitrate;
                            selectedPixelCount = format.getPixelCount();
                        }
                    }
                }
                selectedGroup2 = selectedGroup;
            }
            groupIndex++;
            trackGroupArray = groups;
        }
        if (selectedGroup2 == null) {
            return null;
        }
        return new TrackSelection.Definition(selectedGroup2, selectedTrackIndex);
    }

    protected Pair<TrackSelection.Definition, AudioTrackScore> selectAudioTrack(TrackGroupArray groups, int[][] formatSupports, int mixedMimeTypeAdaptationSupports, Parameters params, boolean enableAdaptiveTrackSelection) throws ExoPlaybackException {
        int selectedTrackIndex = -1;
        int selectedGroupIndex = -1;
        AudioTrackScore selectedTrackScore = null;
        for (int groupIndex = 0; groupIndex < groups.length; groupIndex++) {
            TrackGroup trackGroup = groups.get(groupIndex);
            int[] trackFormatSupport = formatSupports[groupIndex];
            for (int trackIndex = 0; trackIndex < trackGroup.length; trackIndex++) {
                if (isSupported(trackFormatSupport[trackIndex], params.exceedRendererCapabilitiesIfNecessary)) {
                    Format format = trackGroup.getFormat(trackIndex);
                    AudioTrackScore trackScore = new AudioTrackScore(format, params, trackFormatSupport[trackIndex]);
                    if ((trackScore.isWithinConstraints || params.exceedAudioConstraintsIfNecessary) && (selectedTrackScore == null || trackScore.compareTo(selectedTrackScore) > 0)) {
                        selectedGroupIndex = groupIndex;
                        selectedTrackIndex = trackIndex;
                        selectedTrackScore = trackScore;
                    }
                }
            }
        }
        if (selectedGroupIndex == -1) {
            return null;
        }
        TrackGroup selectedGroup = groups.get(selectedGroupIndex);
        TrackSelection.Definition definition = null;
        if (!params.forceHighestSupportedBitrate && !params.forceLowestBitrate && enableAdaptiveTrackSelection) {
            int[] adaptiveTracks = getAdaptiveAudioTracks(selectedGroup, formatSupports[selectedGroupIndex], params.maxAudioBitrate, params.allowAudioMixedMimeTypeAdaptiveness, params.allowAudioMixedSampleRateAdaptiveness, params.allowAudioMixedChannelCountAdaptiveness);
            if (adaptiveTracks.length > 0) {
                definition = new TrackSelection.Definition(selectedGroup, adaptiveTracks);
            }
        }
        if (definition == null) {
            definition = new TrackSelection.Definition(selectedGroup, selectedTrackIndex);
        }
        return Pair.create(definition, (AudioTrackScore) Assertions.checkNotNull(selectedTrackScore));
    }

    /* JADX WARN: Incorrect condition in loop: B:15:0x004f */
    /* JADX WARN: Incorrect condition in loop: B:4:0x000f */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static int[] getAdaptiveAudioTracks(com.google.android.exoplayer2.source.TrackGroup r15, int[] r16, int r17, boolean r18, boolean r19, boolean r20) {
        /*
            r7 = r15
            r0 = 0
            r1 = 0
            java.util.HashSet r2 = new java.util.HashSet
            r2.<init>()
            r8 = r2
            r2 = 0
            r9 = r0
            r10 = r1
            r11 = r2
        Ld:
            int r0 = r7.length
            if (r11 >= r0) goto L41
            com.google.android.exoplayer2.Format r12 = r15.getFormat(r11)
            com.google.android.exoplayer2.trackselection.DefaultTrackSelector$AudioConfigurationTuple r0 = new com.google.android.exoplayer2.trackselection.DefaultTrackSelector$AudioConfigurationTuple
            int r1 = r12.channelCount
            int r2 = r12.sampleRate
            java.lang.String r3 = r12.sampleMimeType
            r0.<init>(r1, r2, r3)
            r13 = r0
            boolean r0 = r8.add(r13)
            if (r0 == 0) goto L3e
        L28:
            r0 = r15
            r1 = r16
            r2 = r13
            r3 = r17
            r4 = r18
            r5 = r19
            r6 = r20
            int r0 = getAdaptiveAudioTrackCount(r0, r1, r2, r3, r4, r5, r6)
            if (r0 <= r9) goto L3e
            r1 = r13
            r2 = r0
            r10 = r1
            r9 = r2
        L3e:
            int r11 = r11 + 1
            goto Ld
        L41:
            r0 = 1
            if (r9 <= r0) goto L70
            com.google.android.exoplayer2.util.Assertions.checkNotNull(r10)
            int[] r11 = new int[r9]
            r0 = 0
            r1 = 0
            r12 = r0
            r13 = r1
        L4d:
            int r0 = r7.length
            if (r13 >= r0) goto L6f
            com.google.android.exoplayer2.Format r14 = r15.getFormat(r13)
            r1 = r16[r13]
            r0 = r14
            r2 = r10
            r3 = r17
            r4 = r18
            r5 = r19
            r6 = r20
            boolean r0 = isSupportedAdaptiveAudioTrack(r0, r1, r2, r3, r4, r5, r6)
            if (r0 == 0) goto L6c
            int r0 = r12 + 1
            r11[r12] = r13
            r12 = r0
        L6c:
            int r13 = r13 + 1
            goto L4d
        L6f:
            return r11
        L70:
            int[] r0 = com.google.android.exoplayer2.trackselection.DefaultTrackSelector.NO_TRACKS
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.trackselection.DefaultTrackSelector.getAdaptiveAudioTracks(com.google.android.exoplayer2.source.TrackGroup, int[], int, boolean, boolean, boolean):int[]");
    }

    private static int getAdaptiveAudioTrackCount(TrackGroup group, int[] formatSupport, AudioConfigurationTuple configuration, int maxAudioBitrate, boolean allowMixedMimeTypeAdaptiveness, boolean allowMixedSampleRateAdaptiveness, boolean allowAudioMixedChannelCountAdaptiveness) {
        int count = 0;
        for (int i = 0; i < group.length; i++) {
            if (isSupportedAdaptiveAudioTrack(group.getFormat(i), formatSupport[i], configuration, maxAudioBitrate, allowMixedMimeTypeAdaptiveness, allowMixedSampleRateAdaptiveness, allowAudioMixedChannelCountAdaptiveness)) {
                count++;
            }
        }
        return count;
    }

    private static boolean isSupportedAdaptiveAudioTrack(Format format, int formatSupport, AudioConfigurationTuple configuration, int maxAudioBitrate, boolean allowMixedMimeTypeAdaptiveness, boolean allowMixedSampleRateAdaptiveness, boolean allowAudioMixedChannelCountAdaptiveness) {
        if (isSupported(formatSupport, false)) {
            if (format.bitrate != -1 && format.bitrate > maxAudioBitrate) {
                return false;
            }
            if (!allowAudioMixedChannelCountAdaptiveness && (format.channelCount == -1 || format.channelCount != configuration.channelCount)) {
                return false;
            }
            if (!allowMixedMimeTypeAdaptiveness && (format.sampleMimeType == null || !TextUtils.equals(format.sampleMimeType, configuration.mimeType))) {
                return false;
            }
            return allowMixedSampleRateAdaptiveness || (format.sampleRate != -1 && format.sampleRate == configuration.sampleRate);
        }
        return false;
    }

    protected Pair<TrackSelection.Definition, TextTrackScore> selectTextTrack(TrackGroupArray groups, int[][] formatSupport, Parameters params, String selectedAudioLanguage) throws ExoPlaybackException {
        TrackGroup selectedGroup = null;
        int selectedTrackIndex = -1;
        TextTrackScore selectedTrackScore = null;
        for (int groupIndex = 0; groupIndex < groups.length; groupIndex++) {
            TrackGroup trackGroup = groups.get(groupIndex);
            int[] trackFormatSupport = formatSupport[groupIndex];
            for (int trackIndex = 0; trackIndex < trackGroup.length; trackIndex++) {
                if (isSupported(trackFormatSupport[trackIndex], params.exceedRendererCapabilitiesIfNecessary)) {
                    Format format = trackGroup.getFormat(trackIndex);
                    TextTrackScore trackScore = new TextTrackScore(format, params, trackFormatSupport[trackIndex], selectedAudioLanguage);
                    if (trackScore.isWithinConstraints && (selectedTrackScore == null || trackScore.compareTo(selectedTrackScore) > 0)) {
                        selectedGroup = trackGroup;
                        selectedTrackIndex = trackIndex;
                        selectedTrackScore = trackScore;
                    }
                }
            }
        }
        if (selectedGroup == null) {
            return null;
        }
        return Pair.create(new TrackSelection.Definition(selectedGroup, selectedTrackIndex), (TextTrackScore) Assertions.checkNotNull(selectedTrackScore));
    }

    protected TrackSelection.Definition selectOtherTrack(int trackType, TrackGroupArray groups, int[][] formatSupport, Parameters params) throws ExoPlaybackException {
        TrackGroup selectedGroup = null;
        int selectedTrackIndex = 0;
        int selectedTrackScore = 0;
        for (int groupIndex = 0; groupIndex < groups.length; groupIndex++) {
            TrackGroup trackGroup = groups.get(groupIndex);
            int[] trackFormatSupport = formatSupport[groupIndex];
            for (int trackIndex = 0; trackIndex < trackGroup.length; trackIndex++) {
                if (isSupported(trackFormatSupport[trackIndex], params.exceedRendererCapabilitiesIfNecessary)) {
                    Format format = trackGroup.getFormat(trackIndex);
                    boolean isDefault = (format.selectionFlags & 1) != 0;
                    int trackScore = isDefault ? 2 : 1;
                    if (isSupported(trackFormatSupport[trackIndex], false)) {
                        trackScore += 1000;
                    }
                    if (trackScore > selectedTrackScore) {
                        selectedGroup = trackGroup;
                        selectedTrackIndex = trackIndex;
                        selectedTrackScore = trackScore;
                    }
                }
            }
        }
        if (selectedGroup == null) {
            return null;
        }
        return new TrackSelection.Definition(selectedGroup, selectedTrackIndex);
    }

    private static void maybeConfigureRenderersForTunneling(MappingTrackSelector.MappedTrackInfo mappedTrackInfo, int[][][] renderererFormatSupports, RendererConfiguration[] rendererConfigurations, TrackSelection[] trackSelections, int tunnelingAudioSessionId) {
        boolean z;
        if (tunnelingAudioSessionId == 0) {
            return;
        }
        int tunnelingAudioRendererIndex = -1;
        int tunnelingVideoRendererIndex = -1;
        boolean enableTunneling = true;
        int i = 0;
        while (true) {
            z = true;
            if (i >= mappedTrackInfo.getRendererCount()) {
                break;
            }
            int rendererType = mappedTrackInfo.getRendererType(i);
            TrackSelection trackSelection = trackSelections[i];
            if ((rendererType == 1 || rendererType == 2) && trackSelection != null && rendererSupportsTunneling(renderererFormatSupports[i], mappedTrackInfo.getTrackGroups(i), trackSelection)) {
                if (rendererType == 1) {
                    if (tunnelingAudioRendererIndex != -1) {
                        enableTunneling = false;
                        break;
                    }
                    tunnelingAudioRendererIndex = i;
                } else if (tunnelingVideoRendererIndex != -1) {
                    enableTunneling = false;
                    break;
                } else {
                    tunnelingVideoRendererIndex = i;
                }
            }
            i++;
        }
        if (tunnelingAudioRendererIndex == -1 || tunnelingVideoRendererIndex == -1) {
            z = false;
        }
        if (enableTunneling & z) {
            RendererConfiguration tunnelingRendererConfiguration = new RendererConfiguration(tunnelingAudioSessionId);
            rendererConfigurations[tunnelingAudioRendererIndex] = tunnelingRendererConfiguration;
            rendererConfigurations[tunnelingVideoRendererIndex] = tunnelingRendererConfiguration;
        }
    }

    private static boolean rendererSupportsTunneling(int[][] formatSupports, TrackGroupArray trackGroups, TrackSelection selection) {
        if (selection == null) {
            return false;
        }
        int trackGroupIndex = trackGroups.indexOf(selection.getTrackGroup());
        for (int i = 0; i < selection.length(); i++) {
            int trackFormatSupport = formatSupports[trackGroupIndex][selection.getIndexInTrackGroup(i)];
            if (RendererCapabilities.CC.getTunnelingSupport(trackFormatSupport) != 32) {
                return false;
            }
        }
        return true;
    }

    public static int compareFormatValues(int first, int second) {
        if (first == -1) {
            return second == -1 ? 0 : -1;
        } else if (second != -1) {
            return first - second;
        } else {
            return 1;
        }
    }

    protected static boolean isSupported(int formatSupport, boolean allowExceedsCapabilities) {
        int maskedSupport = RendererCapabilities.CC.getFormatSupport(formatSupport);
        return maskedSupport == 4 || (allowExceedsCapabilities && maskedSupport == 3);
    }

    protected static String normalizeUndeterminedLanguageToNull(String language) {
        if (TextUtils.isEmpty(language) || TextUtils.equals(language, "und")) {
            return null;
        }
        return language;
    }

    protected static int getFormatLanguageScore(Format format, String language, boolean allowUndeterminedFormatLanguage) {
        if (!TextUtils.isEmpty(language) && language.equals(format.language)) {
            return 4;
        }
        String language2 = normalizeUndeterminedLanguageToNull(language);
        String formatLanguage = normalizeUndeterminedLanguageToNull(format.language);
        if (formatLanguage == null || language2 == null) {
            return (!allowUndeterminedFormatLanguage || formatLanguage != null) ? 0 : 1;
        } else if (!formatLanguage.startsWith(language2) && !language2.startsWith(formatLanguage)) {
            String formatMainLanguage = Util.splitAtFirst(formatLanguage, "-")[0];
            String queryMainLanguage = Util.splitAtFirst(language2, "-")[0];
            if (!formatMainLanguage.equals(queryMainLanguage)) {
                return 0;
            }
            return 2;
        } else {
            return 3;
        }
    }

    private static List<Integer> getViewportFilteredTrackIndices(TrackGroup group, int viewportWidth, int viewportHeight, boolean orientationMayChange) {
        ArrayList<Integer> selectedTrackIndices = new ArrayList<>(group.length);
        for (int i = 0; i < group.length; i++) {
            selectedTrackIndices.add(Integer.valueOf(i));
        }
        if (viewportWidth == Integer.MAX_VALUE || viewportHeight == Integer.MAX_VALUE) {
            return selectedTrackIndices;
        }
        int maxVideoPixelsToRetain = Integer.MAX_VALUE;
        for (int i2 = 0; i2 < group.length; i2++) {
            Format format = group.getFormat(i2);
            if (format.width > 0 && format.height > 0) {
                Point maxVideoSizeInViewport = getMaxVideoSizeInViewport(orientationMayChange, viewportWidth, viewportHeight, format.width, format.height);
                int videoPixels = format.width * format.height;
                if (format.width >= ((int) (maxVideoSizeInViewport.x * FRACTION_TO_CONSIDER_FULLSCREEN)) && format.height >= ((int) (maxVideoSizeInViewport.y * FRACTION_TO_CONSIDER_FULLSCREEN)) && videoPixels < maxVideoPixelsToRetain) {
                    maxVideoPixelsToRetain = videoPixels;
                }
            }
        }
        if (maxVideoPixelsToRetain != Integer.MAX_VALUE) {
            for (int i3 = selectedTrackIndices.size() - 1; i3 >= 0; i3--) {
                int pixelCount = group.getFormat(selectedTrackIndices.get(i3).intValue()).getPixelCount();
                if (pixelCount == -1 || pixelCount > maxVideoPixelsToRetain) {
                    selectedTrackIndices.remove(i3);
                }
            }
        }
        return selectedTrackIndices;
    }

    private static Point getMaxVideoSizeInViewport(boolean orientationMayChange, int viewportWidth, int viewportHeight, int videoWidth, int videoHeight) {
        if (orientationMayChange) {
            boolean z = true;
            boolean z2 = videoWidth > videoHeight;
            if (viewportWidth <= viewportHeight) {
                z = false;
            }
            if (z2 != z) {
                viewportWidth = viewportHeight;
                viewportHeight = viewportWidth;
            }
        }
        int tempViewportWidth = videoWidth * viewportHeight;
        if (tempViewportWidth >= videoHeight * viewportWidth) {
            return new Point(viewportWidth, Util.ceilDivide(viewportWidth * videoHeight, videoWidth));
        }
        return new Point(Util.ceilDivide(viewportHeight * videoWidth, videoHeight), viewportHeight);
    }

    public static int compareInts(int first, int second) {
        if (first > second) {
            return 1;
        }
        return second > first ? -1 : 0;
    }

    /* loaded from: classes3.dex */
    public static final class AudioTrackScore implements Comparable<AudioTrackScore> {
        private final int bitrate;
        private final int channelCount;
        private final boolean isDefaultSelectionFlag;
        public final boolean isWithinConstraints;
        private final boolean isWithinRendererCapabilities;
        private final String language;
        private final int localeLanguageMatchIndex;
        private final int localeLanguageScore;
        private final Parameters parameters;
        private final int preferredLanguageScore;
        private final int sampleRate;

        public AudioTrackScore(Format format, Parameters parameters, int formatSupport) {
            this.parameters = parameters;
            this.language = DefaultTrackSelector.normalizeUndeterminedLanguageToNull(format.language);
            this.isWithinRendererCapabilities = DefaultTrackSelector.isSupported(formatSupport, false);
            this.preferredLanguageScore = DefaultTrackSelector.getFormatLanguageScore(format, parameters.preferredAudioLanguage, false);
            boolean z = true;
            this.isDefaultSelectionFlag = (format.selectionFlags & 1) != 0;
            this.channelCount = format.channelCount;
            this.sampleRate = format.sampleRate;
            this.bitrate = format.bitrate;
            if ((format.bitrate != -1 && format.bitrate > parameters.maxAudioBitrate) || (format.channelCount != -1 && format.channelCount > parameters.maxAudioChannelCount)) {
                z = false;
            }
            this.isWithinConstraints = z;
            String[] localeLanguages = Util.getSystemLanguageCodes();
            int bestMatchIndex = Integer.MAX_VALUE;
            int bestMatchScore = 0;
            int i = 0;
            while (true) {
                if (i >= localeLanguages.length) {
                    break;
                }
                int score = DefaultTrackSelector.getFormatLanguageScore(format, localeLanguages[i], false);
                if (score <= 0) {
                    i++;
                } else {
                    bestMatchIndex = i;
                    bestMatchScore = score;
                    break;
                }
            }
            this.localeLanguageMatchIndex = bestMatchIndex;
            this.localeLanguageScore = bestMatchScore;
        }

        public int compareTo(AudioTrackScore other) {
            int bitrateComparison;
            boolean z = this.isWithinRendererCapabilities;
            int i = 1;
            if (z != other.isWithinRendererCapabilities) {
                return z ? 1 : -1;
            }
            int i2 = this.preferredLanguageScore;
            int i3 = other.preferredLanguageScore;
            if (i2 != i3) {
                return DefaultTrackSelector.compareInts(i2, i3);
            }
            boolean z2 = this.isWithinConstraints;
            if (z2 != other.isWithinConstraints) {
                return z2 ? 1 : -1;
            } else if (this.parameters.forceLowestBitrate && (bitrateComparison = DefaultTrackSelector.compareFormatValues(this.bitrate, other.bitrate)) != 0) {
                return bitrateComparison > 0 ? -1 : 1;
            } else {
                boolean z3 = this.isDefaultSelectionFlag;
                if (z3 != other.isDefaultSelectionFlag) {
                    return z3 ? 1 : -1;
                }
                int i4 = this.localeLanguageMatchIndex;
                int i5 = other.localeLanguageMatchIndex;
                if (i4 != i5) {
                    return -DefaultTrackSelector.compareInts(i4, i5);
                }
                int i6 = this.localeLanguageScore;
                int i7 = other.localeLanguageScore;
                if (i6 != i7) {
                    return DefaultTrackSelector.compareInts(i6, i7);
                }
                if (!this.isWithinConstraints || !this.isWithinRendererCapabilities) {
                    i = -1;
                }
                int resultSign = i;
                int i8 = this.channelCount;
                int i9 = other.channelCount;
                if (i8 != i9) {
                    return DefaultTrackSelector.compareInts(i8, i9) * resultSign;
                }
                int i10 = this.sampleRate;
                int i11 = other.sampleRate;
                if (i10 != i11) {
                    return DefaultTrackSelector.compareInts(i10, i11) * resultSign;
                }
                if (Util.areEqual(this.language, other.language)) {
                    return DefaultTrackSelector.compareInts(this.bitrate, other.bitrate) * resultSign;
                }
                return 0;
            }
        }
    }

    /* loaded from: classes3.dex */
    public static final class AudioConfigurationTuple {
        public final int channelCount;
        public final String mimeType;
        public final int sampleRate;

        public AudioConfigurationTuple(int channelCount, int sampleRate, String mimeType) {
            this.channelCount = channelCount;
            this.sampleRate = sampleRate;
            this.mimeType = mimeType;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            AudioConfigurationTuple other = (AudioConfigurationTuple) obj;
            return this.channelCount == other.channelCount && this.sampleRate == other.sampleRate && TextUtils.equals(this.mimeType, other.mimeType);
        }

        public int hashCode() {
            int result = this.channelCount;
            int result2 = ((result * 31) + this.sampleRate) * 31;
            String str = this.mimeType;
            return result2 + (str != null ? str.hashCode() : 0);
        }
    }

    /* loaded from: classes3.dex */
    public static final class TextTrackScore implements Comparable<TextTrackScore> {
        private final boolean hasCaptionRoleFlags;
        private final boolean hasPreferredIsForcedFlag;
        private final boolean isDefault;
        public final boolean isWithinConstraints;
        private final boolean isWithinRendererCapabilities;
        private final int preferredLanguageScore;
        private final int preferredRoleFlagsScore;
        private final int selectedAudioLanguageScore;

        public TextTrackScore(Format format, Parameters parameters, int trackFormatSupport, String selectedAudioLanguage) {
            boolean z = false;
            this.isWithinRendererCapabilities = DefaultTrackSelector.isSupported(trackFormatSupport, false);
            int maskedSelectionFlags = format.selectionFlags & (parameters.disabledTextTrackSelectionFlags ^ (-1));
            boolean z2 = (maskedSelectionFlags & 1) != 0;
            this.isDefault = z2;
            boolean isForced = (maskedSelectionFlags & 2) != 0;
            int formatLanguageScore = DefaultTrackSelector.getFormatLanguageScore(format, parameters.preferredTextLanguage, parameters.selectUndeterminedTextLanguage);
            this.preferredLanguageScore = formatLanguageScore;
            int bitCount = Integer.bitCount(format.roleFlags & parameters.preferredTextRoleFlags);
            this.preferredRoleFlagsScore = bitCount;
            this.hasCaptionRoleFlags = (format.roleFlags & 1088) != 0;
            this.hasPreferredIsForcedFlag = (formatLanguageScore > 0 && !isForced) || (formatLanguageScore == 0 && isForced);
            boolean selectedAudioLanguageUndetermined = DefaultTrackSelector.normalizeUndeterminedLanguageToNull(selectedAudioLanguage) == null;
            int formatLanguageScore2 = DefaultTrackSelector.getFormatLanguageScore(format, selectedAudioLanguage, selectedAudioLanguageUndetermined);
            this.selectedAudioLanguageScore = formatLanguageScore2;
            if (formatLanguageScore > 0 || ((parameters.preferredTextLanguage == null && bitCount > 0) || z2 || (isForced && formatLanguageScore2 > 0))) {
                z = true;
            }
            this.isWithinConstraints = z;
        }

        public int compareTo(TextTrackScore other) {
            boolean z;
            boolean z2 = this.isWithinRendererCapabilities;
            if (z2 != other.isWithinRendererCapabilities) {
                return z2 ? 1 : -1;
            }
            int i = this.preferredLanguageScore;
            int i2 = other.preferredLanguageScore;
            if (i != i2) {
                return DefaultTrackSelector.compareInts(i, i2);
            }
            int i3 = this.preferredRoleFlagsScore;
            int i4 = other.preferredRoleFlagsScore;
            if (i3 != i4) {
                return DefaultTrackSelector.compareInts(i3, i4);
            }
            boolean z3 = this.isDefault;
            if (z3 != other.isDefault) {
                return z3 ? 1 : -1;
            }
            boolean z4 = this.hasPreferredIsForcedFlag;
            if (z4 != other.hasPreferredIsForcedFlag) {
                return z4 ? 1 : -1;
            }
            int i5 = this.selectedAudioLanguageScore;
            int i6 = other.selectedAudioLanguageScore;
            if (i5 != i6) {
                return DefaultTrackSelector.compareInts(i5, i6);
            }
            if (i3 != 0 || (z = this.hasCaptionRoleFlags) == other.hasCaptionRoleFlags) {
                return 0;
            }
            return z ? -1 : 1;
        }
    }
}
