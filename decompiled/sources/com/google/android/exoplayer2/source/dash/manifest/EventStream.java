package com.google.android.exoplayer2.source.dash.manifest;

import com.google.android.exoplayer2.metadata.emsg.EventMessage;
/* loaded from: classes3.dex */
public final class EventStream {
    public final EventMessage[] events;
    public final long[] presentationTimesUs;
    public final String schemeIdUri;
    public final long timescale;
    public final String value;

    public EventStream(String schemeIdUri, String value, long timescale, long[] presentationTimesUs, EventMessage[] events) {
        this.schemeIdUri = schemeIdUri;
        this.value = value;
        this.timescale = timescale;
        this.presentationTimesUs = presentationTimesUs;
        this.events = events;
    }

    public String id() {
        return this.schemeIdUri + "/" + this.value;
    }
}
