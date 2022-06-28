package com.google.android.exoplayer2.trackselection;

import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
/* loaded from: classes3.dex */
public final class TrackSelectionUtil {

    /* loaded from: classes3.dex */
    public interface AdaptiveTrackSelectionFactory {
        TrackSelection createAdaptiveTrackSelection(TrackSelection.Definition definition);
    }

    private TrackSelectionUtil() {
    }

    public static TrackSelection[] createTrackSelectionsForDefinitions(TrackSelection.Definition[] definitions, AdaptiveTrackSelectionFactory adaptiveTrackSelectionFactory) {
        TrackSelection[] selections = new TrackSelection[definitions.length];
        boolean createdAdaptiveTrackSelection = false;
        for (int i = 0; i < definitions.length; i++) {
            TrackSelection.Definition definition = definitions[i];
            if (definition != null) {
                if (definition.tracks.length > 1 && !createdAdaptiveTrackSelection) {
                    createdAdaptiveTrackSelection = true;
                    selections[i] = adaptiveTrackSelectionFactory.createAdaptiveTrackSelection(definition);
                } else {
                    selections[i] = new FixedTrackSelection(definition.group, definition.tracks[0], definition.reason, definition.data);
                }
            }
        }
        return selections;
    }

    public static DefaultTrackSelector.Parameters updateParametersWithOverride(DefaultTrackSelector.Parameters parameters, int rendererIndex, TrackGroupArray trackGroupArray, boolean isDisabled, DefaultTrackSelector.SelectionOverride override) {
        DefaultTrackSelector.ParametersBuilder builder = parameters.buildUpon().clearSelectionOverrides(rendererIndex).setRendererDisabled(rendererIndex, isDisabled);
        if (override != null) {
            builder.setSelectionOverride(rendererIndex, trackGroupArray, override);
        }
        return builder.build();
    }
}
