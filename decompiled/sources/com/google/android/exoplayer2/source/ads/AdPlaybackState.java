package com.google.android.exoplayer2.source.ads;

import android.net.Uri;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
/* loaded from: classes3.dex */
public final class AdPlaybackState {
    public static final int AD_STATE_AVAILABLE = 1;
    public static final int AD_STATE_ERROR = 4;
    public static final int AD_STATE_PLAYED = 3;
    public static final int AD_STATE_SKIPPED = 2;
    public static final int AD_STATE_UNAVAILABLE = 0;
    public static final AdPlaybackState NONE = new AdPlaybackState(new long[0]);
    public final int adGroupCount;
    public final long[] adGroupTimesUs;
    public final AdGroup[] adGroups;
    public final long adResumePositionUs;
    public final long contentDurationUs;

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface AdState {
    }

    /* loaded from: classes3.dex */
    public static final class AdGroup {
        public final int count;
        public final long[] durationsUs;
        public final int[] states;
        public final Uri[] uris;

        public AdGroup() {
            this(-1, new int[0], new Uri[0], new long[0]);
        }

        private AdGroup(int count, int[] states, Uri[] uris, long[] durationsUs) {
            Assertions.checkArgument(states.length == uris.length);
            this.count = count;
            this.states = states;
            this.uris = uris;
            this.durationsUs = durationsUs;
        }

        public int getFirstAdIndexToPlay() {
            return getNextAdIndexToPlay(-1);
        }

        public int getNextAdIndexToPlay(int lastPlayedAdIndex) {
            int nextAdIndexToPlay = lastPlayedAdIndex + 1;
            while (true) {
                int[] iArr = this.states;
                if (nextAdIndexToPlay >= iArr.length || iArr[nextAdIndexToPlay] == 0 || iArr[nextAdIndexToPlay] == 1) {
                    break;
                }
                nextAdIndexToPlay++;
            }
            return nextAdIndexToPlay;
        }

        public boolean hasUnplayedAds() {
            return this.count == -1 || getFirstAdIndexToPlay() < this.count;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            AdGroup adGroup = (AdGroup) o;
            return this.count == adGroup.count && Arrays.equals(this.uris, adGroup.uris) && Arrays.equals(this.states, adGroup.states) && Arrays.equals(this.durationsUs, adGroup.durationsUs);
        }

        public int hashCode() {
            int result = this.count;
            return (((((result * 31) + Arrays.hashCode(this.uris)) * 31) + Arrays.hashCode(this.states)) * 31) + Arrays.hashCode(this.durationsUs);
        }

        public AdGroup withAdCount(int count) {
            int[] states = copyStatesWithSpaceForAdCount(this.states, count);
            long[] durationsUs = copyDurationsUsWithSpaceForAdCount(this.durationsUs, count);
            Uri[] uris = (Uri[]) Arrays.copyOf(this.uris, count);
            return new AdGroup(count, states, uris, durationsUs);
        }

        public AdGroup withAdUri(Uri uri, int index) {
            int[] states = copyStatesWithSpaceForAdCount(this.states, index + 1);
            long[] durationsUs = this.durationsUs;
            if (durationsUs.length != states.length) {
                durationsUs = copyDurationsUsWithSpaceForAdCount(durationsUs, states.length);
            }
            Uri[] uris = (Uri[]) Arrays.copyOf(this.uris, states.length);
            uris[index] = uri;
            states[index] = 1;
            return new AdGroup(this.count, states, uris, durationsUs);
        }

        public AdGroup withAdState(int state, int index) {
            int i = this.count;
            boolean z = false;
            Assertions.checkArgument(i == -1 || index < i);
            int[] states = copyStatesWithSpaceForAdCount(this.states, index + 1);
            if (states[index] == 0 || states[index] == 1 || states[index] == state) {
                z = true;
            }
            Assertions.checkArgument(z);
            long[] durationsUs = this.durationsUs;
            if (durationsUs.length != states.length) {
                durationsUs = copyDurationsUsWithSpaceForAdCount(durationsUs, states.length);
            }
            Uri[] uris = this.uris;
            if (uris.length != states.length) {
                uris = (Uri[]) Arrays.copyOf(uris, states.length);
            }
            states[index] = state;
            return new AdGroup(this.count, states, uris, durationsUs);
        }

        public AdGroup withAdDurationsUs(long[] durationsUs) {
            Assertions.checkArgument(this.count == -1 || durationsUs.length <= this.uris.length);
            int length = durationsUs.length;
            Uri[] uriArr = this.uris;
            if (length < uriArr.length) {
                durationsUs = copyDurationsUsWithSpaceForAdCount(durationsUs, uriArr.length);
            }
            return new AdGroup(this.count, this.states, this.uris, durationsUs);
        }

        public AdGroup withAllAdsSkipped() {
            if (this.count == -1) {
                return new AdGroup(0, new int[0], new Uri[0], new long[0]);
            }
            int[] iArr = this.states;
            int count = iArr.length;
            int[] states = Arrays.copyOf(iArr, count);
            for (int i = 0; i < count; i++) {
                if (states[i] == 1 || states[i] == 0) {
                    states[i] = 2;
                }
            }
            return new AdGroup(count, states, this.uris, this.durationsUs);
        }

