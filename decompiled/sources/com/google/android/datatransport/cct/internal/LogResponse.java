package com.google.android.datatransport.cct.internal;

import android.util.JsonReader;
import android.util.JsonToken;
import java.io.IOException;
import java.io.Reader;
/* loaded from: classes3.dex */
public abstract class LogResponse {
    private static final String LOG_TAG = "LogResponseInternal";

    public abstract long getNextRequestWaitMillis();

    static LogResponse create(long nextRequestWaitMillis) {
        return new AutoValue_LogResponse(nextRequestWaitMillis);
    }

    public static LogResponse fromJson(Reader reader) throws IOException {
        JsonReader jsonReader = new JsonReader(reader);
        try {
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                String name = jsonReader.nextName();
                if (name.equals("nextRequestWaitMillis")) {
                    if (jsonReader.peek() == JsonToken.STRING) {
                        return create(Long.parseLong(jsonReader.nextString()));
                    }
                    return create(jsonReader.nextLong());
                }
                jsonReader.skipValue();
            }
            throw new IOException("Response is missing nextRequestWaitMillis field.");
        } finally {
            jsonReader.close();
        }
    }
}
