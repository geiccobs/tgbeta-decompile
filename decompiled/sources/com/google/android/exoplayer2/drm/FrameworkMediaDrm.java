package com.google.android.exoplayer2.drm;

import android.media.DeniedByServerException;
import android.media.MediaCryptoException;
import android.media.MediaDrm;
import android.media.MediaDrmException;
import android.media.NotProvisionedException;
import android.media.UnsupportedSchemeException;
import android.os.Handler;
import android.os.PersistableBundle;
import android.text.TextUtils;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.drm.DrmInitData;
import com.google.android.exoplayer2.drm.ExoMediaDrm;
import com.google.android.exoplayer2.extractor.mp4.PsshAtomUtil;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
/* loaded from: classes3.dex */
public final class FrameworkMediaDrm implements ExoMediaDrm<FrameworkMediaCrypto> {
    private static final String CENC_SCHEME_MIME_TYPE = "cenc";
    public static final ExoMediaDrm.Provider<FrameworkMediaCrypto> DEFAULT_PROVIDER = FrameworkMediaDrm$$ExternalSyntheticLambda2.INSTANCE;
    private static final String MOCK_LA_URL = "<LA_URL>https://x</LA_URL>";
    private static final String MOCK_LA_URL_VALUE = "https://x";
    private static final String TAG = "FrameworkMediaDrm";
    private static final int UTF_16_BYTES_PER_CHARACTER = 2;
    private final MediaDrm mediaDrm;
    private int referenceCount = 1;
    private final UUID uuid;

    public static /* synthetic */ ExoMediaDrm lambda$static$0(UUID uuid) {
        try {
            return newInstance(uuid);
        } catch (UnsupportedDrmException e) {
            Log.e(TAG, "Failed to instantiate a FrameworkMediaDrm for uuid: " + uuid + ".");
            return new DummyExoMediaDrm();
        }
    }

    public static FrameworkMediaDrm newInstance(UUID uuid) throws UnsupportedDrmException {
        try {
            return new FrameworkMediaDrm(uuid);
        } catch (UnsupportedSchemeException e) {
            throw new UnsupportedDrmException(1, e);
        } catch (Exception e2) {
            throw new UnsupportedDrmException(2, e2);
        }
    }

    private FrameworkMediaDrm(UUID uuid) throws UnsupportedSchemeException {
        Assertions.checkNotNull(uuid);
        Assertions.checkArgument(!C.COMMON_PSSH_UUID.equals(uuid), "Use C.CLEARKEY_UUID instead");
        this.uuid = uuid;
        MediaDrm mediaDrm = new MediaDrm(adjustUuid(uuid));
        this.mediaDrm = mediaDrm;
        if (C.WIDEVINE_UUID.equals(uuid) && needsForceWidevineL3Workaround()) {
            forceWidevineL3(mediaDrm);
        }
    }

    @Override // com.google.android.exoplayer2.drm.ExoMediaDrm
    public void setOnEventListener(final ExoMediaDrm.OnEventListener<? super FrameworkMediaCrypto> listener) {
        MediaDrm.OnEventListener onEventListener;
        MediaDrm mediaDrm = this.mediaDrm;
        if (listener == null) {
            onEventListener = null;
        } else {
            onEventListener = new MediaDrm.OnEventListener() { // from class: com.google.android.exoplayer2.drm.FrameworkMediaDrm$$ExternalSyntheticLambda0
                @Override // android.media.MediaDrm.OnEventListener
                public final void onEvent(MediaDrm mediaDrm2, byte[] bArr, int i, int i2, byte[] bArr2) {
                    FrameworkMediaDrm.this.m47x6bb92dcc(listener, mediaDrm2, bArr, i, i2, bArr2);
                }
            };
        }
        mediaDrm.setOnEventListener(onEventListener);
    }

    /* renamed from: lambda$setOnEventListener$1$com-google-android-exoplayer2-drm-FrameworkMediaDrm */
    public /* synthetic */ void m47x6bb92dcc(ExoMediaDrm.OnEventListener listener, MediaDrm mediaDrm, byte[] sessionId, int event, int extra, byte[] data) {
        listener.onEvent(this, sessionId, event, extra, data);
    }

