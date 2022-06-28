package com.microsoft.appcenter.crashes;

import com.microsoft.appcenter.crashes.ingestion.models.ErrorAttachmentLog;
import com.microsoft.appcenter.crashes.model.ErrorReport;
/* loaded from: classes3.dex */
public abstract class AbstractCrashesListener implements CrashesListener {
    @Override // com.microsoft.appcenter.crashes.CrashesListener
    public boolean shouldProcess(ErrorReport report) {
        return true;
    }

    @Override // com.microsoft.appcenter.crashes.CrashesListener
    public boolean shouldAwaitUserConfirmation() {
        return false;
    }

    @Override // com.microsoft.appcenter.crashes.CrashesListener
    public Iterable<ErrorAttachmentLog> getErrorAttachments(ErrorReport report) {
        return null;
    }

    @Override // com.microsoft.appcenter.crashes.CrashesListener
    public void onBeforeSending(ErrorReport report) {
    }

    @Override // com.microsoft.appcenter.crashes.CrashesListener
    public void onSendingFailed(ErrorReport report, Exception e) {
    }

    @Override // com.microsoft.appcenter.crashes.CrashesListener
    public void onSendingSucceeded(ErrorReport report) {
    }
}
