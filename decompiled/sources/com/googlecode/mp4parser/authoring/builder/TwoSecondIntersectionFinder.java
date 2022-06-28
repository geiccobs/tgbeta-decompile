package com.googlecode.mp4parser.authoring.builder;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import java.util.Arrays;
/* loaded from: classes3.dex */
public class TwoSecondIntersectionFinder implements FragmentIntersectionFinder {
    private int fragmentLength;
    private Movie movie;

    public TwoSecondIntersectionFinder(Movie movie, int fragmentLength) {
        this.fragmentLength = 2;
        this.movie = movie;
        this.fragmentLength = fragmentLength;
    }

    @Override // com.googlecode.mp4parser.authoring.builder.FragmentIntersectionFinder
    public long[] sampleNumbers(Track track) {
        int fragmentCount;
        double trackLength = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
        for (Track thisTrack : this.movie.getTracks()) {
            double thisTracksLength = thisTrack.getDuration() / thisTrack.getTrackMetaData().getTimescale();
            if (trackLength < thisTracksLength) {
                trackLength = thisTracksLength;
            }
        }
        double d = this.fragmentLength;
        Double.isNaN(d);
        int samples = 1;
        int fragmentCount2 = Math.min(((int) Math.ceil(trackLength / d)) - 1, track.getSamples().size());
        if (fragmentCount2 >= 1) {
            fragmentCount = fragmentCount2;
        } else {
            fragmentCount = 1;
        }
        long[] fragments = new long[fragmentCount];
        Arrays.fill(fragments, -1L);
        int i = 0;
        fragments[0] = 1;
        long time = 0;
        int samples2 = 0;
        long[] sampleDurations = track.getSampleDurations();
        int length = sampleDurations.length;
        int i2 = 0;
        while (i2 < length) {
            long delta = sampleDurations[i2];
            int currentFragment = ((int) ((time / track.getTrackMetaData().getTimescale()) / this.fragmentLength)) + samples;
            if (currentFragment >= fragments.length) {
                break;
            }
            fragments[currentFragment] = samples2 + 1;
            time += delta;
            i2++;
            samples2++;
            sampleDurations = sampleDurations;
            samples = 1;
            i = 0;
        }
        long last = samples2 + 1;
        for (int i3 = fragments.length - samples; i3 >= 0; i3--) {
            if (fragments[i3] == -1) {
                fragments[i3] = last;
            }
            last = fragments[i3];
        }
        long[] cleanedFragments = new long[i];
        int length2 = fragments.length;
        while (i < length2) {
            long fragment = fragments[i];
            if (cleanedFragments.length == 0 || cleanedFragments[cleanedFragments.length - 1] != fragment) {
                long[] cleanedFragments2 = Arrays.copyOf(cleanedFragments, cleanedFragments.length + 1);
                cleanedFragments2[cleanedFragments2.length - 1] = fragment;
                cleanedFragments = cleanedFragments2;
            }
            i++;
        }
        return cleanedFragments;
    }
}
