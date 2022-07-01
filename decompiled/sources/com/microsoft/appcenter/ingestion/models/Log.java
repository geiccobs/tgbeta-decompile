package com.microsoft.appcenter.ingestion.models;

import java.util.Date;
import java.util.Set;
import java.util.UUID;
/* loaded from: classes.dex */
public interface Log extends Model {
    void addTransmissionTarget(String str);

    Device getDevice();

    UUID getSid();

    Date getTimestamp();

    Set<String> getTransmissionTargetTokens();

    String getType();

    void setDevice(Device device);

    void setDistributionGroupId(String str);

    void setTimestamp(Date date);
}
