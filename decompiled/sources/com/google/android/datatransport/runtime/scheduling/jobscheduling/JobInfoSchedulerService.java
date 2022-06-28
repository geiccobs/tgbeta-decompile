package com.google.android.datatransport.runtime.scheduling.jobscheduling;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Base64;
import com.google.android.datatransport.runtime.TransportContext;
import com.google.android.datatransport.runtime.TransportRuntime;
import com.google.android.datatransport.runtime.util.PriorityMapping;
/* loaded from: classes3.dex */
public class JobInfoSchedulerService extends JobService {
    @Override // android.app.job.JobService
    public boolean onStartJob(final JobParameters params) {
        String backendName = params.getExtras().getString("backendName");
        String extras = params.getExtras().getString("extras");
        int priority = params.getExtras().getInt("priority");
        int attemptNumber = params.getExtras().getInt("attemptNumber");
        TransportRuntime.initialize(getApplicationContext());
        TransportContext.Builder transportContext = TransportContext.builder().setBackendName(backendName).setPriority(PriorityMapping.valueOf(priority));
        if (extras != null) {
            transportContext.setExtras(Base64.decode(extras, 0));
        }
        TransportRuntime.getInstance().getUploader().upload(transportContext.build(), attemptNumber, new Runnable() { // from class: com.google.android.datatransport.runtime.scheduling.jobscheduling.JobInfoSchedulerService$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                JobInfoSchedulerService.this.m8x1ff6a938(params);
            }
        });
        return true;
    }

    /* renamed from: lambda$onStartJob$0$com-google-android-datatransport-runtime-scheduling-jobscheduling-JobInfoSchedulerService */
    public /* synthetic */ void m8x1ff6a938(JobParameters params) {
        jobFinished(params, false);
    }

    @Override // android.app.job.JobService
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