        private static int[] copyStatesWithSpaceForAdCount(int[] states, int count) {
            int oldStateCount = states.length;
            int newStateCount = Math.max(count, oldStateCount);
            int[] states2 = Arrays.copyOf(states, newStateCount);
            Arrays.fill(states2, oldStateCount, newStateCount, 0);
            return states2;
        }

        private static long[] copyDurationsUsWithSpaceForAdCount(long[] durationsUs, int count) {
            int oldDurationsUsCount = durationsUs.length;
            int newDurationsUsCount = Math.max(count, oldDurationsUsCount);
            long[] durationsUs2 = Arrays.copyOf(durationsUs, newDurationsUsCount);
            Arrays.fill(durationsUs2, oldDurationsUsCount, newDurationsUsCount, (long) C.TIME_UNSET);
            return durationsUs2;
        }
    }

    public AdPlaybackState(long... adGroupTimesUs) {
        int count = adGroupTimesUs.length;
        this.adGroupCount = count;
        this.adGroupTimesUs = Arrays.copyOf(adGroupTimesUs, count);
        this.adGroups = new AdGroup[count];
        for (int i = 0; i < count; i++) {
            this.adGroups[i] = new AdGroup();
        }
        this.adResumePositionUs = 0L;
        this.contentDurationUs = C.TIME_UNSET;
    }

    private AdPlaybackState(long[] adGroupTimesUs, AdGroup[] adGroups, long adResumePositionUs, long contentDurationUs) {
        this.adGroupCount = adGroups.length;
        this.adGroupTimesUs = adGroupTimesUs;
        this.adGroups = adGroups;
        this.adResumePositionUs = adResumePositionUs;
        this.contentDurationUs = contentDurationUs;
    }

    public int getAdGroupIndexForPositionUs(long positionUs, long periodDurationUs) {
        int index = this.adGroupTimesUs.length - 1;
        while (index >= 0 && isPositionBeforeAdGroup(positionUs, periodDurationUs, index)) {
            index--;
        }
        if (index < 0 || !this.adGroups[index].hasUnplayedAds()) {
            return -1;
        }
        return index;
    }

    public int getAdGroupIndexAfterPositionUs(long positionUs, long periodDurationUs) {
        if (positionUs == Long.MIN_VALUE || (periodDurationUs != C.TIME_UNSET && positionUs >= periodDurationUs)) {
            return -1;
        }
        int index = 0;
        while (true) {
            long[] jArr = this.adGroupTimesUs;
            if (index >= jArr.length || jArr[index] == Long.MIN_VALUE || (positionUs < jArr[index] && this.adGroups[index].hasUnplayedAds())) {
                break;
            }
            index++;
        }
        if (index >= this.adGroupTimesUs.length) {
            return -1;
        }
        return index;
    }

    public boolean isAdInErrorState(int adGroupIndex, int adIndexInAdGroup) {
        AdGroup[] adGroupArr = this.adGroups;
        if (adGroupIndex >= adGroupArr.length) {
            return false;
        }
        AdGroup adGroup = adGroupArr[adGroupIndex];
        return adGroup.count != -1 && adIndexInAdGroup < adGroup.count && adGroup.states[adIndexInAdGroup] == 4;
    }

    public AdPlaybackState withAdCount(int adGroupIndex, int adCount) {
        Assertions.checkArgument(adCount > 0);
        if (this.adGroups[adGroupIndex].count == adCount) {
            return this;
        }
        AdGroup[] adGroupArr = this.adGroups;
        AdGroup[] adGroups = (AdGroup[]) Util.nullSafeArrayCopy(adGroupArr, adGroupArr.length);
        adGroups[adGroupIndex] = this.adGroups[adGroupIndex].withAdCount(adCount);
        return new AdPlaybackState(this.adGroupTimesUs, adGroups, this.adResumePositionUs, this.contentDurationUs);
    }

    public AdPlaybackState withAdUri(int adGroupIndex, int adIndexInAdGroup, Uri uri) {
        AdGroup[] adGroupArr = this.adGroups;
        AdGroup[] adGroups = (AdGroup[]) Util.nullSafeArrayCopy(adGroupArr, adGroupArr.length);
        adGroups[adGroupIndex] = adGroups[adGroupIndex].withAdUri(uri, adIndexInAdGroup);
        return new AdPlaybackState(this.adGroupTimesUs, adGroups, this.adResumePositionUs, this.contentDurationUs);
    }

    public AdPlaybackState withPlayedAd(int adGroupIndex, int adIndexInAdGroup) {
        AdGroup[] adGroupArr = this.adGroups;
        AdGroup[] adGroups = (AdGroup[]) Util.nullSafeArrayCopy(adGroupArr, adGroupArr.length);
        adGroups[adGroupIndex] = adGroups[adGroupIndex].withAdState(3, adIndexInAdGroup);
        return new AdPlaybackState(this.adGroupTimesUs, adGroups, this.adResumePositionUs, this.contentDurationUs);
    }

