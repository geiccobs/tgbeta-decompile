package com.google.android.exoplayer2.source.smoothstreaming.manifest;

import android.net.Uri;
import com.google.android.exoplayer2.util.Util;
/* loaded from: classes3.dex */
public final class SsUtil {
    public static Uri fixManifestUri(Uri manifestUri) {
        String lastPathSegment = manifestUri.getLastPathSegment();
        if (lastPathSegment != null && Util.toLowerInvariant(lastPathSegment).matches("manifest(\\(.+\\))?")) {
            return manifestUri;
        }
        return Uri.withAppendedPath(manifestUri, "Manifest");
    }

    private SsUtil() {
    }
}
