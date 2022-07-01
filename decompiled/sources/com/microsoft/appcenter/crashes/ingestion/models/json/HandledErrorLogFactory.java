package com.microsoft.appcenter.crashes.ingestion.models.json;

import com.microsoft.appcenter.crashes.ingestion.models.HandledErrorLog;
import com.microsoft.appcenter.ingestion.models.json.AbstractLogFactory;
/* loaded from: classes.dex */
public class HandledErrorLogFactory extends AbstractLogFactory {
    private static final HandledErrorLogFactory sInstance = new HandledErrorLogFactory();

    private HandledErrorLogFactory() {
    }

    public static HandledErrorLogFactory getInstance() {
        return sInstance;
    }

    @Override // com.microsoft.appcenter.ingestion.models.json.LogFactory
    public HandledErrorLog create() {
        return new HandledErrorLog();
    }
}
