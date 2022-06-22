package com.microsoft.appcenter.channel;

import com.microsoft.appcenter.channel.Channel;
import com.microsoft.appcenter.ingestion.models.Log;
/* loaded from: classes.dex */
public class AbstractChannelListener implements Channel.Listener {
    @Override // com.microsoft.appcenter.channel.Channel.Listener
    public void onClear(String str) {
    }

    @Override // com.microsoft.appcenter.channel.Channel.Listener
    public void onGloballyEnabled(boolean z) {
    }

    @Override // com.microsoft.appcenter.channel.Channel.Listener
    public void onGroupAdded(String str, Channel.GroupListener groupListener, long j) {
    }

    @Override // com.microsoft.appcenter.channel.Channel.Listener
    public void onGroupRemoved(String str) {
    }

    @Override // com.microsoft.appcenter.channel.Channel.Listener
    public void onPreparedLog(Log log, String str, int i) {
    }

    @Override // com.microsoft.appcenter.channel.Channel.Listener
    public void onPreparingLog(Log log, String str) {
    }

    @Override // com.microsoft.appcenter.channel.Channel.Listener
    public boolean shouldFilter(Log log) {
        return false;
    }
}
