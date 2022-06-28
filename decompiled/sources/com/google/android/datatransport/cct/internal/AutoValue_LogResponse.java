package com.google.android.datatransport.cct.internal;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes3.dex */
public final class AutoValue_LogResponse extends LogResponse {
    private final long nextRequestWaitMillis;

    public AutoValue_LogResponse(long nextRequestWaitMillis) {
        this.nextRequestWaitMillis = nextRequestWaitMillis;
    }

    @Override // com.google.android.datatransport.cct.internal.LogResponse
    public long getNextRequestWaitMillis() {
        return this.nextRequestWaitMillis;
    }

    public String toString() {
        return "LogResponse{nextRequestWaitMillis=" + this.nextRequestWaitMillis + "}";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof LogResponse)) {
            return false;
        }
        LogResponse that = (LogResponse) o;
        return this.nextRequestWaitMillis == that.getNextRequestWaitMillis();
    }

    public int hashCode() {
        int h$ = 1 * 1000003;
        long j = this.nextRequestWaitMillis;
        return h$ ^ ((int) (j ^ (j >>> 32)));
    }
}
