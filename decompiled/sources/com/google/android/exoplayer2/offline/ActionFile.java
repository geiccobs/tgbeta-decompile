package com.google.android.exoplayer2.offline;

import android.net.Uri;
import com.google.android.exoplayer2.offline.DownloadRequest;
import com.google.android.exoplayer2.util.AtomicFile;
import com.google.android.exoplayer2.util.Util;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
@Deprecated
/* loaded from: classes3.dex */
final class ActionFile {
    private static final int VERSION = 0;
    private final AtomicFile atomicFile;

    public ActionFile(File actionFile) {
        this.atomicFile = new AtomicFile(actionFile);
    }

    public boolean exists() {
        return this.atomicFile.exists();
    }

    public void delete() {
        this.atomicFile.delete();
    }

    public DownloadRequest[] load() throws IOException {
        if (!exists()) {
            return new DownloadRequest[0];
        }
        InputStream inputStream = null;
        try {
            inputStream = this.atomicFile.openRead();
            DataInputStream dataInputStream = new DataInputStream(inputStream);
            int version = dataInputStream.readInt();
            if (version > 0) {
                throw new IOException("Unsupported action file version: " + version);
            }
            int actionCount = dataInputStream.readInt();
            ArrayList<DownloadRequest> actions = new ArrayList<>();
            for (int i = 0; i < actionCount; i++) {
                try {
                    actions.add(readDownloadRequest(dataInputStream));
                } catch (DownloadRequest.UnsupportedRequestException e) {
                }
            }
            return (DownloadRequest[]) actions.toArray(new DownloadRequest[0]);
        } finally {
            Util.closeQuietly(inputStream);
        }
    }

    private static DownloadRequest readDownloadRequest(DataInputStream input) throws IOException {
        byte[] data;
        String customCacheKey;
        String type = input.readUTF();
        int version = input.readInt();
        Uri uri = Uri.parse(input.readUTF());
        boolean isRemoveAction = input.readBoolean();
        int dataLength = input.readInt();
        if (dataLength != 0) {
            byte[] data2 = new byte[dataLength];
            input.readFully(data2);
            data = data2;
        } else {
            data = null;
        }
        boolean z = false;
        boolean isLegacyProgressive = version == 0 && DownloadRequest.TYPE_PROGRESSIVE.equals(type);
        List<StreamKey> keys = new ArrayList<>();
        if (!isLegacyProgressive) {
            int keyCount = input.readInt();
            for (int i = 0; i < keyCount; i++) {
                keys.add(readKey(type, version, input));
            }
        }
        if (version < 2 && (DownloadRequest.TYPE_DASH.equals(type) || DownloadRequest.TYPE_HLS.equals(type) || DownloadRequest.TYPE_SS.equals(type))) {
            z = true;
        }
        boolean isLegacySegmented = z;
        if (isLegacySegmented) {
            customCacheKey = null;
        } else {
            customCacheKey = input.readBoolean() ? input.readUTF() : null;
        }
        String id = version < 3 ? generateDownloadId(uri, customCacheKey) : input.readUTF();
        if (isRemoveAction) {
            throw new DownloadRequest.UnsupportedRequestException();
        }
        return new DownloadRequest(id, type, uri, keys, customCacheKey, data);
    }

    private static StreamKey readKey(String type, int version, DataInputStream input) throws IOException {
        int trackIndex;
        int groupIndex;
        int periodIndex;
        if ((DownloadRequest.TYPE_HLS.equals(type) || DownloadRequest.TYPE_SS.equals(type)) && version == 0) {
            periodIndex = 0;
            groupIndex = input.readInt();
            trackIndex = input.readInt();
        } else {
            periodIndex = input.readInt();
            groupIndex = input.readInt();
            trackIndex = input.readInt();
        }
        return new StreamKey(periodIndex, groupIndex, trackIndex);
    }

    private static String generateDownloadId(Uri uri, String customCacheKey) {
        return customCacheKey != null ? customCacheKey : uri.toString();
    }
}
