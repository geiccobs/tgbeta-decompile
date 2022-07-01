package com.microsoft.appcenter.utils.crypto;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.Base64;
import com.microsoft.appcenter.utils.AppCenterLog;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.CertificateExpiredException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Date;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
/* loaded from: classes.dex */
public class CryptoUtils {
    static final ICryptoFactory DEFAULT_CRYPTO_FACTORY = new ICryptoFactory() { // from class: com.microsoft.appcenter.utils.crypto.CryptoUtils.1
        @Override // com.microsoft.appcenter.utils.crypto.CryptoUtils.ICryptoFactory
        public IKeyGenerator getKeyGenerator(String str, String str2) throws Exception {
            final KeyGenerator keyGenerator = KeyGenerator.getInstance(str, str2);
            return new IKeyGenerator(this) { // from class: com.microsoft.appcenter.utils.crypto.CryptoUtils.1.1
                @Override // com.microsoft.appcenter.utils.crypto.CryptoUtils.IKeyGenerator
                public void init(AlgorithmParameterSpec algorithmParameterSpec) throws Exception {
                    keyGenerator.init(algorithmParameterSpec);
                }

                @Override // com.microsoft.appcenter.utils.crypto.CryptoUtils.IKeyGenerator
                public void generateKey() {
                    keyGenerator.generateKey();
                }
            };
        }

        @Override // com.microsoft.appcenter.utils.crypto.CryptoUtils.ICryptoFactory
        public ICipher getCipher(String str, String str2) throws Exception {
            final Cipher cipher = Cipher.getInstance(str, str2);
            return new ICipher(this) { // from class: com.microsoft.appcenter.utils.crypto.CryptoUtils.1.2
                @Override // com.microsoft.appcenter.utils.crypto.CryptoUtils.ICipher
                public void init(int i, Key key) throws Exception {
                    cipher.init(i, key);
                }

                @Override // com.microsoft.appcenter.utils.crypto.CryptoUtils.ICipher
                public void init(int i, Key key, AlgorithmParameterSpec algorithmParameterSpec) throws Exception {
                    cipher.init(i, key, algorithmParameterSpec);
                }

                @Override // com.microsoft.appcenter.utils.crypto.CryptoUtils.ICipher
                public byte[] doFinal(byte[] bArr) throws Exception {
                    return cipher.doFinal(bArr);
                }

                @Override // com.microsoft.appcenter.utils.crypto.CryptoUtils.ICipher
                public byte[] doFinal(byte[] bArr, int i, int i2) throws Exception {
                    return cipher.doFinal(bArr, i, i2);
                }

                @Override // com.microsoft.appcenter.utils.crypto.CryptoUtils.ICipher
                public byte[] getIV() {
                    return cipher.getIV();
                }

                @Override // com.microsoft.appcenter.utils.crypto.CryptoUtils.ICipher
                public int getBlockSize() {
                    return cipher.getBlockSize();
                }
            };
        }
    };
    @SuppressLint({"StaticFieldLeak"})
    private static CryptoUtils sInstance;
    private final int mApiLevel;
    private final Context mContext;
    private final ICryptoFactory mCryptoFactory;
    private final Map<String, CryptoHandlerEntry> mCryptoHandlers;
    private final KeyStore mKeyStore;

    /* loaded from: classes.dex */
    public interface ICipher {
        byte[] doFinal(byte[] bArr) throws Exception;

        byte[] doFinal(byte[] bArr, int i, int i2) throws Exception;

        int getBlockSize();

        byte[] getIV();

        void init(int i, Key key) throws Exception;

        void init(int i, Key key, AlgorithmParameterSpec algorithmParameterSpec) throws Exception;
    }

    /* loaded from: classes.dex */
    public interface ICryptoFactory {
        ICipher getCipher(String str, String str2) throws Exception;

        IKeyGenerator getKeyGenerator(String str, String str2) throws Exception;
    }

    /* loaded from: classes.dex */
    interface IKeyGenerator {
        void generateKey();

        void init(AlgorithmParameterSpec algorithmParameterSpec) throws Exception;
    }

    private CryptoUtils(Context context) {
        this(context, DEFAULT_CRYPTO_FACTORY, Build.VERSION.SDK_INT);
    }

