package com.microsoft.appcenter.ingestion.models.json;

import com.microsoft.appcenter.ingestion.models.CustomPropertiesLog;
import com.microsoft.appcenter.ingestion.models.Log;
/* loaded from: classes3.dex */
public class CustomPropertiesLogFactory extends AbstractLogFactory {
    @Override // com.microsoft.appcenter.ingestion.models.json.LogFactory
    public Log create() {
        return new CustomPropertiesLog();
    }
}
