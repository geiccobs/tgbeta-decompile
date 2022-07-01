package com.microsoft.appcenter.distribute.channel;

import com.microsoft.appcenter.channel.AbstractChannelListener;
import com.microsoft.appcenter.ingestion.models.Log;
/* loaded from: classes.dex */
public class DistributeInfoTracker extends AbstractChannelListener {
    private String mDistributionGroupId;

    public DistributeInfoTracker(String str) {
        this.mDistributionGroupId = str;
    }

    @Override // com.microsoft.appcenter.channel.AbstractChannelListener, com.microsoft.appcenter.channel.Channel.Listener
    public synchronized void onPreparingLog(Log log, String str) {
        String str2 = this.mDistributionGroupId;
        if (str2 == null) {
            return;
        }
        log.setDistributionGroupId(str2);
    }

    public synchronized void updateDistributionGroupId(String str) {
        this.mDistributionGroupId = str;
    }

    public synchronized void removeDistributionGroupId() {
        this.mDistributionGroupId = null;
    }
}