    @Override // com.google.android.exoplayer2.drm.ExoMediaDrm
    public void setOnKeyStatusChangeListener(final ExoMediaDrm.OnKeyStatusChangeListener<? super FrameworkMediaCrypto> listener) {
        MediaDrm.OnKeyStatusChangeListener onKeyStatusChangeListener;
        if (Util.SDK_INT < 23) {
            throw new UnsupportedOperationException();
        }
        MediaDrm mediaDrm = this.mediaDrm;
        if (listener == null) {
            onKeyStatusChangeListener = null;
        } else {
            onKeyStatusChangeListener = new MediaDrm.OnKeyStatusChangeListener() { // from class: com.google.android.exoplayer2.drm.FrameworkMediaDrm$$ExternalSyntheticLambda1
                @Override // android.media.MediaDrm.OnKeyStatusChangeListener
                public final void onKeyStatusChange(MediaDrm mediaDrm2, byte[] bArr, List list, boolean z) {
                    FrameworkMediaDrm.this.m48x8be3cdb4(listener, mediaDrm2, bArr, list, z);
                }
            };
        }
        mediaDrm.setOnKeyStatusChangeListener(onKeyStatusChangeListener, (Handler) null);
    }

    /* renamed from: lambda$setOnKeyStatusChangeListener$2$com-google-android-exoplayer2-drm-FrameworkMediaDrm */
    public /* synthetic */ void m48x8be3cdb4(ExoMediaDrm.OnKeyStatusChangeListener listener, MediaDrm mediaDrm, byte[] sessionId, List keyInfo, boolean hasNewUsableKey) {
        List<ExoMediaDrm.KeyStatus> exoKeyInfo = new ArrayList<>();
        Iterator it = keyInfo.iterator();
        while (it.hasNext()) {
            MediaDrm.KeyStatus keyStatus = (MediaDrm.KeyStatus) it.next();
            exoKeyInfo.add(new ExoMediaDrm.KeyStatus(keyStatus.getStatusCode(), keyStatus.getKeyId()));
        }
        listener.onKeyStatusChange(this, sessionId, exoKeyInfo, hasNewUsableKey);
    }

    @Override // com.google.android.exoplayer2.drm.ExoMediaDrm
    public byte[] openSession() throws MediaDrmException {
        return this.mediaDrm.openSession();
    }

    @Override // com.google.android.exoplayer2.drm.ExoMediaDrm
    public void closeSession(byte[] sessionId) {
        this.mediaDrm.closeSession(sessionId);
    }

    @Override // com.google.android.exoplayer2.drm.ExoMediaDrm
    public ExoMediaDrm.KeyRequest getKeyRequest(byte[] scope, List<DrmInitData.SchemeData> schemeDatas, int keyType, HashMap<String, String> optionalParameters) throws NotProvisionedException {
        String mimeType;
        byte[] initData;
        DrmInitData.SchemeData schemeData = null;
        if (schemeDatas == null) {
            initData = null;
            mimeType = null;
        } else {
            schemeData = getSchemeData(this.uuid, schemeDatas);
            byte[] initData2 = adjustRequestInitData(this.uuid, (byte[]) Assertions.checkNotNull(schemeData.data));
            String mimeType2 = adjustRequestMimeType(this.uuid, schemeData.mimeType);
            initData = initData2;
            mimeType = mimeType2;
        }
        MediaDrm.KeyRequest request = this.mediaDrm.getKeyRequest(scope, initData, mimeType, keyType, optionalParameters);
        byte[] requestData = adjustRequestData(this.uuid, request.getData());
        String licenseServerUrl = request.getDefaultUrl();
        if (MOCK_LA_URL_VALUE.equals(licenseServerUrl)) {
            licenseServerUrl = "";
        }
        if (TextUtils.isEmpty(licenseServerUrl) && schemeData != null && !TextUtils.isEmpty(schemeData.licenseServerUrl)) {
            licenseServerUrl = schemeData.licenseServerUrl;
        }
        return new ExoMediaDrm.KeyRequest(requestData, licenseServerUrl);
    }

    @Override // com.google.android.exoplayer2.drm.ExoMediaDrm
    public byte[] provideKeyResponse(byte[] scope, byte[] response) throws NotProvisionedException, DeniedByServerException {
        if (C.CLEARKEY_UUID.equals(this.uuid)) {
            response = ClearKeyUtil.adjustResponseData(response);
        }
        return this.mediaDrm.provideKeyResponse(scope, response);
    }

    @Override // com.google.android.exoplayer2.drm.ExoMediaDrm
    public ExoMediaDrm.ProvisionRequest getProvisionRequest() {
        MediaDrm.ProvisionRequest request = this.mediaDrm.getProvisionRequest();
        return new ExoMediaDrm.ProvisionRequest(request.getData(), request.getDefaultUrl());
    }