    public AdPlaybackState withSkippedAd(int adGroupIndex, int adIndexInAdGroup) {
        AdGroup[] adGroupArr = this.adGroups;
        AdGroup[] adGroups = (AdGroup[]) Util.nullSafeArrayCopy(adGroupArr, adGroupArr.length);
        adGroups[adGroupIndex] = adGroups[adGroupIndex].withAdState(2, adIndexInAdGroup);
        return new AdPlaybackState(this.adGroupTimesUs, adGroups, this.adResumePositionUs, this.contentDurationUs);
    }

    public AdPlaybackState withAdLoadError(int adGroupIndex, int adIndexInAdGroup) {
        AdGroup[] adGroupArr = this.adGroups;
        AdGroup[] adGroups = (AdGroup[]) Util.nullSafeArrayCopy(adGroupArr, adGroupArr.length);
        adGroups[adGroupIndex] = adGroups[adGroupIndex].withAdState(4, adIndexInAdGroup);
        return new AdPlaybackState(this.adGroupTimesUs, adGroups, this.adResumePositionUs, this.contentDurationUs);
    }

    public AdPlaybackState withSkippedAdGroup(int adGroupIndex) {
        AdGroup[] adGroupArr = this.adGroups;
        AdGroup[] adGroups = (AdGroup[]) Util.nullSafeArrayCopy(adGroupArr, adGroupArr.length);
        adGroups[adGroupIndex] = adGroups[adGroupIndex].withAllAdsSkipped();
        return new AdPlaybackState(this.adGroupTimesUs, adGroups, this.adResumePositionUs, this.contentDurationUs);
    }

    public AdPlaybackState withAdDurationsUs(long[][] adDurationUs) {
        AdGroup[] adGroupArr = this.adGroups;
        AdGroup[] adGroups = (AdGroup[]) Util.nullSafeArrayCopy(adGroupArr, adGroupArr.length);
        for (int adGroupIndex = 0; adGroupIndex < this.adGroupCount; adGroupIndex++) {
            adGroups[adGroupIndex] = adGroups[adGroupIndex].withAdDurationsUs(adDurationUs[adGroupIndex]);
        }
        return new AdPlaybackState(this.adGroupTimesUs, adGroups, this.adResumePositionUs, this.contentDurationUs);
    }

    public AdPlaybackState withAdResumePositionUs(long adResumePositionUs) {
        if (this.adResumePositionUs == adResumePositionUs) {
            return this;
        }
        return new AdPlaybackState(this.adGroupTimesUs, this.adGroups, adResumePositionUs, this.contentDurationUs);
    }

    public AdPlaybackState withContentDurationUs(long contentDurationUs) {
        if (this.contentDurationUs == contentDurationUs) {
            return this;
        }
        return new AdPlaybackState(this.adGroupTimesUs, this.adGroups, this.adResumePositionUs, contentDurationUs);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AdPlaybackState that = (AdPlaybackState) o;
        return this.adGroupCount == that.adGroupCount && this.adResumePositionUs == that.adResumePositionUs && this.contentDurationUs == that.contentDurationUs && Arrays.equals(this.adGroupTimesUs, that.adGroupTimesUs) && Arrays.equals(this.adGroups, that.adGroups);
    }

    public int hashCode() {
        int result = this.adGroupCount;
        return (((((((result * 31) + ((int) this.adResumePositionUs)) * 31) + ((int) this.contentDurationUs)) * 31) + Arrays.hashCode(this.adGroupTimesUs)) * 31) + Arrays.hashCode(this.adGroups);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("AdPlaybackState(adResumePositionUs=");
        sb.append(this.adResumePositionUs);
        sb.append(", adGroups=[");
        for (int i = 0; i < this.adGroups.length; i++) {
            sb.append("adGroup(timeUs=");
            sb.append(this.adGroupTimesUs[i]);
            sb.append(", ads=[");
            for (int j = 0; j < this.adGroups[i].states.length; j++) {
                sb.append("ad(state=");
                switch (this.adGroups[i].states[j]) {
                    case 0:
                        sb.append('_');
                        break;
                    case 1:
                        sb.append('R');
                        break;
                    case 2:
                        sb.append('S');
                        break;
                    case 3:
                        sb.append('P');
                        break;
                    case 4:
                        sb.append('!');
                        break;
                    default:
                        sb.append('?');
                        break;
                }
                sb.append(", durationUs=");
                sb.append(this.adGroups[i].durationsUs[j]);
                sb.append(')');
                if (j < this.adGroups[i].states.length - 1) {
                    sb.append(", ");
                }
            }
            sb.append("])");
            if (i < this.adGroups.length - 1) {
                sb.append(", ");
            }
        }
        sb.append("])");
        return sb.toString();
    }

    private boolean isPositionBeforeAdGroup(long positionUs, long periodDurationUs, int adGroupIndex) {
        if (positionUs == Long.MIN_VALUE) {
            return false;
        }
        long adGroupPositionUs = this.adGroupTimesUs[adGroupIndex];
        return adGroupPositionUs == Long.MIN_VALUE ? periodDurationUs == C.TIME_UNSET || positionUs < periodDurationUs : positionUs < adGroupPositionUs;
    }
}
