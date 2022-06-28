package org.telegram.messenger;

import android.content.SharedPreferences;
import android.net.Uri;
import android.util.SparseArray;
import android.util.SparseIntArray;
import com.microsoft.appcenter.distribute.DistributeConstants;
import java.io.File;
import java.io.FileDescriptor;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.util.ArrayList;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.WriteToSocketDelegate;
/* loaded from: classes4.dex */
public class FileUploadOperation {
    private static final int initialRequestsCount = 8;
    private static final int initialRequestsSlowNetworkCount = 1;
    private static final int maxUploadingKBytes = 2048;
    private static final int maxUploadingSlowNetworkKBytes = 32;
    private static final int minUploadChunkSize = 128;
    private static final int minUploadChunkSlowNetworkSize = 32;
    private long availableSize;
    private int currentAccount;
    private long currentFileId;
    private int currentPartNum;
    private int currentType;
    private int currentUploadRequetsCount;
    private FileUploadOperationDelegate delegate;
    private long estimatedSize;
    private String fileKey;
    private int fingerprint;
    private boolean forceSmallFile;
    private ArrayList<byte[]> freeRequestIvs;
    private boolean isBigFile;
    private boolean isEncrypted;
    private boolean isLastPart;
    private byte[] iv;
    private byte[] ivChange;
    private byte[] key;
    protected long lastProgressUpdateTime;
    private int lastSavedPartNum;
    private int maxRequestsCount;
    private boolean nextPartFirst;
    private int operationGuid;
    private SharedPreferences preferences;
    private byte[] readBuffer;
    private long readBytesCount;
    private int requestNum;
    private int saveInfoTimes;
    private boolean slowNetwork;
    private boolean started;
    private int state;
    private RandomAccessFile stream;
    private long totalFileSize;
    private int totalPartsCount;
    private boolean uploadFirstPartLater;
    private int uploadStartTime;
    private long uploadedBytesCount;
    private String uploadingFilePath;
    private int uploadChunkSize = 65536;
    private SparseIntArray requestTokens = new SparseIntArray();
    private SparseArray<UploadCachedResult> cachedResults = new SparseArray<>();

    /* loaded from: classes4.dex */
    public interface FileUploadOperationDelegate {
        void didChangedUploadProgress(FileUploadOperation fileUploadOperation, long j, long j2);

        void didFailedUploadingFile(FileUploadOperation fileUploadOperation);

