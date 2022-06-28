package com.microsoft.appcenter.distribute.channel;

import com.microsoft.appcenter.channel.AbstractChannelListener;
import com.microsoft.appcenter.ingestion.models.Log;
/* loaded from: classes3.dex */
public class DistributeInfoTracker extends AbstractChannelListener {
    private String mDistributionGroupId;

    public DistributeInfoTracker(String distributionGroupId) {
        this.mDistributionGroupId = distributionGroupId;
    }

    @Override // com.microsoft.appcenter.channel.AbstractChannelListener, com.microsoft.appcenter.channel.Channel.Listener
    public synchronized void onPreparingLog(Log log, String groupName) {
        String str = this.mDistributionGroupId;
        if (str == null) {
            return;
        }
        log.setDistributionGroupId(str);
    }

    public synchronized void updateDistributionGroupId(String distributionGroupId) {
        this.mDistributionGroupId = distributionGroupId;
    }

    public synchronized void removeDistributionGroupId() {
        this.mDistributionGroupId = null;
    }
}
