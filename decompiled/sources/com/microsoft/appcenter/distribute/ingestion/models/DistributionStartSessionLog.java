package com.microsoft.appcenter.distribute.ingestion.models;

import com.microsoft.appcenter.ingestion.models.AbstractLog;
/* loaded from: classes3.dex */
public class DistributionStartSessionLog extends AbstractLog {
    public static final String TYPE = "distributionStartSession";

    @Override // com.microsoft.appcenter.ingestion.models.Log
    public String getType() {
        return TYPE;
    }
}
