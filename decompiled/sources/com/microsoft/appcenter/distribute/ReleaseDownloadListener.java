package com.microsoft.appcenter.distribute;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;
import com.microsoft.appcenter.distribute.download.ReleaseDownloader;
import com.microsoft.appcenter.utils.AppCenterLog;
import com.microsoft.appcenter.utils.HandlerUtils;
import java.text.NumberFormat;
import java.util.Locale;
/* loaded from: classes3.dex */
public class ReleaseDownloadListener implements ReleaseDownloader.Listener {
    private final Context mContext;
    private ProgressDialog mProgressDialog;
    private final ReleaseDetails mReleaseDetails;

    public ReleaseDownloadListener(Context context, ReleaseDetails releaseDetails) {
        this.mContext = context;
        this.mReleaseDetails = releaseDetails;
    }

    @Override // com.microsoft.appcenter.distribute.download.ReleaseDownloader.Listener
    public void onStart(long enqueueTime) {
        AppCenterLog.debug(DistributeConstants.LOG_TAG, String.format(Locale.ENGLISH, "Start download %s (%d) update.", this.mReleaseDetails.getShortVersion(), Integer.valueOf(this.mReleaseDetails.getVersion())));
        Distribute.getInstance().setDownloading(this.mReleaseDetails, enqueueTime);
    }

    @Override // com.microsoft.appcenter.distribute.download.ReleaseDownloader.Listener
    public synchronized boolean onProgress(final long currentSize, final long totalSize) {
        boolean z;
        z = false;
        AppCenterLog.verbose(DistributeConstants.LOG_TAG, String.format(Locale.ENGLISH, "Downloading %s (%d) update: %d KiB / %d KiB", this.mReleaseDetails.getShortVersion(), Integer.valueOf(this.mReleaseDetails.getVersion()), Long.valueOf(currentSize / DistributeConstants.KIBIBYTE_IN_BYTES), Long.valueOf(totalSize / DistributeConstants.KIBIBYTE_IN_BYTES)));
        HandlerUtils.runOnUiThread(new Runnable() { // from class: com.microsoft.appcenter.distribute.ReleaseDownloadListener.1
            @Override // java.lang.Runnable
            public void run() {
                ReleaseDownloadListener.this.updateProgressDialog(currentSize, totalSize);
            }
        });
        if (this.mProgressDialog != null) {
            z = true;
        }
        return z;
    }

    @Override // com.microsoft.appcenter.distribute.download.ReleaseDownloader.Listener
    public boolean onComplete(Uri localUri) {
        Intent intent = InstallerUtils.getInstallIntent(localUri);
        if (intent.resolveActivity(this.mContext.getPackageManager()) == null) {
            AppCenterLog.debug(DistributeConstants.LOG_TAG, "Cannot resolve install intent for " + localUri);
            return false;
        }
        AppCenterLog.debug(DistributeConstants.LOG_TAG, String.format(Locale.ENGLISH, "Download %s (%d) update completed.", this.mReleaseDetails.getShortVersion(), Integer.valueOf(this.mReleaseDetails.getVersion())));
        if (!Distribute.getInstance().notifyDownload(this.mReleaseDetails, intent)) {
            AppCenterLog.info(DistributeConstants.LOG_TAG, "Show install UI for " + localUri);
            this.mContext.startActivity(intent);
            Distribute.getInstance().setInstalling(this.mReleaseDetails);
        }
        return true;
    }

    @Override // com.microsoft.appcenter.distribute.download.ReleaseDownloader.Listener
    public void onError(String errorMessage) {
        AppCenterLog.error(DistributeConstants.LOG_TAG, String.format(Locale.ENGLISH, "Failed to download %s (%d) update: %s", this.mReleaseDetails.getShortVersion(), Integer.valueOf(this.mReleaseDetails.getVersion()), errorMessage));
        HandlerUtils.runOnUiThread(new Runnable() { // from class: com.microsoft.appcenter.distribute.ReleaseDownloadListener.2
            @Override // java.lang.Runnable
            public void run() {
                Toast.makeText(ReleaseDownloadListener.this.mContext, R.string.appcenter_distribute_downloading_error, 0).show();
            }
        });
        Distribute.getInstance().completeWorkflow(this.mReleaseDetails);
    }

    public synchronized ProgressDialog showDownloadProgress(Activity foregroundActivity) {
        if (!this.mReleaseDetails.isMandatoryUpdate()) {
            return null;
        }
        ProgressDialog progressDialog = new ProgressDialog(foregroundActivity);
        this.mProgressDialog = progressDialog;
        progressDialog.setTitle(R.string.appcenter_distribute_downloading_update);
        this.mProgressDialog.setCancelable(false);
        this.mProgressDialog.setProgressStyle(1);
        this.mProgressDialog.setIndeterminate(true);
        this.mProgressDialog.setProgressNumberFormat(null);
        this.mProgressDialog.setProgressPercentFormat(null);
        return this.mProgressDialog;
    }

    public synchronized void hideProgressDialog() {
        final ProgressDialog progressDialog = this.mProgressDialog;
        if (progressDialog != null) {
            this.mProgressDialog = null;
            HandlerUtils.runOnUiThread(new Runnable() { // from class: com.microsoft.appcenter.distribute.ReleaseDownloadListener.3
                @Override // java.lang.Runnable
                public void run() {
                    progressDialog.hide();
                }
            });
            HandlerUtils.getMainHandler().removeCallbacksAndMessages(DistributeConstants.HANDLER_TOKEN_CHECK_PROGRESS);
        }
    }

    public synchronized void updateProgressDialog(long currentSize, long totalSize) {
        ProgressDialog progressDialog = this.mProgressDialog;
        if (progressDialog != null && totalSize >= 0) {
            if (progressDialog.isIndeterminate()) {
                this.mProgressDialog.setProgressPercentFormat(NumberFormat.getPercentInstance());
                this.mProgressDialog.setProgressNumberFormat(this.mContext.getString(R.string.appcenter_distribute_download_progress_number_format));
                this.mProgressDialog.setIndeterminate(false);
                this.mProgressDialog.setMax((int) (totalSize / 1048576));
            }
            this.mProgressDialog.setProgress((int) (currentSize / 1048576));
        }
    }
}
