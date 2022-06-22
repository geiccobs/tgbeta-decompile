package com.microsoft.appcenter.channel;

import com.microsoft.appcenter.channel.Channel;
import com.microsoft.appcenter.http.HttpClient;
import com.microsoft.appcenter.ingestion.Ingestion;
import com.microsoft.appcenter.ingestion.OneCollectorIngestion;
import com.microsoft.appcenter.ingestion.models.Log;
import com.microsoft.appcenter.ingestion.models.json.LogSerializer;
import com.microsoft.appcenter.ingestion.models.one.CommonSchemaLog;
import com.microsoft.appcenter.ingestion.models.one.SdkExtension;
import com.microsoft.appcenter.utils.AppCenterLog;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
/* loaded from: classes.dex */
public class OneCollectorChannelListener extends AbstractChannelListener {
    private final Channel mChannel;
    private final Map<String, EpochAndSeq> mEpochsAndSeqsByIKey;
    private final Ingestion mIngestion;
    private final UUID mInstallId;
    private final LogSerializer mLogSerializer;

    public OneCollectorChannelListener(Channel channel, LogSerializer logSerializer, HttpClient httpClient, UUID uuid) {
        this(new OneCollectorIngestion(httpClient, logSerializer), channel, logSerializer, uuid);
    }

    OneCollectorChannelListener(OneCollectorIngestion oneCollectorIngestion, Channel channel, LogSerializer logSerializer, UUID uuid) {
        this.mEpochsAndSeqsByIKey = new HashMap();
        this.mChannel = channel;
        this.mLogSerializer = logSerializer;
        this.mInstallId = uuid;
        this.mIngestion = oneCollectorIngestion;
    }

    public void setLogUrl(String str) {
        this.mIngestion.setLogUrl(str);
    }

    @Override // com.microsoft.appcenter.channel.AbstractChannelListener, com.microsoft.appcenter.channel.Channel.Listener
    public void onGroupAdded(String str, Channel.GroupListener groupListener, long j) {
        if (isOneCollectorGroup(str)) {
            return;
        }
        this.mChannel.addGroup(getOneCollectorGroupName(str), 50, j, 2, this.mIngestion, groupListener);
    }

    @Override // com.microsoft.appcenter.channel.AbstractChannelListener, com.microsoft.appcenter.channel.Channel.Listener
    public void onGroupRemoved(String str) {
        if (isOneCollectorGroup(str)) {
            return;
        }
        this.mChannel.removeGroup(getOneCollectorGroupName(str));
    }

    @Override // com.microsoft.appcenter.channel.AbstractChannelListener, com.microsoft.appcenter.channel.Channel.Listener
    public void onPreparedLog(Log log, String str, int i) {
        if (!isOneCollectorCompatible(log)) {
            return;
        }
        try {
            Collection<CommonSchemaLog> commonSchemaLog = this.mLogSerializer.toCommonSchemaLog(log);
            for (CommonSchemaLog commonSchemaLog2 : commonSchemaLog) {
                commonSchemaLog2.setFlags(Long.valueOf(i));
                EpochAndSeq epochAndSeq = this.mEpochsAndSeqsByIKey.get(commonSchemaLog2.getIKey());
                if (epochAndSeq == null) {
                    epochAndSeq = new EpochAndSeq(UUID.randomUUID().toString());
                    this.mEpochsAndSeqsByIKey.put(commonSchemaLog2.getIKey(), epochAndSeq);
                }
                SdkExtension sdk = commonSchemaLog2.getExt().getSdk();
                sdk.setEpoch(epochAndSeq.epoch);
                long j = epochAndSeq.seq + 1;
                epochAndSeq.seq = j;
                sdk.setSeq(Long.valueOf(j));
                sdk.setInstallId(this.mInstallId);
            }
            String oneCollectorGroupName = getOneCollectorGroupName(str);
            for (CommonSchemaLog commonSchemaLog3 : commonSchemaLog) {
                this.mChannel.enqueue(commonSchemaLog3, oneCollectorGroupName, i);
            }
        } catch (IllegalArgumentException e) {
            AppCenterLog.error("AppCenter", "Cannot send a log to one collector: " + e.getMessage());
        }
    }

    @Override // com.microsoft.appcenter.channel.AbstractChannelListener, com.microsoft.appcenter.channel.Channel.Listener
    public boolean shouldFilter(Log log) {
        return isOneCollectorCompatible(log);
    }

    private static String getOneCollectorGroupName(String str) {
        return str + "/one";
    }

    @Override // com.microsoft.appcenter.channel.AbstractChannelListener, com.microsoft.appcenter.channel.Channel.Listener
    public void onClear(String str) {
        if (isOneCollectorGroup(str)) {
            return;
        }
        this.mChannel.clear(getOneCollectorGroupName(str));
    }

    private static boolean isOneCollectorGroup(String str) {
        return str.endsWith("/one");
    }

    private static boolean isOneCollectorCompatible(Log log) {
        return !(log instanceof CommonSchemaLog) && !log.getTransmissionTargetTokens().isEmpty();
    }

    @Override // com.microsoft.appcenter.channel.AbstractChannelListener, com.microsoft.appcenter.channel.Channel.Listener
    public void onGloballyEnabled(boolean z) {
        if (!z) {
            this.mEpochsAndSeqsByIKey.clear();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class EpochAndSeq {
        final String epoch;
        long seq;

        EpochAndSeq(String str) {
            this.epoch = str;
        }
    }
}