    /* JADX WARN: Removed duplicated region for block: B:21:0x0044 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    @android.annotation.TargetApi(org.telegram.messenger.R.styleable.MapAttrs_zOrderOnTop)
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    CryptoUtils(android.content.Context r3, com.microsoft.appcenter.utils.crypto.CryptoUtils.ICryptoFactory r4, int r5) {
        /*
            r2 = this;
            r2.<init>()
            java.util.LinkedHashMap r0 = new java.util.LinkedHashMap
            r0.<init>()
            r2.mCryptoHandlers = r0
            android.content.Context r3 = r3.getApplicationContext()
            r2.mContext = r3
            r2.mCryptoFactory = r4
            r2.mApiLevel = r5
            r3 = 0
            java.lang.String r4 = "AppCenter"
            r0 = 19
            if (r5 < r0) goto L2c
            java.lang.String r0 = "AndroidKeyStore"
            java.security.KeyStore r0 = java.security.KeyStore.getInstance(r0)     // Catch: java.lang.Exception -> L27
            r0.load(r3)     // Catch: java.lang.Exception -> L26
            r3 = r0
            goto L2c
        L26:
            r3 = r0
        L27:
            java.lang.String r0 = "Cannot use secure keystore on this device."
            com.microsoft.appcenter.utils.AppCenterLog.error(r4, r0)
        L2c:
            r2.mKeyStore = r3
            if (r3 == 0) goto L42
            r0 = 23
            if (r5 < r0) goto L42
            com.microsoft.appcenter.utils.crypto.CryptoAesHandler r5 = new com.microsoft.appcenter.utils.crypto.CryptoAesHandler     // Catch: java.lang.Exception -> L3d
            r5.<init>()     // Catch: java.lang.Exception -> L3d
            r2.registerHandler(r5)     // Catch: java.lang.Exception -> L3d
            goto L42
        L3d:
            java.lang.String r5 = "Cannot use modern encryption on this device."
            com.microsoft.appcenter.utils.AppCenterLog.error(r4, r5)
        L42:
            if (r3 == 0) goto L52
            com.microsoft.appcenter.utils.crypto.CryptoRsaHandler r3 = new com.microsoft.appcenter.utils.crypto.CryptoRsaHandler     // Catch: java.lang.Exception -> L4d
            r3.<init>()     // Catch: java.lang.Exception -> L4d
            r2.registerHandler(r3)     // Catch: java.lang.Exception -> L4d
            goto L52
        L4d:
            java.lang.String r3 = "Cannot use old encryption on this device."
            com.microsoft.appcenter.utils.AppCenterLog.error(r4, r3)
        L52:
            com.microsoft.appcenter.utils.crypto.CryptoNoOpHandler r3 = new com.microsoft.appcenter.utils.crypto.CryptoNoOpHandler
            r3.<init>()
            java.util.Map<java.lang.String, com.microsoft.appcenter.utils.crypto.CryptoUtils$CryptoHandlerEntry> r4 = r2.mCryptoHandlers
            java.lang.String r5 = r3.getAlgorithm()
            com.microsoft.appcenter.utils.crypto.CryptoUtils$CryptoHandlerEntry r0 = new com.microsoft.appcenter.utils.crypto.CryptoUtils$CryptoHandlerEntry
            r1 = 0
            r0.<init>(r1, r3)
            r4.put(r5, r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.microsoft.appcenter.utils.crypto.CryptoUtils.<init>(android.content.Context, com.microsoft.appcenter.utils.crypto.CryptoUtils$ICryptoFactory, int):void");
    }

    public static CryptoUtils getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new CryptoUtils(context);
        }
        return sInstance;
    }

    private void registerHandler(CryptoHandler cryptoHandler) throws Exception {
        int i = 0;
        String alias = getAlias(cryptoHandler, 0);
        String alias2 = getAlias(cryptoHandler, 1);
        Date creationDate = this.mKeyStore.getCreationDate(alias);
        Date creationDate2 = this.mKeyStore.getCreationDate(alias2);
        if (creationDate2 != null && creationDate2.after(creationDate)) {
            alias = alias2;
            i = 1;
        }
        if (this.mCryptoHandlers.isEmpty() && !this.mKeyStore.containsAlias(alias)) {
            AppCenterLog.debug("AppCenter", "Creating alias: " + alias);
            cryptoHandler.generateKey(this.mCryptoFactory, alias, this.mContext);
        }
        AppCenterLog.debug("AppCenter", "Using " + alias);
        this.mCryptoHandlers.put(cryptoHandler.getAlgorithm(), new CryptoHandlerEntry(i, cryptoHandler));
    }

    private String getAlias(CryptoHandler cryptoHandler, int i) {
        return "appcenter." + i + "." + cryptoHandler.getAlgorithm();
    }

    private KeyStore.Entry getKeyStoreEntry(CryptoHandlerEntry cryptoHandlerEntry) throws Exception {
        return getKeyStoreEntry(cryptoHandlerEntry.mCryptoHandler, cryptoHandlerEntry.mAliasIndex);
    }

    private KeyStore.Entry getKeyStoreEntry(CryptoHandler cryptoHandler, int i) throws Exception {
        if (this.mKeyStore == null) {
            return null;
        }
        return this.mKeyStore.getEntry(getAlias(cryptoHandler, i), null);
    }

    public String encrypt(String str) {
        if (str == null) {
            return null;
        }
        try {
            CryptoHandlerEntry next = this.mCryptoHandlers.values().iterator().next();
            CryptoHandler cryptoHandler = next.mCryptoHandler;
            try {
                String encodeToString = Base64.encodeToString(cryptoHandler.encrypt(this.mCryptoFactory, this.mApiLevel, getKeyStoreEntry(next), str.getBytes("UTF-8")), 0);
                return cryptoHandler.getAlgorithm() + ":" + encodeToString;
            } catch (InvalidKeyException e) {
                if (!(e.getCause() instanceof CertificateExpiredException) && !"android.security.keystore.KeyExpiredException".equals(e.getClass().getName())) {
                    throw e;
                }
                AppCenterLog.debug("AppCenter", "Alias expired: " + next.mAliasIndex);
                int i = next.mAliasIndex ^ 1;
                next.mAliasIndex = i;
                String alias = getAlias(cryptoHandler, i);
                if (this.mKeyStore.containsAlias(alias)) {
                    AppCenterLog.debug("AppCenter", "Deleting alias: " + alias);
                    this.mKeyStore.deleteEntry(alias);
                }
                AppCenterLog.debug("AppCenter", "Creating alias: " + alias);
                cryptoHandler.generateKey(this.mCryptoFactory, alias, this.mContext);
                return encrypt(str);
            }
        } catch (Exception unused) {
            AppCenterLog.error("AppCenter", "Failed to encrypt data.");
            return str;
        }
    }

    public DecryptedData decrypt(String str) {
        if (str == null) {
            return new DecryptedData(null, null);
        }
        String[] split = str.split(":");
        CryptoHandlerEntry cryptoHandlerEntry = split.length == 2 ? this.mCryptoHandlers.get(split[0]) : null;
        CryptoHandler cryptoHandler = cryptoHandlerEntry == null ? null : cryptoHandlerEntry.mCryptoHandler;
        if (cryptoHandler == null) {
            AppCenterLog.error("AppCenter", "Failed to decrypt data.");
            return new DecryptedData(str, null);
        }
        try {
            try {
                return getDecryptedData(cryptoHandler, cryptoHandlerEntry.mAliasIndex, split[1]);
            } catch (Exception unused) {
                return getDecryptedData(cryptoHandler, cryptoHandlerEntry.mAliasIndex ^ 1, split[1]);
            }
        } catch (Exception unused2) {
            AppCenterLog.error("AppCenter", "Failed to decrypt data.");
            return new DecryptedData(str, null);
        }
    }

    private DecryptedData getDecryptedData(CryptoHandler cryptoHandler, int i, String str) throws Exception {
        String str2 = new String(cryptoHandler.decrypt(this.mCryptoFactory, this.mApiLevel, getKeyStoreEntry(cryptoHandler, i), Base64.decode(str, 0)), "UTF-8");
        return new DecryptedData(str2, cryptoHandler != this.mCryptoHandlers.values().iterator().next().mCryptoHandler ? encrypt(str2) : null);
    }

    /* loaded from: classes.dex */
    public static class CryptoHandlerEntry {
        int mAliasIndex;
        final CryptoHandler mCryptoHandler;

        CryptoHandlerEntry(int i, CryptoHandler cryptoHandler) {
            this.mAliasIndex = i;
            this.mCryptoHandler = cryptoHandler;
        }
    }

    /* loaded from: classes.dex */
    public static class DecryptedData {
        final String mDecryptedData;
        final String mNewEncryptedData;

        public DecryptedData(String str, String str2) {
            this.mDecryptedData = str;
            this.mNewEncryptedData = str2;
        }

        public String getDecryptedData() {
            return this.mDecryptedData;
        }

        public String getNewEncryptedData() {
            return this.mNewEncryptedData;
        }
    }
}
