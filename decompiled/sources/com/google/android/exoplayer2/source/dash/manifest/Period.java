package com.google.android.exoplayer2.source.dash.manifest;

import java.util.Collections;
import java.util.List;
/* loaded from: classes3.dex */
public class Period {
    public final List<AdaptationSet> adaptationSets;
    public final Descriptor assetIdentifier;
    public final List<EventStream> eventStreams;
    public final String id;
    public final long startMs;

    public Period(String id, long startMs, List<AdaptationSet> adaptationSets) {
        this(id, startMs, adaptationSets, Collections.emptyList(), null);
    }

    public Period(String id, long startMs, List<AdaptationSet> adaptationSets, List<EventStream> eventStreams) {
        this(id, startMs, adaptationSets, eventStreams, null);
    }

    public Period(String id, long startMs, List<AdaptationSet> adaptationSets, List<EventStream> eventStreams, Descriptor assetIdentifier) {
        this.id = id;
        this.startMs = startMs;
        this.adaptationSets = Collections.unmodifiableList(adaptationSets);
        this.eventStreams = Collections.unmodifiableList(eventStreams);
        this.assetIdentifier = assetIdentifier;
    }

    public int getAdaptationSetIndex(int type) {
        int adaptationCount = this.adaptationSets.size();
        for (int i = 0; i < adaptationCount; i++) {
            if (this.adaptationSets.get(i).type == type) {
                return i;
            }
        }
        return -1;
    }
}
