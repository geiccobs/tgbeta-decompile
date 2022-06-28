package com.microsoft.appcenter.utils.crypto;

import android.content.Context;
import android.os.Build;
import android.util.Base64;
import com.microsoft.appcenter.Constants;
import com.microsoft.appcenter.utils.AppCenterLog;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.CertificateExpiredException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
/* loaded from: classes3.dex */
public class CryptoUtils {
    static final ICryptoFactory DEFAULT_CRYPTO_FACTORY = new ICryptoFactory() { // from class: com.microsoft.appcenter.utils.crypto.CryptoUtils.1
        @Override // com.microsoft.appcenter.utils.crypto.CryptoUtils.ICryptoFactory
        public IKeyGenerator getKeyGenerator(String algorithm, String provider) throws Exception {
            final KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm, provider);
            return new IKeyGenerator() { // from class: com.microsoft.appcenter.utils.crypto.CryptoUtils.1.1
                @Override // com.microsoft.appcenter.utils.crypto.CryptoUtils.IKeyGenerator
                public void init(AlgorithmParameterSpec parameters) throws Exception {
                    keyGenerator.init(parameters);
                }

                @Override // com.microsoft.appcenter.utils.crypto.CryptoUtils.IKeyGenerator
                public void generateKey() {
                    keyGenerator.generateKey();
                }
            };
        }

