package com.google.android.exoplayer2.offline;

import android.net.Uri;
import java.lang.reflect.Constructor;
import java.util.List;
/* loaded from: classes3.dex */
public class DefaultDownloaderFactory implements DownloaderFactory {
    private static final Constructor<? extends Downloader> DASH_DOWNLOADER_CONSTRUCTOR;
    private static final Constructor<? extends Downloader> HLS_DOWNLOADER_CONSTRUCTOR;
    private static final Constructor<? extends Downloader> SS_DOWNLOADER_CONSTRUCTOR;
    private final DownloaderConstructorHelper downloaderConstructorHelper;

    static {
        Constructor<? extends Downloader> dashDownloaderConstructor = null;
        try {
            dashDownloaderConstructor = getDownloaderConstructor(Class.forName("com.google.android.exoplayer2.source.dash.offline.DashDownloader"));
        } catch (ClassNotFoundException e) {
        }
        DASH_DOWNLOADER_CONSTRUCTOR = dashDownloaderConstructor;
        Constructor<? extends Downloader> hlsDownloaderConstructor = null;
        try {
            hlsDownloaderConstructor = getDownloaderConstructor(Class.forName("com.google.android.exoplayer2.source.hls.offline.HlsDownloader"));
        } catch (ClassNotFoundException e2) {
        }
        HLS_DOWNLOADER_CONSTRUCTOR = hlsDownloaderConstructor;
        Constructor<? extends Downloader> ssDownloaderConstructor = null;
        try {
            ssDownloaderConstructor = getDownloaderConstructor(Class.forName("com.google.android.exoplayer2.source.smoothstreaming.offline.SsDownloader"));
        } catch (ClassNotFoundException e3) {
        }
        SS_DOWNLOADER_CONSTRUCTOR = ssDownloaderConstructor;
    }

    public DefaultDownloaderFactory(DownloaderConstructorHelper downloaderConstructorHelper) {
        this.downloaderConstructorHelper = downloaderConstructorHelper;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.google.android.exoplayer2.offline.DownloaderFactory
    public Downloader createDownloader(DownloadRequest request) {
        char c;
        String str = request.type;
        switch (str.hashCode()) {
            case 3680:
                if (str.equals(DownloadRequest.TYPE_SS)) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 103407:
                if (str.equals(DownloadRequest.TYPE_HLS)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 3075986:
                if (str.equals(DownloadRequest.TYPE_DASH)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 1131547531:
                if (str.equals(DownloadRequest.TYPE_PROGRESSIVE)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
                return new ProgressiveDownloader(request.uri, request.customCacheKey, this.downloaderConstructorHelper);
            case 1:
                return createDownloader(request, DASH_DOWNLOADER_CONSTRUCTOR);
            case 2:
                return createDownloader(request, HLS_DOWNLOADER_CONSTRUCTOR);
            case 3:
                return createDownloader(request, SS_DOWNLOADER_CONSTRUCTOR);
            default:
                throw new IllegalArgumentException("Unsupported type: " + request.type);
        }
    }

    private Downloader createDownloader(DownloadRequest request, Constructor<? extends Downloader> constructor) {
        if (constructor == null) {
            throw new IllegalStateException("Module missing for: " + request.type);
        }
        try {
            return constructor.newInstance(request.uri, request.streamKeys, this.downloaderConstructorHelper);
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate downloader for: " + request.type, e);
        }
    }

    private static Constructor<? extends Downloader> getDownloaderConstructor(Class<?> clazz) {
        try {
            return clazz.asSubclass(Downloader.class).getConstructor(Uri.class, List.class, DownloaderConstructorHelper.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Downloader constructor missing", e);
        }
    }
}