        void didFinishUploadingFile(FileUploadOperation fileUploadOperation, TLRPC.InputFile inputFile, TLRPC.InputEncryptedFile inputEncryptedFile, byte[] bArr, byte[] bArr2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class UploadCachedResult {
        private long bytesOffset;
        private byte[] iv;

        private UploadCachedResult() {
        }
    }

    public FileUploadOperation(int instance, String location, boolean encrypted, long estimated, int type) {
        this.currentAccount = instance;
        this.uploadingFilePath = location;
        this.isEncrypted = encrypted;
        this.estimatedSize = estimated;
        this.currentType = type;
        this.uploadFirstPartLater = estimated != 0 && !encrypted;
    }

    public long getTotalFileSize() {
        return this.totalFileSize;
    }

    public void setDelegate(FileUploadOperationDelegate fileUploadOperationDelegate) {
        this.delegate = fileUploadOperationDelegate;
    }

    public void start() {
        if (this.state != 0) {
            return;
        }
        this.state = 1;
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.FileUploadOperation$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                FileUploadOperation.this.m272lambda$start$0$orgtelegrammessengerFileUploadOperation();
            }
        });
    }

    /* renamed from: lambda$start$0$org-telegram-messenger-FileUploadOperation */
    public /* synthetic */ void m272lambda$start$0$orgtelegrammessengerFileUploadOperation() {
        this.preferences = ApplicationLoader.applicationContext.getSharedPreferences("uploadinfo", 0);
        this.slowNetwork = ApplicationLoader.isConnectionSlow();
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("start upload on slow network = " + this.slowNetwork);
        }
        int count = this.slowNetwork ? 1 : 8;
        for (int a = 0; a < count; a++) {
            startUploadRequest();
        }
    }

    public void onNetworkChanged(final boolean slow) {
        if (this.state != 1) {
            return;
        }
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.FileUploadOperation$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                FileUploadOperation.this.m271x2bbbbcc2(slow);
            }
        });
    }

    /* renamed from: lambda$onNetworkChanged$1$org-telegram-messenger-FileUploadOperation */
    public /* synthetic */ void m271x2bbbbcc2(boolean slow) {
        int i;
        if (this.slowNetwork != slow) {
            this.slowNetwork = slow;
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("network changed to slow = " + this.slowNetwork);
            }
            int a = 0;
            while (true) {
                i = 1;
                if (a >= this.requestTokens.size()) {
                    break;
                }
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.requestTokens.valueAt(a), true);
                a++;
            }
            this.requestTokens.clear();
            cleanup();
            this.isLastPart = false;
            this.nextPartFirst = false;
            this.requestNum = 0;
            this.currentPartNum = 0;
            this.readBytesCount = 0L;
            this.uploadedBytesCount = 0L;
            this.saveInfoTimes = 0;
            this.key = null;
            this.iv = null;
            this.ivChange = null;
            this.currentUploadRequetsCount = 0;
            this.lastSavedPartNum = 0;
            this.uploadFirstPartLater = false;
            this.cachedResults.clear();
            this.operationGuid++;
            if (!this.slowNetwork) {
                i = 8;
            }
            int count = i;
            for (int a2 = 0; a2 < count; a2++) {
                startUploadRequest();
            }
        }
    }

    public void cancel() {
        if (this.state == 3) {
            return;
        }
        this.state = 2;
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.FileUploadOperation$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                FileUploadOperation.this.m269lambda$cancel$2$orgtelegrammessengerFileUploadOperation();
            }
        });
        this.delegate.didFailedUploadingFile(this);
        cleanup();
    }

    /* renamed from: lambda$cancel$2$org-telegram-messenger-FileUploadOperation */
    public /* synthetic */ void m269lambda$cancel$2$orgtelegrammessengerFileUploadOperation() {
        for (int a = 0; a < this.requestTokens.size(); a++) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.requestTokens.valueAt(a), true);
        }
    }

    private void cleanup() {
        if (this.preferences == null) {
            this.preferences = ApplicationLoader.applicationContext.getSharedPreferences("uploadinfo", 0);
        }
        SharedPreferences.Editor edit = this.preferences.edit();
        SharedPreferences.Editor remove = edit.remove(this.fileKey + "_time");
        SharedPreferences.Editor remove2 = remove.remove(this.fileKey + "_size");
        SharedPreferences.Editor remove3 = remove2.remove(this.fileKey + "_uploaded");
        SharedPreferences.Editor remove4 = remove3.remove(this.fileKey + "_id");
        SharedPreferences.Editor remove5 = remove4.remove(this.fileKey + "_iv");
        SharedPreferences.Editor remove6 = remove5.remove(this.fileKey + "_key");
        remove6.remove(this.fileKey + "_ivc").commit();
        try {
            RandomAccessFile randomAccessFile = this.stream;
            if (randomAccessFile != null) {
                randomAccessFile.close();
                this.stream = null;
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void checkNewDataAvailable(final long newAvailableSize, final long finalSize) {
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.FileUploadOperation$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                FileUploadOperation.this.m270x53f1b98(finalSize, newAvailableSize);
            }
        });
    }

    /* renamed from: lambda$checkNewDataAvailable$3$org-telegram-messenger-FileUploadOperation */
    public /* synthetic */ void m270x53f1b98(long finalSize, long newAvailableSize) {
        if (this.estimatedSize != 0 && finalSize != 0) {
            this.estimatedSize = 0L;
            this.totalFileSize = finalSize;
            calcTotalPartsCount();
            if (!this.uploadFirstPartLater && this.started) {
                storeFileUploadInfo();
            }
        }
        this.availableSize = finalSize > 0 ? finalSize : newAvailableSize;
        if (this.currentUploadRequetsCount < this.maxRequestsCount) {
            startUploadRequest();
        }
    }

    private void storeFileUploadInfo() {
        SharedPreferences.Editor editor = this.preferences.edit();
        editor.putInt(this.fileKey + "_time", this.uploadStartTime);
        editor.putLong(this.fileKey + "_size", this.totalFileSize);
        editor.putLong(this.fileKey + "_id", this.currentFileId);
        editor.remove(this.fileKey + "_uploaded");
        if (this.isEncrypted) {
            editor.putString(this.fileKey + "_iv", Utilities.bytesToHex(this.iv));
            editor.putString(this.fileKey + "_ivc", Utilities.bytesToHex(this.ivChange));
            editor.putString(this.fileKey + "_key", Utilities.bytesToHex(this.key));
        }
        editor.commit();
    }

    private void calcTotalPartsCount() {
        if (this.uploadFirstPartLater) {
            if (this.isBigFile) {
                long j = this.totalFileSize;
                int i = this.uploadChunkSize;
                this.totalPartsCount = ((int) ((((j - i) + i) - 1) / i)) + 1;
                return;
            }
            long j2 = this.totalFileSize - DistributeConstants.KIBIBYTE_IN_BYTES;
            int i2 = this.uploadChunkSize;
            this.totalPartsCount = ((int) (((j2 + i2) - 1) / i2)) + 1;
            return;
        }
        long j3 = this.totalFileSize;
        int i3 = this.uploadChunkSize;
        this.totalPartsCount = (int) (((j3 + i3) - 1) / i3);
    }

    public void setForceSmallFile() {
        this.forceSmallFile = true;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void startUploadRequest() {
        int currentRequestBytes;
        byte[] currentRequestIv;
        int currentRequestPartNum;
        TLRPC.TL_upload_saveFilePart tL_upload_saveFilePart;
        int connectionType;
        int i;
        boolean rewrite;
        if (this.state != 1) {
            return;
        }
        try {
            this.started = true;
            if (this.stream == null) {
                File cacheFile = new File(this.uploadingFilePath);
                if (AndroidUtilities.isInternalUri(Uri.fromFile(cacheFile))) {
                    throw new Exception("trying to upload internal file");
                }
                this.stream = new RandomAccessFile(cacheFile, "r");
                boolean isInternalFile = false;
                Method getInt = FileDescriptor.class.getDeclaredMethod("getInt$", new Class[0]);
                int fdint = ((Integer) getInt.invoke(this.stream.getFD(), new Object[0])).intValue();
                if (AndroidUtilities.isInternalUri(fdint)) {
                    isInternalFile = true;
                }
                if (isInternalFile) {
                    throw new Exception("trying to upload internal file");
                }
                long j = this.estimatedSize;
                if (j == 0) {
                    this.totalFileSize = cacheFile.length();
                } else {
                    this.totalFileSize = j;
                }
                if (!this.forceSmallFile && this.totalFileSize > 10485760) {
                    this.isBigFile = true;
                }
                long maxUploadParts = MessagesController.getInstance(this.currentAccount).uploadMaxFileParts;
                if (AccountInstance.getInstance(this.currentAccount).getUserConfig().isPremium() && this.totalFileSize > FileLoader.DEFAULT_MAX_FILE_SIZE) {
                    maxUploadParts = MessagesController.getInstance(this.currentAccount).uploadMaxFilePartsPremium;
                }
                int max = (int) Math.max(this.slowNetwork ? 32L : 128L, ((this.totalFileSize + (maxUploadParts * DistributeConstants.KIBIBYTE_IN_BYTES)) - 1) / (maxUploadParts * DistributeConstants.KIBIBYTE_IN_BYTES));
                this.uploadChunkSize = max;
                if (1024 % max != 0) {
                    int chunkSize = 64;
                    while (this.uploadChunkSize > chunkSize) {
                        chunkSize *= 2;
                    }
                    this.uploadChunkSize = chunkSize;
                }
                this.maxRequestsCount = Math.max(1, (this.slowNetwork ? 32 : 2048) / this.uploadChunkSize);
                if (this.isEncrypted) {
                    this.freeRequestIvs = new ArrayList<>(this.maxRequestsCount);
                    for (int a = 0; a < this.maxRequestsCount; a++) {
                        this.freeRequestIvs.add(new byte[32]);
                    }
                }
                int a2 = this.uploadChunkSize;
                this.uploadChunkSize = a2 * 1024;
                calcTotalPartsCount();
                this.readBuffer = new byte[this.uploadChunkSize];
                StringBuilder sb = new StringBuilder();
                sb.append(this.uploadingFilePath);
                sb.append(this.isEncrypted ? "enc" : "");
                this.fileKey = Utilities.MD5(sb.toString());
                long fileSize = this.preferences.getLong(this.fileKey + "_size", 0L);
                this.uploadStartTime = (int) (System.currentTimeMillis() / 1000);
                boolean rewrite2 = false;
                if (!this.uploadFirstPartLater && !this.nextPartFirst && this.estimatedSize == 0 && fileSize == this.totalFileSize) {
                    this.currentFileId = this.preferences.getLong(this.fileKey + "_id", 0L);
                    int date = this.preferences.getInt(this.fileKey + "_time", 0);
                    long uploadedSize = this.preferences.getLong(this.fileKey + "_uploaded", 0L);
                    if (this.isEncrypted) {
                        String ivString = this.preferences.getString(this.fileKey + "_iv", null);
                        String keyString = this.preferences.getString(this.fileKey + "_key", null);
                        if (ivString != null && keyString != null) {
                            this.key = Utilities.hexToBytes(keyString);
                            byte[] hexToBytes = Utilities.hexToBytes(ivString);
                            this.iv = hexToBytes;
                            byte[] bArr = this.key;
                            if (bArr != null && hexToBytes != null && bArr.length == 32 && hexToBytes.length == 32) {
                                byte[] bArr2 = new byte[32];
                                this.ivChange = bArr2;
                                System.arraycopy(hexToBytes, 0, bArr2, 0, 32);
                            } else {
                                rewrite2 = true;
                            }
                        } else {
                            rewrite2 = true;
                        }
                    }
                    if (!rewrite2 && date != 0) {
                        boolean z = this.isBigFile;
                        if (z && date < this.uploadStartTime - 86400) {
                            date = 0;
                        } else if (!z && date < this.uploadStartTime - 5400.0f) {
                            date = 0;
                        }
                        if (date != 0) {
                            if (uploadedSize > 0) {
                                this.readBytesCount = uploadedSize;
                                this.currentPartNum = (int) (uploadedSize / this.uploadChunkSize);
                                if (!z) {
                                    int b = 0;
                                    while (true) {
                                        long uploadedSize2 = uploadedSize;
                                        boolean isInternalFile2 = isInternalFile;
                                        if (b >= this.readBytesCount / this.uploadChunkSize) {
                                            break;
                                        }
                                        int bytesRead = this.stream.read(this.readBuffer);
                                        int toAdd = 0;
                                        if (this.isEncrypted && bytesRead % 16 != 0) {
                                            toAdd = 0 + (16 - (bytesRead % 16));
                                        }
                                        NativeByteBuffer sendBuffer = new NativeByteBuffer(bytesRead + toAdd);
                                        if (bytesRead != this.uploadChunkSize || this.totalPartsCount == this.currentPartNum + 1) {
                                            this.isLastPart = true;
                                        }
                                        sendBuffer.writeBytes(this.readBuffer, 0, bytesRead);
                                        if (this.isEncrypted) {
                                            for (int a3 = 0; a3 < toAdd; a3++) {
                                                sendBuffer.writeByte(0);
                                            }
                                            Utilities.aesIgeEncryption(sendBuffer.buffer, this.key, this.ivChange, true, true, 0, bytesRead + toAdd);
                                        }
                                        sendBuffer.reuse();
                                        b++;
                                        isInternalFile = isInternalFile2;
                                        uploadedSize = uploadedSize2;
                                    }
                                } else {
                                    this.stream.seek(uploadedSize);
                                    if (this.isEncrypted) {
                                        String ivcString = this.preferences.getString(this.fileKey + "_ivc", null);
                                        if (ivcString != null) {
                                            byte[] hexToBytes2 = Utilities.hexToBytes(ivcString);
                                            this.ivChange = hexToBytes2;
                                            if (hexToBytes2 == null || hexToBytes2.length != 32) {
                                                rewrite2 = true;
                                                this.readBytesCount = 0L;
                                                this.currentPartNum = 0;
                                            }
                                        } else {
                                            rewrite2 = true;
                                            this.readBytesCount = 0L;
                                            this.currentPartNum = 0;
                                        }
                                    }
                                }
                            } else {
                                rewrite2 = true;
                            }
                        }
                    } else {
                        rewrite2 = true;
                    }
                    rewrite = rewrite2;
                } else {
                    rewrite = true;
                }
                if (rewrite) {
                    if (this.isEncrypted) {
                        this.iv = new byte[32];
                        this.key = new byte[32];
                        this.ivChange = new byte[32];
                        Utilities.random.nextBytes(this.iv);
                        Utilities.random.nextBytes(this.key);
                        System.arraycopy(this.iv, 0, this.ivChange, 0, 32);
                    }
                    this.currentFileId = Utilities.random.nextLong();
                    if (!this.nextPartFirst && !this.uploadFirstPartLater && this.estimatedSize == 0) {
                        storeFileUploadInfo();
                    }
                }
                if (this.isEncrypted) {
                    try {
                        MessageDigest md = MessageDigest.getInstance("MD5");
                        byte[] arr = new byte[64];
                        System.arraycopy(this.key, 0, arr, 0, 32);
                        System.arraycopy(this.iv, 0, arr, 32, 32);
                        byte[] digest = md.digest(arr);
                        for (int a4 = 0; a4 < 4; a4++) {
                            this.fingerprint |= ((digest[a4] ^ digest[a4 + 4]) & 255) << (a4 * 8);
                        }
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
                this.uploadedBytesCount = this.readBytesCount;
                this.lastSavedPartNum = this.currentPartNum;
                if (this.uploadFirstPartLater) {
                    if (this.isBigFile) {
                        this.stream.seek(this.uploadChunkSize);
                        this.readBytesCount = this.uploadChunkSize;
                    } else {
                        this.stream.seek(DistributeConstants.KIBIBYTE_IN_BYTES);
                        this.readBytesCount = DistributeConstants.KIBIBYTE_IN_BYTES;
                    }
                    this.currentPartNum = 1;
                }
            }
            if (this.estimatedSize != 0 && this.readBytesCount + this.uploadChunkSize > this.availableSize) {
                return;
            }
            if (!this.nextPartFirst) {
                currentRequestBytes = this.stream.read(this.readBuffer);
            } else {
                this.stream.seek(0L);
                if (this.isBigFile) {
                    currentRequestBytes = this.stream.read(this.readBuffer);
                    i = 0;
                } else {
                    i = 0;
                    currentRequestBytes = this.stream.read(this.readBuffer, 0, 1024);
                }
                this.currentPartNum = i;
            }
            if (currentRequestBytes == -1) {
                return;
            }
            int toAdd2 = 0;
            if (this.isEncrypted && currentRequestBytes % 16 != 0) {
                toAdd2 = 0 + (16 - (currentRequestBytes % 16));
            }
            NativeByteBuffer sendBuffer2 = new NativeByteBuffer(currentRequestBytes + toAdd2);
            if (this.nextPartFirst || currentRequestBytes != this.uploadChunkSize || (this.estimatedSize == 0 && this.totalPartsCount == this.currentPartNum + 1)) {
                if (this.uploadFirstPartLater) {
                    this.nextPartFirst = true;
                    this.uploadFirstPartLater = false;
                } else {
                    this.isLastPart = true;
                }
            }
            sendBuffer2.writeBytes(this.readBuffer, 0, currentRequestBytes);
            if (this.isEncrypted) {
                for (int a5 = 0; a5 < toAdd2; a5++) {
                    sendBuffer2.writeByte(0);
                }
                Utilities.aesIgeEncryption(sendBuffer2.buffer, this.key, this.ivChange, true, true, 0, currentRequestBytes + toAdd2);
                byte[] currentRequestIv2 = this.freeRequestIvs.get(0);
                System.arraycopy(this.ivChange, 0, currentRequestIv2, 0, 32);
                this.freeRequestIvs.remove(0);
                currentRequestIv = currentRequestIv2;
            } else {
                currentRequestIv = null;
            }
            if (this.isBigFile) {
                TLRPC.TL_upload_saveBigFilePart req = new TLRPC.TL_upload_saveBigFilePart();
                int currentRequestPartNum2 = this.currentPartNum;
                req.file_part = currentRequestPartNum2;
                req.file_id = this.currentFileId;
                if (this.estimatedSize != 0) {
                    req.file_total_parts = -1;
                } else {
                    req.file_total_parts = this.totalPartsCount;
                }
                req.bytes = sendBuffer2;
                tL_upload_saveFilePart = req;
                currentRequestPartNum = currentRequestPartNum2;
            } else {
                TLRPC.TL_upload_saveFilePart req2 = new TLRPC.TL_upload_saveFilePart();
                int currentRequestPartNum3 = this.currentPartNum;
                req2.file_part = currentRequestPartNum3;
                req2.file_id = this.currentFileId;
                req2.bytes = sendBuffer2;
                tL_upload_saveFilePart = req2;
                currentRequestPartNum = currentRequestPartNum3;
            }
            if (this.isLastPart && this.nextPartFirst) {
                this.nextPartFirst = false;
                this.currentPartNum = this.totalPartsCount - 1;
                this.stream.seek(this.totalFileSize);
            }
            this.readBytesCount += currentRequestBytes;
            this.currentPartNum++;
            this.currentUploadRequetsCount++;
            final int requestNumFinal = this.requestNum;
            this.requestNum = requestNumFinal + 1;
            final long currentRequestBytesOffset = currentRequestPartNum + currentRequestBytes;
            final int requestSize = tL_upload_saveFilePart.getObjectSize() + 4;
            final int currentOperationGuid = this.operationGuid;
            if (this.slowNetwork) {
                connectionType = 4;
            } else {
                int connectionType2 = requestNumFinal % 4;
                connectionType = (connectionType2 << 16) | 4;
            }
            final byte[] bArr3 = currentRequestIv;
            final int i2 = currentRequestBytes;
            final int i3 = currentRequestPartNum;
            int requestToken = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_upload_saveFilePart, new RequestDelegate() { // from class: org.telegram.messenger.FileUploadOperation$$ExternalSyntheticLambda5
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    FileUploadOperation.this.m273xfe4e3d8(currentOperationGuid, requestSize, bArr3, requestNumFinal, i2, i3, currentRequestBytesOffset, tLObject, tL_error);
                }
            }, null, new WriteToSocketDelegate() { // from class: org.telegram.messenger.FileUploadOperation$$ExternalSyntheticLambda6
                @Override // org.telegram.tgnet.WriteToSocketDelegate
                public final void run() {
                    FileUploadOperation.this.m275x95bdb696();
                }
            }, this.forceSmallFile ? 4 : 0, Integer.MAX_VALUE, connectionType, true);
            this.requestTokens.put(requestNumFinal, requestToken);
        } catch (Exception e2) {
            FileLog.e(e2);
            this.state = 4;
            this.delegate.didFailedUploadingFile(this);
            cleanup();
        }
    }

    /* renamed from: lambda$startUploadRequest$4$org-telegram-messenger-FileUploadOperation */
    public /* synthetic */ void m273xfe4e3d8(int currentOperationGuid, int requestSize, byte[] currentRequestIv, int requestNumFinal, int currentRequestBytes, int currentRequestPartNum, long currentRequestBytesOffset, TLObject response, TLRPC.TL_error error) {
        long size;
        int i;
        TLRPC.InputEncryptedFile result;
        TLRPC.InputFile result2;
        if (currentOperationGuid != this.operationGuid) {
            return;
        }
        int networkType = response != null ? response.networkType : ApplicationLoader.getCurrentNetworkType();
        int i2 = this.currentType;
        if (i2 == 50331648) {
            StatsController.getInstance(this.currentAccount).incrementSentBytesCount(networkType, 3, requestSize);
        } else if (i2 == 33554432) {
            StatsController.getInstance(this.currentAccount).incrementSentBytesCount(networkType, 2, requestSize);
        } else if (i2 == 16777216) {
            StatsController.getInstance(this.currentAccount).incrementSentBytesCount(networkType, 4, requestSize);
        } else if (i2 == 67108864) {
            StatsController.getInstance(this.currentAccount).incrementSentBytesCount(networkType, 5, requestSize);
        }
        if (currentRequestIv != null) {
            this.freeRequestIvs.add(currentRequestIv);
        }
        this.requestTokens.delete(requestNumFinal);
        if (!(response instanceof TLRPC.TL_boolTrue)) {
            this.state = 4;
            this.delegate.didFailedUploadingFile(this);
            cleanup();
        } else if (this.state == 1) {
            this.uploadedBytesCount += currentRequestBytes;
            long j = this.estimatedSize;
            if (j != 0) {
                size = Math.max(this.availableSize, j);
            } else {
                long size2 = this.totalFileSize;
                size = size2;
            }
            this.delegate.didChangedUploadProgress(this, this.uploadedBytesCount, size);
            int i3 = this.currentUploadRequetsCount - 1;
            this.currentUploadRequetsCount = i3;
            if (this.isLastPart && i3 == 0 && this.state == 1) {
                this.state = 3;
                if (this.key == null) {
                    if (this.isBigFile) {
                        result2 = new TLRPC.TL_inputFileBig();
                    } else {
                        TLRPC.InputFile tL_inputFile = new TLRPC.TL_inputFile();
                        tL_inputFile.md5_checksum = "";
                        result2 = tL_inputFile;
                    }
                    result2.parts = this.currentPartNum;
                    result2.id = this.currentFileId;
                    String str = this.uploadingFilePath;
                    result2.name = str.substring(str.lastIndexOf("/") + 1);
                    i = 3;
                    this.delegate.didFinishUploadingFile(this, result2, null, null, null);
                    cleanup();
                } else {
                    i = 3;
                    if (this.isBigFile) {
                        result = new TLRPC.TL_inputEncryptedFileBigUploaded();
                    } else {
                        TLRPC.InputEncryptedFile tL_inputEncryptedFileUploaded = new TLRPC.TL_inputEncryptedFileUploaded();
                        tL_inputEncryptedFileUploaded.md5_checksum = "";
                        result = tL_inputEncryptedFileUploaded;
                    }
                    result.parts = this.currentPartNum;
                    result.id = this.currentFileId;
                    result.key_fingerprint = this.fingerprint;
                    this.delegate.didFinishUploadingFile(this, null, result, this.key, this.iv);
                    cleanup();
                }
                int i4 = this.currentType;
                if (i4 == 50331648) {
                    StatsController.getInstance(this.currentAccount).incrementSentItemsCount(ApplicationLoader.getCurrentNetworkType(), i, 1);
                } else if (i4 == 33554432) {
                    StatsController.getInstance(this.currentAccount).incrementSentItemsCount(ApplicationLoader.getCurrentNetworkType(), 2, 1);
                } else if (i4 == 16777216) {
                    StatsController.getInstance(this.currentAccount).incrementSentItemsCount(ApplicationLoader.getCurrentNetworkType(), 4, 1);
                } else if (i4 == 67108864) {
                    StatsController.getInstance(this.currentAccount).incrementSentItemsCount(ApplicationLoader.getCurrentNetworkType(), 5, 1);
                }
            } else if (i3 < this.maxRequestsCount) {
                if (this.estimatedSize == 0 && !this.uploadFirstPartLater && !this.nextPartFirst) {
                    if (this.saveInfoTimes >= 4) {
                        this.saveInfoTimes = 0;
                    }
                    int i5 = this.lastSavedPartNum;
                    if (currentRequestPartNum == i5) {
                        this.lastSavedPartNum = i5 + 1;
                        long offsetToSave = currentRequestBytesOffset;
                        byte[] ivToSave = currentRequestIv;
                        while (true) {
                            UploadCachedResult result3 = this.cachedResults.get(this.lastSavedPartNum);
                            if (result3 == null) {
                                break;
                            }
                            offsetToSave = result3.bytesOffset;
                            ivToSave = result3.iv;
                            this.cachedResults.remove(this.lastSavedPartNum);
                            this.lastSavedPartNum++;
                        }
                        boolean z = this.isBigFile;
                        if ((z && offsetToSave % 1048576 == 0) || (!z && this.saveInfoTimes == 0)) {
                            SharedPreferences.Editor editor = this.preferences.edit();
                            editor.putLong(this.fileKey + "_uploaded", offsetToSave);
                            if (this.isEncrypted) {
                                editor.putString(this.fileKey + "_ivc", Utilities.bytesToHex(ivToSave));
                            }
                            editor.commit();
                        }
                    } else {
                        UploadCachedResult result4 = new UploadCachedResult();
                        result4.bytesOffset = currentRequestBytesOffset;
                        if (currentRequestIv != null) {
                            result4.iv = new byte[32];
                            System.arraycopy(currentRequestIv, 0, result4.iv, 0, 32);
                        }
                        this.cachedResults.put(currentRequestPartNum, result4);
                    }
                    this.saveInfoTimes++;
                }
                startUploadRequest();
            }
        }
    }

    /* renamed from: lambda$startUploadRequest$6$org-telegram-messenger-FileUploadOperation */
    public /* synthetic */ void m275x95bdb696() {
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.FileUploadOperation$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                FileUploadOperation.this.m274xd2d14d37();
            }
        });
    }

    /* renamed from: lambda$startUploadRequest$5$org-telegram-messenger-FileUploadOperation */
    public /* synthetic */ void m274xd2d14d37() {
        if (this.currentUploadRequetsCount < this.maxRequestsCount) {
            startUploadRequest();
        }
    }
}