        @Override // com.microsoft.appcenter.utils.crypto.CryptoUtils.ICryptoFactory
        public ICipher getCipher(String transformation, String provider) throws Exception {
            final Cipher cipher = Cipher.getInstance(transformation, provider);
            return new ICipher() { // from class: com.microsoft.appcenter.utils.crypto.CryptoUtils.1.2
                @Override // com.microsoft.appcenter.utils.crypto.CryptoUtils.ICipher
                public void init(int opMode, Key key) throws Exception {
                    cipher.init(opMode, key);
                }

                @Override // com.microsoft.appcenter.utils.crypto.CryptoUtils.ICipher
                public void init(int opMode, Key key, AlgorithmParameterSpec params) throws Exception {
                    cipher.init(opMode, key, params);
                }

                @Override // com.microsoft.appcenter.utils.crypto.CryptoUtils.ICipher
                public byte[] doFinal(byte[] input) throws Exception {
                    return cipher.doFinal(input);
                }

                @Override // com.microsoft.appcenter.utils.crypto.CryptoUtils.ICipher
                public byte[] doFinal(byte[] input, int inputOffset, int inputLength) throws Exception {
                    return cipher.doFinal(input, inputOffset, inputLength);
                }

                @Override // com.microsoft.appcenter.utils.crypto.CryptoUtils.ICipher
                public byte[] getIV() {
                    return cipher.getIV();
                }

                @Override // com.microsoft.appcenter.utils.crypto.CryptoUtils.ICipher
                public int getBlockSize() {
                    return cipher.getBlockSize();
                }

                @Override // com.microsoft.appcenter.utils.crypto.CryptoUtils.ICipher
                public String getAlgorithm() {
                    return cipher.getAlgorithm();
                }

                @Override // com.microsoft.appcenter.utils.crypto.CryptoUtils.ICipher
                public String getProvider() {
                    return cipher.getProvider().getName();
                }
            };
        }
    };
    private static final String M_KEY_EXPIRED_EXCEPTION = "android.security.keystore.KeyExpiredException";
    private static CryptoUtils sInstance;
    private final int mApiLevel;
    private final Context mContext;
    private final ICryptoFactory mCryptoFactory;
    private final Map<String, CryptoHandlerEntry> mCryptoHandlers;
    private final KeyStore mKeyStore;

    /* loaded from: classes3.dex */
    public interface ICipher {
        byte[] doFinal(byte[] bArr) throws Exception;

        byte[] doFinal(byte[] bArr, int i, int i2) throws Exception;

        String getAlgorithm();

        int getBlockSize();

        byte[] getIV();

        String getProvider();

        void init(int i, Key key) throws Exception;

        void init(int i, Key key, AlgorithmParameterSpec algorithmParameterSpec) throws Exception;
    }

    /* loaded from: classes3.dex */
    public interface ICryptoFactory {
        ICipher getCipher(String str, String str2) throws Exception;

        IKeyGenerator getKeyGenerator(String str, String str2) throws Exception;
    }

    /* loaded from: classes3.dex */
    interface IKeyGenerator {
        void generateKey();

        void init(AlgorithmParameterSpec algorithmParameterSpec) throws Exception;
    }

    private CryptoUtils(Context context) {
        this(context, DEFAULT_CRYPTO_FACTORY, Build.VERSION.SDK_INT);
    }

    CryptoUtils(Context context, ICryptoFactory cryptoFactory, int apiLevel) {
        this.mCryptoHandlers = new LinkedHashMap();
        this.mContext = context.getApplicationContext();
        this.mCryptoFactory = cryptoFactory;
        this.mApiLevel = apiLevel;
        KeyStore keyStore = null;
        if (apiLevel >= 19) {
            try {
                keyStore = KeyStore.getInstance("AndroidKeyStore");
                keyStore.load(null);
            } catch (Exception e) {
                AppCenterLog.error("AppCenter", "Cannot use secure keystore on this device.");
            }
        }
        this.mKeyStore = keyStore;
        if (keyStore != null && apiLevel >= 23) {
            try {
                registerHandler(new CryptoAesHandler());
            } catch (Exception e2) {
                AppCenterLog.error("AppCenter", "Cannot use modern encryption on this device.");
            }
        }
        if (keyStore != null) {
            try {
                registerHandler(new CryptoRsaHandler());
            } catch (Exception e3) {
                AppCenterLog.error("AppCenter", "Cannot use old encryption on this device.");
            }
        }
        CryptoNoOpHandler cryptoNoOpHandler = new CryptoNoOpHandler();
        this.mCryptoHandlers.put(cryptoNoOpHandler.getAlgorithm(), new CryptoHandlerEntry(0, cryptoNoOpHandler));
    }

    public static CryptoUtils getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new CryptoUtils(context);
        }
        return sInstance;
    }

    ICryptoFactory getCryptoFactory() {
        return this.mCryptoFactory;
    }

    private void registerHandler(CryptoHandler handler) throws Exception {
        String alias0 = getAlias(handler, 0);
        String alias1 = getAlias(handler, 1);
        Date aliasDate0 = this.mKeyStore.getCreationDate(alias0);
        Date aliasDate1 = this.mKeyStore.getCreationDate(alias1);
        int index = 0;
        String alias = alias0;
        if (aliasDate1 != null && aliasDate1.after(aliasDate0)) {
            index = 1;
            alias = alias1;
        }
        if (this.mCryptoHandlers.isEmpty() && !this.mKeyStore.containsAlias(alias)) {
            AppCenterLog.debug("AppCenter", "Creating alias: " + alias);
            handler.generateKey(this.mCryptoFactory, alias, this.mContext);
        }
        AppCenterLog.debug("AppCenter", "Using " + alias);
        this.mCryptoHandlers.put(handler.getAlgorithm(), new CryptoHandlerEntry(index, handler));
    }

    private String getAlias(CryptoHandler handler, int index) {
        return "appcenter." + index + "." + handler.getAlgorithm();
    }

    private KeyStore.Entry getKeyStoreEntry(CryptoHandlerEntry handlerEntry) throws Exception {
        return getKeyStoreEntry(handlerEntry.mCryptoHandler, handlerEntry.mAliasIndex);
    }

    private KeyStore.Entry getKeyStoreEntry(CryptoHandler cryptoHandler, int aliasIndex) throws Exception {
        if (this.mKeyStore == null) {
            return null;
        }
        String alias = getAlias(cryptoHandler, aliasIndex);
        return this.mKeyStore.getEntry(alias, null);
    }

    public String encrypt(String data) {
        if (data == null) {
            return null;
        }
        try {
            CryptoHandlerEntry handlerEntry = this.mCryptoHandlers.values().iterator().next();
            CryptoHandler handler = handlerEntry.mCryptoHandler;
            try {
                KeyStore.Entry keyStoreEntry = getKeyStoreEntry(handlerEntry);
                byte[] encryptedBytes = handler.encrypt(this.mCryptoFactory, this.mApiLevel, keyStoreEntry, data.getBytes("UTF-8"));
                String encryptedString = Base64.encodeToString(encryptedBytes, 0);
                return handler.getAlgorithm() + Constants.COMMON_SCHEMA_PREFIX_SEPARATOR + encryptedString;
            } catch (InvalidKeyException e) {
                if (!(e.getCause() instanceof CertificateExpiredException) && !M_KEY_EXPIRED_EXCEPTION.equals(e.getClass().getName())) {
                    throw e;
                }
                AppCenterLog.debug("AppCenter", "Alias expired: " + handlerEntry.mAliasIndex);
                handlerEntry.mAliasIndex = handlerEntry.mAliasIndex ^ 1;
                String newAlias = getAlias(handler, handlerEntry.mAliasIndex);
                if (this.mKeyStore.containsAlias(newAlias)) {
                    AppCenterLog.debug("AppCenter", "Deleting alias: " + newAlias);
                    this.mKeyStore.deleteEntry(newAlias);
                }
                AppCenterLog.debug("AppCenter", "Creating alias: " + newAlias);
                handler.generateKey(this.mCryptoFactory, newAlias, this.mContext);
                return encrypt(data);
            }
        } catch (Exception e2) {
            AppCenterLog.error("AppCenter", "Failed to encrypt data.");
            return data;
        }
    }

    public DecryptedData decrypt(String data) {
        if (data == null) {
            return new DecryptedData(null, null);
        }
        String[] dataSplit = data.split(Constants.COMMON_SCHEMA_PREFIX_SEPARATOR);
        CryptoHandlerEntry handlerEntry = dataSplit.length == 2 ? this.mCryptoHandlers.get(dataSplit[0]) : null;
        CryptoHandler cryptoHandler = handlerEntry == null ? null : handlerEntry.mCryptoHandler;
        if (cryptoHandler == null) {
            AppCenterLog.error("AppCenter", "Failed to decrypt data.");
            return new DecryptedData(data, null);
        }
        try {
            return getDecryptedData(cryptoHandler, handlerEntry.mAliasIndex, dataSplit[1]);
        } catch (Exception e) {
            try {
                return getDecryptedData(cryptoHandler, handlerEntry.mAliasIndex ^ 1, dataSplit[1]);
            } catch (Exception e2) {
                AppCenterLog.error("AppCenter", "Failed to decrypt data.");
                return new DecryptedData(data, null);
            }
        }
    }

    private DecryptedData getDecryptedData(CryptoHandler cryptoHandler, int aliasIndex, String data) throws Exception {
        KeyStore.Entry keyStoreEntry = getKeyStoreEntry(cryptoHandler, aliasIndex);
        byte[] decryptedBytes = cryptoHandler.decrypt(this.mCryptoFactory, this.mApiLevel, keyStoreEntry, Base64.decode(data, 0));
        String decryptedString = new String(decryptedBytes, "UTF-8");
        String newEncryptedData = null;
        if (cryptoHandler != this.mCryptoHandlers.values().iterator().next().mCryptoHandler) {
            newEncryptedData = encrypt(decryptedString);
        }
        return new DecryptedData(decryptedString, newEncryptedData);
    }

    /* loaded from: classes3.dex */
    public static class CryptoHandlerEntry {
        int mAliasIndex;
        final CryptoHandler mCryptoHandler;

        CryptoHandlerEntry(int aliasIndex, CryptoHandler cryptoHandler) {
            this.mAliasIndex = aliasIndex;
            this.mCryptoHandler = cryptoHandler;
        }
    }

    /* loaded from: classes3.dex */
    public static class DecryptedData {
        final String mDecryptedData;
        final String mNewEncryptedData;

        public DecryptedData(String decryptedData, String newEncryptedData) {
            this.mDecryptedData = decryptedData;
            this.mNewEncryptedData = newEncryptedData;
        }

        public String getDecryptedData() {
            return this.mDecryptedData;
        }

        public String getNewEncryptedData() {
            return this.mNewEncryptedData;
        }
    }
}
