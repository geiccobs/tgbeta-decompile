package com.google.mlkit.common.sdkinternal.model;

import com.google.mlkit.common.model.RemoteModel;
import java.io.File;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes3.dex */
public interface ModelValidator {
    ValidationResult validateModel(File file, RemoteModel remoteModel);

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static class ValidationResult {
        public static final ValidationResult VALID = new ValidationResult(ErrorCode.OK, null);
        private final ErrorCode zza;
        private final String zzb;

        /* compiled from: com.google.mlkit:common@@17.0.0 */
        /* loaded from: classes3.dex */
        public enum ErrorCode {
            OK,
            TFLITE_VERSION_INCOMPATIBLE,
            MODEL_FORMAT_INVALID
        }

        public ValidationResult(ErrorCode errorCode, String str) {
            this.zza = errorCode;
            this.zzb = str;
        }

        public boolean isValid() {
            return this.zza == ErrorCode.OK;
        }

        public ErrorCode getErrorCode() {
            return this.zza;
        }

        public String getErrorMessage() {
            return this.zzb;
        }
    }
}
