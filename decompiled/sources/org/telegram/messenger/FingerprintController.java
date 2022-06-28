package org.telegram.messenger;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.Locale;
import javax.crypto.Cipher;
import org.telegram.messenger.support.fingerprint.FingerprintManagerCompat;
/* loaded from: classes4.dex */
public class FingerprintController {
    private static final String KEY_ALIAS = "tmessages_passcode";
    private static Boolean hasChangedFingerprints;
    private static KeyPairGenerator keyPairGenerator;
    private static KeyStore keyStore;

    private static KeyStore getKeyStore() {
        KeyStore keyStore2 = keyStore;
        if (keyStore2 != null) {
            return keyStore2;
        }
        try {
            KeyStore keyStore3 = KeyStore.getInstance("AndroidKeyStore");
            keyStore = keyStore3;
            keyStore3.load(null);
            return keyStore;
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }

    private static KeyPairGenerator getKeyPairGenerator() {
        KeyPairGenerator keyPairGenerator2 = keyPairGenerator;
        if (keyPairGenerator2 != null) {
            return keyPairGenerator2;
        }
        try {
            KeyPairGenerator keyPairGenerator3 = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
            keyPairGenerator = keyPairGenerator3;
            return keyPairGenerator3;
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }

    public static void generateNewKey(final boolean notifyCheckFingerprint) {
        KeyPairGenerator generator = getKeyPairGenerator();
        if (generator != null) {
            try {
                Locale realLocale = Locale.getDefault();
                setLocale(Locale.ENGLISH);
                generator.initialize(new KeyGenParameterSpec.Builder(KEY_ALIAS, 3).setDigests("SHA-256", "SHA-512").setEncryptionPaddings("OAEPPadding").setUserAuthenticationRequired(true).build());
                generator.generateKeyPair();
                setLocale(realLocale);
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.FingerprintController$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didGenerateFingerprintKeyPair, Boolean.valueOf(notifyCheckFingerprint));
                    }
                });
            } catch (InvalidAlgorithmParameterException e) {
                FileLog.e(e);
            } catch (Exception e2) {
                if (!e2.getClass().getName().equals("android.security.KeyStoreException")) {
                    FileLog.e(e2);
                }
            }
        }
    }

    public static void deleteInvalidKey() {
        KeyStore keyStore2 = getKeyStore();
        try {
            keyStore2.deleteEntry(KEY_ALIAS);
        } catch (KeyStoreException e) {
            FileLog.e(e);
        }
        hasChangedFingerprints = null;
        checkKeyReady(false);
    }

    public static void checkKeyReady() {
        checkKeyReady(true);
    }

    public static void checkKeyReady(final boolean notifyCheckFingerprint) {
        if (!isKeyReady() && AndroidUtilities.isKeyguardSecure() && FingerprintManagerCompat.from(ApplicationLoader.applicationContext).isHardwareDetected() && FingerprintManagerCompat.from(ApplicationLoader.applicationContext).hasEnrolledFingerprints()) {
            Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.FingerprintController$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    FingerprintController.generateNewKey(notifyCheckFingerprint);
                }
            });
        }
    }

    public static boolean isKeyReady() {
        try {
            return getKeyStore().containsAlias(KEY_ALIAS);
        } catch (KeyStoreException e) {
            FileLog.e(e);
            return false;
        }
    }

    public static boolean checkDeviceFingerprintsChanged() {
        Boolean bool = hasChangedFingerprints;
        if (bool != null) {
            return bool.booleanValue();
        }
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
            cipher.init(2, keyStore.getKey(KEY_ALIAS, null));
            Boolean bool2 = false;
            hasChangedFingerprints = bool2;
            return bool2.booleanValue();
        } catch (KeyPermanentlyInvalidatedException e) {
            Boolean bool3 = true;
            hasChangedFingerprints = bool3;
            return bool3.booleanValue();
        } catch (Exception e2) {
            FileLog.e(e2);
            Boolean bool4 = false;
            hasChangedFingerprints = bool4;
            return bool4.booleanValue();
        }
    }

    private static void setLocale(Locale locale) {
        Locale.setDefault(locale);
        Resources resources = ApplicationLoader.applicationContext.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }
}