    @Override // com.google.android.exoplayer2.drm.ExoMediaDrm
    public void provideProvisionResponse(byte[] response) throws DeniedByServerException {
        this.mediaDrm.provideProvisionResponse(response);
    }

    @Override // com.google.android.exoplayer2.drm.ExoMediaDrm
    public Map<String, String> queryKeyStatus(byte[] sessionId) {
        return this.mediaDrm.queryKeyStatus(sessionId);
    }

    @Override // com.google.android.exoplayer2.drm.ExoMediaDrm
    public synchronized void acquire() {
        Assertions.checkState(this.referenceCount > 0);
        this.referenceCount++;
    }

    @Override // com.google.android.exoplayer2.drm.ExoMediaDrm
    public synchronized void release() {
        int i = this.referenceCount - 1;
        this.referenceCount = i;
        if (i == 0) {
            this.mediaDrm.release();
        }
    }

    @Override // com.google.android.exoplayer2.drm.ExoMediaDrm
    public void restoreKeys(byte[] sessionId, byte[] keySetId) {
        this.mediaDrm.restoreKeys(sessionId, keySetId);
    }

    @Override // com.google.android.exoplayer2.drm.ExoMediaDrm
    public PersistableBundle getMetrics() {
        if (Util.SDK_INT < 28) {
            return null;
        }
        return this.mediaDrm.getMetrics();
    }

    @Override // com.google.android.exoplayer2.drm.ExoMediaDrm
    public String getPropertyString(String propertyName) {
        return this.mediaDrm.getPropertyString(propertyName);
    }

    @Override // com.google.android.exoplayer2.drm.ExoMediaDrm
    public byte[] getPropertyByteArray(String propertyName) {
        return this.mediaDrm.getPropertyByteArray(propertyName);
    }

    @Override // com.google.android.exoplayer2.drm.ExoMediaDrm
    public void setPropertyString(String propertyName, String value) {
        this.mediaDrm.setPropertyString(propertyName, value);
    }

    @Override // com.google.android.exoplayer2.drm.ExoMediaDrm
    public void setPropertyByteArray(String propertyName, byte[] value) {
        this.mediaDrm.setPropertyByteArray(propertyName, value);
    }

    @Override // com.google.android.exoplayer2.drm.ExoMediaDrm
    public FrameworkMediaCrypto createMediaCrypto(byte[] initData) throws MediaCryptoException {
        boolean forceAllowInsecureDecoderComponents = Util.SDK_INT < 21 && C.WIDEVINE_UUID.equals(this.uuid) && "L3".equals(getPropertyString("securityLevel"));
        return new FrameworkMediaCrypto(adjustUuid(this.uuid), initData, forceAllowInsecureDecoderComponents);
    }

    @Override // com.google.android.exoplayer2.drm.ExoMediaDrm
    public Class<FrameworkMediaCrypto> getExoMediaCryptoType() {
        return FrameworkMediaCrypto.class;
    }

    private static DrmInitData.SchemeData getSchemeData(UUID uuid, List<DrmInitData.SchemeData> schemeDatas) {
        if (!C.WIDEVINE_UUID.equals(uuid)) {
            return schemeDatas.get(0);
        }
        if (Util.SDK_INT >= 28 && schemeDatas.size() > 1) {
            DrmInitData.SchemeData firstSchemeData = schemeDatas.get(0);
            int concatenatedDataLength = 0;
            boolean canConcatenateData = true;
            for (int i = 0; i < schemeDatas.size(); i++) {
                DrmInitData.SchemeData schemeData = schemeDatas.get(i);
                byte[] schemeDataData = (byte[]) Util.castNonNull(schemeData.data);
                if (Util.areEqual(schemeData.mimeType, firstSchemeData.mimeType) && Util.areEqual(schemeData.licenseServerUrl, firstSchemeData.licenseServerUrl) && PsshAtomUtil.isPsshAtom(schemeDataData)) {
                    concatenatedDataLength += schemeDataData.length;
                } else {
                    canConcatenateData = false;
                    break;
                }
            }
            if (canConcatenateData) {
                byte[] concatenatedData = new byte[concatenatedDataLength];
                int concatenatedDataPosition = 0;
                for (int i2 = 0; i2 < schemeDatas.size(); i2++) {
                    byte[] schemeDataData2 = (byte[]) Util.castNonNull(schemeDatas.get(i2).data);
                    int schemeDataLength = schemeDataData2.length;
                    System.arraycopy(schemeDataData2, 0, concatenatedData, concatenatedDataPosition, schemeDataLength);
                    concatenatedDataPosition += schemeDataLength;
                }
                return firstSchemeData.copyWithData(concatenatedData);
            }
        }
        for (int i3 = 0; i3 < schemeDatas.size(); i3++) {
            DrmInitData.SchemeData schemeData2 = schemeDatas.get(i3);
            int version = PsshAtomUtil.parseVersion((byte[]) Util.castNonNull(schemeData2.data));
            if (Util.SDK_INT < 23 && version == 0) {
                return schemeData2;
            }
            if (Util.SDK_INT >= 23 && version == 1) {
                return schemeData2;
            }
        }
        return schemeDatas.get(0);
    }

