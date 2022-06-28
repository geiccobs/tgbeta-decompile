package com.google.android.datatransport;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes3.dex */
public final class AutoValue_Event<T> extends Event<T> {
    private final Integer code;
    private final T payload;
    private final Priority priority;

    public AutoValue_Event(Integer code, T payload, Priority priority) {
        this.code = code;
        if (payload == null) {
            throw new NullPointerException("Null payload");
        }
        this.payload = payload;
        if (priority == null) {
            throw new NullPointerException("Null priority");
        }
        this.priority = priority;
    }

    @Override // com.google.android.datatransport.Event
    public Integer getCode() {
        return this.code;
    }

    @Override // com.google.android.datatransport.Event
    public T getPayload() {
        return this.payload;
    }

    @Override // com.google.android.datatransport.Event
    public Priority getPriority() {
        return this.priority;
    }

    public String toString() {
        return "Event{code=" + this.code + ", payload=" + this.payload + ", priority=" + this.priority + "}";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Event)) {
            return false;
        }
        Event<?> that = (Event) o;
        Integer num = this.code;
        if (num != null ? num.equals(that.getCode()) : that.getCode() == null) {
            if (this.payload.equals(that.getPayload()) && this.priority.equals(that.getPriority())) {
                return true;
            }
        }
        return false;
    }

    public int hashCode() {
        int h$ = 1 * 1000003;
        Integer num = this.code;
        return ((((h$ ^ (num == null ? 0 : num.hashCode())) * 1000003) ^ this.payload.hashCode()) * 1000003) ^ this.priority.hashCode();
    }
}
