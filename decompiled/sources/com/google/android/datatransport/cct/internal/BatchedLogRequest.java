package com.google.android.datatransport.cct.internal;

import com.google.firebase.encoders.DataEncoder;
import com.google.firebase.encoders.annotations.Encodable;
import com.google.firebase.encoders.json.JsonDataEncoderBuilder;
import java.util.List;
@Encodable
/* loaded from: classes3.dex */
public abstract class BatchedLogRequest {
    @Encodable.Field(name = "logRequest")
    public abstract List<LogRequest> getLogRequests();

    public static BatchedLogRequest create(List<LogRequest> logRequests) {
        return new AutoValue_BatchedLogRequest(logRequests);
    }

    public static DataEncoder createDataEncoder() {
        return new JsonDataEncoderBuilder().configureWith(AutoBatchedLogRequestEncoder.CONFIG).ignoreNullValues(true).build();
    }
}
