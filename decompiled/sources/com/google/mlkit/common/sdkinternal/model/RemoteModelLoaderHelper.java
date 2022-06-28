package com.google.mlkit.common.sdkinternal.model;

import com.google.mlkit.common.MlKitException;
import java.nio.MappedByteBuffer;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes3.dex */
public interface RemoteModelLoaderHelper {
    MappedByteBuffer loadModelAtPath(String str) throws MlKitException;
}
