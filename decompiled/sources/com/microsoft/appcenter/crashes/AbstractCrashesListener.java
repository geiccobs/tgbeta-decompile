package com.microsoft.appcenter.crashes;

import com.microsoft.appcenter.crashes.ingestion.models.ErrorAttachmentLog;
import com.microsoft.appcenter.crashes.model.ErrorReport;
/* loaded from: classes.dex */
public abstract class AbstractCrashesListener implements CrashesListener {
    @Override // com.microsoft.appcenter.crashes.CrashesListener
    public Iterable<ErrorAttachmentLog> getErrorAttachments(ErrorReport errorReport) {
        return null;
    }

    @Override // com.microsoft.appcenter.crashes.CrashesListener
    public void onBeforeSending(ErrorReport errorReport) {
    }

    @Override // com.microsoft.appcenter.crashes.CrashesListener
    public void onSendingFailed(ErrorReport errorReport, Exception exc) {
    }

    @Override // com.microsoft.appcenter.crashes.CrashesListener
    public void onSendingSucceeded(ErrorReport errorReport) {
    }

    @Override // com.microsoft.appcenter.crashes.CrashesListener
    public boolean shouldAwaitUserConfirmation() {
        return false;
    }

    @Override // com.microsoft.appcenter.crashes.CrashesListener
    public boolean shouldProcess(ErrorReport errorReport) {
        return true;
    }
}
