package com.google.android.datatransport.runtime.scheduling.persistence;

import com.google.android.datatransport.runtime.EventInternal;
import com.google.android.datatransport.runtime.TransportContext;
/* loaded from: classes3.dex */
public final class AutoValue_PersistedEvent extends PersistedEvent {
    private final EventInternal event;
    private final long id;
    private final TransportContext transportContext;

    public AutoValue_PersistedEvent(long id, TransportContext transportContext, EventInternal event) {
        this.id = id;
        if (transportContext == null) {
            throw new NullPointerException("Null transportContext");
        }
        this.transportContext = transportContext;
        if (event == null) {
            throw new NullPointerException("Null event");
        }
        this.event = event;
    }

    @Override // com.google.android.datatransport.runtime.scheduling.persistence.PersistedEvent
    public long getId() {
        return this.id;
    }

    @Override // com.google.android.datatransport.runtime.scheduling.persistence.PersistedEvent
    public TransportContext getTransportContext() {
        return this.transportContext;
    }

    @Override // com.google.android.datatransport.runtime.scheduling.persistence.PersistedEvent
    public EventInternal getEvent() {
        return this.event;
    }

    public String toString() {
        return "PersistedEvent{id=" + this.id + ", transportContext=" + this.transportContext + ", event=" + this.event + "}";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PersistedEvent)) {
            return false;
        }
        PersistedEvent that = (PersistedEvent) o;
        return this.id == that.getId() && this.transportContext.equals(that.getTransportContext()) && this.event.equals(that.getEvent());
    }

    public int hashCode() {
        int h$ = 1 * 1000003;
        long j = this.id;
        return ((((h$ ^ ((int) (j ^ (j >>> 32)))) * 1000003) ^ this.transportContext.hashCode()) * 1000003) ^ this.event.hashCode();
    }
}
