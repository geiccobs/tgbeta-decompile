package com.microsoft.appcenter.distribute.download.http;

import android.net.TrafficStats;
import android.net.Uri;
import android.os.AsyncTask;
import com.microsoft.appcenter.distribute.DistributeConstants;
import com.microsoft.appcenter.http.HttpUtils;
import com.microsoft.appcenter.utils.AppCenterLog;
import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import javax.net.ssl.HttpsURLConnection;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes3.dex */
public class HttpConnectionDownloadFileTask extends AsyncTask<Void, Void, Void> {
    static final String APK_CONTENT_TYPE = "application/vnd.android.package-archive";
    private final Uri mDownloadUri;
    private final HttpConnectionReleaseDownloader mDownloader;
    private final File mTargetFile;

    public HttpConnectionDownloadFileTask(HttpConnectionReleaseDownloader downloader, Uri downloadUri, File targetFile) {
        this.mDownloader = downloader;
        this.mDownloadUri = downloadUri;
        this.mTargetFile = targetFile;
    }

    public Void doInBackground(Void... params) {
        long totalBytesDownloaded;
        TrafficStats.setThreadStatsTag(HttpUtils.THREAD_STATS_TAG);
        try {
            try {
                long enqueueTime = System.currentTimeMillis();
                this.mDownloader.onDownloadStarted(enqueueTime);
                URLConnection connection = createConnection();
                totalBytesDownloaded = downloadFile(connection);
            } catch (IOException e) {
                this.mDownloader.onDownloadError(e.getMessage());
            }
            if (totalBytesDownloaded > 0) {
                this.mDownloader.onDownloadComplete(this.mTargetFile);
                TrafficStats.clearThreadStatsTag();
                return null;
            }
            throw new IOException("The content of downloaded file is empty");
        } catch (Throwable th) {
            TrafficStats.clearThreadStatsTag();
            throw th;
        }
    }

    private URLConnection createConnection() throws IOException {
        URL url = new URL(this.mDownloadUri.toString());
        HttpsURLConnection connection = HttpUtils.createHttpsConnection(url);
        connection.setInstanceFollowRedirects(true);
        connection.connect();
        String contentType = connection.getContentType();
        if (!APK_CONTENT_TYPE.equals(contentType)) {
            AppCenterLog.warn(DistributeConstants.LOG_TAG, "The requested download has not expected content type.");
        }
        int responseCode = connection.getResponseCode();
        if (responseCode < 200 || responseCode >= 300) {
            throw new IOException("Download failed with HTTP error code: " + responseCode);
        }
        return connection;
    }

    private long downloadFile(URLConnection connection) throws IOException {
        InputStream input = null;
        OutputStream output = null;
        try {
            input = new BufferedInputStream(connection.getInputStream());
            output = new FileOutputStream(this.mTargetFile);
            long copyStream = copyStream(input, output, connection.getContentLength());
            close(input, output);
            return copyStream;
        } catch (Throwable th) {
            close(input, output);
            throw th;
        }
    }

    private long copyStream(InputStream inputStream, OutputStream outputStream, long lengthOfFile) throws IOException {
        byte[] data = new byte[1024];
        long totalBytesDownloaded = 0;
        long lastReportedBytes = 0;
        long lastReportedTime = 0;
        do {
            int count = inputStream.read(data);
            if (count == -1) {
                break;
            }
            totalBytesDownloaded += count;
            outputStream.write(data, 0, count);
            long now = System.currentTimeMillis();
            if (totalBytesDownloaded >= DistributeConstants.UPDATE_PROGRESS_BYTES_THRESHOLD + lastReportedBytes || totalBytesDownloaded == lengthOfFile || now >= 500 + lastReportedTime) {
                this.mDownloader.onDownloadProgress(totalBytesDownloaded, lengthOfFile);
                lastReportedBytes = totalBytesDownloaded;
                lastReportedTime = now;
            }
        } while (!isCancelled());
        outputStream.flush();
        return totalBytesDownloaded;
    }

    private static void close(Closeable... closeables) {
        for (Closeable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
