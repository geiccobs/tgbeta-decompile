package com.microsoft.appcenter.distribute.ingestion.models.json;

import com.microsoft.appcenter.distribute.ingestion.models.DistributionStartSessionLog;
import com.microsoft.appcenter.ingestion.models.json.AbstractLogFactory;
/* loaded from: classes3.dex */
public class DistributionStartSessionLogFactory extends AbstractLogFactory {
    @Override // com.microsoft.appcenter.ingestion.models.json.LogFactory
    public DistributionStartSessionLog create() {
        return new DistributionStartSessionLog();
    }
}
