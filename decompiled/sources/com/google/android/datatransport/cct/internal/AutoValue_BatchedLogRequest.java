package com.google.android.datatransport.cct.internal;

import com.google.firebase.encoders.annotations.Encodable;
import java.util.List;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes3.dex */
public final class AutoValue_BatchedLogRequest extends BatchedLogRequest {
    private final List<LogRequest> logRequests;

    public AutoValue_BatchedLogRequest(List<LogRequest> logRequests) {
        if (logRequests == null) {
            throw new NullPointerException("Null logRequests");
        }
        this.logRequests = logRequests;
    }

    @Override // com.google.android.datatransport.cct.internal.BatchedLogRequest
    @Encodable.Field(name = "logRequest")
    public List<LogRequest> getLogRequests() {
        return this.logRequests;
    }

    public String toString() {
        return "BatchedLogRequest{logRequests=" + this.logRequests + "}";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof BatchedLogRequest) {
            BatchedLogRequest that = (BatchedLogRequest) o;
            return this.logRequests.equals(that.getLogRequests());
        }
        return false;
    }

    public int hashCode() {
        int h$ = 1 * 1000003;
        return h$ ^ this.logRequests.hashCode();
    }
}
