package com.microsoft.appcenter.ingestion.models.json;

import com.microsoft.appcenter.ingestion.models.Log;
import com.microsoft.appcenter.ingestion.models.one.CommonSchemaLog;
import java.util.Collection;
import java.util.Collections;
/* loaded from: classes3.dex */
public abstract class AbstractLogFactory implements LogFactory {
    @Override // com.microsoft.appcenter.ingestion.models.json.LogFactory
    public Collection<CommonSchemaLog> toCommonSchemaLogs(Log log) {
        return Collections.emptyList();
    }
}