    private static UUID adjustUuid(UUID uuid) {
        return (Util.SDK_INT >= 27 || !C.CLEARKEY_UUID.equals(uuid)) ? uuid : C.COMMON_PSSH_UUID;
    }

    private static byte[] adjustRequestInitData(UUID uuid, byte[] initData) {
        byte[] psshData;
        if (C.PLAYREADY_UUID.equals(uuid)) {
            byte[] schemeSpecificData = PsshAtomUtil.parseSchemeSpecificData(initData, uuid);
            if (schemeSpecificData == null) {
                schemeSpecificData = initData;
            }
            initData = PsshAtomUtil.buildPsshAtom(C.PLAYREADY_UUID, addLaUrlAttributeIfMissing(schemeSpecificData));
        }
        if (((Util.SDK_INT < 23 && C.WIDEVINE_UUID.equals(uuid)) || (C.PLAYREADY_UUID.equals(uuid) && "Amazon".equals(Util.MANUFACTURER) && ("AFTB".equals(Util.MODEL) || "AFTS".equals(Util.MODEL) || "AFTM".equals(Util.MODEL) || "AFTT".equals(Util.MODEL)))) && (psshData = PsshAtomUtil.parseSchemeSpecificData(initData, uuid)) != null) {
            return psshData;
        }
        return initData;
    }

    private static String adjustRequestMimeType(UUID uuid, String mimeType) {
        if (Util.SDK_INT < 26 && C.CLEARKEY_UUID.equals(uuid) && (MimeTypes.VIDEO_MP4.equals(mimeType) || MimeTypes.AUDIO_MP4.equals(mimeType))) {
            return "cenc";
        }
        return mimeType;
    }

    private static byte[] adjustRequestData(UUID uuid, byte[] requestData) {
        if (C.CLEARKEY_UUID.equals(uuid)) {
            return ClearKeyUtil.adjustRequestData(requestData);
        }
        return requestData;
    }

    private static void forceWidevineL3(MediaDrm mediaDrm) {
        mediaDrm.setPropertyString("securityLevel", "L3");
    }

    private static boolean needsForceWidevineL3Workaround() {
        return "ASUS_Z00AD".equals(Util.MODEL);
    }

    private static byte[] addLaUrlAttributeIfMissing(byte[] data) {
        ParsableByteArray byteArray = new ParsableByteArray(data);
        int length = byteArray.readLittleEndianInt();
        int objectRecordCount = byteArray.readLittleEndianShort();
        int recordType = byteArray.readLittleEndianShort();
        if (objectRecordCount != 1 || recordType != 1) {
            Log.i(TAG, "Unexpected record count or type. Skipping LA_URL workaround.");
            return data;
        }
        int recordLength = byteArray.readLittleEndianShort();
        String xml = byteArray.readString(recordLength, Charset.forName(C.UTF16LE_NAME));
        if (xml.contains("<LA_URL>")) {
            return data;
        }
        int endOfDataTagIndex = xml.indexOf("</DATA>");
        if (endOfDataTagIndex == -1) {
            Log.w(TAG, "Could not find the </DATA> tag. Skipping LA_URL workaround.");
        }
        String xmlWithMockLaUrl = xml.substring(0, endOfDataTagIndex) + MOCK_LA_URL + xml.substring(endOfDataTagIndex);
        int extraBytes = MOCK_LA_URL.length() * 2;
        ByteBuffer newData = ByteBuffer.allocate(length + extraBytes);
        newData.order(ByteOrder.LITTLE_ENDIAN);
        newData.putInt(length + extraBytes);
        newData.putShort((short) objectRecordCount);
        newData.putShort((short) recordType);
        newData.putShort((short) (xmlWithMockLaUrl.length() * 2));
        newData.put(xmlWithMockLaUrl.getBytes(Charset.forName(C.UTF16LE_NAME)));
        return newData.array();
    }
}
