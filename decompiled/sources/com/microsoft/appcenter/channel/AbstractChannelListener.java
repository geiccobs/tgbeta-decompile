package com.microsoft.appcenter.channel;

import com.microsoft.appcenter.channel.Channel;
import com.microsoft.appcenter.ingestion.models.Log;
/* loaded from: classes3.dex */
public class AbstractChannelListener implements Channel.Listener {
    @Override // com.microsoft.appcenter.channel.Channel.Listener
    public void onGroupAdded(String groupName, Channel.GroupListener groupListener, long batchTimeInterval) {
    }

    @Override // com.microsoft.appcenter.channel.Channel.Listener
    public void onGroupRemoved(String groupName) {
    }

    @Override // com.microsoft.appcenter.channel.Channel.Listener
    public void onPreparingLog(Log log, String groupName) {
    }

    @Override // com.microsoft.appcenter.channel.Channel.Listener
    public void onPreparedLog(Log log, String groupName, int flags) {
    }

    @Override // com.microsoft.appcenter.channel.Channel.Listener
    public boolean shouldFilter(Log log) {
        return false;
    }

    @Override // com.microsoft.appcenter.channel.Channel.Listener
    public void onGloballyEnabled(boolean isEnabled) {
    }

    @Override // com.microsoft.appcenter.channel.Channel.Listener
    public void onClear(String groupName) {
    }

    @Override // com.microsoft.appcenter.channel.Channel.Listener
    public void onPaused(String groupName, String targetToken) {
    }

    @Override // com.microsoft.appcenter.channel.Channel.Listener
    public void onResumed(String groupName, String targetToken) {
    }
}
