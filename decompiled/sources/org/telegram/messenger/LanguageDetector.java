package org.telegram.messenger;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.sdkinternal.MlKitContext;
import com.google.mlkit.nl.languageid.LanguageIdentification;
import org.telegram.messenger.LanguageDetector;
/* loaded from: classes4.dex */
public class LanguageDetector {

    /* loaded from: classes4.dex */
    public interface ExceptionCallback {
        void run(Exception exc);
    }

    /* loaded from: classes4.dex */
    public interface StringCallback {
        void run(String str);
    }

    public static boolean hasSupport() {
        return true;
    }

    public static void detectLanguage(String text, StringCallback onSuccess, ExceptionCallback onFail) {
        detectLanguage(text, onSuccess, onFail, false);
    }

    public static void detectLanguage(String text, final StringCallback onSuccess, final ExceptionCallback onFail, boolean initializeFirst) {
        if (initializeFirst) {
            try {
                MlKitContext.zza(ApplicationLoader.applicationContext);
            } catch (IllegalStateException e) {
                if (!initializeFirst) {
                    detectLanguage(text, onSuccess, onFail, true);
                    return;
                } else if (onFail != null) {
                    onFail.run(e);
                    return;
                } else {
                    return;
                }
            } catch (Exception e2) {
                if (onFail != null) {
                    onFail.run(e2);
                    return;
                }
                return;
            } catch (Throwable th) {
                if (onFail != null) {
                    onFail.run(null);
                    return;
                }
                return;
            }
        }
        LanguageIdentification.getClient().identifyLanguage(text).addOnSuccessListener(new OnSuccessListener() { // from class: org.telegram.messenger.LanguageDetector$$ExternalSyntheticLambda1
            @Override // com.google.android.gms.tasks.OnSuccessListener
            public final void onSuccess(Object obj) {
                LanguageDetector.lambda$detectLanguage$0(LanguageDetector.StringCallback.this, (String) obj);
            }
        }).addOnFailureListener(new OnFailureListener() { // from class: org.telegram.messenger.LanguageDetector$$ExternalSyntheticLambda0
            @Override // com.google.android.gms.tasks.OnFailureListener
            public final void onFailure(Exception exc) {
                LanguageDetector.lambda$detectLanguage$1(LanguageDetector.ExceptionCallback.this, exc);
            }
        });
    }

    public static /* synthetic */ void lambda$detectLanguage$0(StringCallback onSuccess, String str) {
        if (onSuccess != null) {
            onSuccess.run(str);
        }
    }

    public static /* synthetic */ void lambda$detectLanguage$1(ExceptionCallback onFail, Exception e) {
        if (onFail != null) {
            onFail.run(e);
        }
    }
}
